import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("kapt") version "1.7.10"
    kotlin("plugin.spring") version "1.7.10"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
    id("org.springframework.boot") version "3.0.2"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "eu.glasskube.operator"

val javaOperatorVersion: String by project
val crdGeneratorVersion: String by project
val slf4jVersion: String by project
val logbackVersion: String by project
val jacksonVersion: String by project
val bouncyCastleVersion: String by project
val minioVersion: String by project

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")

    implementation("io.javaoperatorsdk", "operator-framework", javaOperatorVersion)
    implementation("org.slf4j", "slf4j-api", slf4jVersion)
    implementation("ch.qos.logback", "logback-core", logbackVersion)
    implementation("ch.qos.logback", "logback-classic", logbackVersion)
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", jacksonVersion)
    implementation("org.bouncycastle", "bcpkix-jdk15to18", bouncyCastleVersion)
    implementation("io.minio", "minio", minioVersion)
    implementation("io.minio", "minio-admin", minioVersion)

    kapt("io.fabric8", "crd-generator-apt", crdGeneratorVersion)

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "${JavaVersion.VERSION_17}"
        javaParameters = true
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("glasskube/operator")
    tags.add("glasskube/operator:$version")
}

gitProperties {
    keys = listOf(
        "git.branch",
        "git.build.version",
        "git.commit.id.abbrev",
        "git.commit.time"
    )
    dateFormat = "yyyy-MM-dd'T'HH:mmX"
}

tasks.create("clearCrd", Delete::class) {
    delete = setOf("../deploy/crd")
}

tasks.create("copyCrd", Copy::class) {
    dependsOn("kaptKotlin")
    dependsOn("clearCrd")
    from("build/tmp/kapt3/classes/main/META-INF/fabric8") {
        include("*glasskube.eu-v1.yml")
    }
    into("../deploy/crd")
}

tasks.findByName("classes")!!.dependsOn("copyCrd")

tasks.create("installCrd", Exec::class) {
    group = "kubernetes"
    dependsOn("copyCrd")
    commandLine(
        "kubectl",
        "apply",
        "-f",
        "../deploy/crd"
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
