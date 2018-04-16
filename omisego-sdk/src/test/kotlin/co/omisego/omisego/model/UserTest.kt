package co.omisego.omisego.model

/*
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 11/5/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test

class UserTest {

    lateinit var user: User

    @Before
    fun setUp() {
        user = User("pizza-1234", "2017-11-5", "OmiseGO", hashMapOf())
    }

    @Test
    fun `user should be correct`() {
        "pizza-1234" shouldEqual user.id
        "2017-11-5" shouldEqual user.providerUserId
        "OmiseGO" shouldEqual user.username
    }
}