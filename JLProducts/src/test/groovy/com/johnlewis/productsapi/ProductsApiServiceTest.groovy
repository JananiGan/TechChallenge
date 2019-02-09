package com.johnlewis.productsapi

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import com.johnlewis.products.rest.ProductsApiService
import spock.lang.Shared
import spock.lang.Specification

import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.spy

class ProductsApiServiceTest extends Specification {

    @Shared
    ProductsApiService productsApi
    JsonObject mockSourceJson

    def setup() {
        productsApi = spy(ProductsApiService.class)
        buildMockJson()
    }

    def buildMockJson() {
        File file = new File("src/test/resources/SourceProducts.json")
        Gson gson = new Gson()
        mockSourceJson = gson.fromJson(new JsonReader(new FileReader(file)), JsonObject.class)
    }

    def "should get the json of products with price reduction with priceLabel ShowWasNow"() {
        given: "LabelType"
        productsApi.labelType = "ShowWasNow"
        doReturn(mockSourceJson).when(productsApi).getSourceJson()
        when:
        "Fetching price reduction products"
        def respJson = productsApi.getProductsWithPriceReduction()
        then: "Expect that price label should display the was and now price"
        respJson.contains("Was £85.00, now £59.00")
    }

    def "should get the json of products with price reduction with priceLabel ShowWasThenNow"() {
        given:
        productsApi.labelType = "ShowWasThenNow"
        doReturn(mockSourceJson).when(productsApi).getSourceJson()
        when:
        "Fetching price reduction products"
        def respJson = productsApi.getProductsWithPriceReduction()
        then: "Expect that price label should display the was,then and now values"
        respJson.contains("Was £85.00, then £68.00, now £59.00")
    }

    def "should get the price reduction products with priceLabel ShowPercDscount and colorswatches if available"() {
        given:
        productsApi.labelType = "ShowPercDscount"
        doReturn(mockSourceJson).when(productsApi).getSourceJson()
        when:
        "Fetching price reduction products"
        def respJson = productsApi.getProductsWithPriceReduction()
        then: "Expect that price label should display the reduction percentage and now price"
        respJson.contains("31% off - now £59.00")
        respJson.contains("\"rgbColor\":\"808080\"")
    }

    def "should get the products with highest price reduction first and no labeltype"() {
        given:
        doReturn(mockSourceJson).when(productsApi).getSourceJson()
        when:
        "Fetching price reduction products"
        def respJson = productsApi.getProductsWithPriceReduction()
        then: "Expect that price label should display the was,then and now values"
        respJson.startsWith("[{\"productId\":\"3467432\"")
        respJson.contains("Was £85.00, now £59.00")
    }
}