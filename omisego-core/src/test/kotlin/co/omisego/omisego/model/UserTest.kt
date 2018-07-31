package co.omisego.omisego.model

/*
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 11/5/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.support.test.runner.AndroidJUnit4
import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.client.utils.DateConverter
import co.omisego.omisego.client.utils.validateParcel
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [23])
class UserTest : GsonDelegator() {
    private val userFile by ResourceFile("user.json", "object")
    private val dateConverter by lazy { DateConverter() }
    private val user: User by lazy { gson.fromJson(userFile.readText(), User::class.java) }

    @Test
    fun `user should be parcelled correctly`() {
        user.validateParcel().apply {
            this shouldEqual user
            this shouldNotBe user
        }
    }

    @Test
    fun `user should be parsed correctly`() {
        with(user) {
            id shouldEqual "cec34607-0761-4a59-8357-18963e42a1aa"
            providerUserId shouldEqual "wijf-fbancomw-dqwjudb"
            username shouldEqual "john.doe@example.com"
            socketTopic shouldBeInstanceOf SocketTopic::class.java
            createdAt shouldEqual dateConverter.fromString("2018-01-01T00:00:00Z")
            updatedAt shouldEqual dateConverter.fromString("2018-01-01T00:00:00Z")
            metadata shouldEqual mapOf<String, Any>()
            encryptedMetadata shouldEqual mapOf<String, Any>()
        }
    }
}
