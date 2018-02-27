
package co.omisego.omisego.networks

import javax.net.ssl.HttpsURLConnection


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/9/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

/**
 * Provide default configuration of http connection
 * 
 */
object HttpConfiguration {
    private val REQUEST_METHOD = "POST"
    private val READ_TIMEOUT = 10_000
    private val CONNECT_TIMEOUT = 15_000

    fun default(): HttpsURLConnection.() -> Unit = {
        requestMethod = REQUEST_METHOD
        readTimeout = READ_TIMEOUT
        connectTimeout = CONNECT_TIMEOUT
        doInput = true
        doOutput = true
    }
}
