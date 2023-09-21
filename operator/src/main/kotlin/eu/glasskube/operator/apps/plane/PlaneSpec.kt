package eu.glasskube.operator.apps.plane

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
    val s3: S3Spec? = null
) {
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
        val tlsEnabled: Boolean = true
    )

    data class S3Spec(
        @field:Required
        val bucket: String,
        @field:Required
        val accessKeySecret: SecretKeySelector,
        @field:Required
        val secretKeySecret: SecretKeySelector,
        @field:Required
        val region: String,
        @field:Nullable
        val endpoint: String?,
        @field:Nullable
        val usePathStyle: Boolean?
    )
}
