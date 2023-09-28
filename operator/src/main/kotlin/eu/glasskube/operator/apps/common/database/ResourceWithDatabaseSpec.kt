package eu.glasskube.operator.apps.common.database

interface ResourceWithDatabaseSpec<T : DatabaseSpec> {
    fun getSpec(): HasDatabaseSpec<T>
}
