package co.omisego.androidsdk.networks

import co.omisego.androidsdk.exceptions.OmiseGOServerException
import co.omisego.androidsdk.models.RawData
import co.omisego.androidsdk.utils.ErrorCode
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.io.IOException


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/8/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class Requestor(private val connection: HttpConnection) {
    fun asyncRequest(endpoint: String, requestOptions: RequestOptions?) = async(CommonPool) {
        /* Setup https connection */
        connection.setup(endpoint)

        /* When requestOptions is not null */
        requestOptions?.let { requestOptions: RequestOptions ->

            /* Add header params */
            connection.setHeaders(requestOptions.getHeader())

            /* Add post param */
            connection.setPostBody(requestOptions.getPostBody())

            /* Close output stream because we're already finished writing connection data. */
            connection.closeOutputStream()
        }

        /* Request API */
        connection.request()

        /* Extract response */
        try {
            val response = connection.response()
            return@async RawData(response, true)
        } catch (e: IOException) {
            return@async RawData(e.message, false, ErrorCode.SDK_NETWORK_ERROR)
        } catch (e: OmiseGOServerException) {
            return@async RawData(e.message, false, ErrorCode.SERVER_INTERNAL_SERVER_ERROR)
        } catch (e: Exception) {
            return@async RawData(e.message, false, ErrorCode.SDK_UNKNOWN_ERROR)
        } finally {
            /* Close input stream because we're already finished read the api response. */
            connection.closeInputStream()
        }
    }
}
