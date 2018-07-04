package co.omisego.omisego.live

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.transaction.request.TransactionRequest
import co.omisego.omisego.model.transaction.request.TransactionRequestCreateParams
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.fail

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class TransactionRequestLiveTest : LiveTest() {
    @Test
    fun `create_transaction_request should return 200 and parsed the response correctly`() {
        // 1. Get tokenId from get_setting
        val setting = client.getSettings().execute()
        val tokenId = setting.body()?.data?.tokens?.get(0)?.id ?: fail("There's no tokens available.")

        // 2. Create a transaction request
        val params = TransactionRequestCreateParams(tokenId = tokenId)
        val transactionRequest = client.createTransactionRequest(params).execute()
        transactionRequest.isSuccessful shouldBe true
        transactionRequest.body()?.data shouldBeInstanceOf TransactionRequest::class.java
    }
}
