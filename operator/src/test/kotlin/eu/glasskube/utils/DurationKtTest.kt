package eu.glasskube.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Duration

class DurationKtTest {
    @Test
    fun `parseGolangDuration when called with 'm1s' should throw IllegalArgumentException`() {
        assertThrows<IllegalArgumentException> { parseGolangDuration("m1s") }
    }

    @Test
    fun `parseGolangDuration when called with '1d1h' should throw IllegalArgumentException`() {
        assertThrows<IllegalArgumentException> { parseGolangDuration("1d1h") }
    }

    @Test
    fun `parseGolangDuration when called with '1h1m1s1ms1us1ns' should parse`() {
        assertEquals(Duration.parse("PT1H1M1.001001001S"), parseGolangDuration("1h1m1s1ms1us1ns"))
    }
}
