package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.websocket.SocketChannelListener
import co.omisego.omisego.websocket.SocketCustomEventListener
import co.omisego.omisego.websocket.SocketCustomEventListener.AnyEventListener
import co.omisego.omisego.websocket.SocketCustomEventListener.TransactionConsumptionListener
import co.omisego.omisego.websocket.SocketCustomEventListener.TransactionRequestListener
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import co.omisego.omisego.websocket.enum.SocketCustomEvent.OTHER
import co.omisego.omisego.websocket.enum.SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED
import co.omisego.omisego.websocket.enum.SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST

/**
 * A listener for dispatch the [SocketCustomEventListener] events.
 */
class CustomEventDispatcher : SocketDispatcherContract.CustomEventDispatcher {
    override val customEventListenerMap: MutableMap<String, SocketCustomEventListener> by lazy {
        mutableMapOf<String, SocketCustomEventListener>()
    }

    override var socketReceive: SocketReceive? = null

    override var socketChannelListener: SocketChannelListener? = null

    override fun clearCustomEventListenerMap() {
        customEventListenerMap.clear()
    }

    override fun handleEvent(customEvent: SocketCustomEvent) {
        val response = socketReceive ?: return
        val listener = customEventListenerMap[response.topic] ?: return

        when (listener) {
            is TransactionRequestListener ->
                listener.handleTransactionRequestEvent(response, customEvent)
            is TransactionConsumptionListener ->
                listener.handleTransactionConsumptionEvent(response, customEvent)
            is AnyEventListener ->
                listener.handleAnyEvent(response)
        }
    }

    override fun SocketCustomEventListener.TransactionRequestListener.handleTransactionRequestEvent(
        socketReceive: SocketReceive,
        customEvent: SocketCustomEvent
    ) {
        when (customEvent) {
            TRANSACTION_CONSUMPTION_REQUEST -> {
                if (socketReceive.data is TransactionConsumption) {
                    onTransactionConsumptionRequest(socketReceive.data)
                }
            }
            TRANSACTION_CONSUMPTION_FINALIZED -> {
                val judge = socketReceive.judgeCustomEventListener(
                    this::onTransactionConsumptionFinalizedFail,
                    this::onTransactionConsumptionFinalizedSuccess
                )

                if (!judge && socketReceive.error != null) {
                    socketChannelListener?.onError(socketReceive.error)
                }
            }
            OTHER -> {
                if (socketReceive.error != null) {
                    socketChannelListener?.onError(socketReceive.error)
                }
            }
        }
    }

    override fun SocketCustomEventListener.TransactionConsumptionListener.handleTransactionConsumptionEvent(
        socketReceive: SocketReceive,
        customEvent: SocketCustomEvent
    ) {
        if (customEvent == TRANSACTION_CONSUMPTION_FINALIZED) {
            val judge = socketReceive.judgeCustomEventListener(
                this::onTransactionConsumptionFinalizedFail,
                this::onTransactionConsumptionFinalizedSuccess
            )

            if (!judge && socketReceive.error != null) {
                socketChannelListener?.onError(socketReceive.error)
            }
        } else if (customEvent == OTHER && socketReceive.error != null) {
            socketChannelListener?.onError(socketReceive.error)
        }
    }

    override fun SocketCustomEventListener.AnyEventListener.handleAnyEvent(
        socketReceive: SocketReceive
    ) = onEventReceived(socketReceive)

    internal inline fun <reified T : SocketReceive.SocketData> SocketReceive.judgeCustomEventListener(
        matchedTypeWithErrorLambda: (T, APIError) -> Unit,
        matchedTypeLambda: (T) -> Unit
    ): Boolean {
        when {
            this.data is T && this.error != null -> matchedTypeWithErrorLambda(this.data, this.error)
            this.data is T -> matchedTypeLambda(this.data)
            else -> return false
        }
        return true
    }
}
