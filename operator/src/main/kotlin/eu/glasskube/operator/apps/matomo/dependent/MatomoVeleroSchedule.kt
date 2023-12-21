package eu.glasskube.operator.apps.matomo.dependent

import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.apps.matomo.MatomoReconciler
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroSchedule
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = MatomoReconciler.SELECTOR)
class MatomoVeleroSchedule : DependentVeleroSchedule<Matomo>()
