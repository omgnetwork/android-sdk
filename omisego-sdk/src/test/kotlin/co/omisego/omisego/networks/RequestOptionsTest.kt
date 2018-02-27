package co.omisego.omisego.networks

import org.amshove.kluent.shouldEqual
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

/**
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 11/7/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class RequestOptionsTest {
    private lateinit var requestOptions: RequestOptions

    @Before
    fun setup() {
        requestOptions = RequestOptions()
    }

    @Test
    fun `Set headers should be work correctly`() {
        // Arrange
        val expectedHeaders = hashMapOf("Content-Type" to "application/json", "Authorization" to "key")

        // Action
        requestOptions.apply {
            setHeaders(
                    "Content-Type" to "application/json",
                    "Authorization" to "key"
            )
        }

        // Assert
        requestOptions.getHeader() shouldEqual expectedHeaders
    }

    @Test
    fun `Set body should be work correctly`() {
        // Arrange
        val expectedJson = JSONObject("{" +
                "\"key_1\":\"value\"," +
                "\"key_2\":3" +
                "}")

        // Action
        requestOptions.apply {
            setBody(
                    "key_1" to "value",
                    "key_2" to 3
            )
        }


        // Assert
        requestOptions.getPostBody() shouldEqual expectedJson.toString()
    }
}
