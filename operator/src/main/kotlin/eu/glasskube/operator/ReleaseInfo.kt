package eu.glasskube.operator

import java.time.Instant
import java.time.ZonedDateTime
import java.util.ResourceBundle

data class ReleaseInfo private constructor(
    val version: String,
    val branch: String,
    val commitHash: String,
    val commitDate: Instant
) {
    fun print() {
        println("Version $version ($commitHash@$branch on $commitDate)")
    }

    companion object {
        private val lazyInstance by lazy {
            val gitProperties = ResourceBundle.getBundle("git")
            ReleaseInfo(
                version = gitProperties.getString("git.build.version"),
                branch = gitProperties.getString("git.branch"),
                commitHash = gitProperties.getString("git.commit.id.abbrev"),
                commitDate = ZonedDateTime.parse(gitProperties.getString("git.commit.time")).toInstant()
            )
        }

        fun getInstance() = lazyInstance
    }
}
