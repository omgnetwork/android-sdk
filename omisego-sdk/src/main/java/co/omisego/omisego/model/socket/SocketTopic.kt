package co.omisego.omisego.model.socket

import android.os.Parcelable
import co.omisego.omisego.websocket.interval.SocketHeartbeat
import kotlinx.android.parcel.Parcelize

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

@Parcelize
data class SocketTopic(val name: String) : Parcelable

/**
 * Run the lambda when the topic is coming from the user (to exclude the heartbeat event).
 */
internal inline fun SocketTopic.runIfNotInternalTopic(lambda: SocketTopic.() -> Unit) {
    if (this.name != SocketHeartbeat.TOPIC) {
        lambda()
    }
}