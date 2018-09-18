package co.omisego.omisego.live.auth

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.BaseAuthTest
import co.omisego.omisego.model.params.TokenListParams
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeGreaterThan
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class ListTokenLiveTest : BaseAuthTest() {

    @Test
    fun `list tokens should be returned successfully`() {
        val response = client.getTokens(
            TokenListParams.create(
                searchTerm = null
            )
        ).execute()

        response.isSuccessful shouldBe true
        response.body()?.data?.pagination?.perPage shouldBe 10
        response.body()?.data?.pagination?.currentPage shouldBe 1
        response.body()?.data?.data?.size!! shouldBeGreaterThan 0
    }
}
