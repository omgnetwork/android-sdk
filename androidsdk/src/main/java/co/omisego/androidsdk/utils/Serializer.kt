package co.omisego.androidsdk.utils

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/3/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

// Serializer
class Serializer<out T>(private val serializeStrategy: (String) -> T) {
    fun serialize(s: String) = serializeStrategy.invoke(s)
}
