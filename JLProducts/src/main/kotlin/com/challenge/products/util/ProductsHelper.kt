package com.challenge.products.util

import com.challenge.products.rest.Colour
import com.challenge.products.rest.Product
import com.challenge.products.rest.ProductPrice
import com.challenge.products.rest.colorSwatch
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.stream.JsonReader
import java.io.FileReader
import kotlin.math.roundToInt

object ProductsHelper {

    lateinit var currencyCodes: LinkedTreeMap<*, *>
    val rgbLookUpTable = mapOf(
        "WHITE" to "FFFFFF",
        "RED" to "FF0000",
        "BLUE" to "0000FF",
        "PINK" to "FFC0CB",
        "GREY" to "808080",
        "BLACK" to "000000",
        "GREEN" to "008000",
        "ORANGE" to "FFA500",
        "PURPLE" to "800080",
        "YELLOW" to "FFFF00",
        "MULTI" to ""
    )

    fun getColorSwatches(colorSwatches: List<colorSwatch>): List<Colour> {
        if (!colorSwatches.isNullOrEmpty()) {
            val colorsWithRgbList = colorSwatches.map { it ->
                Colour(
                    it.color,
                    rgbLookUpTable[it.basicColor.toUpperCase()] ?: "",
                    it.skuid
                )
            }
            return colorsWithRgbList
        } else
            return emptyList()

    }

    fun populateCurrencyCodes() {
        val currencyJson = Gson().fromJson(
            JsonParser().parse(JsonReader(FileReader("""src/main/resources/currencies.json"""))).toString(),
            HashMap::class.java
        )
        currencyCodes = currencyJson.get("""currencies""") as LinkedTreeMap<*, *>
    }

    fun calculatePriceReductionPercent(productsWithWasNowPrice: List<Product>): List<Product> {
        populateCurrencyCodes()
        for (prod in productsWithWasNowPrice) {
            if (prod.price.nowPrice is LinkedTreeMap<*, *>) {
                prod.price.currentPrice =
                    prod.price.nowPrice.get("from").toString()
            } else if (prod.price.nowPrice is String) {
                prod.price.currentPrice = prod.price.nowPrice
            }
            if (!prod.price.currentPrice.isNullOrEmpty()) {
                prod.price.priceReductionPercent =
                    (((prod.price.wasPrice!!.toDouble() - prod.price.currentPrice.toDouble()) * 100) / prod.price.wasPrice.toDouble()).roundToInt()
            }
            prod.price.currencySymbol = currencyCodes.get(prod.price.currency ?: """default""").toString()
        }
        return productsWithWasNowPrice
    }

    fun getFormattedNowPrice(nowPrice: String): String {

        val numericPrice = nowPrice.toDouble()
        try {
            if (numericPrice.compareTo(numericPrice.toInt()) == 0 && numericPrice > 10)
                return numericPrice.toInt().toString()
            else
                return nowPrice
        } catch (e: NumberFormatException) {
            return nowPrice
        }
    }

    fun setNullIfEmpty(str: String?): String? {
        if (str.isNullOrEmpty())
            return null
        else
            return str
    }

    fun getPriceLabel(price: ProductPrice, labelType: String?): String {

        when (labelType) {
            ApiConstants.SHOW_WAS_THEN_NOW -> {
                val thenPrice = setNullIfEmpty(price.then2Price) ?: price.then1Price
                if (!thenPrice.isNullOrEmpty())
                    return """Was ${price.currencySymbol}${price.wasPrice}, then ${price.currencySymbol}${thenPrice}, now ${price.currencySymbol}${price.currentPrice}"""
                else
                    return """Was ${price.currencySymbol}${price.wasPrice}, now ${price.currencySymbol}${price.currentPrice}"""
            }
            ApiConstants.SHOW_PERCENT_NOW -> {
                return ("""${price.priceReductionPercent}% off - now ${price.currencySymbol}${price.currentPrice}""")
            }
            else -> {
                return """Was ${price.currencySymbol}${price.wasPrice}, now ${price.currencySymbol}${price.currentPrice}"""
            }
        }
    }
}