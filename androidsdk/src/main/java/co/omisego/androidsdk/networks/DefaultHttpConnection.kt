package co.omisego.androidsdk.networks

import co.omisego.androidsdk.exceptions.OmiseGOServerException
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/17/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class DefaultHttpConnection(baseURL: String) : HttpConnection {

    private val baseURL: String = baseURL
    private var httpsURLConnection: HttpsURLConnection? = null

    override fun setup(endpoint: String) {
        httpsURLConnection = URL(baseURL + endpoint).openConnection() as HttpsURLConnection
        httpsURLConnection?.apply(HttpConfiguration.default())
    }

    override fun setHeaders(headers: Map<String, String>) {
        headers.entries.forEach { (k, v) -> this.httpsURLConnection?.setRequestProperty(k, v) }
    }

    override fun setPostBody(body: String) {
        DataOutputStream(this.httpsURLConnection?.outputStream).apply {
            writeBytes(body)
            flush()
            close()
        }
    }

    override fun closeOutputStream() {
        this.httpsURLConnection?.outputStream?.close()
    }

    override fun request() {
        this.httpsURLConnection?.connect()
    }

    override fun response(): String {
        val responseCode = this.httpsURLConnection?.responseCode
        when (responseCode) {
            HttpsURLConnection.HTTP_INTERNAL_ERROR,
            HttpsURLConnection.HTTP_NOT_FOUND -> {
                val exception = OmiseGOServerException(responseCode)
                throw exception
            }
            else -> {
                val result = InputStreamReader(this.httpsURLConnection?.inputStream).stringify()
                this.httpsURLConnection?.inputStream?.close()
                return result ?: ""
            }
        }
    }

    override fun closeInputStream() {
        try {
            this.httpsURLConnection?.inputStream?.close()
        } catch (e: IOException) {
            // It's already closed
        }
    }

    override fun InputStreamReader?.stringify(): String? {
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