package eu.glasskube.operator.apps.plane

import eu.glasskube.operator.apps.common.backup.BackupSpec
import eu.glasskube.operator.apps.common.backup.HasBackupSpec
import eu.glasskube.operator.apps.common.cloudstorage.CloudStorageSpec
import eu.glasskube.operator.apps.common.cloudstorage.HasCloudStorageSpec
import eu.glasskube.operator.apps.common.database.HasDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.LocalObjectReference
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.fabric8.kubernetes.api.model.SecretKeySelector

data class PlaneSpec(
    @field:Required
    val host: String,
    val registrationEnabled: Boolean = true,
    val defaultUser: DefaultUserSpec = DefaultUserSpec("root@example.com", "glasskube-operator"),
    val frontend: FrontendSpec = FrontendSpec(),
    val space: SpaceSpec = SpaceSpec(),
    val api: ApiSpec = ApiSpec(),
    val beatWorker: BeatWorkerSpec = BeatWorkerSpec(),
    val worker: WorkerSpec = WorkerSpec(),
    val smtp: SmtpSpec? = null,
    val s3: S3Spec? = null,
    val version: String = "v0.13.2-dev",
    @field:Nullable
    override val database: PostgresDatabaseSpec = PostgresDatabaseSpec(),
    override val backups: BackupSpec?
) : HasBackupSpec, HasCloudStorageSpec, HasDatabaseSpec<PostgresDatabaseSpec> {

    override val cloudStorage get() = s3

    data class DefaultUserSpec(
        @field:Required
        val email: String,
        @field:Required
        val password: String
    )

    data class FrontendSpec(
        @field:Nullable
        val resources: ResourceRequirements = ResourceRequirements(
            null,
            mapOf(
                "cpu" to Quantity("1"),
                "memory" to Quantity("128", "Mi")
            ),
            mapOf(
                "memory" to Quantity("80", "Mi")
            )
        )
    )

    data class SpaceSpec(
        @field:Nullable
        val resources: ResourceRequirements? = ResourceRequirements(
            null,
            mapOf(
                "cpu" to Quantity("1"),
                "memory" to Quantity("128", "Mi")
            ),
            mapOf(
                "memory" to Quantity("110", "Mi")
            )
        )
    )

    data class ApiSpec(
        val concurrency: Int = 2,
        @field:Nullable
        val resources: ResourceRequirements? = ResourceRequirements(
            null,
            mapOf(
                "cpu" to Quantity(concurrency.toString()),
                "memory" to Quantity((128 * concurrency).toString(), "Mi")
            ),
            mapOf(
                "memory" to Quantity((110 * concurrency).toString(), "Mi")
            )
        )
    )

    data class BeatWorkerSpec(
        @field:Nullable
        val resources: ResourceRequirements? = ResourceRequirements(
            null,
            mapOf(
                "cpu" to Quantity("1"),
                "memory" to Quantity("128", "Mi")
            ),
            mapOf(
                "memory" to Quantity("110", "Mi")
            )
        )
    )

    data class WorkerSpec(
        val concurrency: Int = 2,
        @field:Nullable
        val resources: ResourceRequirements? = ResourceRequirements(
            null,
            mapOf(
                "cpu" to Quantity(concurrency.toString()),
                "memory" to Quantity((128 * concurrency).toString(), "Mi")
            ),
            mapOf(
                "memory" to Quantity((90 * concurrency).toString(), "Mi")
            )
        )
    )

    data class SmtpSpec(
        @field:Required
        val host: String,
        val port: Int = 587,
        @field:Required
        val fromAddress: String,
        @field:Required
        val authSecret: LocalObjectReference,
        val tlsEnabled: Boolean = true,
        val sslEnabled: Boolean = false
    )

    data class S3Spec(
        @field:Required
        override val bucket: String,
        @field:Required
        override val accessKeySecret: SecretKeySelector,
        @field:Required
        override val secretKeySecret: SecretKeySelector,
        @field:Required
        override val region: String,
        @field:Nullable
        override val hostname: String?,
        @field:Nullable
        override val port: Int?,
        override val useSsl: Boolean = true,
        @field:Nullable
        override val usePathStyle: Boolean?
    ) : CloudStorageSpec
}
