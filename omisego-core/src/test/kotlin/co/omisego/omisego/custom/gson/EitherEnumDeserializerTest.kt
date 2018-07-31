package co.omisego.omisego.custom.gson

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 15/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.utils.Either
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import co.omisego.omisego.websocket.enum.SocketSystemEvent
import org.amshove.kluent.shouldEqual
import org.junit.Test

class EitherEnumDeserializerTest : GsonDelegator() {

    data class TestData(
        val event: Either<SocketSystemEvent, SocketCustomEvent>
    )

    @Test
    fun `deserialize an existed enum in EitherLeft and a non-existed in EitherRight should return EitherLeft`() {
        val predicate = """{"event": "phx_reply"}"""
        val either: Either<SocketSystemEvent, SocketCustomEvent> = gson.fromJson(predicate, TestData::class.java).event
        either shouldEqual Either.Left(SocketSystemEvent.REPLY)
    }

    @Test
    fun `deserialize a non-existed enum in EitherLeft and an existed in EitherRight should return EitherRight`() {
        val predicate = """{"event": "transaction_consumption_request"}"""
        val either: Either<SocketSystemEvent, SocketCustomEvent> = gson.fromJson(predicate, TestData::class.java).event
        either shouldEqual Either.Right(SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST)
    }

    @Test
    fun `deserialize both cannot found an existed enum and EitherLeft doesn't has an enum OTHER but EitherRight has then return EitherRight`() {
        val predicate = """{"event": "wtf_event_is_that"}"""
        val either: Either<SocketSystemEvent, SocketCustomEvent> = gson.fromJson(predicate, TestData::class.java).event
        either shouldEqual Either.Right(SocketCustomEvent.OTHER)
    }
}
