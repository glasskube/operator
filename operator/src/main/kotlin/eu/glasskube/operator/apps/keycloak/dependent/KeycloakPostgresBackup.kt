package eu.glasskube.operator.apps.keycloak.dependent

import eu.glasskube.operator.apps.keycloak.Keycloak
import eu.glasskube.operator.apps.keycloak.KeycloakReconciler
import eu.glasskube.operator.generic.dependent.postgres.DependentPostgresScheduledBackup
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = KeycloakReconciler.SELECTOR)
class KeycloakPostgresBackup : DependentPostgresScheduledBackup<Keycloak>(Keycloak.Postgres) {
    class ReconcilePrecondition : DependentPostgresScheduledBackup.ReconcilePrecondition<Keycloak>()
}
