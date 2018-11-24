package co.omisego.omisego.model.filterable

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 21/11/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.OMGEnum

/**
 * Represents a filter that can be used in filterable queries
 *
 * @param field A field name of the object.
 * This param should specify the field name of the filterable response object.
 * Read full specifications [in our advanced filtering guide](https://github.com/omisego/ewallet/blob/master/docs/guides/advanced_filtering.md)
 * @param comparator A [Comparator] object that will be used for filtering, depending on the object type.
 * There're currently 3 types supported:
 * - [Comparator.StringComparator]
 * - [Comparator.BooleanComparator]
 * - [Comparator.NumberComparator]
 * @param value A value to be used for comparing.
 */
data class Filter internal constructor(
    val field: String,
    val comparator: Comparator,
    val value: Any
) {
    companion object {

        /**
         * A static function for create a [Filter] object from [String] value.
         */
        fun create(field: String, comparator: Comparator, value: String) =
            Filter(field, comparator, value)

        /**
         * A static function for create a [Filter] object from [Boolean] value.
         */
        fun create(field: String, comparator: Comparator, value: Boolean) =
            Filter(field, comparator, value)

        /**
         * A static function for create a [Filter] object from [Number] value.
         */
        fun create(field: String, comparator: Comparator, value: Number) =
            Filter(field, comparator, value)
    }
}

/**
 * A convenient function to build a collection of [Filter] object with the [Filterable] scope.
 *
 * For example,
 * <code>
 * val filterList = buildFilterList<Filterable.TransactionFields> { field ->
 *      // Test number comparator
 *      add(field.fromAmount eq 100)
 *      add(field.fromAmount neq 101)
 *      add(field.toAmount gt 1.3)
 *      add(field.toAmount gte 2.8)
 *      add(field.toAmount lt 10)
 *      add(field.toAmount lte 1.0)
 *
 *      // Test string comparator
 *      add(field.status eq Paginable.Transaction.TransactionStatus.CONFIRMED)
 *      add(field.type eq "some_type")
 *      add(field.id startsWith "123")
 *      add(field.status contains "confirm")
 *
 *      // Test boolean comparator
 *      add("success" eq true)
 *      add("fail" neq true)
 *  }
 * </code>
 *
 */
inline fun <reified T : Filterable> buildFilterList(lambda: FilterListBuilder.(T) -> Unit): List<Filter> {
    with(FilterListBuilder()) {
        lambda(this, T::class.java.newInstance())
        return filterList
    }
}

fun buildFilterList(lambda: FilterListBuilder.() -> Unit): List<Filter> {
    with(FilterListBuilder()) {
        lambda(this)
        return filterList
    }
}

class FilterListBuilder {
    private val _filterList: MutableList<Filter> by lazy { mutableListOf<Filter>() }
    val filterList: List<Filter>
        get() = _filterList

    /**
     * Add a filter object to the filter list
     *
     * @param filter A filter object
     */
    fun add(filter: Filter) {
        _filterList.add(filter)
    }

    /**
     * A convenient infix function to create a [Filter] object for [String] value.
     *
     * @param value A [String] value.
     * @return a [Filter] object with [Comparator.StringComparator.EQUAL]
     */
    infix fun String.eq(value: String) = Filter(this, Comparator.StringComparator.EQUAL(), value)

    /**
     * A convenient infix function to create a [Filter] object for [String] value.
     *
     * @param value [OMGEnum] value
     * @return a [Filter] object with [Comparator.StringComparator.EQUAL]
     */
    infix fun String.eq(value: OMGEnum) = Filter(this, Comparator.StringComparator.EQUAL(), value.value)

    /**
     * A convenient infix function to create a [Filter] object for [String] value.
     *
     * @param value A [String] value
     * @return a [Filter] object with [Comparator.StringComparator.CONTAINS]
     */
    infix fun String.contains(value: String) = Filter(this, Comparator.StringComparator.CONTAINS(), value)

    /**
     * A convenient infix function to create a [Filter] object for [String] value.
     *
     * @param value A [String] value
     * @return A [Filter] object with [Comparator.StringComparator.STARTS_WITH]
     */
    infix fun String.startsWith(value: String) = Filter(this, Comparator.StringComparator.STARTS_WITH(), value)

    /**
     * A convenient infix function to create a [Filter] object for [Boolean] value.
     *
     * @param value A [Boolean] value
     * @return A [Filter] object with [Comparator.BooleanComparator.EQUAL]
     */
    infix fun String.eq(value: Boolean) = Filter(this, Comparator.BooleanComparator.EQUAL(), value)

