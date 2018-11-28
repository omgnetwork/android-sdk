package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 28/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.OMGAPIAdmin
import co.omisego.omisego.custom.retrofit2.adapter.OMGCall
import co.omisego.omisego.model.params.TransactionConsumptionActionParams

/**
 * An extension function that uses the id from `TransactionConsumption` object to approve the transaction
 *
 * @param omgAPIAdmin the [co.omisego.omisego.OMGAPIAdmin] object in your application to be used to approve the transaction
 * @return The [OMGCall<TransactionConsumption>] object that you need to call enqueue method on to actually perform the request to the API
 */
fun TransactionConsumption.approve(omgAPIAdmin: OMGAPIAdmin): OMGCall<TransactionConsumption> =
    omgAPIAdmin.approveTransactionConsumption(TransactionConsumptionActionParams(this.id))

/**
 * An extension function that uses the id from `TransactionConsumption` object to reject the transaction
 *
 * @param omgAPIAdmin the [co.omisego.omisego.OMGAPIAdmin] object in your application to be used to reject the transaction
 * @return The [OMGCall<TransactionConsumption>] object that you need to call enqueue method on to actually perform the request to the API
 */
fun TransactionConsumption.reject(omgAPIAdmin: OMGAPIAdmin) =
    omgAPIAdmin.rejectTransactionConsumption(TransactionConsumptionActionParams(this.id))
