package eu.glasskube.operator.generic.dependent.postgres

import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.infra.minio.MinioBucket
import eu.glasskube.operator.infra.minio.bucketName
import eu.glasskube.operator.infra.minio.secretName
import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceDiscriminator

abstract class MinioBucketBackupInfoProvider<P : HasMetadata> : PostgresBackupInfoProvider<P> {
    abstract fun getMinioBucket(primary: P, context: Context<P>): MinioBucket
    override fun getBackupInfo(primary: P, context: Context<P>) =
        getMinioBucket(primary, context).run { PostgresBackupInfo(bucketName = bucketName, secretName = secretName) }

    class Default<T : HasMetadata> : MinioBucketBackupInfoProvider<T>() {
        override fun getMinioBucket(primary: T, context: Context<T>): MinioBucket =
            context.getSecondaryResource<MinioBucket>().orElseThrow()
    }

    class WithDiscriminator<T : HasMetadata>(private val discriminator: ResourceDiscriminator<MinioBucket, T>) :
        MinioBucketBackupInfoProvider<T>() {
        override fun getMinioBucket(primary: T, context: Context<T>): MinioBucket =
            context.getSecondaryResource(discriminator).orElseThrow()
    }
}
