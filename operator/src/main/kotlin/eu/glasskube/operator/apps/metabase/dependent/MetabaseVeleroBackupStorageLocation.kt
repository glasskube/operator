package eu.glasskube.operator.apps.metabase.dependent

import eu.glasskube.operator.apps.metabase.Metabase
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroBackupStorageLocation
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent
class MetabaseVeleroBackupStorageLocation : DependentVeleroBackupStorageLocation<Metabase>()
