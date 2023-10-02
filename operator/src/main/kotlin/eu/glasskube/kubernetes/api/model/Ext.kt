package eu.glasskube.kubernetes.api.model

import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.IntOrString
import io.fabric8.kubernetes.api.model.Quantity

fun intOrString(value: String) = IntOrString(value)
fun intOrString(value: Int) = IntOrString(value)

fun String.toQuantity(): Quantity = Quantity.parse(this)

val HasMetadata.namespace: String?
    get() = metadata.namespace

val HasMetadata.loggingId
    get() = "${javaClass.simpleName}{${metadata.namespace}.${metadata.name}}"
