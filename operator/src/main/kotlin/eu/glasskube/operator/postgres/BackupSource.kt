package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.fabric8.kubernetes.api.model.SecretKeySelector

data class BackupSource @JsonCreator constructor(
    @JsonProperty("endpointCA")
    val endpointCA: SecretKeySelector
)
