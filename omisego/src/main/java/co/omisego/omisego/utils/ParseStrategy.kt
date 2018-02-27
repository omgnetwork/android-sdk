package co.omisego.omisego.utils

import co.omisego.omisego.extensions.bd
import co.omisego.omisego.models.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/3/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

object ParseStrategy {
    val GENERAL: (String) -> General = {
        val jsonObject = JSONObject(it)
        General(jsonObject.getString("version"), jsonObject.getBoolean("success"), JSONObject(it))
    }

    val API_ERROR: (String) -> ApiError = {
        val jsonObject = JSONObject(it)
        val errorObject = jsonObject.getJSONObject("data")
        ApiError(
                ErrorCode.from(errorObject.getString("code")),
                errorObject.getString("description")
        )
    }

    val USER: (String) -> User = {
        val jsonObject = JSONObject(it)
        val data = jsonObject.getJSONObject("data")

        val metadata: JSONObject? = try {
            data.getJSONObject("metadata")
        } catch (e: JSONException) {
            null
        }

        User(
                data.getString("id"),
                data.getString("provider_user_id"),
                data.getString("username"),
                parseJSONObject(metadata)
        )
    }

    val LIST_BALANCES: (String) -> List<Address> = {
        val jsonObject = JSONObject(it)
        val data = jsonObject.getJSONObject("data").getJSONArray("data")
        val listAddress = mutableListOf<Address>()
        for (index in 0 until data.length()) {
            val balances = data.getJSONObject(index).getJSONArray("balances")
            val listBalances = mutableListOf<Balance>()

            // Add balance to list
            for (balanceIndex in 0 until balances.length()) {
                val token = balances.getJSONObject(balanceIndex).getJSONObject("minted_token")
                val mintedToken = MintedToken(
                        token.getString("id"),
                        token.getString("symbol"),
                        token.getString("name"),
                        token.getDouble("subunit_to_unit").bd
                )

                val balance = Balance(balances.getJSONObject(balanceIndex).getDouble("amount").bd, mintedToken)
                listBalances.add(balance)
            }

            // Add address to the list
            val address = Address(data.getJSONObject(index).getString("address"), listBalances)
            listAddress.add(address)
        }

        listAddress.toList()
    }

    val SETTING: (String) -> Setting = {
        val jsonObject = JSONObject(it)
        val data = jsonObject.getJSONObject("data").getJSONArray("minted_tokens")
        val listMintedTokens = (0 until data.length()).map {
            MintedToken(
                    data.getJSONObject(it).getString("id"),
                    data.getJSONObject(it).getString("symbol"),
                    data.getJSONObject(it).getString("name"),
                    data.getJSONObject(it).getDouble("subunit_to_unit").bd
            )
        }

        Setting(listMintedTokens)
    }

    private fun parseJSONObject(json: JSONObject?): HashMap<String, Any>? {
        if (json == null) return null
        val map = hashMapOf<String, Any>()
        for (key in json.keys()) {
            val value = json[key]
            when (value) {
                is JSONObject -> map.put(key, parseJSONObject(value)!!)
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
