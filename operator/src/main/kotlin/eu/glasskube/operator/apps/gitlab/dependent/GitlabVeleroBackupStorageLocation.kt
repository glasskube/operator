package eu.glasskube.operator.apps.gitlab.dependent

import eu.glasskube.operator.apps.gitlab.Gitlab
import eu.glasskube.operator.apps.gitlab.GitlabReconciler
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroBackupStorageLocation
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GitlabReconciler.SELECTOR)
class GitlabVeleroBackupStorageLocation : DependentVeleroBackupStorageLocation<Gitlab>()
