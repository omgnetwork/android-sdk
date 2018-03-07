package co.omisego.omisego.model

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/6/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import com.google.gson.annotations.SerializedName

/**
 * Represents the global settings of the provider
 *
 * @param mintedTokens A list of minted tokens available for the provider
 */
data class Setting(@SerializedName("minted_tokens") val mintedTokens: List<MintedToken>)
