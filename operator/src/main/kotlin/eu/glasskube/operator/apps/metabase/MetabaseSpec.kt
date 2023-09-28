package eu.glasskube.operator.apps.metabase

import eu.glasskube.operator.apps.common.database.HasDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
import eu.glasskube.operator.validation.Patterns
import io.fabric8.generator.annotation.Nullable
import io.fabric8.generator.annotation.Pattern
import io.fabric8.generator.annotation.Required
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceRequirements

data class MetabaseSpec(
    @field:Required
    val host: String,
    val replicas: Int = 1,
    @field:Nullable
    val smtp: MetabaseSmtp? = null,
    val resources: ResourceRequirements = ResourceRequirements(
        null,
        mapOf("memory" to Quantity("800", "Mi")),
        mapOf("memory" to Quantity("700", "Mi"))
    ),
    @field:Pattern(Patterns.SEMVER)
    val version: String = "0.47.1",
    @field:Nullable
    override val database: PostgresDatabaseSpec = PostgresDatabaseSpec()
) : HasDatabaseSpec<PostgresDatabaseSpec>
