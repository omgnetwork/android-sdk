package co.omisego.omisego.model

/*
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 11/5/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.support.test.runner.AndroidJUnit4
import co.omisego.omisego.extension.bd
import co.omisego.omisego.utils.validateParcel
import co.omisego.omisego.model.socket.SocketTopic
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.Date

@RunWith(AndroidJUnit4::class)
@Config(sdk = [23])
class UserTest {
    lateinit var user: User
    private lateinit var createdAt: Date
    private lateinit var updatedAt: Date

    @Before
    fun setUp() {
        createdAt = Date()
        updatedAt = Date()
        user = User(
            "pizza-1234",
            "2017-11-5",
            "OmiseGO",
            mapOf("One" to 1, "ThreeBigDecimal" to 3.bd),
            mapOf("Word" to "Cryptocurrency", "Boolean" to false), "test",
            createdAt,
            updatedAt
        )
    }

    @Test
    fun `User should be correctly initialized`() {
        "pizza-1234" shouldEqual user.id
        "2017-11-5" shouldEqual user.providerUserId
        "OmiseGO" shouldEqual user.username
        createdAt shouldEqual user.createdAt
        updatedAt shouldEqual user.updatedAt
    }

    @Test
    fun `User should be parcelled correctly`() {
        user.validateParcel().apply {
            this shouldEqual user
            this shouldNotBe user
        }
    }
}