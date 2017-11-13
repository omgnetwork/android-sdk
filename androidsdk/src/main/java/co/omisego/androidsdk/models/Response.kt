package co.omisego.androidsdk.models


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/13/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

data class Response<out T>(val version: String, val success: Boolean, val data: T)
