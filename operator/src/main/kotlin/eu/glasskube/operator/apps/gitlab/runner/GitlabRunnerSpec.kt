package eu.glasskube.operator.apps.gitlab.runner

import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.LocalObjectReference

data class GitlabRunnerSpec(
    @field: Required
    val token: String,
    @field: Required
    val gitlab: LocalObjectReference,
    val concurrency: Int = 1
)
