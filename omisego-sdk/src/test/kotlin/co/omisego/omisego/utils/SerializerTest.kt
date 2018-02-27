package co.omisego.omisego.utils

import org.amshove.kluent.shouldEqual
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

/**
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 11/5/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class SerializerTest {

    data class MockObject(val name: String, val amount: Double)

    private lateinit var testJsonString: String
    private lateinit var testSerializerStrategy: (String) -> MockObject

    @Before
    fun setUp() {
        testJsonString =
                "{\n" +
                        "  \"name\": \"John Doe\",\n" +
                        "  \"amount\": 299.9\n" +
                        "}"

        testSerializerStrategy = {
            val jsonObject = JSONObject(it)
            with(jsonObject) {
                MockObject(getString("name"), getDouble("amount"))
            }
        }
    }

    @Test
    fun serialize() {
        // Arrange
        val serializer: Serializer<MockObject> = Serializer(testSerializerStrategy)

        // Action
        val mockObject = serializer.serialize(testJsonString)

        // Assert
        "John Doe" shouldEqual mockObject.name
        299.9 shouldEqual mockObject.amount
    }

}