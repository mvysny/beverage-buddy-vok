import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// The Beverage Buddy sample project ported to Kotlin.
// Original project: https://github.com/vaadin/beverage-starter-flow

val vaadinonkotlin_version = "0.15.0"
val vaadin_version = "24.0.4"

plugins {
    kotlin("jvm") version "1.8.20"
    id("application")
    id("com.vaadin") version "24.0.4"
}

defaultTasks("clean", "build")

repositories {
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        // to see the exceptions of failed tests in Travis-CI console.
        exceptionFormat = TestExceptionFormat.FULL
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // Vaadin
    implementation("com.vaadin:vaadin-core:$vaadin_version") {
        afterEvaluate {
            if (vaadin.productionMode) {
                exclude(module = "vaadin-dev")
            }
        }
    }
    implementation("eu.vaadinonkotlin:vok-framework-vokdb:$vaadinonkotlin_version") {
        exclude(module = "vaadin-core")
    }
    implementation("com.github.mvysny.vaadin-boot:vaadin-boot:11.2")

    implementation("com.zaxxer:HikariCP:5.0.1")

    // logging
    // currently we are logging through the SLF4J API to SLF4J-Simple. See src/main/resources/simplelogger.properties file for the logger configuration
    implementation("org.slf4j:slf4j-simple:2.0.6")

    // db
    implementation("org.flywaydb:flyway-core:9.15.2")
    implementation("com.h2database:h2:2.1.214") // remove this and replace it with a database driver of your choice.

    // REST
    implementation("eu.vaadinonkotlin:vok-rest:$vaadinonkotlin_version")

    // testing
    testImplementation("com.github.mvysny.kaributesting:karibu-testing-v24:2.0.2")
    testImplementation("com.github.mvysny.dynatest:dynatest:0.24")
    testImplementation("eu.vaadinonkotlin:vok-rest-client:$vaadinonkotlin_version")
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
