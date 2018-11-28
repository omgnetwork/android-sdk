package co.omisego.omisego.admin.model.params.admin

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 26/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.admin.utils.GsonDelegator
import co.omisego.omisego.extension.bd
import co.omisego.omisego.model.TransactionRequestType
import co.omisego.omisego.model.params.admin.TransactionRequestCreateParams
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.Test

class TransactionRequestCreateParamsTest : GsonDelegator() {
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

    @Test
    fun `TransactionRequestCreateParams should be created correctly`() {
        val expectedJson = """
            {
              "type": "receive",
              "token_id": "tok_CGO_01cr7e8yptgh08r43jweqaddqj",
              "address": "xjbe703812859522",
              "expiration_date": null,
              "allow_amount_override": true,
              "consumption_lifetime": null,
              "exchange_wallet_address": "exchange_wallet_address",
              "exchange_account_id": "exchange_account_id",
              "metadata": {},
              "encrypted_metadata": {}
            }
        """.trimIndent()

        gson.fromJson(expectedJson, TransactionRequestCreateParams::class.java) shouldEqual TransactionRequestCreateParams(
            type = TransactionRequestType.RECEIVE,
            tokenId = "tok_CGO_01cr7e8yptgh08r43jweqaddqj",
            address = "xjbe703812859522",
            expirationDate = null,
            allowAmountOverride = true,
            consumptionLifetime = null,
            requireConfirmation = false,
            exchangeWalletAddress = "exchange_wallet_address",
            exchangeAccountId = "exchange_account_id"
        )
    }
}
