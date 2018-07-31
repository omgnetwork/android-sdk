package co.omisego.omisego.utils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 31/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Exceptions
import co.omisego.omisego.model.CredentialConfiguration
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class OMGEncryptionTest {

    data class TestCredentialConfiguration(
        override val baseURL: String,
        override val authenticationToken: String,
        override val userId: String?,
        override val apiKey: String?,
        override val authScheme: String
    ) : CredentialConfiguration

    private val clientConfiguration: TestCredentialConfiguration by lazy {
        TestCredentialConfiguration(
            "http://test.com/",
            "authenticationToken",
            null,
            "apiKey",
            "OMGClient"
        )
    }

    private val adminConfiguration: TestCredentialConfiguration by lazy {
        TestCredentialConfiguration(
            "http://test.com/",
            "authenticationToken",
            "userId",
            null,
            "OMGClient"
        )
    }

    private val invalidConfiguration: TestCredentialConfiguration by lazy {
        TestCredentialConfiguration(
            "http://test.com/",
            "authenticationToken",
            null,
            null,
            "OMGClient"
        )
    }

    private val omgEncryption by lazy { OMGEncryption() }

    @Test
    fun `createAuthorizationHeader should return base64 of the apiKey and authenticationToken correctly`() {
        omgEncryption.createAuthorizationHeader(clientConfiguration) shouldEqualTo "YXBpS2V5OmF1dGhlbnRpY2F0aW9uVG9rZW4="
    }

    @Test
    fun `createAuthorizationHeader should return base64 of the userId and authenticationToken correctly`() {
        omgEncryption.createAuthorizationHeader(adminConfiguration) shouldEqualTo "dXNlcklkOmF1dGhlbnRpY2F0aW9uVG9rZW4="
    }

    @Test
    fun `createAuthorizationHeader should throw IllegalStateException if apiKey and userId are null or empty`() {
        val exception = { omgEncryption.createAuthorizationHeader(invalidConfiguration) }
        val exception2 = { omgEncryption.createAuthorizationHeader(invalidConfiguration.copy(apiKey = "")) }
        val exception3 = { omgEncryption.createAuthorizationHeader(invalidConfiguration.copy(userId = "")) }

        exception shouldThrow IllegalStateException::class withMessage Exceptions.MSG_EMPTY_API_KEY_OR_USER_ID
        exception2 shouldThrow IllegalStateException::class withMessage Exceptions.MSG_EMPTY_API_KEY_OR_USER_ID
        exception3 shouldThrow IllegalStateException::class withMessage Exceptions.MSG_EMPTY_API_KEY_OR_USER_ID
    }
}
