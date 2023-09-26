package eu.glasskube.operator.apps.gitlab

import eu.glasskube.operator.apps.common.SemanticVersionUpdatesSpec
import io.fabric8.generator.annotation.Required

data class GitlabRunnerSpecTemplate(
    @field: Required
    val token: String,
    val concurrency: Int = 1,
    val updates: SemanticVersionUpdatesSpec? = null
)

val GitlabRunnerSpecTemplate.tokenHash: String
    get() = (token.hashCode().toLong() + Int.MAX_VALUE).toString(16)
