
package co.omisego.androidsdk.networks

import co.omisego.androidsdk.models.RawData
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/8/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

object Requestor : HttpConnection {
    fun asyncRequest(endpoint: String, requestOptions: RequestOptions?) = async(CommonPool) {
        /* open https connection */
        val connection = provideConnection(endpoint)

        /* Setup https connection */
        connection.setup()

        /* RequestOptions is not null */
        requestOptions?.let { requestOptions: RequestOptions ->

            /* Add header params */
            connection.setHeaders(requestOptions.getHeader())

            /* Add post param */
            connection.setPostBody(requestOptions.getPostBody())

            connection.outputStream.close()
        }

        /* Request API */
        connection.request()

        /* Extract response */
        var inputStream: InputStreamReader? = null
        try {
            inputStream = InputStreamReader(connection.inputStream)
            val response = inputStream.response()
            return@async RawData(response, true)
        } catch (e: IOException) {
            println("IOException error: " + e.message)
            return@async RawData(e.message, false)
        } catch (e: Exception) {
            println("Exception errors: " + e.message)
            return@async RawData(e.message, false)
        } finally {
            inputStream?.close()
        }
    }

    override fun provideConnection(endpoint: String): HttpsURLConnection = URL(endpoint).openConnection() as HttpsURLConnection

    override fun HttpsURLConnection.setup() {
        this.apply(HttpConfiguration.default())
    }

    override fun HttpsURLConnection.setHeaders(headers: Map<String, String>) {
        headers.entries.forEach { (k, v) -> this.setRequestProperty(k, v) }
    }

    override fun HttpsURLConnection.setPostBody(body: String) {
        DataOutputStream(this.outputStream).apply {
            writeBytes(body)
            flush()
            close()
        }
    }

    override fun HttpsURLConnection.request() = this.connect()

    override fun InputStreamReader.response(): String {
        val result = this.stringify()
        this.close()
        return result ?: ""
    }

    private fun InputStreamReader?.stringify(): String? {
        if (this == null) return null

        val r = BufferedReader(this)

        // Add each line of http response buffered to StringBuilder
        val total = StringBuilder().apply {
            r.readLines().forEach { append(it) }
        }

        r.close()

        return String(total)
    }
}
