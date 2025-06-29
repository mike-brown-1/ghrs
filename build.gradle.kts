import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    application
    id("com.github.johnrengelman.shadow") version("8.1.1")
    id("com.github.ben-manes.versions") version("0.52.0")
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

repositories {
    mavenCentral()
//    flatDir {
//        dirs("libs")
//    }
}

dependencies {
    implementation(files("libs/openapi-java-client-1.1.4-all.jar"))
/* **************
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.7")
    implementation("com.squareup.moshi:moshi:1.15.2")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.2")
    implementation("org.kohsuke:github-api:2.0-rc.3")
**************** */
    implementation("com.github.ajalt.clikt:clikt:5.0.3")
    implementation("com.sksamuel.hoplite:hoplite-core:2.9.0")
    implementation("com.sksamuel.hoplite:hoplite-hocon:2.9.0")
    implementation(kotlin("stdlib"))
    runtimeOnly("org.slf4j:slf4j-api:2.0.17")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.17")
    // Upgrading the following to version 6 causes RESTEASY003145 to happen again
    // TODO Try updating the API project to the latest version of resteasy (currently 6.2.12)
    // then update the following
    implementation("org.jboss.resteasy:resteasy-jackson2-provider:5.0.5.Final")

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
    mainClass = "ghrs.CommandKt"
}
version = "0.8.1"

tasks.named<Test>("test") {
    useJUnitPlatform()
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

// https://github.com/ben-manes/gradle-versions-plugin
tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}

detekt {
    ignoreFailures = true
    toolVersion = "1.23.8" // Use the appropriate version
    config = files("$rootDir/config/detekt/detekt.yml") // Use your custom config
}

tasks {
    shadowJar {
        mergeServiceFiles()
        append("META-INF/services/javax.ws.rs.ext.Providers")
        append("META-INF/services/javax.ws.rs.ext.MessageBodyReader")
        append("META-INF/services/javax.ws.rs.ext.MessageBodyWriter")
    }
}
