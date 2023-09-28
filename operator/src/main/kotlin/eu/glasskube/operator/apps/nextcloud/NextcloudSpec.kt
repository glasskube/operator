package eu.glasskube.operator.apps.nextcloud

import eu.glasskube.operator.apps.common.database.HasDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
import eu.glasskube.operator.validation.Patterns
import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Pattern
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements

data class NextcloudSpec(
    val host: String,
    val defaultPhoneRegion: String?,
    val apps: NextcloudAppsSpec = NextcloudAppsSpec(),
    @field:Nullable
    val smtp: NextcloudSmtpSpec?,
    val storage: NextcloudStorageSpec?,
    @field:Pattern(Patterns.SEMVER)
    val version: String = "27.0.1",
    val server: ServerSpec = ServerSpec(),
    @field:Nullable
    override val database: PostgresDatabaseSpec?
) : HasDatabaseSpec<PostgresDatabaseSpec> {
    data class ServerSpec(
        @field:Nullable
        val resources: ResourceRequirements = ResourceRequirements(
            null,
            mapOf("memory" to Quantity("600", "Mi")),
            mapOf("memory" to Quantity("300", "Mi"))
        ),
        val maxChildren: Int = 256,
        val startServers: Int = maxChildren / 8,
        val minSpareServers: Int = maxChildren / 16,
        val maxSpareServers: Int = maxChildren / 4
    )
}
