package co.omisego.omisego.extension

import android.content.res.Resources


/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * Convert density independent pixels into pixels.
 *
 * This simplifies code dealing with dimensions.
 */
inline val Int.dp: Float
    get() = (this * Resources.getSystem().displayMetrics.density)