package eu.glasskube.operator.infra.minio

data class MinioBucketStatus(
    val bucketCreated: Boolean,
    val username: String?,
    val userCreated: Boolean,
    val policyCreated: Boolean,
    val policyLinked: Boolean
)

internal val MinioBucketStatus.allCreated: Boolean
    get() = bucketCreated && userCreated && policyCreated && policyLinked
