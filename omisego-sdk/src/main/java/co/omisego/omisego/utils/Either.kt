package co.omisego.omisego.utils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 10/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * Represents a value of one of two possible types (a disjoint union).
 * Instances of [Either] are either an instance of [Left] or [Right].
 * FP Convention dictates that [Left] is used for "failure" and [Right] is used for "success".
 *
 * @see Left
 * @see Right
 */

sealed class Either<out L, out R> {
    data class Left<out L>(val a: L) : Either<L, Nothing>()
    data class Right<out R>(val b: R) : Either<Nothing, R>()

    val isRight get() = this is Right<R>
    val isLeft get() = this is Left<L>

    fun either(fnL: (L) -> Any, fnR: (R) -> Any): Any =
        when (this) {
            is Either.Left -> fnL(a)
            is Either.Right -> fnR(b)
        }
}