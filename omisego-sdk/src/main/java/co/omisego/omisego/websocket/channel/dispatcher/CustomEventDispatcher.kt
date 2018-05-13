package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.socket.SocketReceiveData
import co.omisego.omisego.websocket.SocketChannelCallback
import co.omisego.omisego.websocket.SocketCustomEventCallback
import co.omisego.omisego.websocket.enum.SocketFeaturedEvent
import co.omisego.omisego.websocket.enum.SocketFeaturedEvent.TRANSACTION_CONSUMPTION_FINALIZED
import co.omisego.omisego.websocket.enum.SocketFeaturedEvent.TRANSACTION_CONSUMPTION_REQUEST

class CustomEventDispatcher : SocketDispatcherContract.CustomEventDispatcher {
    override var socketCustomEventCallback: SocketCustomEventCallback? = null
    override var socketReceive: SocketReceive? = null
    override var socketChannelCallback: SocketChannelCallback? = null

    override fun handleEvent(featuredEvent: SocketFeaturedEvent) {
        val response = socketReceive ?: return
        val listenEvent = socketCustomEventCallback ?: return

        when (listenEvent) {
            is SocketCustomEventCallback.TransactionRequestCallback ->
                listenEvent.handleTransactionRequestEvent(response, featuredEvent)
            is SocketCustomEventCallback.TransactionConsumptionCallback ->
                listenEvent.handleTransactionConsumptionEvent(response, featuredEvent)
        }
    }

    override fun SocketCustomEventCallback.TransactionRequestCallback.handleTransactionRequestEvent(
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
            socketChannelCallback?.onError(socketReceive.error)
        }
    }

    override fun SocketCustomEventCallback.TransactionConsumptionCallback.handleTransactionConsumptionEvent(socketReceive: SocketReceive, featuredEvent: SocketFeaturedEvent) {
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
            socketChannelCallback?.onError(socketReceive.error)
        }
    }
}
