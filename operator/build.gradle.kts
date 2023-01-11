import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.allopen") version "1.7.21"
    id("io.quarkus")
    id("org.jlleitschuh.gradle.ktlint")
}

group = "eu.glasskube.operator"

val quarkusPlatformVersion: String by project
val quarkusOperatorSdkVersion: String by project
val apacheCommonsVersion: String by project
val bouncyCastleVersion: String by project

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:$quarkusPlatformVersion"))
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-arc")
    // implementation("io.quarkus:quarkus-resteasy-reactive")
    // implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
    // implementation("io.quarkus:quarkus-container-image-jib")

    implementation(enforcedPlatform("io.quarkiverse.operatorsdk:quarkus-operator-sdk-bom:$quarkusOperatorSdkVersion"))
    implementation("io.quarkiverse.operatorsdk:quarkus-operator-sdk")

    implementation("org.apache.commons", "commons-lang3", apacheCommonsVersion)
    implementation("org.bouncycastle", "bcpkix-jdk15to18", bouncyCastleVersion)

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    kotlinOptions.javaParameters = true
}

tasks.test {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

/*
jib {
    from {
        image = "ghcr.io/graalvm/jdk:ol9-java17-22.3.0"
    }

    to {
        image = "glasskube/operator"
        tags = setOf(version as String)
    }

    container {
        user = "333"
    }
}
*/

tasks.create("clearCrd", Delete::class) {
    delete = setOf("../deploy/crd")
}

tasks.create("copyCrd", Copy::class) {
    // dependsOn("kaptKotlin")
    dependsOn("clearCrd")
    from("build/kubernetes") {
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
