package eu.glasskube.utils

import java.time.Duration
import java.time.temporal.ChronoUnit

private val durationPattern = Regex("""^-?(\d+(ns|us|ms|s|m|h))+$""")
private val durationSegmentPattern = Regex("""(\d+)(\D+)""")

fun parseGolangDuration(value: String): Duration {
    require(value.matches(durationPattern)) { "invalid duration \"$value\"" }
    return durationSegmentPattern.findAll(value)
        .fold(Duration.ZERO) { duration, match ->
            val amount = match.groupValues[1].toLong()
            when (val unit = match.groupValues[2]) {
                "ns" -> duration.plusNanos(amount)
                "us" -> duration.plus(amount, ChronoUnit.MICROS)
                "ms" -> duration.plusMillis(amount)
                "s" -> duration.plusSeconds(amount)
                "m" -> duration.plusMinutes(amount)
                "h" -> duration.plusHours(amount)
                else -> throw IllegalArgumentException("unknown unit \"$unit\" in duration \"$value\"")
            }
        }
        .let { if (value[0] == '-') it.negated() else it }
}
