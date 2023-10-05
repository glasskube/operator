package eu.glasskube.operator.apps.metabase.dependent

import eu.glasskube.operator.apps.metabase.Metabase
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroSchedule
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent
class MetabaseVeleroSchedule : DependentVeleroSchedule<Metabase>()
