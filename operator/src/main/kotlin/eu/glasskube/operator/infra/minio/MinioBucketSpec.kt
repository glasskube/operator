package eu.glasskube.operator.infra.minio

import io.fabric8.generator.annotation.Nullable
import io.fabric8.kubernetes.api.model.LocalObjectReference

data class MinioBucketSpec(
    @field:Nullable val userSecret: LocalObjectReference? = null,
    @field:Nullable val bucketNameOverride: String? = null,
    @field:Nullable val policyOverride: String? = null
)
