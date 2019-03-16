package co.omisego.omisego.model.params

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 15/3/2019 AD.
 * Copyright © 2019 OmiseGO. All rights reserved.
 */

/**
 * Represents a structure used to update the password of a user following a reset
 *
 * @param email The email of the user (obtained from the params in the link sent to the email of the user)
 * @param token The unique reset password token obtained from the params in the link sent to the email of the user
 * @param password The updated password
 * @param passwordConfirmation The password confirmation that should match the updated password
 */
data class UpdatePasswordParams(
    val email: String,
    val token: String?,
    val password: String,
    val passwordConfirmation: String
)
