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
import co.omisego.omisego.testUtils.DateConverter
import com.google.gson.reflect.TypeToken
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import java.util.Date

class DateTest : GsonDelegator() {
    private val dateFile by ResourceFile("date.json", "object")
    private val dateFormatter: DateConverter by lazy { DateConverter() }
    private val multiFormatDate by lazy {
        val typeToken = object : TypeToken<Map<String, Date>>() {}.type
        gson.fromJson<Map<String, Date>>(dateFile.readText(), typeToken)
    }

    @Test
    fun `date should be parsed correctly`() {
        multiFormatDate["date_1"] shouldNotBe null
        multiFormatDate["date_1"] shouldEqual dateFormatter.fromString("2018-01-01T01:00:00.000000Z")
        multiFormatDate["date_2"] shouldNotBe null
        multiFormatDate["date_2"] shouldEqual dateFormatter.fromString("2018-01-01T02:00:00.000Z")
        multiFormatDate["date_3"] shouldNotBe null
        multiFormatDate["date_3"] shouldEqual dateFormatter.fromString("2018-01-01T03:00:00Z")
    }
}
