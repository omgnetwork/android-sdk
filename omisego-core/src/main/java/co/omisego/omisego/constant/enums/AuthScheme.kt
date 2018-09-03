package co.omisego.omisego.constant.enums

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/8/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

enum class AuthScheme(override val value: String) : OMGEnum {
    ADMIN("OMGAdmin"),
    Client("OMGClient");

    override fun toString() = value
}
