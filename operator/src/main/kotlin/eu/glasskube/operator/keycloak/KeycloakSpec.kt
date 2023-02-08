package eu.glasskube.operator.keycloak

import io.fabric8.kubernetes.api.model.LocalObjectReference

data class KeycloakSpec(
    val instances: Int? = null,
    val image: String? = null,
    val imagePullSecrets: List<LocalObjectReference>? = null,
    val additionalOptions: List<ValueOrSecret>? = null,
    val http: HttpSpec? = null,
    val unsupported: UnsupportedSpec? = null,
    val ingress: IngressSpec? = null,
    val features: FeatureSpec? = null,
    val transaction: TransactionsSpec? = null,
    val db: DatabaseSpec,
    val hostname: HostnameSpec
)
