package eu.glasskube.operator.apps.vault

data class VaultStatus(
    val readyReplicas: Int,
    val postgresReady: Boolean
)
