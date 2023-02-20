package eu.glasskube.operator.odoo

import eu.glasskube.operator.api.reconciler.informerEventSource
import eu.glasskube.operator.decodeBase64
import eu.glasskube.operator.odoo.dependent.OdooConfigMap
import eu.glasskube.operator.odoo.dependent.OdooDatabaseBackupSecret
import eu.glasskube.operator.odoo.dependent.OdooDeployment
import eu.glasskube.operator.odoo.dependent.OdooIngress
import eu.glasskube.operator.odoo.dependent.OdooPersistentVolumeClaim
import eu.glasskube.operator.odoo.dependent.OdooPostgresCluster
import eu.glasskube.operator.odoo.dependent.OdooPostgresScheduledBackup
import eu.glasskube.operator.odoo.dependent.OdooService
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Cleaner
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.api.reconciler.DeleteControl
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer
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

@ControllerConfiguration(
    dependents = [
        Dependent(
            type = OdooDeployment::class,
            dependsOn = ["OdooPostgresCluster", "OdooConfigMap", "OdooPersistentVolumeClaim"]
        ),
        Dependent(
            name = "OdooConfigMap",
            type = OdooConfigMap::class
        ),
        Dependent(
            name = "OdooPersistentVolumeClaim",
            type = OdooPersistentVolumeClaim::class
        ),
        Dependent(type = OdooService::class),
        Dependent(type = OdooIngress::class),
        Dependent(
            type = OdooPostgresScheduledBackup::class,
            dependsOn = ["OdooPostgresCluster"]
        ),
        Dependent(
            name = "OdooPostgresCluster",
            type = OdooPostgresCluster::class,
            dependsOn = ["OdooDatabaseBackupSecret"]
        ),
        Dependent(
            name = "OdooDatabaseBackupSecret",
            type = OdooDatabaseBackupSecret::class,
            useEventSourceWithName = OdooReconciler.SECRETS_EVENT_SOURCE_NAME
        )
    ]
)
class OdooReconciler(
    private val minioClient: MinioClient,
    private val minioAdminClient: MinioAdminClient
) : Reconciler<Odoo>, EventSourceInitializer<Odoo>, Cleaner<Odoo> {

    override fun reconcile(resource: Odoo, context: Context<Odoo>): UpdateControl<Odoo> {
        if (resource.status.demoEnabledOnInstall == !resource.spec.demoEnabled) {
            throw IllegalStateException("demoEnabled can not be altered after first reconciliation")
        }

        resource.createBucket()
        resource.createBucketPolicy()

        context.getSecondaryResource(Secret::class.java, OdooDatabaseBackupSecret.Discriminator()).ifPresent {
            val password = it.data["password"]?.decodeBase64()
            if (password != null) {
                resource.createBucketUser(password)
            }
        }

        return resource.newStatus()
            .takeIf { it != resource.status }
            ?.let {
                resource.status = it
                UpdateControl.updateStatus(resource)
            }
            ?: UpdateControl.noUpdate()
    }

    private fun Odoo.newStatus() =
        OdooStatus(demoEnabledOnInstall = spec.demoEnabled)

    private val Odoo.hasBucket: Boolean
        get() = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())

    private fun Odoo.createBucket() {
        if (!hasBucket) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
            LOG.info("bucket $bucketName created")
        } else {
            LOG.info("bucket $bucketName already exists")
        }
    }

    private fun Odoo.deleteBucket() {
        runCatching { minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build()) }
            .onSuccess { LOG.info("bucket $bucketName deleted") }
            .onFailure { LOG.warn("could not delete bucket $bucketName", it) }
    }

    private val Odoo.hasBucketUser: Boolean
        get() = dbBackupUsername in minioAdminClient.listUsers()

    private fun Odoo.createBucketUser(password: String) {
        if (!hasBucketUser) {
            minioAdminClient.addUser(dbBackupUsername, UserInfo.Status.ENABLED, password, bucketName, null)
            LOG.info("minio user $dbBackupUsername created")
        } else {
            LOG.info("minio user $dbBackupUsername already exists")
        }

        minioAdminClient.setPolicy(dbBackupUsername, false, bucketName)
        LOG.info("bucket policy $bucketName set for user $dbBackupUsername")
    }

    private fun Odoo.deleteBucketUser() {
        runCatching { minioAdminClient.deleteUser(dbBackupUsername) }
            .onSuccess { LOG.info("minio user $dbBackupUsername deleted") }
            .onFailure { LOG.warn("could not delete user $dbBackupUsername", it) }
    }

    private val Odoo.hasBucketPolicy: Boolean
        get() = bucketName in minioAdminClient.listCannedPolicies()

    private fun Odoo.createBucketPolicy() {
        if (!hasBucketPolicy) {
            minioAdminClient.addCannedPolicy(bucketName, bucketPolicy)
            LOG.info("bucket policy $bucketName created")
        } else {
            LOG.info("bucket policy $bucketName already exists")
        }
    }

    private fun Odoo.deleteBucketPolicy() {
        runCatching { minioAdminClient.removeCannedPolicy(bucketName) }
            .onSuccess { LOG.info("bucket policy $bucketName deleted") }
            .onFailure { LOG.warn("could not delete bucket policy $bucketName", it) }
    }

    override fun cleanup(resource: Odoo, context: Context<Odoo>): DeleteControl {
        resource.deleteBucket()
        resource.deleteBucketUser()
        resource.deleteBucketPolicy()
        return DeleteControl.defaultDelete()
    }

    override fun prepareEventSources(context: EventSourceContext<Odoo>) = with(context) {
        mutableMapOf(SECRETS_EVENT_SOURCE_NAME to informerEventSource<Secret>())
    }

    companion object {
        const val LABEL = "glasskube.eu/Odoo"
        const val APP_NAME = "odoo"
        const val SELECTOR = "app.kubernetes.io/managed-by=glasskube-operator,app=$APP_NAME"
        const val SECRETS_EVENT_SOURCE_NAME = "OdooSecretEventSource"

        private val LOG = LoggerFactory.getLogger(OdooReconciler::class.java)
    }
}
