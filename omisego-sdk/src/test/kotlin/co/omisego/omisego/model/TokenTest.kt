package co.omisego.omisego.model

/*
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 17/12/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.extension.bd
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class TokenTest {
    val token1 = Token("OMG:8bcda572-9411-43c8-baae-cd56eb0155f3", "OMG", "OmiseGO", 10000.0.bd, mapOf(), mapOf())
    val token2 = Token("ETH:8bcda572-9411-43c8-baae-cd56eb0155f3", "ETH", "Ethereum", 10000.0.bd, mapOf(), mapOf())
    val token3 = Token("OMG:8bcda572-9411-43c8-baae-cd56eb0155f3", "OMG", "OmiseGO", 10.0.bd, mapOf(), mapOf())
    val token4 = Token("OMG:8bcda572-9411-43c8-baae-cd56eb0155f3", "OMG", "OmiseGO", 10000.0.bd, mapOf(), mapOf())

    @Test
    fun `token1 should be not compatible with token2`() {
        token1 compatWith token2 shouldEqualTo false
    }

    @Test
    fun `token1 should be not compatible with token3`() {
        token1 compatWith token3 shouldEqualTo false
    }

    @Test
    fun `token1 should be compatible with token4`() {
        token1 compatWith token4 shouldEqualTo true
    }
}