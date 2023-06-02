package eu.glasskube.operator.apps.gitlab

import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.SecretKeySelector

data class GitlabSpec(
    @field:Required
    val host: String,
    val sshHost: String?,
    val sshEnabled: Boolean = true,
    val initialRootPasswordSecret: SecretKeySelector?,
    @field:Nullable
    val smtp: GitlabSmtp?
)
