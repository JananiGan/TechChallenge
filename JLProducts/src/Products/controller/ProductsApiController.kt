package Products.controller

import com.johnlewis.ProductsApi
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
fun Application.module(){
    routing {

        get("/v1/{category}/products/pricereduction") {
            ProductsApi.Companion.categoryId= call.parameters["category"]
            ProductsApi.Companion.labelType= call.request.queryParameters["labelType"]?:""
            val jsonResponse=ProductsApi.getPriceReductionProducts()
            call.respond(jsonResponse)
            //ProductsApi.getProductsJson()?.let { it1 -> call.respond(it1.toString()) }
            //val item = model.items.firstOrNull { it.category == call.parameters["key"] }
            //if (item == null)
              //  call.respond(HttpStatusCode.NotFound)
            //else
              //  call.respond(item)
        }
    }
}