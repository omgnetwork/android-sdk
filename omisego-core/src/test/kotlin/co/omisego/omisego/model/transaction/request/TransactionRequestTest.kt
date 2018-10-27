package co.omisego.omisego.model.transaction.request

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 22/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.support.test.runner.AndroidJUnit4
import co.omisego.omisego.extension.bd
import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.TransactionRequest
import co.omisego.omisego.model.TransactionRequestStatus
import co.omisego.omisego.model.TransactionRequestType
import co.omisego.omisego.model.User
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.utils.DateConverter
import co.omisego.omisego.utils.validateParcel
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [23])
class TransactionRequestTest : GsonDelegator() {
    private val transactionRequestFile by ResourceFile("transaction_request.json", "object")
    private val transactionRequest: TransactionRequest by lazy { gson.fromJson(transactionRequestFile.readText(), TransactionRequest::class.java) }
    private val dateConverter by lazy { DateConverter() }

    @Test
    fun `transaction_request should be parcelized correctly`() {
        transactionRequest.validateParcel().apply {
            this shouldEqual transactionRequest
            this shouldNotBe transactionRequest
        }
    }

    @Test
    fun `transaction_request should be parsed correctly`() {
        with(transactionRequest) {
            type shouldEqual TransactionRequestType.RECEIVE
            id shouldEqualTo "8eb0160e-1c96-481a-88e1-899399cc84dc"
            amount shouldEqual 1337.bd
            address shouldEqual "3b7f1c68-e3bd-4f8f-9916-4af19be95d00"
            user shouldBeInstanceOf User::class.java
            correlationId shouldEqual "31009545-db10-4287-82f4-afb46d9741d8"
            status shouldEqual TransactionRequestStatus.VALID
            socketTopic shouldBeInstanceOf SocketTopic::class.java
            requireConfirmation shouldEqualTo true
            maxConsumptions shouldEqual 1
            consumptionLifetime shouldEqual 1000
            createdAt shouldEqual dateConverter.fromString("2018-01-01T00:00:00Z")
            expirationDate shouldEqual dateConverter.fromString("2019-01-01T00:00:00Z")
            expirationReason shouldEqual "Expired"
            expiredAt shouldEqual dateConverter.fromString("2019-01-01T00:00:00Z")
            allowAmountOverride shouldEqualTo true
            maxConsumptionsPerUser shouldBe null
            formattedId shouldEqualTo "|8eb0160e-1c96-481a-88e1-899399cc84dc"
            metadata shouldEqual mapOf<String, Any>()
            encryptedMetadata shouldEqual mapOf<String, Any>()
        }
    }
}
