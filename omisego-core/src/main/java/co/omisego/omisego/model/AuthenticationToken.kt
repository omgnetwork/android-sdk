package co.omisego.omisego.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/8/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

@Parcelize
open class AuthenticationToken(
    open val authenticationToken: String,
    open val userId: String,
    open val user: User
) : Parcelable
