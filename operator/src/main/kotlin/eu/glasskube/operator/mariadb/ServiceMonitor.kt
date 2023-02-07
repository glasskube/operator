package eu.glasskube.operator.mariadb

data class ServiceMonitor(
    val prometheusRelease: String,
    val interval: String? = "10s",
    val scrapeTimeout: String? = "10s"
)
