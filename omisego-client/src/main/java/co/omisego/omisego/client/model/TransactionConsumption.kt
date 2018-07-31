package co.omisego.omisego.client.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 28/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.client.OMGAPIClient
import co.omisego.omisego.custom.retrofit2.adapter.OMGCall
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.model.transaction.consumption.TransactionConsumptionActionParams

/**
 * An extension function that uses the id from `TransactionConsumption` object to approve the transaction
 *
 * @param omgAPIClient the [OMGAPIClient] object in your application to be used to approve the transaction
 * @return The [OMGCall<TransactionConsumption>] object that you need to call enqueue method on to actually perform the request to the API
 */
fun TransactionConsumption.approve(omgAPIClient: OMGAPIClient): OMGCall<TransactionConsumption> =
    omgAPIClient.approveTransactionConsumption(TransactionConsumptionActionParams(this.id))

/**
 * An extension function that uses the id from `TransactionConsumption` object to reject the transaction
 *
 * @param omgAPIClient the [OMGAPIClient] object in your application to be used to reject the transaction
 * @return The [OMGCall<TransactionConsumption>] object that you need to call enqueue method on to actually perform the request to the API
 */
fun TransactionConsumption.reject(omgAPIClient: OMGAPIClient) =
    omgAPIClient.rejectTransactionConsumption(TransactionConsumptionActionParams(this.id))
