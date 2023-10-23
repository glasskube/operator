package eu.glasskube.operator.apps.gitlab.dependent

import eu.glasskube.operator.apps.gitlab.Gitlab
import eu.glasskube.operator.apps.gitlab.GitlabReconciler
import eu.glasskube.operator.generic.dependent.backups.DependentCloudStorageBackupCronJob
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GitlabReconciler.SELECTOR)
class GitlabCloudStorageBackupCronJob : DependentCloudStorageBackupCronJob<Gitlab>() {
    internal class ReconcilePrecondition : DependentCloudStorageBackupCronJob.ReconcilePrecondition<Gitlab>()
}
