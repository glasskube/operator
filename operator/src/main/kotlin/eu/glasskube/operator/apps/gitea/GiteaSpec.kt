package eu.glasskube.operator.apps.gitea

import com.fasterxml.jackson.annotation.JsonPropertyDescription
import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.LocalObjectReference
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements

data class GiteaSpec(
    @field:Required
    val host: String,
    val sshEnabled: Boolean = true,
    val sshHost: String = host,
    @field:JsonPropertyDescription(value = "Secret containing data of the admin user to create on pod initialization. Expected keys are GITEA_ADMIN_USER, GITEA_ADMIN_EMAIL and GITEA_ADMIN_PASSWORD")
    val adminSecret: LocalObjectReference? = null,
    val registrationEnabled: Boolean = false,
    val replicas: Int = 1,
    @field:Nullable
    val smtp: GiteaSmtp? = null,
    val resources: ResourceRequirements = ResourceRequirements(
        null,
        mapOf("memory" to Quantity("400", "Mi")),
        mapOf("memory" to Quantity("200", "Mi"))
    )
)
