package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/7/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.os.Parcelable
import co.omisego.omisego.constant.enums.ErrorCode
import kotlinx.android.parcel.Parcelize

/**
 * Represents an error caused by the SDK or the OmiseGO API.
 *
 * @param code An error code contained in [ErrorCode]
 * @param description The description associated with the received error code
 */
@Parcelize
data class APIError(val code: ErrorCode, val description: String) : Parcelable
