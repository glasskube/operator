package eu.glasskube.operator.apps.gitea

import eu.glasskube.utils.resourceHash
import io.fabric8.generator.annotation.Required

data class GiteaActionRunnerSpecTemplate(
    @field:Required
    val token: String,
    val labels: List<String>? = null
)

val GiteaActionRunnerSpecTemplate.tokenHash: String
    get() = token.resourceHash
