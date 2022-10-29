package com.vaadin.starter.beveragebuddy.backend

import eu.vaadinonkotlin.rest.*
import io.javalin.Javalin
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Provides access to person list. To test, just run `curl http://localhost:8080/rest/categories`
 */
@WebServlet(urlPatterns = ["/rest/*"], name = "JavalinRestServlet", asyncSupported = false)
class JavalinRestServlet : HttpServlet() {
    val javalin = Javalin.createStandalone().apply {
        gsonMapper(VokRest.gson)
        get("/rest/categories") { ctx -> ctx.json(Category.findAll()) }
        crud2("/rest/reviews", Review.getCrudHandler(false))
    }.javalinServlet()

    override fun service(req: HttpServletRequest, resp: HttpServletResponse) {
        javalin.service(req, resp)
    }
}
