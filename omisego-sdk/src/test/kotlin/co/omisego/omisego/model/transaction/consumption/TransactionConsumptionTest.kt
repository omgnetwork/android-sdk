package co.omisego.omisego.model.transaction.consumption

import co.omisego.omisego.OMGAPIClient
import co.omisego.omisego.model.transaction.consume.TransactionConsumptionAction
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.amshove.kluent.mock
import org.junit.Test

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
class TransactionConsumptionTest {

    //    private val mEWalletAPI: EWalletAPI by lazy { mock() }
    private val mOMGAPIClient: OMGAPIClient by lazy { mock<OMGAPIClient>() }
    private val mTransactionConsumption: TransactionConsumption by lazy {
        TransactionConsumption(
            "OMG-1234",
            mock(),
            mock(),
            mock(),
            null,
            "",
            mock(),
            "",
            mock(),
            mock(),
            mock(),
            "",
            mock(),
            mock(),
            mock(),
            mock(),
            mock(),
            mock(),
            mock(),
            mapOf(),
            mapOf()
        )
    }

    @Test
    fun `When approve is invoked, OMGAPIClient should invoke the approveTransactionConsumption function properly`() {
        mTransactionConsumption.approve(mOMGAPIClient)
        verify(mOMGAPIClient, times(1)).approveTransactionConsumption(TransactionConsumptionAction("OMG-1234"))
    }

    @Test
    fun `When reject is invoked, OMGAPIClient should invoke the rejectTransactionConsumption function properly`() {
        mTransactionConsumption.reject(mOMGAPIClient)
        verify(mOMGAPIClient, times(1)).rejectTransactionConsumption(TransactionConsumptionAction("OMG-1234"))
    }
}