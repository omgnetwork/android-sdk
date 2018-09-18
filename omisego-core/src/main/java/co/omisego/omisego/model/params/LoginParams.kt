package co.omisego.omisego.model.params

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/8/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * Represents a structure used to login an existing user.
 *
 * @param email The email of the user
 * @param password The password of the user
 */
data class LoginParams(
    val email: String,
    val password: String
)
