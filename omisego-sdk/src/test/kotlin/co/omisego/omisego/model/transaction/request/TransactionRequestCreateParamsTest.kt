package co.omisego.omisego.model.transaction.request

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 26/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.extension.bd
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldNotBe
import org.junit.Test

class TransactionRequestCreateParamsTest {

    @Test
    fun `TransactionRequestCreateParams should return null if allowAmountOverride is false and the amount is null`() {
        val req = TransactionRequestCreateParams.init(TransactionRequestType.RECEIVE, "1234", null)

        req shouldBe null
    }

    @Test
    fun `TransactionRequestCreateParams should be created successfully`() {
        val req = TransactionRequestCreateParams.init(
            TransactionRequestType.RECEIVE,
            "OMG-1234-5678",
            100.bd
        )

        req shouldNotBe null
    }
}
