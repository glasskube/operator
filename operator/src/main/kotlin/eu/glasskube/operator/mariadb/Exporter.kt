package eu.glasskube.operator.mariadb

data class Exporter(
    val image: MariaDBImage,
    val resources: MariaDBResources? = null
)
