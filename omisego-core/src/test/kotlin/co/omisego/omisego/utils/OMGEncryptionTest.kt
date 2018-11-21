package co.omisego.omisego.utils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 31/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.AuthScheme
import co.omisego.omisego.constant.enums.AuthScheme.ADMIN
import co.omisego.omisego.constant.enums.AuthScheme.Client
import co.omisego.omisego.model.CredentialConfiguration
import org.amshove.kluent.shouldEqualTo
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
        override val authScheme: AuthScheme
    ) : CredentialConfiguration

    private val clientConfiguration: TestCredentialConfiguration by lazy {
        TestCredentialConfiguration(
            "http://test.com/",
            "authenticationToken",
            null,
            "apiKey",
            Client
        )
    }

    private val adminConfiguration: TestCredentialConfiguration by lazy {
        TestCredentialConfiguration(
            "http://test.com/",
            "authenticationToken",
            "userId",
            null,
            ADMIN
        )
    }

    private val nullAPIKeyClientConfiguration: TestCredentialConfiguration by lazy {
        TestCredentialConfiguration(
            "http://test.com/",
            "authenticationToken",
            null,
            null,
            Client
        )
    }

    private val emptyUserIdConfiguration: TestCredentialConfiguration by lazy {
        TestCredentialConfiguration(
            "http://test.com/",
            "authenticationToken",
            "",
            null,
            ADMIN
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
    fun `createAuthorizationHeader should return empty string if authScheme is OMGClient and apiKey is nullOrEmpty`() {
        omgEncryption.createAuthorizationHeader(nullAPIKeyClientConfiguration) shouldEqualTo ""
        omgEncryption.createAuthorizationHeader(nullAPIKeyClientConfiguration.copy(apiKey = "")) shouldEqualTo ""
    }

    @Test
    fun `createAuthorizationHeader should return empty string if authScheme is OMGAdmin and userId is nullOrEmpty`() {
        omgEncryption.createAuthorizationHeader(emptyUserIdConfiguration) shouldEqualTo ""
        omgEncryption.createAuthorizationHeader(emptyUserIdConfiguration.copy(userId = null)) shouldEqualTo ""
    }
}
