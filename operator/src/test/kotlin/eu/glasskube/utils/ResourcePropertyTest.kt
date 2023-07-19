package eu.glasskube.utils

import eu.glasskube.utils.ResourceProperty.splitCamel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ResourcePropertyTest {
    private val testTxt by resourceProperty()

    @Test
    fun resourcePropertyTest() {
        assertEquals("test\n", testTxt)
    }

    @Test
    fun splitCamel() {
        assertEquals(listOf<String>(), "".splitCamel())
        assertEquals(listOf("foo"), "Foo".splitCamel())
        assertEquals(listOf("foo", "bar"), "fooBar".splitCamel())
        assertEquals(listOf("foo", "bar"), "FooBar".splitCamel())
    }

    @Test
    fun combineToFilename() {
        assertThrows<IllegalArgumentException> { ResourceProperty.combineToFilename(emptyList()) }
        assertEquals("foo", ResourceProperty.combineToFilename(listOf("foo")))
        assertEquals("foo.bar", ResourceProperty.combineToFilename(listOf("foo", "bar")))
        assertEquals("foo-bar.txt", ResourceProperty.combineToFilename(listOf("foo", "bar", "txt")))
    }
}
