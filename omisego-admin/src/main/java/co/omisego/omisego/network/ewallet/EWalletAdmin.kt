package co.omisego.omisego.network.ewallet

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.AdminConfiguration
import co.omisego.omisego.model.AuthenticationToken
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.network.BaseClient
import co.omisego.omisego.network.HeaderInterceptor
import co.omisego.omisego.utils.OMGEncryption
import okhttp3.Interceptor

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

        /**
         * Create an [EWalletAdmin] instance.
         */
        override fun build(): EWalletAdmin {
            with(super.build()) {
                this.client.networkInterceptors().add(provideAuthenticationTokenInterceptor(header))
                return EWalletAdmin().also {
                    it.header = header
                    it.client = client
                    it.retrofit = retrofit
                    it.eWalletAPI = retrofit.create(EWalletAdminAPI::class.java)
                }
            }
        }

        internal fun provideAuthenticationTokenInterceptor(header: HeaderInterceptor): Interceptor {
            return Interceptor { chain ->
                chain.proceed(chain.request()).also {
                    val body = it.body()
                    if (body is OMGResponse<*>) {
                        val data = body.data

                        /* If it is the login API response */
                        if (data is AuthenticationToken) {

                            /* Get unauthorized admin configuration */
                            val unauthorizedConfig = this@Builder.clientConfiguration as AdminConfiguration

                            /* Create authorized admin configuration */
                            val authorizedConfig = unauthorizedConfig.copy(
                                userId = data.userId,
                                authenticationToken = data.authenticationToken
                            )

                            /* Create new header */
                            val newHeader = OMGEncryption().createAuthorizationHeader(authorizedConfig)

                            /* Replace with authorized header */
                            header.setHeader(newHeader)
                        }
                    }
                }
            }
        }
    }
}
