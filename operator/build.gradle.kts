import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("kapt") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("com.gorylenko.gradle-git-properties") version "2.4.2"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "eu.glasskube.operator"

val javaOperatorVersion: String by project
val crdGeneratorVersion: String by project
val bouncyCastleVersion: String by project
val minioVersion: String by project

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")

    implementation("io.javaoperatorsdk", "operator-framework", javaOperatorVersion)
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin")
    implementation("org.bouncycastle", "bcpkix-jdk15to18", bouncyCastleVersion)
    implementation("io.minio", "minio", minioVersion)
    implementation("io.minio", "minio-admin", minioVersion)
    implementation("io.fabric8", "generator-annotations", crdGeneratorVersion)
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

// https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/
tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("glasskube/operator")
    tags.add("glasskube/operator:$version")
    builder.set("paketobuildpacks/builder-jammy-base") // https://paketo.io/docs/reference/builders-reference/
    runImage.set("paketobuildpacks/run-jammy-base")
    environment.set(
        environment.get() + mapOf(
            "BP_JVM_VERSION" to "17",
            "BP_SPRING_CLOUD_BINDINGS_DISABLED" to "true"
        )
    )
    docker {
        publishRegistry {
            username.set(System.getenv("DOCKERHUB_USERNAME"))
            password.set(System.getenv("DOCKERHUB_TOKEN"))
        }
    }
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
    dependsOn("bootBuildImage")
    commandLine(
        "minikube",
        "image",
        "load",
        "glasskube/operator:$version"
    )
}
