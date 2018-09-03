package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.Test

class MetadataTest : GsonDelegator() {
    private val metadataFile by ResourceFile("metadata.json", "object")
    private val metadata by lazy { gson.fromJson(metadataFile.readText(), Map::class.java) }

    @Test
    fun `metadata should be parsed successfully`() {
        with(metadata) {
            this["a_string"] shouldEqual "some_string"
            this["an_integer"] shouldEqual 1.0
            this["a_bool"] shouldEqual true
            this["a_double"] shouldEqual 12.34

            val customObject = this["an_object"] as Map<*, *>
            customObject["a_key"] shouldEqual "a_value"

            val nestedObject = customObject["a_nested_object"] as Map<*, *>
            nestedObject["a_nested_key"] shouldEqual "a_nested_value"

            val customArray = this["an_array"] as List<String>
            customArray[0] shouldEqual "value_1"
            customArray[1] shouldEqual "value_2"

            this["a_null_key"] shouldBe null
        }
    }
}
