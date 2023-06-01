package eu.glasskube.operator.infra.mariadb

data class Metrics(
    val exporter: Exporter,
    val serviceMonitor: ServiceMonitor
)
