package co.omisego.omisego.model

import com.google.gson.annotations.SerializedName


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/6/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

/**
 * Represents the global settings of the provider
 *
 * @param mintedTokens A list of minted tokens available for the provider
 */
data class Setting(@SerializedName("minted_tokens") val mintedTokens: List<MintedToken>)
