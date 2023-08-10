package eu.glasskube.operator.apps.nextcloud

import io.fabric8.generator.annotation.Nullable

data class NextcloudAppsSpec(
    @field:Nullable
    val office: Office? = null,
    val oidc: Oidc? = null
) {
    data class Office(
        val host: String
    )
    data class Oidc(
        val name: String,
        val clientId: String,
        val clientSecret: String,
        val discoveryEndpoint: String,
    )
}
