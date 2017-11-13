
package co.omisego.androidsdk.networks

import java.io.InputStreamReader
import javax.net.ssl.HttpsURLConnection


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/9/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

interface HttpConnection {
    fun provideConnection(endpoint: String): HttpsURLConnection
    fun HttpsURLConnection.setup()
    fun HttpsURLConnection.setHeaders(headers: Map<String, String>)
    fun HttpsURLConnection.setPostBody(body: String)
    fun HttpsURLConnection.request()
    fun InputStreamReader.response(): String
}
