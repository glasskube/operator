package eu.glasskube.operator.apps.gitlab

import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Required

data class GitlabRegistrySpec(
    @field:Required
    val host: String,
    @field:Nullable
    val storage: GitlabRegistryStorageSpec?
)
