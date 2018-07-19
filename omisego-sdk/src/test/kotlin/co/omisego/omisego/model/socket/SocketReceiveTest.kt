package co.omisego.omisego.model.socket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/6/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.utils.Either
import co.omisego.omisego.websocket.channel.dispatcher.delegator.SocketReceiveParser
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import co.omisego.omisego.websocket.enum.SocketSystemEvent
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class SocketReceiveTest {
    private val socketReceiveParser by lazy { SocketReceiveParser() }
    private val socketReceiveSuccessFile by ResourceFile("socket_receive.json", "object")
    private val socketReceiveFailureFile by ResourceFile("socket_receive_failure.json", "object")
    private val socketReceiveReplyFile by ResourceFile("socket_receive_reply.json", "object")
    private val socketReceiveUnknownFile by ResourceFile("socket_receive_unknown.json", "object")

    private val socketReceiveSuccess by lazy { socketReceiveParser.parse(socketReceiveSuccessFile.readText()) }
    private val socketReceiveFailure by lazy { socketReceiveParser.parse(socketReceiveFailureFile.readText()) }
    private val socketReceiveReply by lazy { socketReceiveParser.parse(socketReceiveReplyFile.readText()) }
    private val socketReceiveUnknown by lazy { socketReceiveParser.parse(socketReceiveUnknownFile.readText()) }

    @Test
    fun `socket_receive should be parsed success case correctly`() {
        with(socketReceiveSuccess) {
            version shouldEqualTo "1"
            success shouldEqualTo true
            topic shouldEqualTo "a_topic"
            ref shouldEqual "1"
            event shouldEqual Either.Right(SocketCustomEvent.OTHER)
            error shouldEqual null
            data shouldBeInstanceOf TransactionConsumption::class.java
        }
    }

    @Test
    fun `socket_receive should be parsed failure case correctly`() {
        with(socketReceiveFailure) {
            version shouldEqualTo "1"
            success shouldEqualTo false
            topic shouldEqualTo "a_topic"
            ref shouldEqual "1"
            event shouldEqual Either.Left(SocketSystemEvent.ERROR)
            error shouldBeInstanceOf APIError::class.java
            data shouldBe null
            error?.code shouldEqual ErrorCode.CLIENT_INVALID_PARAMETER
            error?.description shouldEqual "Invalid parameters"
        }
    }

    @Test
    fun `socket_receive should be parsed reply case correctly`() {
        with(socketReceiveReply) {
            version shouldEqualTo "1"
            success shouldEqualTo true
            topic shouldEqualTo "a_topic"
            ref shouldEqual "2"
            event shouldEqual Either.Left(SocketSystemEvent.REPLY)
            data shouldBe null
        }
    }

    @Test
    fun `socket_receive should be parsed unknown case correctly`() {
        with(socketReceiveUnknown) {
            version shouldEqualTo "1"
            success shouldEqualTo true
            topic shouldEqualTo "a_topic"
            ref shouldEqual "1"
            event shouldEqual Either.Right(SocketCustomEvent.OTHER)
            error shouldBe null
            data shouldBeInstanceOf SocketReceive.Other::class.java
            val other = data as SocketReceive.Other
            with(other) {
                data["object"] shouldEqual "an unknown object"
                data["a_param"] shouldEqual "a_value"
            }
        }
    }
}
