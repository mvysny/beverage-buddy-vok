package com.vaadin.starter.beveragebuddy.backend

import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.dynatest.expectList
import eu.vaadinonkotlin.restclient.exec
import eu.vaadinonkotlin.restclient.jsonArray
import com.vaadin.starter.beveragebuddy.ui.usingApp
import eu.vaadinonkotlin.restclient.OkHttpClientVokPlugin
import io.javalin.Javalin
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Uses the VoK `vok-rest-client` module for help with testing of the REST endpoints. See docs on the
 * [vok-rest-client](https://github.com/mvysny/vaadin-on-kotlin/tree/master/vok-rest-client) module for more details.
 */
class PersonRestClient(val baseUrl: String) {
    init {
        require(!baseUrl.endsWith("/")) { "$baseUrl must not end with a slash" }
    }
    private val client: OkHttpClient = OkHttpClientVokPlugin.okHttpClient!!
    fun getAllCategories(): List<Category> {
        val request = Request.Builder().url("$baseUrl/categories").build()
        return client.exec(request) { response -> response.jsonArray(Category::class.java) }
    }
    fun getAllReviews(): List<Review> {
        val request = Request.Builder().url("$baseUrl/reviews").build()
        return client.exec(request) { response -> response.jsonArray(Review::class.java) }
    }
}

/**
 * The REST test. It bootstraps the app, then it starts Javalin with Jetty so that we can access it via the
 * [PersonRestClient].
 */
class RestServiceTest : DynaTest({
    usingApp()

    lateinit var javalin: Javalin
    beforeGroup {
        javalin = Javalin.create()
        javalin.configureRest().start(9876)
    }
    afterGroup { javalin.stop() }

    lateinit var client: PersonRestClient
    beforeEach { client = PersonRestClient("http://localhost:9876/rest") }

    test("categories smoke test") {
        expectList() { client.getAllCategories() }
    }

    test("reviews smoke test") {
        expectList() { client.getAllReviews() }
    }
})
