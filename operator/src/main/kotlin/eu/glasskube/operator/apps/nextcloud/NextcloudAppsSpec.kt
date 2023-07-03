package eu.glasskube.operator.apps.nextcloud

import io.fabric8.generator.annotation.Nullable

data class NextcloudAppsSpec(
    @field:Nullable
    val office: Office? = null
) {
    data class Office(
        val host: String
    )
}
