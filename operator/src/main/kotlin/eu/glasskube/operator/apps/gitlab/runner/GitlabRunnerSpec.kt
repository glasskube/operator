package eu.glasskube.operator.apps.gitlab.runner

import eu.glasskube.operator.validation.Patterns.SEMVER
import io.fabric8.generator.annotation.Pattern
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.LocalObjectReference

data class GitlabRunnerSpec(
    @field: Required
    val token: String,
    @field: Required
    val gitlab: LocalObjectReference,
    val concurrency: Int = 1,
    @field:Pattern(SEMVER)
    val version: String = "16.0.2"
)
