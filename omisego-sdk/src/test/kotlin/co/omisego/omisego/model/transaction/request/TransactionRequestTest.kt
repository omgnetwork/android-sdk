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
import co.omisego.omisego.model.Token
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.utils.validateParcel
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.Date

@RunWith(AndroidJUnit4::class)
@Config(sdk = [23])
class TransactionRequestTest : GsonDelegator() {
    private lateinit var transactionRequest: TransactionRequest

    @Before
    fun setup() {
        transactionRequest = TransactionRequest(
            "1234",
            TransactionRequestType.RECEIVE,
            Token("1234", "OMG", "OmiseGO", 1000.bd, Date(), Date(), mapOf(), mapOf()),
            100.bd,
            expirationDate = Date(),
            requireConfirmation = false,
            socketTopic = SocketTopic("1234"),
            maxConsumption = 1234,
            allowAmountOverride = false,
            maxConsumptionsPerUser = null,
            address = "1234",
            user = null,
            consumptionLifetime = 10,
            expiredAt = Date(),
            createdAt = Date(),
            expirationReason = "Hello",
            status = TransactionRequestStatus.VALID,
            formattedId = "1234",
            metadata = mapOf(),
            encryptedMetadata = mapOf()
        )
    }

    @Test
    fun `TransactionRequest should be parcelized correctly`() {
        transactionRequest.validateParcel().apply {
            this shouldEqual transactionRequest
            this shouldNotBe transactionRequest
        }
    }
}
