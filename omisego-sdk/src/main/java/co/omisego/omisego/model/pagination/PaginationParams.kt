package co.omisego.omisego.model.pagination

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.OMGEnum
import co.omisego.omisego.custom.gson.OMGEnumAdapter
import com.google.gson.annotations.JsonAdapter

@JsonAdapter(OMGEnumAdapter::class)
enum class SortDirection constructor(override val value: String) : OMGEnum {
    ASCENDING("asc"),
    DESCENDING("desc");
}

sealed class Paginable {
    open class Transaction : Paginable() {
        // Represents transaction's searchable fields
        @JsonAdapter(OMGEnumAdapter::class)
        enum class SearchableFields constructor(override val value: String) : OMGEnum {
            ID("id"),
            STATUS("status"),
            FROM("from"),
            TO("to");

            override fun toString(): String = value
        }

        // Represents transaction's sortable fields.
        @JsonAdapter(OMGEnumAdapter::class)
        enum class SortableFields constructor(override val value: String) : OMGEnum {
            ID("id"),
            STATUS("status"),
            FROM("from"),
            TO("to"),
            CREATE_AT("create_at"),
            UPDATED_AT("updated_at");

            override fun toString(): String = value
        }

        // Represents transaction statuses.
        @JsonAdapter(OMGEnumAdapter::class)
        enum class TransactionStatus constructor(override val value: String) : OMGEnum {
            PENDING("pending"),
            CONFIRMED("confirmed"),
            FAILED("failed"),
            UNKNOWN("unknown");

            override fun toString(): String = value
        }
    }
}
