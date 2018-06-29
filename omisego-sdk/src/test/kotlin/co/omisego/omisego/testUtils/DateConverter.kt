package co.omisego.omisego.testUtils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/6/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Date

class DateConverter {
    fun fromString(date: String): Date {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val accessor = formatter.parse(date)
        return Date.from(Instant.from(accessor))
    }
}
