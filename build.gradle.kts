import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// The Beverage Buddy sample project ported to Kotlin.
// Original project: https://github.com/vaadin/beverage-starter-flow

plugins {
    kotlin("jvm") version "1.9.20"
    id("application")
    id("com.vaadin")
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
    implementation("com.vaadin:vaadin-core:${properties["vaadinVersion"]}") {
        afterEvaluate {
            if (vaadin.productionMode) {
                exclude(module = "vaadin-dev")
            }
        }
    }
    implementation("eu.vaadinonkotlin:vok-framework-vokdb:${properties["vokVersion"]}") {
        exclude(module = "vaadin-core")
    }
    implementation("com.github.mvysny.vaadin-boot:vaadin-boot:12.2")

    implementation("com.zaxxer:HikariCP:5.0.1")

    // logging
    // currently we are logging through the SLF4J API to SLF4J-Simple. See src/main/resources/simplelogger.properties file for the logger configuration
    implementation("org.slf4j:slf4j-simple:2.0.7")

    // db
    implementation("org.flywaydb:flyway-core:9.22.3") // newest version: https://repo1.maven.org/maven2/org/flywaydb/flyway-core/
    implementation("com.h2database:h2:2.2.224") // remove this and replace it with a database driver of your choice.

    // REST
    implementation("eu.vaadinonkotlin:vok-rest:${properties["vokVersion"]}")

    // testing
    testImplementation("com.github.mvysny.kaributesting:karibu-testing-v24:2.1.2")
    testImplementation("com.github.mvysny.dynatest:dynatest:0.24")
    testImplementation("eu.vaadinonkotlin:vok-rest-client:${properties["vokVersion"]}")
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
