package co.omisego.omisego.client.extension

import java.util.Calendar
import java.util.Date

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 2/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

fun Date.toCalendar(): Calendar = Calendar.getInstance().apply { time = this@toCalendar }
