package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonInclude
import io.fabric8.kubernetes.api.model.SecretKeySelector

@JsonInclude(JsonInclude.Include.NON_NULL)
data class S3Credentials(
    val accessKeyId: SecretKeySelector? = null,
    val secretAccessKey: SecretKeySelector? = null,
    val region: SecretKeySelector? = null,
    val sessionToken: SecretKeySelector? = null,
    val inheritFromIAMRole: Boolean = false
)
