package com.vaadin.starter.beveragebuddy

import com.gitlab.mvysny.jdbiorm.JdbiOrm
import com.vaadin.flow.server.InitialPageSettings
import com.vaadin.flow.server.ServiceInitEvent
import com.vaadin.flow.server.VaadinServiceInitListener
import eu.vaadinonkotlin.VaadinOnKotlin
import com.vaadin.starter.beveragebuddy.backend.DemoData
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.h2.Driver
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

/**
 * Boots the app:
 *
 * * Makes sure that the database is up-to-date, by running migration scripts with Flyway. This will work even in cluster as Flyway
 *   automatically obtains a cluster-wide database lock.
 * * Initializes the VaadinOnKotlin framework.
 * * Maps Vaadin to `/`, maps REST server to `/rest`
 * @author mvy
 */
@WebListener
class Bootstrap: ServletContextListener {
    override fun contextInitialized(sce: ServletContextEvent?) {
        log.info("Starting up")

        // this will configure your database. For demo purposes, an in-memory embedded H2 database is used. To use a production-ready database:
        // 1. fill in the proper JDBC URL here
        // 2. make sure to include the database driver into the classpath, by adding a dependency on the driver into the build.gradle file.
        val cfg = HikariConfig().apply {
            driverClassName = Driver::class.java.name
            jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
            username = "sa"
            password = ""
        }
        JdbiOrm.setDataSource(HikariDataSource(cfg))

        // Initializes the VoK framework
        log.info("Initializing VaadinOnKotlin")
        VaadinOnKotlin.init()

        // Makes sure the database is up-to-date
        log.info("Running DB migrations")
        val flyway: Flyway = Flyway.configure()
            .dataSource(JdbiOrm.getDataSource())
            .load()
        flyway.migrate()

        // pre-populates the database with a demo data
        log.info("Populating database with testing data")
        DemoData.createDemoData()
        log.info("Initialization complete")
    }

    override fun contextDestroyed(sce: ServletContextEvent?) {
        log.info("Shutting down");
        log.info("Destroying VaadinOnKotlin")
        VaadinOnKotlin.destroy()
        log.info("Shutdown complete")
    }

    companion object {
        private val log = LoggerFactory.getLogger(Bootstrap::class.java)
    }
}

/**
 * Configures Vaadin. Registered via the Java Service Loader API.
 */
class MyServiceInitListener : VaadinServiceInitListener {
    override fun serviceInit(event: ServiceInitEvent) {
        event.addBootstrapListener {
            it.document.head().addMetaTag("apple-mobile-web-app-capable", "yes")
            it.document.head().addMetaTag("apple-mobile-web-app-status-bar-style", "black")
        }
    }
}

/**
 * Appends a `<meta name="foo" content="baz">` element to the html head. Useful
 * for [com.vaadin.flow.server.BootstrapListener] as a replacement for
 * [com.vaadin.flow.server.PageConfigurator]'s [InitialPageSettings.addMetaTag].
 */
@Deprecated("use function from karibu-tools once 0.5 is out")
fun Element.addMetaTag(name: String, content: String): Element =
    appendElement("meta").attr("name", name).attr("content", content)
