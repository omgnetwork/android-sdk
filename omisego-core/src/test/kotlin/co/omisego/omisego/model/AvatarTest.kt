package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import org.amshove.kluent.shouldEqual
import org.junit.Test

class AvatarTest : GsonDelegator() {
    private val avatarFile by ResourceFile("avatar.json", "object")
    private val avatar by lazy { gson.fromJson(avatarFile.readText(), Avatar::class.java) }

    @Test
    fun `avatar should be parsed correctly`() {
        with(avatar) {
            original shouldEqual "original_url"
            large shouldEqual "large_url"
            small shouldEqual "small_url"
            thumb shouldEqual "thumb_url"
        }
    }
}
