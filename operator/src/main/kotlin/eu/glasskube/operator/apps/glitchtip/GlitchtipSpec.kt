package eu.glasskube.operator.apps.glitchtip

import eu.glasskube.operator.apps.common.HasUpdatesSpec
import eu.glasskube.operator.apps.common.SemanticVersionUpdatesSpec
import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements

data class GlitchtipSpec(
    @field:Required
    val host: String,
    val replicas: Int = 1,
    val registrationEnabled: Boolean = false,
    val organizationCreationEnabled: Boolean = false,
    @field:Nullable
    val smtp: GlitchtipSmtp? = null,
    val resources: ResourceRequirements = ResourceRequirements(
        null,
        mapOf("memory" to Quantity("800", "Mi")),
        mapOf("memory" to Quantity("700", "Mi"))
    ),
    override val updates: SemanticVersionUpdatesSpec = SemanticVersionUpdatesSpec("3.3.1")
) : HasUpdatesSpec
