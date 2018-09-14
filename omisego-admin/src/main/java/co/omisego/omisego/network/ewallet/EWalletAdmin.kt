package co.omisego.omisego.network.ewallet

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.network.BaseClient
import co.omisego.omisego.utils.Base64Encoder

class EWalletAdmin : BaseClient() {
    internal lateinit var eWalletAPI: EWalletAdminAPI

    /**
     * A builder user for build an [EWalletAdmin] instance.
     * clientConfiguration is required before calling [Builder.build].
     * Set [debug] true for printing a log
     *
     * @receiver A [Builder]'s methods.
     */
    class Builder(init: BaseClient.Builder.() -> Unit) : BaseClient.Builder(init) {

        override lateinit var authenticationHeader: AuthenticationHeader

        /**
         * Create an [EWalletAdmin] instance.
         */
        override fun build(): EWalletAdmin {
            authenticationHeader = AdminAuthenticationHeader(Base64Encoder())

            with(super.build()) {
                return EWalletAdmin().also {
                    it.header = header
                    it.client = client
                    it.retrofit = retrofit
                    it.eWalletAPI = retrofit.create(EWalletAdminAPI::class.java)
                }
            }
        }
    }
}
