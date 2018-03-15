package co.omisego.omisego.exception

/*
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 4/12/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import org.amshove.kluent.shouldEqual
import org.junit.Test

class OMGServerExceptionTest {
    @Test
    fun `When response code is 404 the message should be 404 - Endpoint not found`() {
        val omgException = OMGServerException(404)
        omgException.message shouldEqual  "404 - Endpoint not found"
    }

    @Test
    fun `When response code is 500 the message should be 500 - Internal server error`() {
        val omgException = OMGServerException(500)
        omgException.message shouldEqual  "500 - Internal server error"
    }
}