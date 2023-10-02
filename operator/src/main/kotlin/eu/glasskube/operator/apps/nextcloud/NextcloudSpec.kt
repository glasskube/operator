package eu.glasskube.operator.apps.nextcloud

import eu.glasskube.operator.validation.Patterns
import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Pattern
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements

data class NextcloudSpec(
    val host: String,
    val defaultPhoneRegion: String?,
    val apps: NextcloudAppsSpec = NextcloudAppsSpec(),
    val resources: ResourceRequirements = defaultResourceRequirements,
    @field:Nullable
    val smtp: NextcloudSmtpSpec?,
    val storage: NextcloudStorageSpec?,
    @field:Pattern(Patterns.SEMVER)
    val version: String = "27.0.1"
) {
    companion object {
        private val defaultResourceRequirements
            get() = ResourceRequirements(
                null,
                mapOf("memory" to Quantity("400", "Mi")),
                mapOf("memory" to Quantity("100", "Mi"))
            )
    }
}
