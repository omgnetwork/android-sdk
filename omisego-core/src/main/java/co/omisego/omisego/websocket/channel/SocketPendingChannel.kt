package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 22/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

class SocketPendingChannel : SocketChannelContract.PendingChannel {
    override val pendingChannelsQueue: BlockingQueue<SocketSend> by lazy {
        ArrayBlockingQueue<SocketSend>(10, true)
    }

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun execute(join: (topic: String, payload: Map<String, Any>) -> Unit) {
        for (socketSend in pendingChannelsQueue) {
            join(socketSend.topic, socketSend.data)
        }
    }

    override fun add(socketSend: SocketSend, period: Long) {
        // If the queue capacity is full, then the next join message will wait to add to the queue for maximum 5 seconds
        pendingChannelsQueue.offer(socketSend, period, TimeUnit.MILLISECONDS)
    }

    override fun remove(topic: String) {
        pendingChannelsQueue.findLast { it.topic == topic }?.let {
            pendingChannelsQueue.remove(it)
        }
    }
}
