package co.omisego.omisego.websocket.channel.dispatcher.delegator

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 13/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.model.transaction.consumption.TransactionConsumptionStatus
import co.omisego.omisego.model.transaction.request.TransactionRequestStatus
import co.omisego.omisego.model.transaction.request.TransactionRequestType
import co.omisego.omisego.utils.Either
import co.omisego.omisego.utils.GsonProvider
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
    private val socketReceiveTxConsumption: File by ResourceFile("websocket/socket_receive_transaction_consumption.json")
    private val socketReceiveError: File by ResourceFile("websocket/socket_receive_null_data_with_error.json")
    private lateinit var socketReceiveParser: SocketReceiveParser

    @Before
    fun setup() {
        socketReceiveParser = SocketReceiveParser(GsonProvider.create())
    }

    @Test
    fun `Parse raw json with a transaction consumption data and a null error successfully`() {
        val socketReceive = socketReceiveParser.parse(socketReceiveTxConsumption.readText())
        with(socketReceive) {
            topic shouldEqualTo "transaction_request:328e61ac-9f35-4da5-a891-bd39f5442283"
            ref!! shouldEqualTo "991238"
            event shouldEqual Either.Right(SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST)
            data shouldBeInstanceOf TransactionConsumption::class
            error shouldBe null

            val transactionConsumptionData = data as TransactionConsumption
            with(transactionConsumptionData) {
                status shouldEqual TransactionConsumptionStatus.PENDING
                socketTopic shouldEqualTo "transaction_consumption:42292c2d-2249-467b-bfd1-bb557211399b"
                user?.id shouldEqual "2b1f058c-b927-44b0-8ea0-c16cf1244ebd"
                user?.username shouldEqual "user02"
                transactionRequest.type shouldEqual TransactionRequestType.RECEIVE
                transactionRequest.status shouldEqual TransactionRequestStatus.VALID
                transactionRequest.socketTopic shouldEqualTo "transaction_request:328e61ac-9f35-4da5-a891-bd39f5442283"
                transactionRequest.requireConfirmation shouldEqualTo true
                transactionRequest.token.id shouldEqualTo "OMG:a9ef7096-4060-4155-b79d-b36c42d5d095"
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
