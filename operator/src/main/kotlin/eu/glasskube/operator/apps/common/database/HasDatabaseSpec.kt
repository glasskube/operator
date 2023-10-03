package eu.glasskube.operator.apps.common.database

interface HasDatabaseSpec<out T : DatabaseSpec> {
    val database: T
}
