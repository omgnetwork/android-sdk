package co.omisego.omisego.utils

import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.constant.enums.OMGEnum
import co.omisego.omisego.custom.gson.EitherEnumDeserializer
import co.omisego.omisego.custom.gson.ErrorCodeDeserializer
import co.omisego.omisego.custom.gson.OMGEnumAdapter
import co.omisego.omisego.custom.gson.SocketReceiveDataDeserializer
import co.omisego.omisego.model.socket.SocketReceive
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

internal class GsonProvider {
    companion object {
        fun create(): Gson {
            return GsonBuilder()
                .registerTypeAdapter(ErrorCode::class.java, ErrorCodeDeserializer())
                .registerTypeAdapter(Either::class.java, EitherEnumDeserializer<OMGEnum, OMGEnum>())
                .registerTypeAdapter(SocketReceive.Data::class.java, SocketReceiveDataDeserializer())
                .registerTypeHierarchyAdapter(OMGEnum::class.java, OMGEnumAdapter<OMGEnum>())
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
        }
    }
}
