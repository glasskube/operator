package eu.glasskube.operator.apps.common.database

interface ResourceWithDatabaseSpec<out T : DatabaseSpec> {
    fun getSpec(): HasDatabaseSpec<T>
}
