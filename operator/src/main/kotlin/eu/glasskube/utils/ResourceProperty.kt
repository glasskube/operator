package eu.glasskube.utils

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun resourceProperty(): ReadOnlyProperty<Any, String> = ResourceProperty

internal object ResourceProperty : ReadOnlyProperty<Any, String> {
    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        val fileName = combineToFilename(property.name.splitCamel())
        log.debug("reading resource '{}' for {}", fileName, thisRef.javaClass.canonicalName)
        return thisRef.javaClass.getResource(fileName)!!.readText()
    }

    internal fun String.splitCamel(): List<String> = mutableListOf<String>().also { words ->
        var lastUpperIndex = 0
        forEachIndexed { index, character ->
            if (index > 0 && character.isUpperCase()) {
                words += substring(lastUpperIndex, index).lowercase()
                lastUpperIndex = index
            } else if (index == length - 1) {
                words += substring(lastUpperIndex).lowercase()
            }
        }
    }

    internal fun combineToFilename(segments: List<String>): String = when (segments.size) {
        0 -> throw IllegalArgumentException("segments must not be empty")
        1 -> segments.first()
        else -> segments.dropLast(1).joinToString("-") + "." + segments.last()
    }

    private val log = logger()
}
