package co.omisego.androidsdk.extensions


/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/5/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */


fun HashMap<String, Any>.getAsHashMap(key: String): HashMap<String, Any> {
    if (this[key] != null && this[key] is HashMap<*, *>) {
        return this[key] as HashMap<String, Any>
    }
    throw ClassCastException("Cannot convert Any to HashMap<String, Any>")
}

fun HashMap<String, Any>.getAsArray(key: String): List<HashMap<String, Any>> {
    if (this[key] != null && this[key] is List<*>) {
        return this[key] as List<HashMap<String, Any>>
    }
    throw ClassCastException("Cannot convert Any to List<HashMap<String, Any>>")
}