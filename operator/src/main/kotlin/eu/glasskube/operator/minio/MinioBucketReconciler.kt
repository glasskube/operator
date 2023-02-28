package eu.glasskube.operator.minio

import eu.glasskube.kubernetes.client.patchOrUpdateStatus
import eu.glasskube.operator.Labels
import eu.glasskube.operator.decodeBase64
import eu.glasskube.operator.minio.dependent.MinioBucketSecret
import io.fabric8.kubernetes.api.model.Secret
import io.fabric8.kubernetes.client.KubernetesClient
import io.javaoperatorsdk.operator.api.reconciler.Cleaner
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.DeleteControl
import io.javaoperatorsdk.operator.api.reconciler.MaxReconciliationInterval
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.RemoveBucketArgs
import io.minio.admin.MinioAdminClient
import io.minio.admin.UserInfo
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

@ControllerConfiguration(
    dependents = [
        Dependent(
            type = MinioBucketSecret::class,
            reconcilePrecondition = MinioBucketSecret.ReconcilePrecondition::class
        )
    ],
    maxReconciliationInterval = MaxReconciliationInterval(interval = 5, timeUnit = TimeUnit.MINUTES)
)
class MinioBucketReconciler(
    private val minioClient: MinioClient,
    private val minioAdminClient: MinioAdminClient,
    private val kubernetesClient: KubernetesClient
) : Reconciler<MinioBucket>, Cleaner<MinioBucket> {

    override fun reconcile(resource: MinioBucket, context: Context<MinioBucket>): UpdateControl<MinioBucket> {
        log.info("reconciling {}@{}", resource.metadata.name, resource.metadata.namespace)

        val userSecret = resource.getUserSecret(context)
        val username = userSecret?.data?.get(MinioBucket.USERNAME_KEY)?.decodeBase64()
        val password = userSecret?.data?.get(MinioBucket.PASSWORD_KEY)?.decodeBase64()
        val bucketCreated = resource.createBucket()
        val policyCreated = resource.createBucketPolicy()
        val userCreated = username != null && password != null && resource.createBucketUser(username, password)
        val policyLinked = username != null && userCreated && policyCreated && resource.linkUserAndPolicy(username)

        when (val oldUsername = resource.status?.username) {
            null -> log.debug("username was previously null")
            username -> log.debug("username did not change since last reconciliation")
            else -> resource.deleteBucketUser(oldUsername)
        }

        val status = MinioBucketStatus(
            bucketCreated,
            username ?: resource.status?.username,
            userCreated,
            policyCreated,
            policyLinked
        )

        return resource.patchOrUpdateStatus(status).apply {
            if (!status.allCreated) {
                log.info("retry after 5s")
                rescheduleAfter(5, TimeUnit.SECONDS)
            }
        }
    }

    override fun cleanup(resource: MinioBucket, context: Context<MinioBucket>): DeleteControl {
        with(resource) {
            when (val username = resource.status.username) {
                null -> log.warn("can not delete user because username is null")
                else -> deleteBucketUser(username)
            }
            deleteBucketPolicy()
            deleteBucket()
        }

        return DeleteControl.defaultDelete()
    }

    private fun MinioBucket.getUserSecret(context: Context<MinioBucket>): Secret? =
        when (val selector = spec.userSecret) {
            null -> context.getSecondaryResource(Secret::class.java).orElse(null)
            else -> kubernetesClient.secrets().inNamespace(metadata.namespace).withName(selector.name).get()
        }

    private val MinioBucket.hasBucket: Boolean
        get() = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())

    private fun MinioBucket.createBucket(): Boolean =
        if (!hasBucket) {
            runCatching { minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build()) }
                .onSuccess { log.info("bucket $bucketName created") }
                .onFailure { log.warn("could not create bucket", it) }
                .isSuccess
        } else {
            log.info("bucket $bucketName already exists")
            true
        }

    private fun MinioBucket.deleteBucket() {
        runCatching { minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build()) }
            .onSuccess { log.info("bucket $bucketName deleted") }
            .onFailure { log.warn("could not delete bucket $bucketName", it) }
    }

    private fun hasBucketUser(username: String): Boolean =
        username in minioAdminClient.listUsers()

    private fun MinioBucket.createBucketUser(username: String, password: String): Boolean =
        if (!hasBucketUser(username)) {
            runCatching { minioAdminClient.addUser(username, UserInfo.Status.ENABLED, password, policyName, null) }
                .onSuccess { log.info("minio user $username created") }
                .onFailure { log.warn("could not create user $username", it) }
                .isSuccess
        } else {
            log.info("minio user $username already exists")
            true
        }

    private fun MinioBucket.deleteBucketUser(username: String) {
        runCatching { minioAdminClient.deleteUser(username) }
            .onSuccess { log.info("minio user $username deleted") }
            .onFailure { log.warn("could not delete user $username", it) }
    }

    private val MinioBucket.hasBucketPolicy: Boolean
        get() = policyName in minioAdminClient.listCannedPolicies()

    private fun MinioBucket.createBucketPolicy(): Boolean =
        if (!hasBucketPolicy) {
            runCatching { minioAdminClient.addCannedPolicy(policyName, policy) }
                .onSuccess { log.info("bucket policy $policyName created") }
                .onFailure { log.warn("could not create bucket policy", it) }
                .isSuccess
        } else {
            log.info("bucket policy $policyName already exists")
            true
        }

    private fun MinioBucket.deleteBucketPolicy() {
        runCatching { minioAdminClient.removeCannedPolicy(policyName) }
            .onSuccess { log.info("bucket policy $policyName deleted") }
            .onFailure { log.warn("could not delete bucket policy $policyName", it) }
    }

    private fun MinioBucket.linkUserAndPolicy(username: String): Boolean {
        return runCatching { minioAdminClient.setPolicy(username, false, policyName) }
            .onSuccess { log.info("bucket policy $policyName set for user $username") }
            .onFailure { log.warn("could not set policy $policyName for user $username", it) }
            .isSuccess
    }

    companion object {
        const val SELECTOR =
            "${Labels.MANAGED_BY_GLASSKUBE},${Labels.NAME}=${MinioBucket.APP_NAME}"

        private val log = LoggerFactory.getLogger(MinioBucketReconciler::class.java)
    }
}
