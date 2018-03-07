package co.omisego.omisego.custom

import co.omisego.omisego.constant.ErrorCode
import co.omisego.omisego.constant.Versions
import co.omisego.omisego.model.ApiError
import co.omisego.omisego.model.OMGResponse
import org.amshove.kluent.shouldEqual
import java.io.IOException
import kotlin.test.Test

/**
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 7/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
class SerializerTest {
    private val serializer by lazy { Serializer() }

    @Test
    fun `Serialize throwable failure`() {
        val msg = "No internet connection"
        val actual = serializer.failure(IOException(msg))
        val expected = OMGResponse(Versions.EWALLET_API, false, ApiError(ErrorCode.SDK_NETWORK_ERROR, msg))
        actual shouldEqual expected
    }

    @Test
    fun `Serialize API response parsing failure`() {

    }

    @Test
    fun `Serialize API failure`() {

    }

    @Test
    fun `Serialize API success`() {

    }
}