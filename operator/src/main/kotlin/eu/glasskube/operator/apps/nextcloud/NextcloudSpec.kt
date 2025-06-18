package eu.glasskube.operator.apps.nextcloud

import eu.glasskube.operator.apps.common.backup.BackupSpec
import eu.glasskube.operator.apps.common.backup.HasBackupSpec
import eu.glasskube.operator.apps.common.cloudstorage.CloudStorageSpec
import eu.glasskube.operator.apps.common.cloudstorage.HasCloudStorageSpec
import eu.glasskube.operator.apps.common.database.HasDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
import eu.glasskube.operator.apps.common.storage.StorageSpec
import eu.glasskube.operator.validation.Patterns
import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Pattern
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.fabric8.kubernetes.api.model.SecretKeySelector

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
    override val database: PostgresDatabaseSpec = PostgresDatabaseSpec(),
    override val backups: BackupSpec?
) : HasBackupSpec, HasCloudStorageSpec, HasDatabaseSpec<PostgresDatabaseSpec> {

    override val cloudStorage get() = storage?.s3

    data class ServerSpec(
        @field:Nullable
        val resources: ResourceRequirements = ResourceRequirements(
            null,
            mapOf("memory" to Quantity("1800", "Mi")),
            mapOf("memory" to Quantity("900", "Mi"))
        ),
        val maxChildren: Int = 512,
        val startServers: Int = maxChildren / 8,
        val minSpareServers: Int = maxChildren / 16,
        val maxSpareServers: Int = maxChildren / 4
    )

    data class NextcloudStorageSpec(
        override val size: Quantity?,
        override val storageClassName: String?,
        val s3: S3?
    ) : StorageSpec {
        data class S3(
            @field:Required
            override val bucket: String,
            @field:Required
            override val accessKeySecret: SecretKeySelector,
            @field:Required
            override val secretKeySecret: SecretKeySelector,
            @field:Nullable
            override val region: String?,
            @field:Nullable
            override val hostname: String?,
            @field:Nullable
            override val port: Int?,
            @field:Nullable
            val objectPrefix: String?,
            @field:Nullable
            val autoCreate: Boolean?,
            override val useSsl: Boolean = true,
            @field:Nullable
            override val usePathStyle: Boolean?,
            @field:Nullable
            val legacyAuth: Boolean?
        ) : CloudStorageSpec
    }
}
