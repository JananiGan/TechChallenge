package com.johnlewis.products.rest

import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductsApiApplicationKtTest {

    @Test
    fun testRestEndPoint() {
        withTestApplication(Application::module) {
            with(handleRequest(HttpMethod.Get, "/v1/123456/products/pricereduction")) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

}