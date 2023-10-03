package eu.glasskube.operator.generic.dependent.backups

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.secret
import eu.glasskube.operator.apps.common.backup.ResourceWithBackups
import eu.glasskube.operator.boot.VeleroProperties
import eu.glasskube.utils.decodeBase64
import eu.glasskube.utils.encodeBase64
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDNoGCKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.event.ResourceID
import org.springframework.beans.factory.annotation.Autowired

abstract class DependentVeleroSecret<P> :
    CRUDNoGCKubernetesDependentResource<Secret, P>(Secret::class.java), BackupDependentResource<P>
    where P : HasMetadata, P : ResourceWithBackups {

    abstract class Discriminator<P> :
        ResourceIDMatcherDiscriminator<Secret, P>({
            ResourceID(it.velero.resourceNameWithNamespace, VeleroProperties.INSTANCE.namespace)
        })
        where P : HasMetadata, P : ResourceWithBackups

    @Autowired
    private lateinit var veleroProperties: VeleroProperties

    private fun getAwsCredentialsFile(primary: P, context: Context<P>): String {
        val backupSpec = primary.getSpec().requireBackups()
        val accessKeySelector = backupSpec.s3.accessKeySecret
        val secretKeySelector = backupSpec.s3.secretKeySecret
        val accessKeySecret = context.client.secrets()
            .inNamespace(primary.namespace).withName(accessKeySelector.name).require()
        val secretKeySecret = if (secretKeySelector.name == accessKeySelector.name) {
            accessKeySecret
        } else {
            context.client.secrets().inNamespace(primary.namespace).withName(secretKeySelector.name).require()
        }
        return """
            [default]
            aws_access_key_id=${accessKeySecret.data.getValue(accessKeySelector.key).decodeBase64()}
            aws_secret_access_key=${secretKeySecret.data.getValue(secretKeySelector.key).decodeBase64()}
        """.trimIndent().encodeBase64()
    }

    override fun desired(primary: P, context: Context<P>) = secret {
        metadata {
            name(primary.velero.resourceNameWithNamespace)
            namespace(veleroProperties.namespace)
            labels(primary.velero.resourceLabels)
        }
        data = mapOf(
            "cloud" to getAwsCredentialsFile(primary, context)
        )
    }
}
