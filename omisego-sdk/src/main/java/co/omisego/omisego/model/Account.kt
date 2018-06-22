package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 25/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.Date

/**
 * Represents an account.
 *
 * @param id The unique identifier of the account.
 * @param parentId The id of the parent account.
 * @param name The name of the account.
 * @param description The description of the account.
 * @param isMaster A boolean indicating if the account is a master account or not.
 * @param avatar The avatar object containing urls.
 * @param metadata Any additional metadata that need to be stored as a dictionary.
 * @param encryptedMetadata Any additional encrypted metadata that need to be stored as a dictionary.
 * @param createdAt The creation date of the account.
 * @param updatedAt The date when the account was last updated.
 */
@Parcelize
data class Account(
    val id: String,
    val parentId: String,
    val name: String,
    val description: String,
    val isMaster: Boolean,
    val avatar: Avatar,
    val metadata: @RawValue Map<String, Any>,
    val encryptedMetadata: @RawValue Map<String, Any>,
    val createdAt: Date?,
    val updatedAt: Date?
) : Parcelable
