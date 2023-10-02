package eu.glasskube.operator.apps.common.database.mariadb

import eu.glasskube.operator.apps.common.database.StorageSpec

data class MariaDbStorageSpec(
    val size: String?,
    val storageClass: String?
) : StorageSpec
