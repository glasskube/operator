package eu.glasskube.operator.apps.metabase

import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Required

data class MetabaseSpec(
    @field:Required
    val host: String,
    val replicas: Int = 1,
    @field:Nullable
    val smtp: MetabaseSmtp? = null
)
