package eu.glasskube.operator.apps.matomo.dependent

import eu.glasskube.operator.apps.matomo.Matomo
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroBackupStorageLocation
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent
class MatomoVeleroBackupStorageLocation : DependentVeleroBackupStorageLocation<Matomo>()
