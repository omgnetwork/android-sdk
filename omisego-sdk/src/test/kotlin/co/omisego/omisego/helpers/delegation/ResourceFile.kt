package co.omisego.omisego.helpers.delegation

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 13/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import java.io.File
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class ResourceFile(private val fileName: String) : ReadOnlyProperty<Any, File> {
    override fun getValue(thisRef: Any, property: KProperty<*>): File {
        return File(javaClass.classLoader.getResource(fileName).path)
    }
}
