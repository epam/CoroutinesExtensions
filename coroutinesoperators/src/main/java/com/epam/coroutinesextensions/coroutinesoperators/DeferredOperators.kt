package com.epam.coroutinesextensions.coroutinesoperators

import kotlinx.coroutines.*

/**
 * @function - zip: Combine items from two Deferreds together via a specified function and return items based on the results of this function
 * @param source1 - first deferred, whose result will be used in zipper
 * @param source2 - second deferred, whose result will be used in zipper
 * @param zipper - function, that will be called with deferred's results
 */
suspend fun <T1, T2, R> zip(source1: Deferred<T1>, source2: Deferred<T2>, coroutineStart: CoroutineStart = CoroutineStart.DEFAULT, zipper: (T1, T2) -> R): Deferred<R> =
        coroutineScope {
            async(start = coroutineStart) {
                zipper(source1.await(), source2.await())
            }
        }

/**
 * @function - zipWith: Instance version of zip() function
 * @param other - second Deferred that will be zipped to first deferred instance
 * @param zipper - function, that will applied to result of two deferreds
 */
suspend fun <T1, T2, R> Deferred<T1>.zipWith(other: Deferred<T2>, coroutineStart: CoroutineStart = CoroutineStart.DEFAULT, zipper: (T1, T2) -> R): Deferred<R> {
    return zip(this, other, coroutineStart, zipper)
}

/**
 * @function - map: Transform the items from Deferred by applying some function
 * @param mapper - function that will be used for deferred result
 */
suspend fun <T, R> Deferred<T>.map(mapper: (T) -> R): Deferred<R> =
        coroutineScope {
            async {
                mapper(this@map.await())
            }
        }

/**
 * @function - flatMap: Transform the Collection items in Deferred into Deferreds, then flatten this into a single Collection
 * @param mapper - function that will be applied for each element if deferred's result
 */
suspend fun <K, T : Collection<K>, R> Deferred<T>.flatMap(coroutineStart: CoroutineStart = CoroutineStart.DEFAULT, mapper: (K) -> R): Deferred<Collection<R>> =
        coroutineScope {
            async(start = coroutineStart) {
                val result = mutableListOf<R>()
                val list = mutableListOf<Deferred<Boolean>>()
                await().forEach { list.add(async { result.add(mapper(it)) }) }
                list.forEach { it.await() }
                return@async result
            }
        }

/**
 * @function - concatMap: Transform the Collection items in Deferred into Deferreds, then flatten this into a single Collection, without interleaving
 * @param mapper - function that will be applied for each element if deferred's result
 */
suspend fun <K, T : Collection<K>, R> Deferred<T>.concatMap(coroutineStart: CoroutineStart = CoroutineStart.DEFAULT, mapper: (K) -> R): Deferred<Collection<R>> =
        coroutineScope {
            async(start = coroutineStart) {
                await().map { async { mapper(it) } }.map { it.await() }
            }
        }