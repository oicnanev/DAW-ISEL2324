package isel.leic.daw.gomoku.utils

/* ********************************************************************************************
 * Sealed classes represent restricted class hierarchies that provide more control over inheritance.
 * All direct subclasses of a sealed class are known at compile time.
 * No other subclasses may appear outside the module and package within which the sealed class is defined.
 * https://blog.devgenius.io/kotlin-either-logic-based-on-sealed-class-97510c16882b
 ********************************************************************************************* */
sealed class Either<out L, out R> {
    data class Left<out L>(val value: L) : Either<L, Nothing>()
    data class Right<out R>(val value: R) : Either<Nothing, R>()
}

// Functions for when using Either to represent success/failure
fun <R> success(value: R) = Either.Right(value)
fun <L> failure(error: L) = Either.Left(error)

typealias Success<S> = Either.Right<S>
typealias Failure<F> = Either.Left<F>
