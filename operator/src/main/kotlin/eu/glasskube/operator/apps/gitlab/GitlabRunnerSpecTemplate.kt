package eu.glasskube.operator.apps.gitlab

import io.fabric8.generator.annotation.Required

data class GitlabRunnerSpecTemplate(
    @field: Required
    val token: String,
    val concurrency: Int = 1
)

val GitlabRunnerSpecTemplate.tokenHash: String
    get() = (token.hashCode().toLong() + Int.MAX_VALUE).toString(16)
