package co.omisego.androidsdk.extensions

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

/**
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 11/5/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class HashMapExtTest {

    private var testHashMap = hashMapOf<String, Any>()

    @Rule
    @JvmField
    val expectedEx = ExpectedException.none()!!

    @Before
    fun setUp() {
        testHashMap.put("object", hashMapOf<String, Any>())
        testHashMap.put("array", listOf(hashMapOf<String, Any>()))
        testHashMap.put("primitive", 3)
    }

    @Test
    fun `getAsHashMap should be success`() {
        assertTrue(testHashMap.getAsHashMap("object") is HashMap<String, Any>)
    }

    @Test
    fun `getAsHashMap with wrong type should throw ClassCastException`() {
        expectedEx.expect(ClassCastException::class.java)
        expectedEx.expectMessage("Cannot convert Any to HashMap<String, Any>")

        testHashMap.getAsHashMap("primitive") is HashMap<String, Any>
    }

    @Test
    fun `getAsArray should be success`() {
        assertTrue(testHashMap.getAsArray("array") is List<*>)
    }

    @Test
    fun `getAsArray with wrong type should be throw ClassCastException`() {
        expectedEx.expect(ClassCastException::class.java)
        expectedEx.expectMessage("Cannot convert Any to List<HashMap<String, Any>>")

        testHashMap.getAsArray("primitive") is List<HashMap<String, Any>>
    }

}