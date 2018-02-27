
package co.omisego.omisego.networks

import java.io.InputStreamReader


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/9/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

interface HttpConnection {
    fun setup(endpoint: String)
    fun setHeaders(headers: Map<String, String>)
    fun setPostBody(body: String)
    fun request()
    fun closeInputStream()
    fun closeOutputStream()
    fun response(): String
    fun InputStreamReader?.stringify(): String?
}
