package co.omisego.androidsdk.utils

import co.omisego.androidsdk.extensions.getAsArray
import co.omisego.androidsdk.extensions.getAsHashMap
import co.omisego.androidsdk.models.*
import co.omisego.androidsdk.models.ApiError
import co.omisego.androidsdk.models.Balance
import co.omisego.androidsdk.models.Setting
import co.omisego.androidsdk.models.User
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.io.File


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/3/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class ParseStrategyTest {
    private lateinit var userFile: File
    private lateinit var listBalancesFile: File
    private lateinit var settingFile: File
    private lateinit var errorFile: File

    @Rule
    @JvmField
    val expectedEx = ExpectedException.none()!!


    @Before
    fun setup() {
        val resourceUserURL = javaClass.classLoader.getResource("user.me-post.json")
        userFile = File(resourceUserURL.path)
        userFile shouldNotBe null

        val resourceListBalancesURL = javaClass.classLoader.getResource("me.list_balances-post.json")
        listBalancesFile = File(resourceListBalancesURL.path)
        listBalancesFile shouldNotBe null

        val resourceSettingURL = javaClass.classLoader.getResource("me.get_settings-post.json")
        settingFile = File(resourceSettingURL.path)
        settingFile shouldNotBe null

        val resourceErrorURL = javaClass.classLoader.getResource("dummy.failure-post.json")
        errorFile = File(resourceErrorURL.path)
        errorFile shouldNotBe null
    }

    @Test
    fun `parse user correctly should success`() {
        // Arrange
        val userJson = userFile.readText()

        // Action
        val user: User = Serializer(ParseStrategy.USER).serialize(userJson)

        // Assert
        "cec34607-0761-4a59-8357-18963e42a1aa" shouldEqual user.id
        "wijf-fbancomw-dqwjudb" shouldEqual user.providerUserId
        "john.doe@example.com" shouldEqual user.username
        "John" shouldEqual user.metaData["first_name"]
        "my_value" shouldEqual user.metaData.getAsHashMap("object")["my_key"]
        "my_nested_value" shouldEqual user.metaData.getAsHashMap("object").getAsHashMap("my_nested_object")["my_nested_key"]
        "value_3" shouldEqual user.metaData.getAsArray("array")[1]["key_1"]
        "value_2" shouldEqual user.metaData.getAsArray("array")[0]["key_2"]
    }

    @Test
    fun `parse user with non-existing key should be null`() {
        // Arrange
        val userJson = userFile.readText()

        // Action
        val user: User = Serializer(ParseStrategy.USER).serialize(userJson)

        // Assert
        user.metaData["abcd"] shouldEqual null
    }

    @Test
    @Throws(ClassCastException::class)
    fun `parse user value as HashMap should throw ClassCastException`() {
        // Arrange
        val userJson = userFile.readText()

        // Action
        val user: User = Serializer(ParseStrategy.USER).serialize(userJson)

        // Assert
        expectedEx.expect(ClassCastException::class.java)
        expectedEx.expectMessage("Cannot convert Any to HashMap<String, Any>")

        "test" shouldEqual user.metaData.getAsHashMap("first_name")
    }

    @Test
    @Throws(ClassCastException::class)
    fun `parse user HashMap as a List should throw ClassCastException`() {
        // Arrange
        val userJson = userFile.readText()

        // Action
        val user: User = Serializer(ParseStrategy.USER).serialize(userJson)

        // Assert
        expectedEx.expect(ClassCastException::class.java)
        expectedEx.expectMessage("Cannot convert Any to List<HashMap<String, Any>>")

        "test" shouldEqual user.metaData.getAsArray("object")
    }

    @Test
    fun `parse list balances correctly should success`() {
        // Arrange
        val listBalanceJson = listBalancesFile.readText()

        // Action
        val listBalances: List<Balance> = Serializer(ParseStrategy.LIST_BALANCES).serialize(listBalanceJson)

        // Assert
        listBalances[0].mintedToken.symbol shouldEqual "MNT"
        listBalances[0].mintedToken.name shouldEqual "Mint"
        listBalances[0].mintedToken.subUnitToUnit shouldEqual 100000.0
        listBalances[0].address shouldEqual "my_mnt_address"
        listBalances[0].amount shouldEqual 10.0

        listBalances[1].mintedToken.symbol shouldEqual "OMG"
        listBalances[1].mintedToken.name shouldEqual "OmiseGO"
        listBalances[1].mintedToken.subUnitToUnit shouldEqual 100000000.0
        listBalances[1].address shouldEqual "my_omg_address"
        listBalances[1].amount shouldEqual 52.0
    }

    @Test
    fun `parse setting correctly should success`() {
        // Arrange
        val settingJson = settingFile.readText()

        // Action
        val setting: Setting = Serializer(ParseStrategy.SETTING).serialize(settingJson)

        // Assert
        setting.mintedTokens.size shouldEqual 2

        setting.mintedTokens[0].symbol shouldEqual "MNT"
        setting.mintedTokens[0].name shouldEqual "Mint"
        setting.mintedTokens[0].subUnitToUnit shouldEqual 100000.0

        setting.mintedTokens[1].symbol shouldEqual "OMG"
        setting.mintedTokens[1].name shouldEqual "OmiseGO"
        setting.mintedTokens[1].subUnitToUnit shouldEqual 100000000.0
    }


    @Test
    fun `parse error response`() {
        // Arrange
        val errorJson = errorFile.readText()

        // Action
        val error: ApiError = Serializer(ParseStrategy.API_ERROR).serialize(errorJson)

        // Assert
        error.code shouldEqual "error_code"
        error.description shouldEqual "error_message"
    }

    @Test
    fun `parse response to get version, success, and setting object successfully`() {
        // Arrange
        val settingJson = settingFile.readText()

        // Action
        val responseSetting: Response = Serializer(ParseStrategy.RESPONSE).serialize(settingJson)

        // Assert
        responseSetting.version shouldEqual "1"
        responseSetting.success shouldEqual true

        /* Test parse data object */
        // Arrange
        val responseSettingJson = responseSetting.data.toString()

        val originalSettingJson = settingFile.readText()

        // Action
        val setting: Setting = Serializer(ParseStrategy.SETTING).serialize(responseSettingJson)

        val expectedSetting = Serializer(ParseStrategy.SETTING).serialize(originalSettingJson)

        // Assert
        setting shouldEqual expectedSetting
    }

    @Test
    fun `parse response to get version, success, and list balance object successfully`() {
        // Arrange
        val listBalancesJson = listBalancesFile.readText()

        // Action
        val responseListBalance: Response = Serializer(ParseStrategy.RESPONSE).serialize(listBalancesJson)

        // Assert
        responseListBalance.version shouldEqual "1"
        responseListBalance.success shouldEqual true

        /* Test parse list balance object */

        // Arrange
        val responseListBalanceJson = responseListBalance.data.toString()

        // Action
        val originalListBalanceJson = listBalancesFile.readText()
        val listBalance: List<Balance> = Serializer(ParseStrategy.LIST_BALANCES).serialize(responseListBalanceJson)
        val expectedListBalance = Serializer(ParseStrategy.LIST_BALANCES).serialize(originalListBalanceJson)

        // Assert
        listBalance shouldEqual expectedListBalance
    }

    @Test
    fun `parse response to get version, success, and user object successfully`() {

        // Arrange
        val userJson = userFile.readText()

        // Action
        val responseUser: Response = Serializer(ParseStrategy.RESPONSE).serialize(userJson)

        // Assert
        responseUser.version shouldEqual "1"
        responseUser.success shouldEqual true

        /* Test parse user object */

        // Arrange
        val responseUserJson = responseUser.data.toString()

        // Action
        val originalUserJson = userFile.readText()
        val user: User = Serializer(ParseStrategy.USER).serialize(responseUserJson)
        val expectedUser = Serializer(ParseStrategy.USER).serialize(originalUserJson)

        // Assert
        user shouldEqual expectedUser
    }
}
