package co.omisego.omisego.model.transaction.request

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 26/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.extension.bd
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.Test

class TransactionRequestCreateParamsTest {
    @Test
    fun `TransactionRequestCreateParams should throw IllegalArgumentException if allowAmountOverride is false and the amount is null`() {
        val exception = {
            TransactionRequestCreateParams(
                TransactionRequestType.RECEIVE,
                "1234",
                null,
                allowAmountOverride = false
            )
        }

        exception shouldThrow IllegalArgumentException::class withMessage
            "The amount cannot be null if the allowAmountOverride is false"
    }

    @Test
    fun `TransactionRequestCreateParams should be created successfully if using the default parameters`() {
        TransactionRequestCreateParams(
            tokenId = "OMG-1234-5678"
        )
    }

    @Test
    fun `TransactionRequestCreateParams should be created successfully if the allowAmountOverride is false and the amount is not null`() {
        TransactionRequestCreateParams(
            TransactionRequestType.RECEIVE,
            "OMG-1234-5678",
            100.bd
        )
    }

    @Test
    fun `TransactionRequestCreateParams should be able to be created if allowAmountOverride is true and the amount is null`() {
        TransactionRequestCreateParams(
            TransactionRequestType.RECEIVE,
            "1234",
            null,
            allowAmountOverride = true
        )
    }

    @Test
    fun `TransactionRequestCreateParams should be able to be created if allowAmountOverride is true and the amount is not null`() {
        TransactionRequestCreateParams(
            TransactionRequestType.RECEIVE,
            "1234",
            100.bd,
            allowAmountOverride = true
        )
    }
}
