package com.challenge.products.rest

import com.challenge.products.util.ApiConstants.DEFAULT_ERR_MSG
import com.challenge.products.util.ApiConstants.URL_HOST
import com.challenge.products.util.ProductsHelper
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.BadResponseStatusException
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL
import java.net.UnknownHostException

open class ProductsApiService {

    var logger : Logger = LoggerFactory.getLogger(ProductsApiService::class.java)

    companion object {
        var categoryId: String? = null
    }

    var labelType: String? = null

    fun getProductsWithPriceReduction(): String {
        val productsJson: JsonObject? = getSourceJson()
        if (productsJson != null)
            return getPriceReductionProducts(productsJson)
        else
            return DEFAULT_ERR_MSG
    }

    open fun getSourceJson(): JsonObject? {
        logger.debug("Get the Source Json")
        val client = HttpClient(Apache) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }
        var productsJson: JsonObject? = null
        try {
            runBlocking {

                productsJson = client.get<JsonObject?> {
                    url(URL("""${URL_HOST}$categoryId/products?key=2ALHCAAs6ikGRBoy6eTHA58RaG097Fma"""))
                    contentType(ContentType.Application.Json)
                }
            }
        } catch (e: BadResponseStatusException) {
            logger.error(e.message)
        }catch (e: UnknownHostException){
            logger.error("Unknown Host "+e.message)
        }
        return productsJson
    }


    private fun getPriceReductionProducts(inputJson: JsonObject?): String {

        val productsObj = Gson().fromJson(inputJson, Products::class.java)
        val productsWithWasNowPrice =
            ProductsHelper.calculatePriceReductionPercent(productsObj.prods.filter { !(it.price.wasPrice.isNullOrEmpty()) })
        productsWithWasNowPrice.filter {
            it.price.priceReductionPercent > 0
        }
        val priceReducedProductsList: List<ProductWithReduction> =
            productsWithWasNowPrice.sortedWith(compareBy({ it.price.priceReductionPercent })).asReversed()
                .map { it ->
                    ProductWithReduction(
                        it.productId,
                        it.title,
                        ProductsHelper.getColorSwatches(it.colorSwatches),
                        it.price.currencySymbol + ProductsHelper.getFormattedNowPrice(it.price.currentPrice),
                        ProductsHelper.getPriceLabel(it.price, labelType)
                    )
                }

        return Gson().toJson(priceReducedProductsList)
    }
}

data class Products(@SerializedName("products") val prods: List<Product>)
data class Product(
    @SerializedName("productId") val productId: String,
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: ProductPrice,
    @SerializedName("colorSwatches") val colorSwatches: List<colorSwatch>
)

data class ProductPrice(
    @SerializedName("was") val wasPrice: String?,
    @SerializedName("then1") val then1Price: String?,
    @SerializedName("then2") val then2Price: String?,
    @SerializedName("now") val nowPrice: Any,
    @SerializedName("currency") val currency: String?,
    var currentPrice: String,
    var currencySymbol: String?,
    var priceReductionPercent: Int = 0
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

