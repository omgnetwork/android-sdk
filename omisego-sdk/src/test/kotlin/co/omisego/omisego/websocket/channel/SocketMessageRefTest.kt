package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 15/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import org.amshove.kluent.shouldEqualTo
import org.junit.Before
import org.junit.Test

class SocketMessageRefTest {
    private lateinit var socketMessageRef: SocketMessageRef

    @Before
    fun setup() {
        socketMessageRef = SocketMessageRef(scheme = "test")
    }

    @Test
    fun `value should be increased for every access`() {
        socketMessageRef.value shouldEqualTo "test:1"
        socketMessageRef.value shouldEqualTo "test:2"
        socketMessageRef.value shouldEqualTo "test:3"
    }

    @Test
    fun `the scheme should be prepended to the value correctly`() {
        socketMessageRef.value shouldEqualTo "test:1"
        socketMessageRef.value shouldEqualTo "test:2"
        socketMessageRef.value shouldEqualTo "test:3"
    }
}
