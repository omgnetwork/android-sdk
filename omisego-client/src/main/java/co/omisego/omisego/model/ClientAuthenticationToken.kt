package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/8/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

data class ClientAuthenticationToken(
    override val authenticationToken: String,
    override val userId: String,
    override val user: User
) : AuthenticationToken(authenticationToken, userId, user)
