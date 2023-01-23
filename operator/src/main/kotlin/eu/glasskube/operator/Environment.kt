package eu.glasskube.operator

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object Environment {
    val MANAGE_NAMESPACE by EnvProperty()
    val MANAGE_CURRENT_NAMESPACE by EnvProperty()
    val NAMESPACE by EnvProperty()
    val MINIO_HOST_NAME by EnvProperty("glasskube-minio")
    val MINIO_SECRET_NAME by EnvProperty("glasskube-minio")

    private class EnvProperty(private val defaultValue: String? = null) : ReadOnlyProperty<Any, String?> {
        override fun getValue(thisRef: Any, property: KProperty<*>): String? =
            System.getenv(property.name) ?: defaultValue
    }
}
