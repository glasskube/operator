package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.generic.dependent.postgres.DependentPostgresScheduledBackup
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent
class PlanePostgresBackup : DependentPostgresScheduledBackup<Plane>(Plane.Postgres)
