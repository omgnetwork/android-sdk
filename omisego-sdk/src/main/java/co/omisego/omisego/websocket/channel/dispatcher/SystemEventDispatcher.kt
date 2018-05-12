package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.util.Log
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.model.socket.runIfNotInternalTopic
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.SocketTopicCallback
import co.omisego.omisego.websocket.enum.SocketBasicEvent

class SystemEventDispatcher : SocketDispatcherContract.SystemEventDispatcher {
    override var socketConnectionCallback: SocketConnectionCallback? = null
    override var socketTopicCallback: SocketTopicCallback? = null
    override var socketChannel: SocketDispatcherContract.SocketChannel? = null
    override var socketReceive: SocketReceive? = null

    override fun handleEvent(basicEvent: SocketBasicEvent) {
        val response = socketReceive ?: return
        when (basicEvent) {
            SocketBasicEvent.CLOSE -> {
                val topic = SocketTopic(response.topic)
                topic.runIfNotInternalTopic {
                    socketChannel?.onLeftChannel(topic)
                    socketTopicCallback?.onUnSubscribedTopic(topic)
                }
            }
            SocketBasicEvent.REPLY -> {
                val topic = SocketTopic(response.topic)
                topic.runIfNotInternalTopic {
                    topic.runIfFirstJoined {
                        socketChannel?.onJoinedChannel(topic)
                        socketTopicCallback?.onSubscribedTopic(topic)
                    }
                }
            }
            SocketBasicEvent.ERROR -> {
                Log.d("SocketDispatcher", "Receive an error event.")
            }
            SocketBasicEvent.OTHER -> {
                //TODO: Handle other event
            }
        }
    }

    /**
     * Run the lambda when meets the following condition
     *  - The topic hasn't joined yet
     */
    internal inline fun SocketTopic.runIfFirstJoined(lambda: () -> Unit) {
        if (socketChannel?.joined(this) == false) {
            lambda()
        }
    }
}
