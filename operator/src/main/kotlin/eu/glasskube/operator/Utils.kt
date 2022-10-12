package eu.glasskube.operator

fun resourceLabels(app: String, vararg additionalLabels: Pair<String, String>) =
    mapOf(
        "app.kubernetes.io/managed-by" to "glasskube-operator",
        "app" to app,
        *additionalLabels
    )
