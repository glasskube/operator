package eu.glasskube.operator.apps.common

import io.fabric8.generator.annotation.Required

data class SimpleUpdatesSpec(
    @Required
    override val version: String
) : UpdatesSpec<String>
