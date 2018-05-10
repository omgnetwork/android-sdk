package co.omisego.omisego.testUtils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 15/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.constant.enums.OMGEnum
import co.omisego.omisego.custom.gson.ErrorCodeDeserializer
import co.omisego.omisego.custom.gson.OMGEnumAdapter
import co.omisego.omisego.custom.gson.SocketReceiveDataDeserializer
import co.omisego.omisego.model.socket.SocketReceiveData
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder

internal object GsonProvider {
    fun provide(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(ErrorCode::class.java, ErrorCodeDeserializer())
            .registerTypeAdapter(SocketReceiveData::class.java, SocketReceiveDataDeserializer())
            .registerTypeHierarchyAdapter(OMGEnum::class.java, OMGEnumAdapter<OMGEnum>())
            .serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }
}
