package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroSchedule
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent
class PlaneVeleroSchedule : DependentVeleroSchedule<Plane>()
