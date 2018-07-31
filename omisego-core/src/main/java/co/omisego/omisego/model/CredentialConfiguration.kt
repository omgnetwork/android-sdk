package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 23/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

interface CredentialConfiguration {
    val baseURL: String
    val authenticationToken: String
    val userId: String?
    val apiKey: String?
    val authScheme: String
}
