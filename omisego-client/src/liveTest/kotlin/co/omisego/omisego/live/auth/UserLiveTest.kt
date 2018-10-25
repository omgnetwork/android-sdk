package co.omisego.omisego.live

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.User
import co.omisego.omisego.operation.startListeningEvents
import co.omisego.omisego.websocket.event.TransactionConsumptionRequestEvent
import co.omisego.omisego.websocket.listener.SocketConnectionListener
import co.omisego.omisego.websocket.listener.SocketCustomEventListener
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class UserLiveTest : BaseAuthTest() {

    @Test
    fun `get_current_user should return 200 and parsed the response correctly`() {
        val user = client.getCurrentUser().execute()
        user.isSuccessful shouldBe true
        user.body()?.data shouldNotBe null
        user.body()?.data shouldBeInstanceOf User::class.java
    }

    @Test
    fun `user should be able to join channel`() {
        val mockConnectListener: SocketConnectionListener = mock()
        val user = client.getCurrentUser().execute()
        socketClient.addConnectionListener(mockConnectListener)
        user.body()?.data?.startListeningEvents(
            socketClient,
            listener = SocketCustomEventListener.forEvent<TransactionConsumptionRequestEvent> {
            })

        Thread.sleep(3000)
        verify(mockConnectListener, times(1)).onConnected()
    }
}
