package co.omisego.androidsdk.exceptions


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/20/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class OmiseGOServerErrorException() : Exception() {
    override val message: String
        get() = "OmiseGO server error with code 500"
}