package eu.glasskube.operator.mariadb

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Exporter @JsonCreator constructor(
    @JsonProperty("image")
    val image: MariaDBImage,
    @JsonProperty("resources")
    val resources: MariaDBResources? = null
)
