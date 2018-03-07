package co.omisego.omisego.network.ewallet

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Endpoints.GET_CURRENT_USER
import co.omisego.omisego.constant.Endpoints.GET_SETTINGS
import co.omisego.omisego.constant.Endpoints.LIST_BALANCE
import co.omisego.omisego.constant.Endpoints.LOGOUT
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.POST

interface EWalletAPI {
    @POST(GET_CURRENT_USER)
    fun getCurrentUser(): Call<JsonElement>

    @POST(LOGOUT)
    fun logout(): Call<JsonElement>

    @POST(LIST_BALANCE)
    fun listBalance(): Call<JsonElement>

    @POST(GET_SETTINGS)
    fun getSettings(): Call<JsonElement>
}