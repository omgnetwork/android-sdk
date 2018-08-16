package co.omisego.omisego.model.pagination

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

@Parcelize
data class Pagination(
    val perPage: Int,
    val isLastPage: Boolean,
    val isFirstPage: Boolean,
    val currentPage: Int
) : Parcelable
