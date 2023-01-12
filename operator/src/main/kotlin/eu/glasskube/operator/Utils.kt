package eu.glasskube.operator

import io.fabric8.kubernetes.client.dsl.Resource

fun resourceLabels(app: String, vararg additionalLabels: Pair<String, String>) =
    mapOf(
        "app.kubernetes.io/managed-by" to "glasskube-operator",
        "app" to app,
        *additionalLabels
    )

val Resource<*>.exists: Boolean
    get() = fromServer().get() != null
