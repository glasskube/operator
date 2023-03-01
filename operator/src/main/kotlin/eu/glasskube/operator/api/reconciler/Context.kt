package eu.glasskube.operator.api.reconciler

import eu.glasskube.operator.orNull
import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceDiscriminator
import java.util.Optional
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class LazyContextDelegate<R, P : HasMetadata>(
    private val context: Context<P>,
    private val valueSupplier: Context<P>.() -> R
) : ReadOnlyProperty<Any?, R> {
    private val value by lazy { valueSupplier.invoke(context) }
    override fun getValue(thisRef: Any?, property: KProperty<*>) = value
}

inline fun <reified R, P : HasMetadata> Context<P>.getSecondaryResource(): Optional<R> =
    getSecondaryResource(R::class.java)

inline fun <reified R, P : HasMetadata> Context<P>.getSecondaryResource(discriminator: ResourceDiscriminator<R, P>): Optional<R> =
    getSecondaryResource(R::class.java, discriminator)

inline fun <reified R, P : HasMetadata> Context<P>.secondaryResource(): ReadOnlyProperty<Any?, R?> =
    LazyContextDelegate(this) { getSecondaryResource<R, P>().orNull() }

inline fun <reified R, P : HasMetadata> Context<P>.secondaryResource(discriminator: ResourceDiscriminator<R, P>): ReadOnlyProperty<Any?, R?> =
    LazyContextDelegate(this) { getSecondaryResource(discriminator).orNull() }

inline fun <reified R : Any, P : HasMetadata> Context<P>.requireSecondaryResource(): ReadOnlyProperty<Any?, R> =
    LazyContextDelegate(this) { getSecondaryResource<R, P>().orElseThrow() }

inline fun <reified R : Any, P : HasMetadata> Context<P>.requireSecondaryResource(discriminator: ResourceDiscriminator<R, P>): ReadOnlyProperty<Any?, R> =
    LazyContextDelegate(this) { getSecondaryResource(discriminator).orElseThrow() }
