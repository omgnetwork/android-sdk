package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.socket.SocketReceiveData
import co.omisego.omisego.websocket.SocketListenEvent
import co.omisego.omisego.websocket.SocketTopicCallback
import co.omisego.omisego.websocket.enum.SocketFeaturedEvent
import co.omisego.omisego.websocket.enum.SocketFeaturedEvent.TRANSACTION_CONSUMPTION_FINALIZED
import co.omisego.omisego.websocket.enum.SocketFeaturedEvent.TRANSACTION_CONSUMPTION_REQUEST

class SendableEventDispatcher : SocketDispatcherContract.SendableEventDispatcher {
    override var socketListenEvent: SocketListenEvent? = null
    override var socketReceive: SocketReceive? = null
    override var socketTopicCallback: SocketTopicCallback? = null

    override fun handleEvent(featuredEvent: SocketFeaturedEvent) {
        val response = socketReceive ?: return
        val listenEvent = socketListenEvent ?: return

        when (listenEvent) {
            is SocketListenEvent.TransactionRequestEvent ->
                listenEvent.handleTransactionRequestEvent(response, featuredEvent)
            is SocketListenEvent.TransactionConsumptionEvent ->
                listenEvent.handleTransactionConsumptionEvent(response, featuredEvent)
        }
    }

    override fun SocketListenEvent.TransactionRequestEvent.handleTransactionRequestEvent(
        socketReceive: SocketReceive,
        featuredEvent: SocketFeaturedEvent
    ) {
        val socketReceiveData = socketReceive.data as? SocketReceiveData.SocketConsumeTransaction ?: return
        when (featuredEvent) {
            TRANSACTION_CONSUMPTION_REQUEST -> {
                onTransactionConsumptionRequest(socketReceiveData.data)
            }
            TRANSACTION_CONSUMPTION_FINALIZED -> {
                when (socketReceive.error) {
                    null -> onTransactionConsumptionFinalizedSuccess(socketReceiveData.data)
                    else -> onTransactionConsumptionFinalizedFail(socketReceiveData.data, socketReceive.error)
                }
            }
        }

        if (socketReceive.error != null) {
            socketTopicCallback?.onError(socketReceive.error)
        }
    }

    override fun SocketListenEvent.TransactionConsumptionEvent.handleTransactionConsumptionEvent(socketReceive: SocketReceive, featuredEvent: SocketFeaturedEvent) {
        val socketReceiveData = socketReceive.data as? SocketReceiveData.SocketConsumeTransaction ?: return
        if (featuredEvent == TRANSACTION_CONSUMPTION_FINALIZED) {
            when (socketReceive.error) {
                null -> {
                    onTransactionConsumptionFinalizedSuccess(socketReceiveData.data)
                }
                else -> {
                    onTransactionConsumptionFinalizedFail(socketReceiveData.data, socketReceive.error)
                }
            }
        } else if (socketReceive.error != null) {
            socketTopicCallback?.onError(socketReceive.error)
        }
    }
}
