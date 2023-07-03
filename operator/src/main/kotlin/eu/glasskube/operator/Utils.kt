package eu.glasskube.operator

import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
fun String.encodeBase64(): String = Base64.getEncoder().encodeToString(this.encodeToByteArray())

fun <T> T.addTo(list: MutableCollection<T>): Boolean = list.add(this)

inline fun <reified T : Any> T.logger(): Logger = with(T::class) {
    LoggerFactory.getLogger(if (isCompanion) java.enclosingClass else java)
}

inline fun <reified T : Any> T.resourceAsString(name: String) = T::class.java.getResource(name)!!.readText()
