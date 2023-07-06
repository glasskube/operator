package eu.glasskube.operator.apps.keycloak.dependent

import eu.glasskube.operator.apps.keycloak.Keycloak
import eu.glasskube.operator.generic.condition.PostgresReadyCondition
import eu.glasskube.operator.generic.dependent.postgres.DependentPostgresCluster

class KeycloakPostgresCluster : DependentPostgresCluster<Keycloak>(Keycloak.Postgres) {
    class ReadyCondition : PostgresReadyCondition<Keycloak>()
    override val Keycloak.storageSize get() = "10Gi"
}
