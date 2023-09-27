package eu.glasskube.operator.generic.dependent.postgres.backup.bucketinfo

import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context

fun interface MinioBucketInfoProvider<P : HasMetadata> {
    fun getMinioBucketInfo(primary: P, context: Context<P>): MinioBucketInfo
}
