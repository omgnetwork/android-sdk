package co.omisego.omisego.client.network.ewallet

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.network.ewallet.ClientAuthenticationHeader
import co.omisego.omisego.utils.Base64Encoder
import com.nhaarman.mockito_kotlin.verify
import org.amshove.kluent.mock
import org.junit.Test

class ClientAuthenticationHeaderTest {
    private val encoder: Base64Encoder = mock()

    private val clientAuthenticationHeader: ClientAuthenticationHeader by lazy {
        ClientAuthenticationHeader(
            "api_key",
            encoder
        )
    }

    @Test
    fun `should be create a client header correctly`() {
        clientAuthenticationHeader.create("auth_token", null)

        verify(encoder).encode("api_key", "auth_token")
    }
}
