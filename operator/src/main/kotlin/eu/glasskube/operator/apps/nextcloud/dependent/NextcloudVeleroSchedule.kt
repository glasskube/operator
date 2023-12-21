package eu.glasskube.operator.apps.nextcloud.dependent

import eu.glasskube.operator.apps.nextcloud.Nextcloud
import eu.glasskube.operator.apps.nextcloud.NextcloudReconciler
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroSchedule
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = NextcloudReconciler.SELECTOR)
class NextcloudVeleroSchedule : DependentVeleroSchedule<Nextcloud>()
