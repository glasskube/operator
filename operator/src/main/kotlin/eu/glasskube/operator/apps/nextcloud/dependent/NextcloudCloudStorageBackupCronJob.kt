package eu.glasskube.operator.apps.nextcloud.dependent

import eu.glasskube.operator.apps.nextcloud.Nextcloud
import eu.glasskube.operator.generic.dependent.backups.DependentCloudStorageBackupCronJob
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(resourceDiscriminator = NextcloudCloudStorageBackupCronJob.Discriminator::class)
class NextcloudCloudStorageBackupCronJob : DependentCloudStorageBackupCronJob<Nextcloud>() {
    internal class Discriminator : DependentCloudStorageBackupCronJob.Discriminator<Nextcloud>()
    internal class ReconcilePrecondition : DependentCloudStorageBackupCronJob.ReconcilePrecondition<Nextcloud>()
}
