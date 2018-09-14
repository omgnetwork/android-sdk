package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/8/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

data class AdminAuthenticationToken(
    override val authenticationToken: String,
    override val userId: String,
    override val user: User,
    val accountId: String,
    val account: Account,
    val masterAdmin: Boolean,
    val role: String
) : AuthenticationToken(authenticationToken, userId, user)
