package eu.glasskube.operator

import java.util.Base64

@Deprecated(
    "Use Labels.resourceLabels instead to opt in to Kubernetes Recommended Labels.",
    ReplaceWith(
        "Labels.resourceLabels(app, TODO(\"specify instance name\"), app) + \n additionalLabels",
        "eu.glasskube.Labels"
    )
)
fun resourceLabels(app: String, vararg additionalLabels: Pair<String, String>) =
    mapOf(
        "app.kubernetes.io/managed-by" to "glasskube-operator",
        "app" to app,
        *additionalLabels
    )

fun String.decodeBase64() = String(Base64.getDecoder().decode(this))
fun String.encodeBase64() = Base64.getEncoder().encodeToString(this.encodeToByteArray())
