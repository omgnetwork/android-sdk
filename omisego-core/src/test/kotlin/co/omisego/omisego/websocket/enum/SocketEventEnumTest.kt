package co.omisego.omisego.websocket.enum

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.TransactionConsumption
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.junit.Test

class SocketEventEnumTest : GsonDelegator() {

    private val mockSocketReceive: SocketReceive<TransactionConsumption> = mock()

    @Test
    fun `transaction_consumption_finalized should be initialized correctly`() {
        val json = "transaction_consumption_finalized"
        val result = gson.fromJson<SocketCustomEvent>(json, SocketCustomEvent::class.java)
        result shouldBe SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED
        result.eventBuilder(mockSocketReceive)?.socketReceive shouldBe mockSocketReceive
    }

    @Test
    fun `transaction_consumption_request should be initialized correctly`() {
        val json = "transaction_consumption_request"
        val result = gson.fromJson<SocketCustomEvent>(json, SocketCustomEvent::class.java)
        result shouldBe SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST
        result.eventBuilder(mockSocketReceive)?.socketReceive shouldBe mockSocketReceive
    }

    @Test
    fun `unexpected custom event should be considered to event OTHER`() {
        val json = "unexpected_event"
        val result = gson.fromJson<SocketCustomEvent>(json, SocketCustomEvent::class.java)
        result shouldBe SocketCustomEvent.OTHER
        result.eventBuilder(mockSocketReceive) shouldBe null
    }
}
