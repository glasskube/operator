package eu.glasskube.kubernetes.api.model

import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.IntOrString

fun intOrString(value: String) = IntOrString(value)
fun intOrString(value: Int) = IntOrString(value)

val HasMetadata.namespace: String?
    get() = metadata.namespace
