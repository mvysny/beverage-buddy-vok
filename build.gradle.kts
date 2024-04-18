import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// The Beverage Buddy sample project ported to Kotlin.
// Original project: https://github.com/vaadin/beverage-starter-flow

plugins {
    kotlin("jvm") version "1.9.23"
    application
    alias(libs.plugins.vaadin)
}

defaultTasks("clean", "build")

repositories {
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        // to see the exception stacktraces of failed tests in the CI console
        exceptionFormat = TestExceptionFormat.FULL
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // Vaadin
    implementation(libs.vaadin.core) {
        if (vaadin.effective.productionMode.get()) {
            exclude(module = "vaadin-dev")
        }
    }
    implementation(libs.vok.db)
    implementation(libs.karibu.dsl)
    implementation(libs.vaadin.boot)

    implementation(libs.hikaricp)

    // logging
    // currently we are logging through the SLF4J API to SLF4J-Simple. See src/main/resources/simplelogger.properties file for the logger configuration
    implementation(libs.slf4j.simple)

    // db
    implementation(libs.flyway) // newest version: https://repo1.maven.org/maven2/org/flywaydb/flyway-core/
    implementation(libs.h2) // remove this and replace it with a database driver of your choice.

    // REST
    implementation(libs.vok.rest.server)

    // testing
    testImplementation(libs.karibu.testing)
    testImplementation(libs.dynatest)
    testImplementation(libs.vok.rest.client)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

application {
    mainClass.set("com.vaadin.starter.beveragebuddy.MainKt")
}
