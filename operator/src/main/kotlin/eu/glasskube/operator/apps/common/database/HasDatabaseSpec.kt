package eu.glasskube.operator.apps.common.database

interface HasDatabaseSpec<T : DatabaseSpec> {
    val database: T
}
