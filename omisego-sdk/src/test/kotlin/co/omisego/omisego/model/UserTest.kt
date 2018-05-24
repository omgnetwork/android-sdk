package co.omisego.omisego.model

/*
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 11/5/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketTopic
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import java.util.Date

class UserTest {

    lateinit var user: User
    private lateinit var createdAt: Date
    private lateinit var updatedAt: Date

    @Before
    fun setUp() {
        createdAt = Date()
        updatedAt = Date()
        user = User("pizza-1234", "2017-11-5", "OmiseGO", mapOf(), mapOf(), SocketTopic("test"), createdAt, updatedAt)
    }

    @Test
    fun `user should be correct`() {
        "pizza-1234" shouldEqual user.id
        "2017-11-5" shouldEqual user.providerUserId
        "OmiseGO" shouldEqual user.username
        createdAt shouldEqual user.createdAt
        updatedAt shouldEqual user.updatedAt
    }
}