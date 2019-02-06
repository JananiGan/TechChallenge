package com.johnlewis.products.controller

import com.johnlewis.products.api.ProductsApi
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
fun Application.module() {
    routing {

        get("/v1/{category}/products/pricereduction") {
            ProductsApi.Companion.categoryId = call.parameters["category"]
            ProductsApi.Companion.labelType = call.request.queryParameters["labelType"] ?: ""
            val jsonResponse = ProductsApi.getProductsWithPriceReduction()
            call.respond(jsonResponse)
        }
    }
}