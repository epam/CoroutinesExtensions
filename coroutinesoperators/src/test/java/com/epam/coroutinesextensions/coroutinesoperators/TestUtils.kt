package com.epam.coroutinesextensions.coroutinesoperators

fun <T> Collection<T>.deepEquals(other: Collection<T>) =
        this.containsAll(other) && other.containsAll(this)
