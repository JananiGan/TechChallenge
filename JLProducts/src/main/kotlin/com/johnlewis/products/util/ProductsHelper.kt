package com.johnlewis.products.util

import com.google.gson.internal.LinkedTreeMap
import com.johnlewis.products.rest.Colour
import com.johnlewis.products.rest.Product
import com.johnlewis.products.rest.ProductPrice
import com.johnlewis.products.rest.colorSwatch
import javafx.scene.paint.Color
import kotlin.math.roundToInt

object ProductsHelper {

    fun getColorSwatches(colorSwatches: List<colorSwatch>): List<Colour> {
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

    fun calculatePriceReductionPercent(productsWithWasNowPrice: List<Product>): List<Product> {
        for (prod in productsWithWasNowPrice) {
            if (prod.price.nowPrice is LinkedTreeMap<*, *>) {
                prod.price.currentPrice =
                    prod.price.nowPrice.get("from").toString()
            } else if (prod.price.nowPrice is String) {
                prod.price.currentPrice = prod.price.nowPrice
            }
            if (!prod.price.currentPrice.isNullOrEmpty())
                prod.price.priceReductionPercent =
                    (((prod.price.wasPrice!!.toDouble() - prod.price.currentPrice.toDouble()) * 100) / prod.price.wasPrice.toDouble()).roundToInt()
        }
        return productsWithWasNowPrice
    }

    fun getFormattedNowPrice(nowPrice: String): String {

        val numericPrice = nowPrice.toDouble()
        try {
            if (numericPrice.compareTo(numericPrice.toInt()) == 0 && numericPrice > 10)
                return ApiConstants.CURR_FORMAT + (numericPrice.toInt().toString())
            else
                return ApiConstants.CURR_FORMAT + nowPrice
        } catch (e: NumberFormatException) {
            return ApiConstants.CURR_FORMAT + nowPrice
        }
    }

    fun getRGBColor(basicColor: String): String {
        try {
            val c = Color.valueOf(basicColor).toString()
            return c.removePrefix("0x").removeSuffix("ff")
        } catch (e: Exception) {
            return ""
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
                    return """Was ${ApiConstants.CURR_FORMAT}${price.wasPrice}, then ${ApiConstants.CURR_FORMAT}${thenPrice}, now ${ApiConstants.CURR_FORMAT}${price.currentPrice}"""
                else
                    return """Was ${ApiConstants.CURR_FORMAT}${price.wasPrice}, now ${ApiConstants.CURR_FORMAT}${price.currentPrice}"""
            }
            ApiConstants.SHOW_PERCENT_NOW -> {
                return ("""${price.priceReductionPercent}% off - now ${ApiConstants.CURR_FORMAT}${price.currentPrice}""")
            }
            else -> {
                return """Was ${ApiConstants.CURR_FORMAT}${price.wasPrice}, now ${ApiConstants.CURR_FORMAT}${price.currentPrice}"""
            }
        }
    }
}