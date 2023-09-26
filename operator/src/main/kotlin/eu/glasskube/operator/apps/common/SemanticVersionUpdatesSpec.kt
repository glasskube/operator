package eu.glasskube.operator.apps.common

import io.fabric8.generator.annotation.Pattern
import io.fabric8.generator.annotation.Required

data class SemanticVersionUpdatesSpec(
    @Required
    @Pattern("\\d+\\.\\d+\\.\\d+")
    override val version: String
) : UpdatesSpec<String>
