package co.omisego.omisego.utils

import org.json.JSONObject
import java.io.File

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

open class ResourceFileLoader {
    fun loadSecretFile(filename: String): JSONObject {
        val resourceUserURL = javaClass.classLoader?.getResource(filename) // This is invisible because it's stored in local ("secret").

        return try {
            val secretFile = File(resourceUserURL?.path)
            JSONObject(secretFile.readText())
        } catch (e: IllegalStateException) {
            throw IllegalStateException("Please create the file $filename. See the file secret.example.json for the reference.")
        }
    }
}
