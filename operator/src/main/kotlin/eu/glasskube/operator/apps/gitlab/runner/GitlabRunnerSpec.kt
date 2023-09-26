package eu.glasskube.operator.apps.gitlab.runner

import eu.glasskube.operator.apps.common.HasUpdatesSpec
import eu.glasskube.operator.apps.common.SemanticVersionUpdatesSpec
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.LocalObjectReference

data class GitlabRunnerSpec(
    @field: Required
    val token: String,
    @field: Required
    val gitlab: LocalObjectReference,
    val concurrency: Int = 1,
    override val updates: SemanticVersionUpdatesSpec = SemanticVersionUpdatesSpec("16.0.2")
) : HasUpdatesSpec
