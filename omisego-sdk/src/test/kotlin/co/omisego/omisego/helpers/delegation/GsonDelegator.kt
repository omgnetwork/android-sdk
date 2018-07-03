package co.omisego.omisego.helpers.delegation

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/6/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.utils.GsonProvider

open class GsonDelegator {
    protected val gson by lazy { GsonProvider.create() }
}
