package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import com.google.gson.reflect.TypeToken
import org.amshove.kluent.shouldBe
import org.junit.Test
import java.util.Calendar
import java.util.Date

class DateTest : GsonDelegator() {
    private val dateFile by ResourceFile("date.json", "object")
    private val multiFormatDate by lazy {
        val typeToken = object : TypeToken<Map<String, Date>>() {}.type
        gson.fromJson<Map<String, Date>>(dateFile.readText(), typeToken)
    }

    @Test
    fun `date should be parsed correctly`() {
        multiFormatDate["date_1"]?.compareTo(Calendar.getInstance().apply {
            set(2018, 0, 1, 8, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.time) shouldBe 0

        multiFormatDate["date_2"]?.compareTo(Calendar.getInstance().apply {
            set(2018, 0, 1, 9, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.time) shouldBe 0

        multiFormatDate["date_3"]?.compareTo(Calendar.getInstance().apply {
            set(2018, 0, 1, 10, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.time) shouldBe 0
    }
}
