@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package co.omisego.omisego.utils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 22/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

/* Credit: https://gist.github.com/tomaszpolanski/92a2eada1e06e4a4c71abb298d397173#file-utils-kt */

inline fun <reified R : Parcelable> R.validateParcel(): R {
    val bytes = marshallParcelable(this)
    return unmarshallParcelable(bytes)
}

inline fun <reified R : Parcelable> marshallParcelable(parcelable: R): ByteArray {
    val bundle = Bundle().apply { putParcelable(R::class.java.name, parcelable) }
    return marshall(bundle)
}

fun marshall(bundle: Bundle): ByteArray =
    Parcel.obtain().use {
        it.writeBundle(bundle)
        it.marshall()
    }

inline fun <reified R : Parcelable> unmarshallParcelable(bytes: ByteArray): R = unmarshall(bytes)
    .readBundle()
    .run {
        classLoader = R::class.java.classLoader
        getParcelable(R::class.java.name)
    }

fun unmarshall(bytes: ByteArray): Parcel =
    Parcel.obtain().apply {
        unmarshall(bytes, 0, bytes.size)
        setDataPosition(0)
    }

private fun <T> Parcel.use(block: (Parcel) -> T): T =
    try {
        block(this)
    } finally {
        this.recycle()
    }
