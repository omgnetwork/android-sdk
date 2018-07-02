package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.extension.toCalendar
import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import com.google.gson.reflect.TypeToken
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBe
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
        multiFormatDate["date_1"] shouldNotBe null
        with(multiFormatDate["date_1"]!!.toCalendar()) {
            get(Calendar.DAY_OF_MONTH) shouldEqualTo 1
            get(Calendar.MONTH) shouldEqualTo 0
            get(Calendar.YEAR) shouldEqualTo 2018
            get(Calendar.HOUR_OF_DAY) shouldEqualTo 8
            get(Calendar.MINUTE) shouldEqualTo 0
            get(Calendar.SECOND) shouldEqualTo 0
        }

        multiFormatDate["date_2"] shouldNotBe null
        with(multiFormatDate["date_2"]!!.toCalendar()) {
            get(Calendar.DAY_OF_MONTH) shouldEqualTo 1
            get(Calendar.MONTH) shouldEqualTo 0
            get(Calendar.YEAR) shouldEqualTo 2018
            get(Calendar.HOUR_OF_DAY) shouldEqualTo 9
            get(Calendar.MINUTE) shouldEqualTo 0
            get(Calendar.SECOND) shouldEqualTo 0
        }

        multiFormatDate["date_3"] shouldNotBe null
        with(multiFormatDate["date_3"]!!.toCalendar()) {
            get(Calendar.DAY_OF_MONTH) shouldEqualTo 1
            get(Calendar.MONTH) shouldEqualTo 0
            get(Calendar.YEAR) shouldEqualTo 2018
            get(Calendar.HOUR_OF_DAY) shouldEqualTo 10
            get(Calendar.MINUTE) shouldEqualTo 0
            get(Calendar.SECOND) shouldEqualTo 0
        }
    }
}
