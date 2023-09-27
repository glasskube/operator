package eu.glasskube.operator.generic.dependent.postgres.backup.bucketinfo

import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.infra.minio.MinioBucket
import eu.glasskube.operator.infra.minio.bucketName
import eu.glasskube.operator.infra.minio.secretName
import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceDiscriminator

abstract class SecondaryResourceMinioBucketInfoProvider<P : HasMetadata> : MinioBucketInfoProvider<P> {
    override fun getMinioBucketInfo(primary: P, context: Context<P>) =
        getMinioBucket(primary, context).run { MinioBucketInfo(bucketName, secretName) }

    protected abstract fun getMinioBucket(primary: P, context: Context<P>): MinioBucket

    class Default<P : HasMetadata> : SecondaryResourceMinioBucketInfoProvider<P>() {
        override fun getMinioBucket(primary: P, context: Context<P>): MinioBucket =
            context.getSecondaryResource<MinioBucket>().orElseThrow()
    }

    class WithDiscriminator<P : HasMetadata>(private val discriminator: ResourceDiscriminator<MinioBucket, P>) :
        SecondaryResourceMinioBucketInfoProvider<P>() {
        override fun getMinioBucket(primary: P, context: Context<P>): MinioBucket =
            context.getSecondaryResource(discriminator).orElseThrow()
    }
}
