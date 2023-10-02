package eu.glasskube.operator.apps.keycloak

import eu.glasskube.operator.Labels
import eu.glasskube.operator.generic.dependent.postgres.PostgresNameMapper
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

@Group("glasskube.eu")
@Version("v1alpha1")
class Keycloak : CustomResource<KeycloakSpec, KeycloakStatus>(), Namespaced {
    companion object {
        internal const val APP_NAME = "keycloak"
    }

    object Postgres : PostgresNameMapper<Keycloak>() {
        override fun getName(primary: Keycloak) = "${primary.genericResourceName}-db"
        override fun getLabels(primary: Keycloak) = primary.resourceLabels
        override fun getDatabaseName(primary: Keycloak) = "keycloak"
    }
}

internal val Keycloak.resourceLabels
    get() = Labels.resourceLabels(Keycloak.APP_NAME, metadata.name, Keycloak.APP_NAME, spec.version)
internal val Keycloak.resourceLabelSelector
    get() = Labels.resourceLabelSelector(Keycloak.APP_NAME, metadata.name, Keycloak.APP_NAME)
internal val Keycloak.genericResourceName get() = "${Keycloak.APP_NAME}-${metadata.name}"
internal val Keycloak.backupBucketName get() = "$genericResourceName-backup"
internal val Keycloak.ingressTlsCertName get() = "$genericResourceName-tls"
internal val Keycloak.discoveryServiceName get() = "$genericResourceName-discovery"
internal val Keycloak.appImage get() = "quay.io/keycloak/${Keycloak.APP_NAME}:${spec.version}"
