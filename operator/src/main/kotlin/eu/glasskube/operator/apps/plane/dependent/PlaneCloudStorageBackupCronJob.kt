package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.generic.dependent.backups.DependentCloudStorageBackupCronJob
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent
class PlaneCloudStorageBackupCronJob : DependentCloudStorageBackupCronJob<Plane>() {
    internal class ReconcilePrecondition : DependentCloudStorageBackupCronJob.ReconcilePrecondition<Plane>()
}
