package eu.glasskube.operator.apps.gitlab

import eu.glasskube.operator.validation.Patterns.SEMVER
import eu.glasskube.utils.resourceHash
import io.fabric8.generator.annotation.Pattern
import io.fabric8.generator.annotation.Required

data class GitlabRunnerSpecTemplate(
    @field: Required
    val token: String,
    val concurrency: Int = 1,
    @field:Pattern(SEMVER)
    val version: String? = null
)

val GitlabRunnerSpecTemplate.tokenHash: String
    get() = token.resourceHash
