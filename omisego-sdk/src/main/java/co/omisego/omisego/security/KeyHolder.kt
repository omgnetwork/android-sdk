package co.omisego.omisego.security

import java.security.KeyStore

/**
 * OmiseGO
 *
 * Created by Yannick Badoual on 2/28/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

internal data class KeyHolder(val keyStore: KeyStore, val keyAlias: String)