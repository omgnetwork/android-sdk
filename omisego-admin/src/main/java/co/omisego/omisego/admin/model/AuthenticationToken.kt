package co.omisego.omisego.admin.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/8/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.Account
import co.omisego.omisego.model.User

data class AuthenticationToken(
    val authenticationToken: String,
    val userId: String,
    val user: User,
    val accountId: String,
    val account: Account,
    val masterAdmin: Boolean,
    val role: String
)
