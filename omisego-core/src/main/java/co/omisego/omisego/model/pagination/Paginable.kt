package co.omisego.omisego.model.pagination

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.OMGEnum

enum class SortDirection constructor(override val value: String) : OMGEnum {
    ASCENDING("asc"),
    DESCENDING("desc");
}

sealed class Paginable {

    interface SortableFields : OMGEnum

    open class Transaction : Paginable() {

        /**
         * Represents transaction's searchable fields
         */
        enum class FilterableFields constructor(override val value: String) : OMGEnum {
            ID("id"),
            STATUS("status"),
            FROM("from"),
            TO("to"),
            CREATED_AT("created_at"),
            UPDATED_AT("updated_at");

            override fun toString(): String = value
        }

        /**
         * Represents transaction's sortable fields.
         */
        enum class SortableFields constructor(override val value: String) : Paginable.SortableFields {
            ID("id"),
            STATUS("status"),
            FROM("from"),
            TO("to"),
            CREATED_AT("created_at"),
            UPDATED_AT("updated_at");

            override fun toString(): String = value
        }

        /**
         * Represents transaction statuses.
         */
        enum class TransactionStatus constructor(override val value: String) : OMGEnum {
            PENDING("pending"),
            CONFIRMED("confirmed"),
            FAILED("failed"),
            UNKNOWN("unknown");

            override fun toString(): String = value
        }
    }

    open class Account : Paginable() {

        /**
         * Represents account's searchable fields
         */
        enum class FilterableFields constructor(override val value: String) : OMGEnum {
            ID("id"),
            NAME("name"),
            DESCRIPTION("description"),
            CREATED_AT("created_at"),
            UPDATED_AT("updated_at");

            override fun toString(): String = value
        }

        /**
         * Represents account's sortable fields.
         */
        enum class SortableFields constructor(override val value: String) : Paginable.SortableFields {
            ID("id"),
            NAME("name"),
            DESCRIPTION("description"),
            CREATED_AT("created_at"),
            UPDATED_AT("updated_at");

            override fun toString(): String = value
        }
    }

    open class Token : Paginable() {
        /**
         * Represents token's searchable fields
         */
        enum class FilterableFields constructor(override val value: String) : OMGEnum {
            SYMBOL("symbol"),
            NAME("name");

            override fun toString(): String = value
        }

        /**
         * Represents token's sortable fields.
         */
        enum class SortableFields constructor(override val value: String) : Paginable.SortableFields {
            NAME("name"),
            SYMBOL("symbol"),
            CREATED_AT("created_at");

            override fun toString(): String = value
        }
    }

    open class Wallet : Paginable() {
        /**
         * Represents wallet's searchable fields
         */
        enum class FilterableFields constructor(override val value: String) : OMGEnum {
            ADDRESS("address"),
            NAME("name");

            override fun toString(): String = value
        }

        /**
         * Represents wallet's sortable fields.
         */
        enum class SortableFields constructor(override val value: String) : Paginable.SortableFields {
            NAME("name"),
            ADDRESS("address"),
            CREATED_AT("created_at");

            override fun toString(): String = value
        }
    }
}
