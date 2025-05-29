plugins {
    alias(libs.plugins.kotlin.jvm)
    application
    id("com.github.johnrengelman.shadow") version("8.1.1")
    id("com.github.ben-manes.versions") version("0.51.0")

}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    implementation("org.json:json:20231013")
    implementation("com.github.ajalt.clikt:clikt:5.0.3")
    implementation("com.sksamuel.hoplite:hoplite-core:2.9.0")
    implementation("com.sksamuel.hoplite:hoplite-hocon:2.9.0")
    implementation("org.kohsuke:github-api:2.0-rc.3")
    implementation(kotlin("stdlib"))
    runtimeOnly("org.slf4j:slf4j-api:2.0.17")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.17")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "ghutil.CommandKt"
}
version = "0.4.0"

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
