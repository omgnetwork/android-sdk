package co.omisego.omisego.model

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/13/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * Used to represent whether an API request was successful or encountered an error.
 *
 * @param version API version
 * @param success *true* if the request was successful, *false* if the request was fail.
 * @param data The serialization of the provided associated Data
 */
data class OMGResponse<out T>(val version: String, val success: Boolean, val data: T)
