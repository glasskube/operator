package eu.glasskube.operator.apps.gitea.dependent

import eu.glasskube.operator.apps.gitea.Gitea
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroSchedule
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent
class GiteaVeleroSchedule : DependentVeleroSchedule<Gitea>()
