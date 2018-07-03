package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/6/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.testUtils.DateConverter
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class AccountTest : GsonDelegator() {
    private val accountFile by ResourceFile("account.json", "object")
    private val dateConverter by lazy { DateConverter() }

    @Test
    fun `account should be parsed correctly`() {
        val account = gson.fromJson<Account>(accountFile.readText(), Account::class.java)
        with(account) {
            id shouldEqualTo "acc_01CA2P8JQANS5ATY5GJ5ETMJCF"
            name shouldEqualTo "Account Name"
            parentId shouldEqualTo "acc_01CA26PKGE49AABZD6K6MSHN0Y"
            description shouldEqualTo "The account description"
            master shouldEqualTo false
            createdAt shouldEqual dateConverter.fromString("2018-01-01T00:00:00Z")
            updatedAt shouldEqual dateConverter.fromString("2018-01-01T10:00:00Z")
        }
    }
}
