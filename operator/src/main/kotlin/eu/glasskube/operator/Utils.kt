package eu.glasskube.operator

import java.util.Base64

fun resourceLabels(app: String, vararg additionalLabels: Pair<String, String>) =
    mapOf(
        "app.kubernetes.io/managed-by" to "glasskube-operator",
        "app" to app,
        *additionalLabels
    )

fun String.decodeBase64() = String(Base64.getDecoder().decode(this))
fun String.encodeBase64() = Base64.getEncoder().encodeToString(this.encodeToByteArray())
