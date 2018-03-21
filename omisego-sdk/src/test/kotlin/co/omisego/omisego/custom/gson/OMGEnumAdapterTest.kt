package co.omisego.omisego.custom.gson

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 20/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.OMGEnum
import com.google.gson.Gson
import com.google.gson.annotations.JsonAdapter
import org.amshove.kluent.shouldEqual
import org.junit.Before
import kotlin.test.Test

class OMGEnumAdapterTest {
    private lateinit var gson: Gson
    @Before
    fun setUp() {
        gson = Gson()
    }

    @JsonAdapter(OMGEnumAdapter::class)
    enum class TestEnum constructor(override val value: String) : OMGEnum {
        OMISEGO("omg")
    }

    @Test
    fun `OMGEnumAdapter should parse enum class successfully`() {
        val omisego = gson.toJson(TestEnum.OMISEGO)
        omisego shouldEqual "\"omg\""
    }
}
