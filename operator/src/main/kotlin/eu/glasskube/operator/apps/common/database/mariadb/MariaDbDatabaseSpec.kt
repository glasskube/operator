package eu.glasskube.operator.apps.common.database.mariadb

import eu.glasskube.operator.apps.common.database.DatabaseSpec

data class MariaDbDatabaseSpec(
    override val storage: MariaDbStorageSpec? = null
) : DatabaseSpec {
    override val backups = null
}
