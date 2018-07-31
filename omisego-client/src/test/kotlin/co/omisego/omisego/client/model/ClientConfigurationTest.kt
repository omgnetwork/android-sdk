package co.omisego.omisego.client.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 24/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Exceptions
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.Test

class ClientConfigurationTest {

    private lateinit var clientConfiguration: ClientConfiguration

    @Test
    fun `ClientConfiguration should throws IllegalStateException if baseURL is empty`() {
        val error = { clientConfiguration = ClientConfiguration("", "apiKey", "authToken") }
        error shouldThrow IllegalStateException::class withMessage Exceptions.MSG_EMPTY_BASE_URL
    }

    @Test
    fun `ClientConfiguration should throws IllegalStateException if apiKey is empty`() {
        val error = { clientConfiguration = ClientConfiguration("baseURL", "", "authToken") }
        error shouldThrow IllegalStateException::class withMessage Exceptions.MSG_EMPTY_API_KEY
    }

    @Test
    fun `ClientConfiguration should throws IllegalStateException if authenticationToken is empty`() {
        val error = { clientConfiguration = ClientConfiguration("baseURL", "apiKey", "") }
        error shouldThrow IllegalStateException::class withMessage Exceptions.MSG_EMPTY_AUTH_TOKEN
    }

    @Test
    fun `ClientConfiguration should not throws any Exception if all params are not empty`() {
        clientConfiguration = ClientConfiguration("baseURL", "apiKey", "authToken")
        // If the above statement throws something this test will fail now, so no need to validate.
    }
}
