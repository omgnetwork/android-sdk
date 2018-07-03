package co.omisego.omisego.model

/*
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 17/12/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.extension.bd
import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.testUtils.DateConverter
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import java.util.Date

class TokenTest : GsonDelegator() {
    private val dateConverter by lazy { DateConverter() }
    val tokenFile by ResourceFile("token.json", "object")
    val token1 = Token("OMG:8bcda572-9411-43c8-baae-cd56eb0155f3", "OMG", "OmiseGO", 10000.0.bd, Date(), Date(), mapOf(), mapOf())
    val token2 = Token("ETH:8bcda572-9411-43c8-baae-cd56eb0155f3", "ETH", "Ethereum", 10000.0.bd, Date(), Date(), mapOf(), mapOf())
    val token3 = Token("OMG:8bcda572-9411-43c8-baae-cd56eb0155f3", "OMG", "OmiseGO", 10.0.bd, Date(), Date(), mapOf(), mapOf())
    val token4 = Token("OMG:8bcda572-9411-43c8-baae-cd56eb0155f3", "OMG", "OmiseGO", 10000.0.bd, Date(), Date(), mapOf(), mapOf())

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

    @Test
    fun `token should be parsed correctly`() {
        val token = gson.fromJson<Token>(tokenFile.readText(), Token::class.java)
        with(token) {
            id shouldEqual "tok_ETH_01cbfge9qhmsdbjyb7a8e8pxt3"
            symbol shouldEqual "ETH"
            name shouldEqual "Ethereum"
            subunitToUnit shouldEqual 100000.bd
            metadata shouldEqual kotlin.collections.mapOf<String, Any>()
            encryptedMetadata shouldEqual kotlin.collections.mapOf<String, Any>()
            createdAt shouldEqual dateConverter.fromString("2018-01-01T00:00:00Z")
            updatedAt shouldEqual dateConverter.fromString("2018-01-01T00:00:00Z")
        }
    }
}
