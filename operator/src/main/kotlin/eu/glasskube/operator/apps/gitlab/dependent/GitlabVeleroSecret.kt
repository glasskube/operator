package eu.glasskube.operator.apps.gitlab.dependent

import eu.glasskube.operator.apps.gitlab.Gitlab
import eu.glasskube.operator.apps.gitlab.GitlabReconciler
import eu.glasskube.operator.generic.dependent.backups.BackupSpecNotNullCondition
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroSecret
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GitlabReconciler.SELECTOR)
class GitlabVeleroSecret : DependentVeleroSecret<Gitlab>() {
    internal class ReconcilePrecondition : BackupSpecNotNullCondition<Secret, Gitlab>()
}
