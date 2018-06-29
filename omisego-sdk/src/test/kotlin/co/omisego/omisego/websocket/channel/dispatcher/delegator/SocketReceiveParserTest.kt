package co.omisego.omisego.websocket.channel.dispatcher.delegator

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 13/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.model.transaction.consumption.TransactionConsumptionStatus
import co.omisego.omisego.model.transaction.request.TransactionRequestStatus
import co.omisego.omisego.model.transaction.request.TransactionRequestType
import co.omisego.omisego.utils.Either
import co.omisego.omisego.utils.GsonProvider
import co.omisego.omisego.websocket.SocketCustomEventListener
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Test
import java.io.File

class SocketReceiveParserTest {
    private val socketReceiveTxConsumption: File by ResourceFile("socket_receive_transaction_consumption.json", "websocket")
    private val socketReceiveError: File by ResourceFile("socket_receive_null_data_with_error.json", "websocket")
    private lateinit var socketReceiveParser: SocketReceiveParser

    @Before
    fun setup() {
        socketReceiveParser = SocketReceiveParser(GsonProvider.create())
    }

    @Test
    fun `Parse raw json with a transaction consumption data and a null error successfully`() {
        val socketReceive = socketReceiveParser.parse(socketReceiveTxConsumption.readText())
        with(socketReceive) {
            topic shouldEqualTo "a_topic"
            ref!! shouldEqualTo "1"
            event shouldEqual Either.Right(SocketCustomEvent.OTHER)
            data shouldBeInstanceOf TransactionConsumption::class
            error shouldBe null

            val transactionConsumptionData = data as TransactionConsumption
            with(transactionConsumptionData) {
                status shouldEqual TransactionConsumptionStatus.CONFIRMED
                socketTopic shouldEqual SocketTopic<SocketCustomEventListener.TransactionConsumptionListener>(
                    "transaction_consumption:8eb0160e-1c96-481a-88e1-899399cc84dc"
                )
                user?.id shouldEqual "6f56efa1-caf9-4348-8e0f-f5af283f17ee"
                user?.username shouldEqual "john.doe@example.com"
                transactionRequest.type shouldEqual TransactionRequestType.RECEIVE
                transactionRequest.status shouldEqual TransactionRequestStatus.VALID
                transactionRequest.socketTopic shouldEqual SocketTopic<SocketCustomEventListener.TransactionConsumptionListener>(
                    "transaction_request:8eb0160e-1c96-481a-88e1-899399cc84dc"
                )
                transactionRequest.requireConfirmation shouldEqualTo true
                transactionRequest.token.id shouldEqualTo "BTC:861020af-17b6-49ee-a0cb-661a4d2d1f95"
            }
        }
    }

    @Test
    fun `Parse raw json with a null data and a non-null error successfully`() {
        val socketError = socketReceiveParser.parse(socketReceiveError.readText())

        with(socketError) {
            data shouldBe null
            error shouldNotBe null
            with(error!!) {
                code shouldEqual ErrorCode.TRANSACTION_REQUEST_NOT_FOUND
                description shouldEqualTo "Transaction request cannot be found"
            }
        }
    }
}
