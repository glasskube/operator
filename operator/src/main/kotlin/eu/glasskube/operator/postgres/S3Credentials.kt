package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.fabric8.kubernetes.api.model.SecretKeySelector

data class S3Credentials @JsonCreator constructor(
    @JsonProperty("accessKeyId")
    val accessKeyId: SecretKeySelector? = null,
    @JsonProperty("secretAccessKey")
    val secretAccessKey: SecretKeySelector? = null,
    @JsonProperty("region")
    val region: SecretKeySelector? = null,
    @JsonProperty("sessionToken")
    val sessionToken: SecretKeySelector? = null,
    @JsonProperty("inheritFromIAMRole")
    val inheritFromIAMRole: Boolean = false
)
