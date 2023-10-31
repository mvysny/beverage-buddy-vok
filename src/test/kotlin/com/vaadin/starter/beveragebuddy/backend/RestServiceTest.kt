package com.vaadin.starter.beveragebuddy.backend

import com.github.mvysny.dynatest.*
import com.vaadin.starter.beveragebuddy.ui.usingApp
import eu.vaadinonkotlin.restclient.*
import org.eclipse.jetty.ee10.webapp.WebAppContext
import org.eclipse.jetty.server.Server
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
    private val client: HttpClient = HttpClientVokPlugin.httpClient!!
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

@DynaTestDsl
fun DynaNodeGroup.usingJavalin() {
    lateinit var server: Server
    beforeGroup {
        val ctx = WebAppContext()
        // This used to be EmptyResource, but it got removed in Jetty 12. Let's use some dummy resource instead.
        ctx.baseResource = ctx.resourceFactory.newClassLoaderResource("java/lang/String.class")
        ctx.addServlet(JavalinRestServlet::class.java, "/rest/*")
        server = Server(9876)
        server.handler = ctx
        server.start()
    }
    afterGroup { server.stop() }
}

/**
 * The REST test. It bootstraps the app, then it starts Javalin with Jetty so that we can access it via the
 * [PersonRestClient].
 */
class RestServiceTest : DynaTest({
    usingApp()
    usingJavalin()

    lateinit var client: PersonRestClient
    beforeEach { client = PersonRestClient("http://localhost:9876/rest") }

    test("categories smoke test") {
        expectList() { client.getAllCategories() }
    }

    test("reviews smoke test") {
        expectList() { client.getAllReviews() }
    }

    test("404") {
        expectThrows<FileNotFoundException> {
            client.nonexistingEndpoint()
        }
    }
})
