package co.omisego.omisego.model

/**
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 17/12/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.extension.bd
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class MintedTokenTest {
    val mintedToken1 = MintedToken("OMG:8bcda572-9411-43c8-baae-cd56eb0155f3", "OMG", "OmiseGO", 10000.0.bd)
    val mintedToken2 = MintedToken("ETH:8bcda572-9411-43c8-baae-cd56eb0155f3", "ETH", "Ethereum", 10000.0.bd)
    val mintedToken3 = MintedToken("OMG:8bcda572-9411-43c8-baae-cd56eb0155f3", "OMG", "OmiseGO", 10.0.bd)
    val mintedToken4 = MintedToken("OMG:8bcda572-9411-43c8-baae-cd56eb0155f3", "OMG", "OmiseGO", 10000.0.bd)

    @Test
    fun `mintedToken1 should be not compatible with mintedToken2`() {
        mintedToken1 compatWith mintedToken2 shouldEqualTo false
    }

    @Test
    fun `mintedToken1 should be not compatible with mintedToken3`() {
        mintedToken1 compatWith mintedToken3 shouldEqualTo false
    }

    @Test
    fun `mintedToken1 should be compatible with mintedToken4`() {
        mintedToken1 compatWith mintedToken4 shouldEqualTo true
    }
}