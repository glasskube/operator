package eu.glasskube.operator.mariadb

data class Metrics(
    val exporter: Exporter,
    val serviceMonitor: ServiceMonitor
)
