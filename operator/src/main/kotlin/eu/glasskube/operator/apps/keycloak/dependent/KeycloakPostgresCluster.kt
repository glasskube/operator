package eu.glasskube.operator.apps.keycloak.dependent

import eu.glasskube.operator.apps.keycloak.Keycloak
import eu.glasskube.operator.config.ConfigService
import eu.glasskube.operator.generic.condition.PostgresReadyCondition
import eu.glasskube.operator.generic.dependent.postgres.DependentPostgresCluster

class KeycloakPostgresCluster(configService: ConfigService) :
    DependentPostgresCluster<Keycloak>(Keycloak.Postgres, configService) {
    class ReadyCondition : PostgresReadyCondition<Keycloak>()

    override val Keycloak.defaultStorageSize get() = "10Gi"
}
