package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/3/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * Represents the current user
 *
 * @param id The unique identifier on the wallet server side.
 * @param providerUserId The user identifier on the provider server side.
 * @param username The user's username, it can be an email or any name describing this user
 * @param metaData Any additional metadata that need to be stored as a [HashMap]
 *
 */
data class User(val id: String,
                val providerUserId: String,
                val username: String,
                val metaData: Map<String, Any>)
