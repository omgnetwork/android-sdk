package co.omisego.omisego.model

import android.os.Parcelable
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.operation.Listenable
import co.omisego.omisego.websocket.SocketCustomEventListener
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.Date

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/3/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * Represents the current user
 *
 * @param id The unique identifier on the wallet server side.
 * @param providerUserId The user identifier on the provider server side.
 * @param username The user's username, it can be an email or any name describing this user.
 * @param metadata Any additional metadata that need to be stored as a [HashMap].
 * @param encryptedMetadata Any additional encrypted metadata that need to be stored as a dictionary.
 * @param socketTopic The socket URL from where to receive from.
 * @param createdAt The creation date of the user.
 * @param updatedAt The last update date of the user.
 */

@Parcelize
data class User(
    val id: String,
    val providerUserId: String,
    val username: String,
    val metadata: @RawValue Map<String, Any>,
    val encryptedMetadata: @RawValue Map<String, Any>,
    override val socketTopic: SocketTopic<SocketCustomEventListener>,
    val createdAt: Date,
    val updatedAt: Date
) : Parcelable, Listenable<SocketCustomEventListener>
