import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// The Beverage Buddy sample project ported to Kotlin.
// Original project: https://github.com/vaadin/beverage-starter-flow

val vaadinonkotlin_version = "0.7.1"
val vaadin10_version = "13.0.10"

plugins {
    kotlin("jvm") version "1.3.41"
    id("org.gretty") version "2.3.1"  // https://github.com/gretty-gradle-plugin/gretty
    war
}

defaultTasks("clean", "build")

repositories {
    jcenter()  // doesn't work with mavenCentral(): Gretty won't find its gretty-runner-jetty94
    maven { setUrl("https://maven.vaadin.com/vaadin-prereleases/") }  // because of Vaadin 13.0.0.beta1
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
    compile(enforcedPlatform("com.vaadin:vaadin-bom:$vaadin10_version"))
    // Vaadin-on-Kotlin dependency, includes Vaadin
    compile("eu.vaadinonkotlin:vok-framework-v10-sql2o:$vaadinonkotlin_version")
    providedCompile("javax.servlet:javax.servlet-api:3.1.0")

    compile(kotlin("stdlib-jdk8"))

    // logging
    // currently we are logging through the SLF4J API to LogBack. See src/main/resources/logback.xml file for the logger configuration
    compile("ch.qos.logback:logback-classic:1.2.3")
    compile("org.slf4j:slf4j-api:1.7.25")

    // db
    compile("org.flywaydb:flyway-core:5.2.4")
    compile("com.h2database:h2:1.4.198") // remove this and replace it with a database driver of your choice.

    // REST
    compile("eu.vaadinonkotlin:vok-rest:$vaadinonkotlin_version")

    // testing
    testCompile("com.github.mvysny.kaributesting:karibu-testing-v10:1.1.8")
    testCompile("com.github.mvysny.dynatest:dynatest-engine:0.15")
    testCompile("eu.vaadinonkotlin:vok-rest-client:$vaadinonkotlin_version")
    testCompile("org.eclipse.jetty.websocket:websocket-server:9.4.12.v20180830")

    // heroku app runner
    staging("com.github.jsimone:webapp-runner-main:9.0.20.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
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

