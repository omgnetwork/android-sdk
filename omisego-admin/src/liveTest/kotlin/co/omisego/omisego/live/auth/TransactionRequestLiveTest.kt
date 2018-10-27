package co.omisego.omisego.live.auth

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.live.BaseAuthTest
import co.omisego.omisego.model.TransactionRequest
import co.omisego.omisego.model.params.TransactionRequestParams
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class GetTransactionRequestLiveTest : BaseAuthTest() {

    private val testTransactionRequestId: String by lazy { secret.getString("transaction_request_id") }

    @Test
    fun `getTransactionRequest should return the corresponding transaction request correctly`() {
        val response = client.getTransactionRequest(TransactionRequestParams(testTransactionRequestId)).execute()
        response.isSuccessful shouldBe true
        response.body()?.data shouldBeInstanceOf TransactionRequest::class.java
        response.body()?.data?.formattedId shouldEqual testTransactionRequestId
    }
}
