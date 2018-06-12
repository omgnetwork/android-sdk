package co.omisego.omisego.model.transaction.consumption

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.support.test.runner.AndroidJUnit4
import co.omisego.omisego.OMGAPIClient
import co.omisego.omisego.extension.bd
import co.omisego.omisego.model.Token
import co.omisego.omisego.model.pagination.Paginable
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.model.transaction.list.Transaction
import co.omisego.omisego.model.transaction.list.TransactionExchange
import co.omisego.omisego.model.transaction.list.TransactionSource
import co.omisego.omisego.model.transaction.request.TransactionRequest
import co.omisego.omisego.model.transaction.request.TransactionRequestStatus
import co.omisego.omisego.model.transaction.request.TransactionRequestType
import co.omisego.omisego.utils.validateParcel
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.Date

@RunWith(AndroidJUnit4::class)
@Config(sdk = [23])
class TransactionConsumptionTest {
    private val mOMGAPIClient: OMGAPIClient by lazy { mock<OMGAPIClient>() }
    val token = Token("1234", "OMG", "OmiseGO", 10000.bd)
    private val mTransactionConsumption: TransactionConsumption by lazy {
        TransactionConsumption(
            "OMG-1234",
            TransactionConsumptionStatus.APPROVED,
            100.bd,
            token,
            null,
            "1234",
            Transaction(
                "1234",
                Paginable.Transaction.TransactionStatus.CONFIRMED,
                TransactionSource("1234", 1234.bd, token),
                TransactionSource("3456", 3456.bd, token),
                TransactionExchange(2.0),
                mapOf("Test" to "1234"),
                mapOf(),
                Date()
            ),
            "1234",
            null,
            null,
            TransactionRequest(
                "1234",
                TransactionRequestType.RECEIVE,
                token,
                100.bd,
                expirationDate = Date(),
                requireConfirmation = false,
                socketTopic = SocketTopic("1234"),
                maxConsumption = 1234,
                allowAmountOverride = false,
                address = "1234",
                user = null,
                consumptionLifetime = 10,
                expiredAt = Date(),
                createdAt = Date(),
                expirationReason = "Hello",
                status = TransactionRequestStatus.VALID
            ),
            SocketTopic("test"),
            Date(),
            Date(),
            Date(),
            Date(),
            Date(),
            Date(),
            Date(),
            mapOf("Test" to "1"),
            mapOf("Hello" to "100.bd")
        )
    }

    @Test
    fun `When approve is invoked, OMGAPIClient should invoke the approveTransactionConsumption function properly`() {
        mTransactionConsumption.approve(mOMGAPIClient)
        verify(mOMGAPIClient, times(1)).approveTransactionConsumption(TransactionConsumptionActionParams("OMG-1234"))
    }

    @Test
    fun `When reject is invoked, OMGAPIClient should invoke the rejectTransactionConsumption function properly`() {
        mTransactionConsumption.reject(mOMGAPIClient)
        verify(mOMGAPIClient, times(1)).rejectTransactionConsumption(TransactionConsumptionActionParams("OMG-1234"))
    }

    @Test
    fun `TransactionConsumption should be parcelized correctly`() {
        mTransactionConsumption.validateParcel().apply {
            this shouldEqual mTransactionConsumption
            this shouldNotBe mTransactionConsumption
        }
        mTransactionConsumption.validateParcel()
    }
}
