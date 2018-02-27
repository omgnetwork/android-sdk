package co.omisego.omisego.extensions

import java.math.BigDecimal


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 6/12/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

/**
 *  Convenient extension property to convert double to BigDecimal
 *  For example, 100.0.bd is equal to BigDecimal.valueOf(100.0)
 */
val Double.bd: BigDecimal
    get() = BigDecimal.valueOf(this)