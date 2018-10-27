package co.omisego.omisego.client.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.support.test.runner.AndroidJUnit4
import co.omisego.omisego.OMGAPIClient
import co.omisego.omisego.client.util.DateConverter
import co.omisego.omisego.client.util.GsonDelegator
import co.omisego.omisego.client.util.ResourceFile
import co.omisego.omisego.client.util.validateParcel
import co.omisego.omisego.extension.bd
import co.omisego.omisego.model.Token
import co.omisego.omisego.model.Transaction
import co.omisego.omisego.model.TransactionConsumption
import co.omisego.omisego.model.TransactionConsumptionStatus
import co.omisego.omisego.model.TransactionRequest
import co.omisego.omisego.model.User
import co.omisego.omisego.model.approve
import co.omisego.omisego.model.params.TransactionConsumptionActionParams
import co.omisego.omisego.model.reject
import co.omisego.omisego.model.socket.SocketTopic
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [23])
class TransactionConsumptionTest : GsonDelegator() {
    private val dateConverter by lazy { DateConverter() }
    private val mOMGAPIClient: OMGAPIClient by lazy { mock<OMGAPIClient>() }
    private val transactionConsumptionFile by ResourceFile("transaction_consumption.json", "object")
    private val transactionConsumption: TransactionConsumption by lazy {
        gson.fromJson(transactionConsumptionFile.readText(), TransactionConsumption::class.java)
    }

    @Test
    fun `when approve is invoked, OMGAPIClient should invoke the approveTransactionConsumption function properly`() {
        transactionConsumption.approve(mOMGAPIClient)
        verify(mOMGAPIClient, times(1)).approveTransactionConsumption(
            TransactionConsumptionActionParams("8eb0160e-1c96-481a-88e1-899399cc84dc")
        )
    }

    @Test
    fun `when reject is invoked, OMGAPIClient should invoke the rejectTransactionConsumption function properly`() {
        transactionConsumption.reject(mOMGAPIClient)
        verify(mOMGAPIClient, times(1)).rejectTransactionConsumption(
            TransactionConsumptionActionParams("8eb0160e-1c96-481a-88e1-899399cc84dc")
        )
    }

    @Test
    fun `transaction_consumption should be parcelized correctly`() {
        transactionConsumption.validateParcel().apply {
            this shouldEqual transactionConsumption
            this shouldNotBe transactionConsumption
        }
        transactionConsumption.validateParcel()
    }

    @Test
    fun `transaction_consumption should be parsed correctly`() {
        with(transactionConsumption) {
            id shouldEqual "8eb0160e-1c96-481a-88e1-899399cc84dc"
            status shouldEqual TransactionConsumptionStatus.CONFIRMED
            amount shouldEqual 1337.bd
            estimatedRequestAmount shouldEqual 1337.bd
            estimatedConsumptionAmount shouldEqual 1337.bd
            finalizedRequestAmount shouldEqual null
            finalizedConsumptionAmount shouldEqual null
            token shouldBeInstanceOf Token::class.java
            correlationId shouldEqual "31009545-db10-4287-82f4-afb46d9741d8"
            idempotencyToken shouldEqual "31009545-db10-4287-82f4-afb46d9741d8"
            transaction shouldBeInstanceOf Transaction::class.java
            transactionRequest shouldBeInstanceOf TransactionRequest::class.java
            address shouldEqual "3b7f1c68-e3bd-4f8f-9916-4af19be95d00"
            user shouldBeInstanceOf User::class.java
            socketTopic shouldBeInstanceOf SocketTopic::class.java
            expirationDate shouldEqual dateConverter.fromString("2019-01-01T00:00:00Z")
            approvedAt shouldEqual null
            rejectedAt shouldEqual null
            confirmedAt shouldEqual null
            failedAt shouldEqual null
            expiredAt shouldEqual null
            createdAt shouldEqual dateConverter.fromString("2018-01-01T00:00:00Z")
            metadata shouldEqual mapOf<String, Any>()
            encryptedMetadata shouldEqual mapOf<String, Any>()
        }
    }
}
