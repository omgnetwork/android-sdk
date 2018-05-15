package co.omisego.omisego.websocket.channel

import org.amshove.kluent.shouldEqualTo
import org.junit.Before
import org.junit.Test

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 15/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

class SocketMessageRefTest {

    private lateinit var socketMessageRef: SocketMessageRef

    @Before
    fun setup(){
        socketMessageRef = SocketMessageRef()
    }

    @Test
    fun `value should be increased for every access`(){
        socketMessageRef.value shouldEqualTo "1"
        socketMessageRef.value shouldEqualTo "2"
        socketMessageRef.value shouldEqualTo "3"
    }
}
