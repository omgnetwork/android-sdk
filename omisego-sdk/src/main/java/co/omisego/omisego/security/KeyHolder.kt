package co.omisego.omisego.security

import java.security.KeyStore

internal data class KeyHolder(val keyStore: KeyStore, val keyAlias: String)