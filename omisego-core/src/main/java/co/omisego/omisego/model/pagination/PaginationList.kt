package co.omisego.omisego.model.pagination

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

@Parcelize
data class PaginationList<out T>(
    val data: @RawValue List<T>,
    val pagination: Pagination
) : Parcelable
