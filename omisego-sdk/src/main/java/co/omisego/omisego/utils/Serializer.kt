package co.omisego.omisego.utils

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/3/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

/**
 * A serializer class that has responsibility for converting a string of [JSONObject] to a specific class.
 *
 * @param serializeStrategy A strategy used for converting a string to a class
 */
class Serializer<out T>(private val serializeStrategy: (String) -> T) {

    /**
     * Serializes the data using the strategy passed to the class.
     */
    fun serialize(s: String) = serializeStrategy.invoke(s)
}
