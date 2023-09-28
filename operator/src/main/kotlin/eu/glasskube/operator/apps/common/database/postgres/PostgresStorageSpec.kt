package eu.glasskube.operator.apps.common.database.postgres

import eu.glasskube.operator.apps.common.database.StorageSpec

data class PostgresStorageSpec(
    val size: String?,
    val storageClass: String?
) : StorageSpec
