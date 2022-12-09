package eu.glasskube.operator

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object Environment {
    val MANAGE_NAMESPACE by EnvProperty()
    val MANAGE_CURRENT_NAMESPACE by EnvProperty()

    private class EnvProperty : ReadOnlyProperty<Any, String?> {
        override fun getValue(thisRef: Any, property: KProperty<*>): String? = System.getenv(property.name)
    }
}
