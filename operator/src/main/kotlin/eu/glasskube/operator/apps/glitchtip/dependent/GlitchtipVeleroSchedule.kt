package eu.glasskube.operator.apps.glitchtip.dependent

import eu.glasskube.operator.apps.glitchtip.Glitchtip
import eu.glasskube.operator.apps.glitchtip.GlitchtipReconciler
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroSchedule
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GlitchtipReconciler.SELECTOR)
class GlitchtipVeleroSchedule : DependentVeleroSchedule<Glitchtip>()
