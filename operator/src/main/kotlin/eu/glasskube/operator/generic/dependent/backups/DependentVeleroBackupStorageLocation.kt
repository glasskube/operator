package eu.glasskube.operator.generic.dependent.backups

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.secretKeySelector
import eu.glasskube.operator.apps.common.backup.ResourceWithBackups
import eu.glasskube.operator.boot.VeleroProperties
import eu.glasskube.operator.infra.velero.VeleroBackupStorageLocation
import eu.glasskube.operator.infra.velero.veleroBackupStorageLocation
import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDNoGCKubernetesDependentResource
import org.springframework.beans.factory.annotation.Autowired

abstract class DependentVeleroBackupStorageLocation<P> :
    CRUDNoGCKubernetesDependentResource<VeleroBackupStorageLocation, P>(VeleroBackupStorageLocation::class.java),
    BackupDependentResource<P>
    where P : HasMetadata, P : ResourceWithBackups {

    @Autowired
    private lateinit var veleroProperties: VeleroProperties

    override fun desired(primary: P, context: Context<P>) =
        veleroBackupStorageLocation {
            val backupsSpec = primary.getSpec().requireBackups()

            metadata {
                name(primary.velero.resourceNameWithNamespace)
                namespace(veleroProperties.namespace)
                labels(primary.velero.resourceLabels)
            }

            spec = backupsSpec.s3.run {
                VeleroBackupStorageLocation.Spec(
                    objectStorage = VeleroBackupStorageLocation.Spec.ObjectStorage(
                        bucket = bucket
                    ),
                    credential = secretKeySelector(primary.velero.resourceNameWithNamespace, "cloud"),
                    config = VeleroBackupStorageLocation.Spec.Config(
                        region = region,
                        s3Url = endpoint,
                        s3ForcePathStyle = VeleroBackupStorageLocation.Spec.Config.S3ForcePathStyle.from(usePathStyle)
                    )
                )
            }
        }
}
