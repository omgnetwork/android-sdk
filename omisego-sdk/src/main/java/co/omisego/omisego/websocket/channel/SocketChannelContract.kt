package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.websocket.SocketChannelCallback
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.SocketCustomEventCallback
import co.omisego.omisego.websocket.enum.SocketStatusCode
import okhttp3.WebSocketListener
import java.util.Timer

interface SocketChannelContract {
    /* Channel Package */
    interface Channel {

        /**
         * A [Dispatcher] is responsible dispatch all events to the client.
         */
        val socketDispatcher: Dispatcher

        /**
         * A [SocketClient] for sending a message to the eWallet web socket API and close the web socket connection.
         */
        val socketClient: SocketClient

        /**
         * A [SocketHeartbeat] is responsible for schedule sending the heartbeat event for keep the connection alive
         */
        val socketHeartbeat: SocketInterval

        /**
         * A [SocketMessageRef] is responsible for create unique ref value to be included in the [SocketSend] request.
         */
        val socketMessageRef: SocketChannelContract.MessageRef

        /**
         * Create a [SocketSend] instance to be used for join a channel.
         *
         * @param topic A topic indicating which channel will be joined.
         * @param payload (Optional) the additional data you might want to send bundled with the request.
         *
         * @return A [SocketSend] instance used for joining the channel.
         */
        fun createJoinMessage(topic: SocketTopic, payload: Map<String, Any>): SocketSend

        /**
         * Create a [SocketSend] instance to be used for join a channel.
         *
         * @param topic A topic indicating which channel will be joined.
         * @param payload (Optional) the additional data you might want to send bundled with the request.
         *
         * @return A [SocketSend] instance used for leaving the channel.
         */
        fun createLeaveMessage(topic: SocketTopic, payload: Map<String, Any>): SocketSend
    }

    /**
     * The [MessageRef] is responsible for create a unique string to be used for including in the [SocketSend].
     */
    interface MessageRef {
        var value: String
    }

    /* WebSocket Package */
    interface SocketClient {
        /**
         * Send the [SocketSend] request to the eWallet web socket API.
         *
         * @return A boolean indicating if the message was sent successfully.
         */
        fun send(message: SocketSend): Boolean

        /**
         * Close the web socket connection
         *
         * @param status The web socket status code
         * @param reason The detail why the connection is closed
         */
        fun closeConnection(status: SocketStatusCode, reason: String)
    }

    /* Interval Package */
    interface SocketInterval {
        /**
         * The timer for scheduling the [SocketSend] periodically to be sent to the server.
         */
        var timer: Timer?

        /**
         * [SocketMessageRef] is responsible for creating a unique ref value to be sent with the [SocketSend].
         */
        val socketMessageRef: SocketChannelContract.MessageRef

        /**
         * An interval of milliseconds between the end of the previous task and the start of the next one.
         */
        var period: Long

        /**
         * Start to schedule the [SocketSend] to be sent to the server periodically.
         *
         * @param task A lambda with a [SocketSend] parameter. This will be executed periodically that starts immediately.
         */
        fun startInterval(task: (SocketSend) -> Unit)

        /**
         * Stop to schedule the task to be sent to the server.
         */
        fun stopInterval()
    }

    /* Dispatcher Package */
    interface Dispatcher {
        /**
         * Set the socket connection callback to be used for dispatch the connection status event.
         */
        fun setSocketConnectionCallback(connectionListener: SocketConnectionCallback?)

        /**
         * Set the socket channel callback to be used for dispatch the channel status event.
         */
        fun setSocketChannelCallback(channelListener: SocketChannelCallback?)

        /**
         * Set the socket custom events callback to be used for dispatch the custom events.
         */
        fun setSocketCustomEVentCallback(customEventListener: SocketCustomEventCallback?)

        /**
         * Retrieves the [WebSocketListener] to be used for initializing the [Websocket] in the [SocketClient].
         */
        fun retrieveWebSocketListener(): WebSocketListener
    }
}
