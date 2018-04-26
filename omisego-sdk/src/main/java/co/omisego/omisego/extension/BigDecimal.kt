package co.omisego.omisego.extension

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 6/12/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import java.math.BigDecimal

/**
 *  Convenient extension property to convert double to BigDecimal
 *  For example, 100.0.bd is equal to BigDecimal.valueOf(100.0)
 */
val Double.bd: BigDecimal
    get() = BigDecimal.valueOf(this)