package com.johnlewis

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import java.net.URL

class ProductsApi {
    companion object {
        val client = HttpClient(Apache) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }
        var categoryId: String? = null

        fun getProductsJson(): JsonObject? {
            val client = HttpClient(Apache) {
                install(JsonFeature) {
                    serializer = GsonSerializer()
                }
            }
            var productsJson: JsonObject? = null
            runBlocking {

                productsJson = client.get<JsonObject?> {
                    url(URL("https://jl-nonprod-syst.apigee.net/v1/categories/${categoryId}/products?key=2ALHCAAs6ikGRBoy6eTHA58RaG097Fma"))
                    contentType(ContentType.Application.Json)
                }
                //println(GsonBuilder().setPrettyPrinting().create().toJson(message))
                println(productsJson)

            }
            return productsJson
        }

        fun getPriceReductionProducts() {
            val productsObj = Gson().fromJson(getProductsJson(), Products::class.java)
            val productsWithWasNowPrice=productsObj.prods.filter{!it.price.wasPrice.isNullOrEmpty()}
            for(prod in productsWithWasNowPrice){
                if(prod.price.nowPrice is NowPrice)
                    prod.price.toPrice=prod.price.nowPrice.to
            }
            productsWithWasNowPrice.filter {
                (!it.price.toPrice.isNullOrEmpty()&&(it.price.wasPrice!!.toDouble()- it.price.toPrice?.toDouble()!!)>0)||(it.price.nowPrice is String && (it.price.wasPrice!!.toDouble()-it.price.nowPrice.toDouble())>0)

            }
            for(i in productsWithWasNowPrice)
                println(i.productId)
            //val prodItems=products.prods.get(1).productId
            println("check" + productsObj.prods.get(1).productId)
            //var products: JsonArray =productsJson.parseJson("products").jsonArray


        }

    }


}


data class Products(@SerializedName("products") val prods: List<Product>)
data class Product(
    @SerializedName("productId") val productId: String,
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: ProdPrice,
    @SerializedName("colorSwatches") val colorSwatches: List<colorSwatch>
)

data class ProdPrice(
    @SerializedName("was") val wasPrice: String?,
    @SerializedName("then1") val then1Price: String?,
    @SerializedName("then2") val then2Price: String?,
    @SerializedName("now") val nowPrice: Any,
    var toPrice: String?
)

data class NowPrice(
    @SerializedName("from") val from: String?,
    @SerializedName("to") val to: String?
)

data class colorSwatch(
    @SerializedName("color") val color: String,
    @SerializedName("skuId") val skuid: String
)

data class Color(val color: String, val rgbColor: String, val skuid: String)

data class ProductWithReduction(
    val productId: String,
    val title: String,
    val colorSwatches: List<Color>,
    val nowPrice: String,
    val priceLabel: String
)




//fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

//@Suppress("unused") // Referenced in application.conf
//@kotlin.jvm.JvmOverloads
/*fun Application.module(testing: Boolean = false) {
    val client = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }
    runBlocking {
        // Sample for making a HTTP Client request
        /*
        val message = client.post<JsonSampleClass> {
        url(URL("http://127.0.0.1:8080/path/to/endpoint"))
        contentType(ContentType.Application.Json)
        body = JsonSampleClass(hello = "world")
    }*/
        val message = client.get<String> {
            url(URL("https://jl-nonprod-syst.apigee.net/v1/categories/600001506/products?key=2ALHCAAs6ikGRBoy6eTHA58RaG097Fma"))
            contentType(ContentType.Application.Json)
        }
        println(GsonBuilder().setPrettyPrinting().create().toJson(message))
        println(message)

    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }
    }
}
*/