    /**
     * A convenient infix function to create a [Filter] object for [Boolean] value.
     *
     * @param value A [Boolean] value
     * @return A [Filter] object with [Comparator.BooleanComparator.NOT_EQUAL]
     */
    infix fun String.neq(value: Boolean) = Filter(this, Comparator.BooleanComparator.NOT_EQUAL(), value)

    /**
     * A convenient infix function to create a [Filter] object for [Number] value.
     *
     * @param value A [String] value
     * @return A [Filter] object with [Comparator.NumberComparator.EQUAL]
     */
    infix fun String.eq(value: Number) = Filter(this, Comparator.NumberComparator.EQUAL(), value)

    /**
     * A convenient infix function to create a [Filter] object for [Number] value.
     *
     * @param value A [Number] value
     * @return A [Filter] object with [Comparator.NumberComparator.NOT_EQUAL]
     */
    infix fun String.neq(value: Number) = Filter(this, Comparator.NumberComparator.NOT_EQUAL(), value)

    /**
     * A convenient infix function to create a [Filter] object for [Number] value.
     *
     * @param value A [Number] value
     * @return A [Filter] object with [Comparator.NumberComparator.LESS_THAN]
     */
    infix fun String.lt(value: Number) = Filter(this, Comparator.NumberComparator.LESS_THAN(), value)

    /**
     * A convenient infix function to create a [Filter] object for [Number] value.
     *
     * @param value A [Number] value
     * @return A [Filter] object with [Comparator.NumberComparator.LESS_THAN_OR_EQUAL]
     */
    infix fun String.lte(value: Number) = Filter(this, Comparator.NumberComparator.LESS_THAN_OR_EQUAL(), value)

    /**
     * A convenient infix function to create a [Filter] object for [Number] value.
     *
     * @param value A [Number] value
     * @return A [Filter] object with [Comparator.NumberComparator.GREATER_THAN]
     */
    infix fun String.gt(value: Number) = Filter(this, Comparator.NumberComparator.GREATER_THAN(), value)

    /**
     * A convenient infix function to create a [Filter] object for [Number] value.
     *
     * @param value A [Number] value
     * @return A [Filter] object with [Comparator.NumberComparator.GREATER_THAN_OR_EQUAL]
     */
    infix fun String.gte(value: Number) = Filter(this, Comparator.NumberComparator.GREATER_THAN_OR_EQUAL(), value)
}

sealed class Comparator(override val value: String) : OMGEnum {
    override fun toString(): String {
        return value
    }

    /**
     * A comparator for [Boolean] value
     *
     * - EQUAL: The value must match exactly fhe field
     * - NOT_EQUAL: The value must be different from the field
     *
     * Note: EQUAL with a true value is the same as NOT_EQUAL with a false value
     */
    sealed class BooleanComparator(comparator: String) : Comparator(comparator) {
        class EQUAL : BooleanComparator("eq")
        class NOT_EQUAL : BooleanComparator("neq")
    }

    /**
     * A comparator for [String] values
     *
     * - EQUAL: The value must match exactly fhe field
     * - CONTAINS: The value must be a substring of the field
     * - STARTS_WITH: The field must start with the value
     */
    sealed class StringComparator(comparator: String) : Comparator(comparator) {
        class EQUAL : StringComparator("eq")
        class CONTAINS : StringComparator("contains")
        class STARTS_WITH : StringComparator("starts_with")
    }

    /**
     * A comparator for [Number] values (BigDecimal)
     *
     * - EQUAL: The value must match exactly the field
     * - NOT_EQUAL: The value must be different from the field
     * - LESS_THAN: The value must be inferior to the field
     * - LESS_THAN_OR_EQUAL: The value must be inferior or equal to the field
     * - GREATER_THAN: The value must be superior to the field
     * - GREATER_THAN_OR_EQUAL: The value must me superior or equal to the field
     */
    sealed class NumberComparator(comparator: String) : Comparator(comparator) {
        class EQUAL : NumberComparator("eq")
        class NOT_EQUAL : NumberComparator("neq")
        class LESS_THAN : NumberComparator("lt")
        class LESS_THAN_OR_EQUAL : NumberComparator("lte")
        class GREATER_THAN : NumberComparator("gt")
        class GREATER_THAN_OR_EQUAL : NumberComparator("gte")
    }
}
