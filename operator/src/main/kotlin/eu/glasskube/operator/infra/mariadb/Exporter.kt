package eu.glasskube.operator.infra.mariadb

data class Exporter(
    val image: MariaDBImage,
    val resources: MariaDBResources? = null
)
