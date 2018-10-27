package co.omisego.omisego.admin.network.ewallet

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.AdminConfiguration
import co.omisego.omisego.network.ewallet.EWalletAdmin
import co.omisego.omisego.network.ewallet.EWalletAdminAPI
import co.omisego.omisego.network.interceptor.HeaderInterceptor
import okhttp3.OkHttpClient
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class EWalletAdminTest {
    private val eWalletAdmin = EWalletAdmin.Builder {
        this.clientConfiguration = AdminConfiguration("http://localhost")
    }.build()

    @Test
    fun `build should adds the omisego header correctly`() {
        eWalletAdmin.header shouldBeInstanceOf HeaderInterceptor::class
        eWalletAdmin.eWalletAPI shouldBeInstanceOf EWalletAdminAPI::class.java
        eWalletAdmin.retrofit shouldBeInstanceOf Retrofit::class.java
        eWalletAdmin.client shouldBeInstanceOf OkHttpClient::class.java
    }
}
