package eu.glasskube.operator.apps.common.database.postgres

import eu.glasskube.operator.apps.common.database.DatabaseSpec
import io.fabric8.generator.annotation.Min

data class PostgresDatabaseSpec(
    @field:Min(1.0)
    val instances: Int = 1,
    override val backups: PostgresBackupsSpec? = null,
    override val storage: PostgresStorageSpec? = null
) : DatabaseSpec
