import net.researchgate.release.ReleaseExtension

subprojects {
    repositories {
        mavenCentral()
    }
}

plugins {
    id("net.researchgate.release") version "3.0.2"
}

configure<ReleaseExtension> {
    preCommitText.set("release: ")
    preTagCommitMessage.set("update version to ")
    newVersionCommitMessage.set("update version to ")
}
