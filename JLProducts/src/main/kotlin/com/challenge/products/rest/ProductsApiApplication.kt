package com.challenge.products.rest

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
fun Application.module() {

    val p = ProductsApiService()

    routing {
        get("/v1/{category}/products/pricereduction") {
            ProductsApiService.categoryId = call.parameters["category"]
            p.labelType = call.request.queryParameters["labelType"] ?: ""
            val jsonResponse = p.getProductsWithPriceReduction()
            call.respond(jsonResponse)
        }
    }
}