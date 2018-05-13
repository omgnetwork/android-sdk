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
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import co.omisego.omisego.websocket.enum.SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED
import co.omisego.omisego.websocket.enum.SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST

class CustomEventDispatcher : SocketDispatcherContract.CustomEventDispatcher {
    /**
     * For dispatching the [SocketCustomEventCallback] event.
     */
    override var socketCustomEventCallback: SocketCustomEventCallback? = null

    /**
     * The web socket replied object from eWallet API.
     */
    override var socketReceive: SocketReceive? = null

    /**
     * For dispatching the [SocketChannelCallback] event.
     */
    override var socketChannelCallback: SocketChannelCallback? = null

    /**
     * Handles the [SocketCustomEvent] and dispatch the [SocketCustomEventCallback] to the client.
     *
     * @param customEvent To indicate the actual type of generic [SocketCustomEvent]
     */
    override fun handleEvent(customEvent: SocketCustomEvent) {
        val response = socketReceive ?: return
        val listenEvent = socketCustomEventCallback ?: return

        when (listenEvent) {
            is SocketCustomEventCallback.TransactionRequestCallback ->
                listenEvent.handleTransactionRequestEvent(response, customEvent)
            is SocketCustomEventCallback.TransactionConsumptionCallback ->
                listenEvent.handleTransactionConsumptionEvent(response, customEvent)
        }
    }

    /**
     * Handles the [SocketCustomEvent] event and dispatch the [SocketCustomEventCallback.TransactionRequestCallback].
     * This method will be invoked by the [handleEvent] method.
     *
     * @param socketReceive The web socket replied object from eWallet API.
     * @param customEvent The custom event used for decide the event to be dispatched
     */
    override fun SocketCustomEventCallback.TransactionRequestCallback.handleTransactionRequestEvent(
        socketReceive: SocketReceive,
        customEvent: SocketCustomEvent
    ) {
        val socketReceiveData = socketReceive.data as? SocketReceiveData.SocketConsumeTransaction ?: return
        when (customEvent) {
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

    /**
     * Handles the [SocketCustomEvent] event and dispatch the [SocketCustomEventCallback.TransactionConsumptionCallback].
     * This method will be invoked by the [handleEvent] method.
     *
     * @param socketReceive The web socket replied object from eWallet API.
     * @param customEvent The custom event used for decide the event to be dispatched
     */
    override fun SocketCustomEventCallback.TransactionConsumptionCallback.handleTransactionConsumptionEvent(socketReceive: SocketReceive, customEvent: SocketCustomEvent) {
        val socketReceiveData = socketReceive.data as? SocketReceiveData.SocketConsumeTransaction ?: return
        if (customEvent == TRANSACTION_CONSUMPTION_FINALIZED) {
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
