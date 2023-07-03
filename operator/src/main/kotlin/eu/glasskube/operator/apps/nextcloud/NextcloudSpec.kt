package eu.glasskube.operator.apps.nextcloud

import io.fabric8.generator.annotation.Nullable
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements

data class NextcloudSpec(
    val host: String,
    val defaultPhoneRegion: String?,
    val apps: NextcloudAppsSpec = NextcloudAppsSpec(),
    val resources: ResourceRequirements = ResourceRequirements(
        null,
        mapOf(
            "cpu" to Quantity("1", ""),
            "memory" to Quantity("400", "Mi")
        ),
        mapOf("memory" to Quantity("100", "Mi"))
    ),
    @field:Nullable
    val smtp: NextcloudSmtp?
)
