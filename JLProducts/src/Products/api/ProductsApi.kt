package com.johnlewis

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.google.gson.internal.LinkedTreeMap
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javafx.scene.paint.Color
import kotlinx.coroutines.runBlocking
import java.net.URL
import kotlin.math.roundToInt

class ProductsApi {
    companion object {
        val client = HttpClient(Apache) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }
        var categoryId: String? = null
        var labelType: String? = null

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

        fun getPriceReductionProducts(): String {
            val productsObj = Gson().fromJson(getProductsJson(), Products::class.java)
            val productsWithWasNowPrice = productsObj.prods.filter { !it.price.wasPrice.isNullOrEmpty() }
            for (prod in productsWithWasNowPrice) {
                if (prod.price.nowPrice is LinkedTreeMap<*, *>) {
                    prod.price.currentPrice = prod.price.nowPrice.get("to").toString()
                } else if (prod.price.nowPrice is String) {
                    prod.price.currentPrice = prod.price.nowPrice
                }
                if(!prod.price.currentPrice.isNullOrEmpty())
                prod.price.priceDiff = prod.price.wasPrice!!.toDouble() - prod.price.currentPrice!!.toDouble()
            }
            productsWithWasNowPrice.filter {
                it.price.priceDiff > 0
            }

            val priceReducedProductsList: List<ProductWithReduction> = productsWithWasNowPrice.sortedWith(compareBy({it.price.priceDiff})).asReversed().map { it ->
                ProductWithReduction(
                    it.productId,
                    it.title,
                    getColorSwatches(it.colorSwatches),
                    it.price.currentPrice,
                    getpriceLabel(it.price)
                )
            }
            //for (i in productsWithWasNowPrice)
            //   println(i.productId)

            return Gson().toJson(priceReducedProductsList)
            // println("rgb value for black " + Integer.toHexString(Color.BLACK.rgb))

           // for (i in productsWithWasNowPrice)
               // println(i.productId)
            //val prodItems=products.prods.get(1).productId
            //println("check" + productsObj.prods.get(1).productId)
            //var products: JsonArray =productsJson.parseJson("products").jsonArray


        }

        private fun getColorSwatches(colorSwatches: List<colorSwatch>): List<Colour> {
            if (!colorSwatches.isNullOrEmpty()) {
                val colorsWithRgbList = colorSwatches.map { it ->
                    Colour(
                        it.color,
                        getRGBColor(it.basicColor),
                        it.skuid
                    )
                }
                return colorsWithRgbList
            } else
                return emptyList()

        }

        fun getRGBColor(basicColor: String): String {
            //Integer.toHexString(it.basicColor.rgb)
            try {
                val c = Color.valueOf(basicColor).toString()
                return c.removePrefix("0x").removeSuffix("ff")
            } catch (e: Exception) {
                return ""
            }


            //return ""
        }

        enum class BasicColours {}

        private fun getpriceLabel(price: ProdPrice): String {

            when (labelType) {

                "ShowWasThenNow" -> {
                    val thenPrice = price.then2Price ?: price.then1Price
                    if (!thenPrice.isNullOrEmpty())
                        return """Was £${price.wasPrice} then £${thenPrice} now £${price.currentPrice}"""
                    else
                        return """Was £${price.wasPrice} now £${price.currentPrice}"""
                }
                "ShowPercDscount" -> {
                    val percentDisc =
                        ((((price.wasPrice!!.toDouble()) - price.currentPrice.toDouble()) * 100) / price.wasPrice!!.toDouble()).roundToInt()
                    return ("""${percentDisc}% off - now £${price.currentPrice}""")
                }
                else -> {
                    return """Was £${price.wasPrice} now £${price.currentPrice}"""
                }
            }
            return ""
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
    var currentPrice: String,
    var priceDiff: Double=0.00
)

data class NowPrice(
    @SerializedName("from") val from: String,
    @SerializedName("to") val to: String
)

data class colorSwatch(
    @SerializedName("color") val color: String,
    @SerializedName("basicColor") val basicColor: String,
    @SerializedName("skuId") val skuid: String
)

data class Colour(val colour: String, val rgbColor: String, val skuid: String)

data class ProductWithReduction(
    val productId: String,
    val title: String,
    val colorSwatches: List<Colour>,
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





