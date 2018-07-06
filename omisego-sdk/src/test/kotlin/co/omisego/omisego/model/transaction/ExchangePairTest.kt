package co.omisego.omisego.model.transaction

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/6/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.extension.bd
import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.Token
import co.omisego.omisego.utils.DateConverter
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.junit.Test

class ExchangePairTest : GsonDelegator() {
    private val dateConverter by lazy { DateConverter() }
    private val exchangePairFile by ResourceFile("exchange_pair.json", "object")

    @Test
    fun `test transaction_exchange parsing`() {
        val exchangePair = gson.fromJson(exchangePairFile.readText(), ExchangePair::class.java)
        with(exchangePair) {
            id shouldEqual "exg_01cgvppyrz2pprj6s0zmc26p2p"
            name shouldEqual "ETH/OMG"
            fromTokenId shouldEqual "tok_ETH_01cbfge9qhmsdbjyb7a8e8pxt3"
            fromToken shouldBeInstanceOf Token::class.java
            toTokenId shouldEqual "tok_OMG_01cgvrqbfpa23ehkmrtqpbsyyp"
            toToken shouldBeInstanceOf Token::class.java
            rate shouldEqual 0.017.bd
            createdAt shouldEqual dateConverter.fromString("2018-01-01T00:00:00Z")
            updatedAt shouldEqual dateConverter.fromString("2018-01-01T10:00:00Z")
        }
    }
}
