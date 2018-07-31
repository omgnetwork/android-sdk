package co.omisego.omisego.extension

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 13/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.io.File

fun File.mockEnqueueWithHttpCode(mockWebServer: MockWebServer, responseCode: Int = 200) {
    mockWebServer.enqueue(MockResponse().apply {
        setBody(this@mockEnqueueWithHttpCode.readText())
        setResponseCode(responseCode)
    })
}
