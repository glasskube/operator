package eu.glasskube.operator.apps.common.database.postgres

import eu.glasskube.operator.apps.common.database.DatabaseSpec
import io.fabric8.generator.annotation.Nullable

data class PostgresDatabaseSpec(
    @field:Nullable
    override val backups: PostgresBackupsSpec?
) : DatabaseSpec
