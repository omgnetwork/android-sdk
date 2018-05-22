package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 22/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.support.test.runner.AndroidJUnit4
import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.utils.validateParcel
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [23])
class APIErrorTest {
    private lateinit var apiError: APIError

    @Before
    fun setup() {
        apiError = APIError(ErrorCode.TRANSACTION_REQUEST_NOT_FOUND, "TransactionRequest has not found")
    }

    @Test
    fun `APIError should be parcelized correctly`() {
        apiError.validateParcel().apply {
            this shouldNotBe apiError
            this shouldEqual apiError
        }
    }
}
