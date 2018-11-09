package com.vaadin.starter.beveragebuddy.backend

import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.dynatest.expectList
import com.github.vok.restclient.RetrofitClientVokPlugin
import com.github.vok.restclient.exec
import com.github.vok.restclient.jsonArray
import com.vaadin.starter.beveragebuddy.ui.usingApp
import io.javalin.Javalin
import okhttp3.OkHttpClient
import okhttp3.Request

class PersonRestClient(val baseUrl: String) {
    init {
        require(!baseUrl.endsWith("/")) { "$baseUrl must not end with a slash" }
    }
    private val client: OkHttpClient = RetrofitClientVokPlugin.okHttpClient!!
    fun getAllCategories(): List<Category> {
        val request = Request.Builder().url("$baseUrl/categories").build()
        return client.exec(request) { response -> response.jsonArray(Category::class.java) }
    }
    fun getAllReviews(): List<Review> {
        val request = Request.Builder().url("$baseUrl/reviews").build()
        return client.exec(request) { response -> response.jsonArray(Review::class.java) }
    }
}

class RestServiceTest : DynaTest({
    usingApp()

    lateinit var javalin: Javalin
    beforeGroup {
        javalin = Javalin.create().disableStartupBanner()
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
