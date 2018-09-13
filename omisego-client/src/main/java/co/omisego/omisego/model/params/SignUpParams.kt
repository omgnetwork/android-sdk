package co.omisego.omisego.model.params

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 13/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

data class SignUpParams(
    private val email: String,
    private val password: String,
    private val passwordConfirmation: String,
    private val verificationUrl: String? = null,
    private val successUrl: String? = null
)
