package co.omisego.omisego.network.interceptor

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

interface HeaderHandler {
    fun setHeader(authenticationToken: String)
}