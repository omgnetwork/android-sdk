package co.omisego.omisego.custom.gson

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 20/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.OMGEnum
import co.omisego.omisego.helpers.delegation.GsonDelegator
import org.amshove.kluent.shouldEqual
import kotlin.test.Test

class OMGEnumAdapterTest : GsonDelegator() {
    enum class TestEnum constructor(override val value: String) : OMGEnum {
        OMISEGO("omg")
    }

    @Test
    fun `OMGEnumAdapter should parse enum class successfully`() {
        val omisego = gson.toJson(TestEnum.OMISEGO)
        omisego shouldEqual "\"omg\""
    }
}
