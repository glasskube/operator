package eu.glasskube.operator.apps.nextcloud

import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.LocalObjectReference

data class NextcloudAppsSpec(
    @field:Nullable
    val office: Office? = null,
    @field:Nullable
    val oidc: Oidc? = null
) {
    data class Office(
        val host: String,
        val version: String = "23.05.2.2.1"
    )
    data class Oidc(
        @field:Required
        val name: String,
        @field:Required
        val oidcSecret: LocalObjectReference,
        @field:Required
        val issuerUrl: String
    )
}
