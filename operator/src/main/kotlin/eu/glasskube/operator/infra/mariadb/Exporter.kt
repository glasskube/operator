package eu.glasskube.operator.infra.mariadb

data class Exporter(
    val image: String,
    var imagePullPolicy: String = "IfNotPresent",
    val resources: MariaDBResources? = null
)
