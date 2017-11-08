package co.omisego.androidsdk.utils

import co.omisego.androidsdk.models.*
import org.json.JSONArray
import org.json.JSONObject


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/3/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

object ParseStrategy {
    val RESPONSE: (String) -> Response = {
        val jsonObject = JSONObject(it)
        Response(jsonObject.getString("version"), jsonObject.getBoolean("success"), JSONObject(it))
    }

    val API_ERROR: (String) -> ApiError = {
        val jsonObject = JSONObject(it)
        val errorObject = jsonObject.getJSONObject("data")
        ApiError(
                errorObject.getString("code"),
                errorObject.getString("description")
        )
    }

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

    val LIST_BALANCES: (String) -> List<Balance> = {
        val jsonObject = JSONObject(it)
        val data = jsonObject.getJSONObject("data").getJSONArray("data")
        val listBalances = mutableListOf<Balance>()
        for (index in 0 until data.length()) {
            val balance = data.getJSONObject(index)
            val mint = balance.getJSONObject("minted_token")
            val mintedToken = MintedToken(
                    mint.getString("symbol"),
                    mint.getString("name"),
                    mint.getDouble("subunit_to_unit")
            )
            val balanceObject = Balance(
                    balance.getString("address"),
                    balance.getDouble("amount"),
                    mintedToken
            )
            listBalances.add(balanceObject)
        }

        listBalances.toList()
    }

    val SETTING: (String) -> Setting = {
        val jsonObject = JSONObject(it)
        val data = jsonObject.getJSONObject("data").getJSONArray("minted_tokens")
        val listMintedTokens = (0 until data.length()).map {
            MintedToken(
                    data.getJSONObject(it).getString("symbol"),
                    data.getJSONObject(it).getString("name"),
                    data.getJSONObject(it).getDouble("subunit_to_unit")
            )
        }

        Setting(listMintedTokens)
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
