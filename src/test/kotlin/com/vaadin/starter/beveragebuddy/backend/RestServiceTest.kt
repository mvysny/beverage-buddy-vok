package com.vaadin.starter.beveragebuddy.backend

import com.github.mvysny.kaributesting.v10.expectList
import com.vaadin.starter.beveragebuddy.AbstractAppTest
import eu.vaadinonkotlin.restclient.*
import org.eclipse.jetty.ee10.webapp.WebAppContext
import org.eclipse.jetty.server.Server
import org.junit.jupiter.api.*
import java.io.FileNotFoundException
import java.net.http.HttpClient

/**
 * Uses the VoK `vok-rest-client` module for help with testing of the REST endpoints. See docs on the
 * [vok-rest-client](https://github.com/mvysny/vaadin-on-kotlin/tree/master/vok-rest-client) module for more details.
 */
class PersonRestClient(val baseUrl: String) {
    init {
        require(!baseUrl.endsWith("/")) { "$baseUrl must not end with a slash" }
    }
    private val client: HttpClient = VokRestClient.httpClient
    fun getAllCategories(): List<Category> {
        val request = "$baseUrl/categories".buildUrl().buildRequest()
        return client.exec(request) { response -> response.jsonArray(Category::class.java) }
    }
    fun getAllReviews(): List<Review> {
        val request = "$baseUrl/reviews".buildUrl().buildRequest()
        return client.exec(request) { response -> response.jsonArray(Review::class.java) }
    }
    fun nonexistingEndpoint() {
        val request = "$baseUrl/nonexisting".buildUrl().buildRequest()
        client.exec(request) { }
    }
}

/**
 * The REST test. It bootstraps the app, then it starts Javalin with Jetty so that we can access it via the
 * [PersonRestClient].
 */
class RestServiceTest : AbstractAppTest() {
    companion object {
        private lateinit var server: Server
        @BeforeAll @JvmStatic fun startJavalin() {
            val ctx = WebAppContext()
            // This used to be EmptyResource, but it got removed in Jetty 12. Let's use some dummy resource instead.
            ctx.baseResource = ctx.resourceFactory.newClassLoaderResource("java/lang/String.class")
            ctx.addServlet(JavalinRestServlet::class.java, "/rest/*")
            server = Server(9876)
            server.handler = ctx
            server.start()
        }
        @AfterAll @JvmStatic fun stopJavalin() { server.stop() }
    }

    private lateinit var client: PersonRestClient
    @BeforeEach fun createClient() { client = PersonRestClient("http://localhost:9876/rest") }

    @Test fun `categories smoke test`() {
        expectList() { client.getAllCategories() }
    }

    @Test fun `reviews smoke test`() {
        expectList() { client.getAllReviews() }
    }

    @Test fun `404`() {
        assertThrows<FileNotFoundException> {
            client.nonexistingEndpoint()
        }
    }
}
