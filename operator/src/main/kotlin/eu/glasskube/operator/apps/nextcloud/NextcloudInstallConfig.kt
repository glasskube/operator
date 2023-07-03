package eu.glasskube.operator.apps.nextcloud

data class NextcloudInstallConfig(
    val system: Map<String, Any>? = null,
    val apps: Map<String, Map<String, Any>>? = null
)
