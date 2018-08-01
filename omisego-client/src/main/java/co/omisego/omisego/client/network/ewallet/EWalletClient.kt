package co.omisego.omisego.client.network.ewallet

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.network.BaseClient

class EWalletClient : BaseClient() {
    internal lateinit var eWalletAPI: EWalletAPI

    /**
     * Build a new [EWalletClient].
     * Set [apiKey], [authenticationToken] and [baseUrl] are required before calling [Builder.build].
     * Set [debug] true for printing a log
     *
     * @receiver A [Builder]'s methods.
     */
    class Builder(init: BaseClient.Builder.() -> Unit) : BaseClient.Builder(init) {
        override fun build(): EWalletClient {
            with(super.build()) {
                return EWalletClient().also {
                    it.header = header
                    it.client = client
                    it.retrofit = retrofit
                    it.eWalletAPI = retrofit.create(EWalletAPI::class.java)
                }
            }
        }
    }
}