package co.omisego.omisego.model.filterable

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 23/11/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */


sealed class Filterable {
    /**
     * Represents transaction's filterable fields
     */
    class TransactionFields : Filterable() {
        val id = "id"
        val status = "status"
        val errorCode = "error_code"
        val errorDescription = "error_description"
        val type = "type"
        val fromAmount = "from_amount"
        val toAmount = "to_amount"
        val calculatedAt = "calculated_at"
        val insertedAt = "inserted_at"
        val createdAt = "created_at"
        val updatedAt = "updated_at"
    }

    class AccountFields : Filterable() {
        val id = "id"
        val name = "name"
        val description = "description"
        val createdAt = "created_at"
        val updatedAt = "updated_at"
        val metadata = "metadata"
    }

    class WalletFields : Filterable() {
        val address = "address"
        val name = "name"
        val identifier = "identifier"
        val enabled = "enabled"
        val updatedAt = "updated_at"
        val metadata = "metadata"
    }
}
