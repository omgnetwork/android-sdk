package co.omisego.omisego.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 7/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * Represent a list containing a list of addresses
 *
 * @param data The list of the addresses
 */
@Parcelize
data class WalletList(val data: List<Wallet>) : Parcelable
