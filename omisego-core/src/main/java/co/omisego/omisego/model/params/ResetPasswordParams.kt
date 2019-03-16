package co.omisego.omisego.model.params

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 15/3/2019 AD.
 * Copyright © 2019 OmiseGO. All rights reserved.
 */

/**
 * Represents a structure used to request a password reset for the user.
 *
 * @param email The email of the user
 * @param redirectUrl The URL where the user will be taken when clicking the link in the email.
 */
data class ResetPasswordParams(
    val email: String,
    val redirectUrl: String
)
