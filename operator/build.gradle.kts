import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("kapt") version "1.7.10"
    id("com.google.cloud.tools.jib") version "3.3.0"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    application
}

group = "eu.glasskube.operator"
version = "0.1-SNAPSHOT"

val javaOperatorVersion: String by project
val crdGeneratorVersion: String by project
val slf4jVersion: String by project
val logbackVersion: String by project
val jacksonVersion: String by project

dependencies {
    implementation("io.javaoperatorsdk", "operator-framework", javaOperatorVersion)
    kapt("io.javaoperatorsdk", "operator-framework", javaOperatorVersion)
    kapt("io.fabric8", "crd-generator-apt", crdGeneratorVersion)

    implementation("org.slf4j", "slf4j-api", slf4jVersion)
    implementation("ch.qos.logback", "logback-core", logbackVersion)
    implementation("ch.qos.logback", "logback-classic", logbackVersion)
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", jacksonVersion)

    testImplementation(kotlin("test"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "${JavaVersion.VERSION_11}"
    kotlinOptions.javaParameters = true
}

tasks.test {
    useJUnitPlatform()
}

jib {
    to {
        image = "glasskube-operator"
        tags = setOf(version as String)
    }
    container {
        user = "333"
    }
}

application {
    mainClass.set("eu.glasskube.operator.MainKt")
}

tasks.create("installCrd", Exec::class) {
    group = "kubernetes"
    dependsOn("build")
    commandLine(
        "kubectl",
        "apply",
        "-f",
        "build/tmp/kapt3/classes/main/META-INF/fabric8/*glasskube.eu-v1.yml"
    )
}

tasks.create("loadImage", Exec::class) {
    group = "kubernetes"
    dependsOn("jibBuildTar")
    commandLine(
        "minikube",
        "image",
        "load",
        "build/jib-image.tar"
    )
}
