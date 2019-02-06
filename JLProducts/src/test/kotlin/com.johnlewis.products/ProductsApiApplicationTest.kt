package com.johnlewis.products

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import java.net.URL
import kotlinx.coroutines.*
import kotlin.test.*
import io.ktor.server.testing.*
import org.junit.Test
import com.johnlewis.products.controller.module
import com.johnlewis.products.api.ProductsApi

class ProductsApiApplicationTest {
    @Test
    /*fun testRoot() = {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/v1/123456/products/pricereduction").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO WORLD!", response.content)
            }
        }

    }*/

    @Test
    fun `should return the products with price reduction`() {
        val inputJson = ""
        val expectedResponse = ""
        assertEquals(expectedResponse, ProductsApi.getPriceReductionProducts())
    }
}