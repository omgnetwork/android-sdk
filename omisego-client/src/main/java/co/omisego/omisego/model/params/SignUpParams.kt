package co.omisego.omisego.model.params

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 13/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * Represents a structure used to signup a new user.
 *
 * @param email The email to use for signup.
 * @param password The password to use for signup.
 * @param passwordConfirmation The password confirmation that should match the password.
 * @param successUrl An optional success URL to redirect the user to upon successful verification.
 */
data class SignUpParams(
    private val email: String,
    private val password: String,
    private val passwordConfirmation: String,
    private val successUrl: String? = null
)
