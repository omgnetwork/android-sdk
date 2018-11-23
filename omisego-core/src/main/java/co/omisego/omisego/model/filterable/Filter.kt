package co.omisego.omisego.model.filterable

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 21/11/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.OMGEnum

data class Filter(
    val field: String,
    val comparator: Comparator,
    val value: Any
)

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

    fun add(f: Filter) {
        _filterList.add(f)
    }

    infix fun String.eq(value: String) = Filter(this, Comparator.StringComparator.EQUAL(), value)

    infix fun String.eq(value: OMGEnum) = Filter(this, Comparator.StringComparator.EQUAL(), value.value)

    infix fun String.contains(value: String) = Filter(this, Comparator.StringComparator.CONTAINS(), value)

    infix fun String.startsWith(value: String) = Filter(this, Comparator.StringComparator.STARTS_WITH(), value)

    infix fun String.eq(value: Boolean) = Filter(this, Comparator.BooleanComparator.EQUAL(), value)

    infix fun String.neq(value: Boolean) = Filter(this, Comparator.BooleanComparator.NOT_EQUAL(), value)

    infix fun String.eq(value: Number) = Filter(this, Comparator.NumericComparator.EQUAL(), value)

    infix fun String.neq(value: Number) = Filter(this, Comparator.NumericComparator.NOT_EQUAL(), value)

    infix fun String.lt(value: Number) = Filter(this, Comparator.NumericComparator.LESS_THAN(), value)

    infix fun String.lte(value: Number) = Filter(this, Comparator.NumericComparator.LESS_THAN_OR_EQUAL(), value)

    infix fun String.gt(value: Number) = Filter(this, Comparator.NumericComparator.GREATER_THAN(), value)

    infix fun String.gte(value: Number) = Filter(this, Comparator.NumericComparator.GREATER_THAN_OR_EQUAL(), value)
}

sealed class Comparator(override val value: String) : OMGEnum {
    override fun toString(): String {
        return value
    }

    sealed class BooleanComparator(comparator: String) : Comparator(comparator) {
        class EQUAL : BooleanComparator("eq")
        class NOT_EQUAL : BooleanComparator("neq")
    }

    sealed class StringComparator(comparator: String) : Comparator(comparator) {
        class EQUAL : StringComparator("eq")
        class CONTAINS : StringComparator("contains")
        class STARTS_WITH : StringComparator("starts_with")
    }

    sealed class NumericComparator(comparator: String) : Comparator(comparator) {
        class EQUAL : NumericComparator("eq")
        class NOT_EQUAL : NumericComparator("neq")
        class LESS_THAN : NumericComparator("lt")
        class LESS_THAN_OR_EQUAL : NumericComparator("lte")
        class GREATER_THAN : NumericComparator("gt")
        class GREATER_THAN_OR_EQUAL : NumericComparator("gte")
    }
}
