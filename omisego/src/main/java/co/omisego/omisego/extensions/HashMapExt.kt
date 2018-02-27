package co.omisego.omisego.extensions


/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/5/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

/**
 * Extension function of HashMap<String, Any> used for get value as a HashMap<String, Any>
 *
 * @param key the key whose associated value is to be returned
 */
fun HashMap<String, Any>.getAsHashMap(key: String): HashMap<String, Any> {
    if (this[key] != null && this[key] is HashMap<*, *>) {
        return this[key] as HashMap<String, Any>
    }
    throw ClassCastException("Cannot convert Any to HashMap<String, Any>")
}

/**
 * Extension function of HashMap<String, Any> used for get value as a List<HashMap<String, Any>>
 *
 * @param key the key whose associated value is to be returned
 */
fun HashMap<String, Any>.getAsArray(key: String): List<HashMap<String, Any>> {
    if (this[key] != null && this[key] is List<*>) {
        return this[key] as List<HashMap<String, Any>>
    }
    throw ClassCastException("Cannot convert Any to List<HashMap<String, Any>>")
}