package co.omisego.androidsdk.models


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/3/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

data class User(val id: String,
                val providerUserId: String,
                val username: String,
                val metaData: HashMap<String, Any>?)
