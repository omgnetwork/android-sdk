package co.omisego.omisego.exception

import org.amshove.kluent.shouldEqual
import org.junit.Test

/**
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 4/12/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class OmiseGOServerExceptionTest {
    @Test
    fun `When response code is 404 the message should be 404 - Endpoint not found`() {
        val omgException = OmiseGOServerException(404)
        omgException.message shouldEqual  "404 - Endpoint not found"
    }

    @Test
    fun `When response code is 500 the message should be 500 - Internal server error`() {
        val omgException = OmiseGOServerException(500)
        omgException.message shouldEqual  "500 - Internal server error"
    }
}