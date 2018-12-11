package co.omisego.omisego.client.extension

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 21/11/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.utils.GsonProvider

fun String.toMap(): Map<*, *>? {
    return GsonProvider.create().fromJson(this, Map::class.java)
}
