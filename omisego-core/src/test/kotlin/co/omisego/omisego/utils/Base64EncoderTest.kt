package co.omisego.omisego.utils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 17/8/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class Base64EncoderTest {
    val encoder: Base64Encoder by lazy { Base64Encoder() }

    @Test
    fun `encode should work properly`() {
        encoder.encode("1234", "5678") shouldEqualTo "MTIzNDo1Njc4"
        encoder.encode("usr_01cj68fvbka7hd4j5fpdg9hk6s", "Fo29OCXnV59_zecQdoSFzl8f4zavUHURQ1ObNDIPuK8") shouldEqualTo "dXNyXzAxY2o2OGZ2YmthN2hkNGo1ZnBkZzloazZzOkZvMjlPQ1huVjU5X3plY1Fkb1NGemw4ZjR6YXZVSFVSUTFPYk5ESVB1Szg="
    }
}
