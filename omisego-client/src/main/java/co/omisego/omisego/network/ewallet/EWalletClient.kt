package co.omisego.omisego.network.ewallet

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Exceptions
import co.omisego.omisego.network.BaseClient
import co.omisego.omisego.utils.Base64Encoder

class EWalletClient : BaseClient() {
    internal lateinit var eWalletAPI: EWalletClientAPI

    /**
     * A builder user for build an [EWalletClient] instance.
     * clientConfiguration is required before calling [Builder.build].
     * Set [debug] true for printing a log
     *
     * @receiver A [Builder]'s methods.
     */
    class Builder(init: BaseClient.Builder.() -> Unit) : BaseClient.Builder(init) {

        override lateinit var authenticationHeader: AuthenticationHeader

        override fun build(): EWalletClient {
            /* Verify if the [CredentialConfiguration] is initialized correctly */
            val config = clientConfiguration ?: throw IllegalStateException(Exceptions.MSG_NULL_CLIENT_CONFIGURATION)

            authenticationHeader = ClientAuthenticationHeader(config.apiKey!!, Base64Encoder())

            with(super.build()) {
                return EWalletClient().also {
                    it.header = header
                    it.client = client
                    it.retrofit = retrofit
                    it.eWalletAPI = retrofit.create(EWalletClientAPI::class.java)
                }
            }
        }
    }
}
