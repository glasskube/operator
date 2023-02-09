package eu.glasskube.operator

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import java.time.Instant
import java.time.ZonedDateTime

@Configuration
@PropertySource("classpath:git.properties")
data class ReleaseInfo(
    @Value("\${git.build.version}")
    val version: String,
    @Value("\${git.branch}")
    val branch: String,
    @Value("\${git.commit.id.abbrev}")
    val commitHash: String,
    @Value("\${git.commit.time}")
    private val commitDateString: String
) {

    val commitDate: Instant =
        ZonedDateTime.parse(commitDateString).toInstant()

    fun print() {
        log.info("Version $version ($commitHash@$branch on $commitDate)")
    }

    companion object {
        private val log = LoggerFactory.getLogger(ReleaseInfo::class.java)
    }

}
