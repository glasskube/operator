package eu.glasskube.operator.apps.metabase

data class MetabaseStatus(
    val readyReplicas: Int,
    val postgresReady: Boolean
)
