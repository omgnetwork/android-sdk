package co.omisego.omisego.admin.extension

import co.omisego.omisego.utils.GsonProvider

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 26/10/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

fun Any.prettify() = GsonProvider.create().toJson(this)
