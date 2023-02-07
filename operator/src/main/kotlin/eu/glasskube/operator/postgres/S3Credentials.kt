package eu.glasskube.operator.postgres

import io.fabric8.kubernetes.api.model.SecretKeySelector

data class S3Credentials(
    val accessKeyId: SecretKeySelector? = null,
    val secretAccessKey: SecretKeySelector? = null,
    val region: SecretKeySelector? = null,
    val sessionToken: SecretKeySelector? = null,
    val inheritFromIAMRole: Boolean = false
)
