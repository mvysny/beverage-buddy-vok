import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// The Beverage Buddy sample project ported to Kotlin.
// Original project: https://github.com/vaadin/beverage-starter-flow

val vaadinonkotlin_version = "0.13.0"
val vaadin_version = "23.2.1"

plugins {
    kotlin("jvm") version "1.7.10"
    id("org.gretty") version "3.0.6"  // https://github.com/gretty-gradle-plugin/gretty
    war
    id("com.vaadin") version "23.2.1"
}

defaultTasks("clean", "build")

repositories {
    mavenCentral()
}

gretty {
    contextPath = "/"
    servletContainer = "jetty9.4"
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        // to see the exceptions of failed tests in Travis-CI console.
        exceptionFormat = TestExceptionFormat.FULL
    }
}

val staging by configurations.creating

dependencies {
    implementation("com.vaadin:vaadin-core:$vaadin_version")
    
    // Vaadin-on-Kotlin dependency, includes Vaadin
    implementation("eu.vaadinonkotlin:vok-framework-vokdb:$vaadinonkotlin_version")
    implementation("com.github.mvysny.karibudsl:karibu-dsl-v23:1.1.3")
    implementation("com.zaxxer:HikariCP:5.0.1")
    providedCompile("javax.servlet:javax.servlet-api:4.0.1")

    implementation(kotlin("stdlib-jdk8"))

    // logging
    // currently we are logging through the SLF4J API to SLF4J-Simple. See src/main/resources/simplelogger.properties file for the logger configuration
    implementation("org.slf4j:slf4j-simple:2.0.0")

    // db
    implementation("org.flywaydb:flyway-core:8.5.12")
    implementation("com.h2database:h2:2.1.212") // remove this and replace it with a database driver of your choice.

    // REST
    implementation("eu.vaadinonkotlin:vok-rest:$vaadinonkotlin_version")

    // testing
    testImplementation("com.github.mvysny.kaributesting:karibu-testing-v23:1.3.20")
    testImplementation("com.github.mvysny.dynatest:dynatest:0.24")
    testImplementation("eu.vaadinonkotlin:vok-rest-client:$vaadinonkotlin_version")
    testImplementation("org.eclipse.jetty.websocket:websocket-server:9.4.44.v20210927") // vok-rest uses Javalin 4.3.0 which depends on Jetty 9.x

    // heroku app runner
    staging("com.heroku:webapp-runner-main:9.0.52.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

// Heroku
tasks {
    val copyToLib by registering(Copy::class) {
        into("$buildDir/server")
        from(staging) {
            include("webapp-runner*")
        }
    }
    val stage by registering {
        dependsOn("build", copyToLib)
    }
}

vaadin {
    if (gradle.startParameter.taskNames.contains("stage")) {
        productionMode = true
    }
}
