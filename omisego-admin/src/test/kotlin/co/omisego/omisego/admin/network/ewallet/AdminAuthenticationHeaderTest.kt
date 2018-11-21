package co.omisego.omisego.admin.network.ewallet

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.network.ewallet.AdminAuthenticationHeader
import co.omisego.omisego.utils.Base64Encoder
import com.nhaarman.mockito_kotlin.verify
import org.amshove.kluent.mock
import org.junit.Test

class AdminAuthenticationHeaderTest {
    private val encoder: Base64Encoder = mock()

    private val clientAuthenticationHeader: AdminAuthenticationHeader by lazy {
        AdminAuthenticationHeader(
            encoder
        )
    }

    @Test
    fun `should be create a client header correctly`() {
        clientAuthenticationHeader.create("auth_token", "user_id")

        verify(encoder).encode("user_id", "auth_token")
    }
}
