package co.omisego.omisego.utils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 10/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * Represents value value of one of two possible types (value disjoint union).
 * Instances of [Either] are either an instance of [Left] or [Right].
 *
 * @see Left
 * @see Right
 */

sealed class Either<out L, out R> {
    data class Left<out L>(val value: L) : Either<L, Nothing>()
    data class Right<out R>(val value: R) : Either<Nothing, R>()

    val isRight get() = this is Right<R>
    val isLeft get() = this is Left<L>

    inline fun either(doOnLeft: (L) -> Any, doOnRight: (R) -> Any): Any =
        when (this) {
            is Left -> doOnLeft(value)
            is Right -> doOnRight(value)
        }
}