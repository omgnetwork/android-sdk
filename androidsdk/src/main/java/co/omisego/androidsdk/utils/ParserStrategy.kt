package co.omisego.androidsdk.utils

import co.omisego.androidsdk.models.User
import org.json.JSONArray
import org.json.JSONObject


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/3/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

object ParseStrategy {
    val USER: (String) -> User = {
        val jsonObject = JSONObject(it)
        val data = jsonObject.getJSONObject("data")

        User(
                data.getString("id"),
                data.getString("provider_user_id"),
                data.getString("username"),
                parseJSONObject(data.getJSONObject("metadata"))
        )
    }

    private fun parseJSONObject(json: JSONObject): HashMap<String, Any> {
        val map = hashMapOf<String, Any>()
        for (key in json.keys()) {
            val value = json[key]
            when (value) {
                is JSONObject -> map.put(key, parseJSONObject(value))
                is JSONArray -> map.put(key, parseJSONArray(value))
                else -> map.put(key, value)
            }
        }
        return map
    }

    private fun parseJSONArray(json: JSONArray): List<HashMap<String, Any>> {
        val list: MutableList<HashMap<String, Any>> = mutableListOf()
        for (i in 0 until json.length()) {
            val keys = json.getJSONObject(i).keys()
            val hashMap = hashMapOf<String, Any>()
            for (key in keys) {
                hashMap.put(key, json.getJSONObject(i).get(key))
            }
            list.add(hashMap)
        }
        return list
    }
}
