rootProject.name = "Glasskube Operator"

pluginManagement {
    val quarkusPluginVersion: String by settings
    val jibPluginVersion: String by settings
    val ktlintPluginVersion: String by settings

    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }

    plugins {
        id("io.quarkus") version quarkusPluginVersion
        id("com.google.cloud.tools.jib") version jibPluginVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintPluginVersion
    }
}

include(":operator")
