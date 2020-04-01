package com.epam.coroutinesextensions.coroutinesoperators

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.*
import org.junit.Test
import kotlin.coroutines.CoroutineContext

class DeferredOperatorsTests {

    @Test
    fun zipTest() = runBlocking {
        val first = 1.toDeferred()
        val second = 2.toDeferred()
        val result = 3

        assertEquals(result, zip(first, second) { firstValue: Int, secondValue: Int -> firstValue + secondValue }.await())
    }

    @Test
    fun zipWithTest() = runBlocking {
        val first = 1.toDeferred()
        val second = 2.toDeferred()
        val result = 3

        assertEquals(result, first.zipWith(second) { firstValue: Int, secondValue: Int -> firstValue + secondValue }.await())
    }


    @Test
    fun mapTest() = runBlocking {
        val mapper = { a: Int -> a * 3 }
        val from = 1.toDeferred()
        val result = 3

        assertEquals(result, from.map(mapper).await())
    }

    @Test
    fun flatMapTest() = runBlocking {
        val dataArray = async { listOf(1, 2, 3, 4, 5) }
        val resultDataArray = listOf(10, 8, 6, 4, 2)
        var result: Collection<Int>? = null
        GlobalScope.async {
            val time = System.currentTimeMillis()
            result = dataArray.flatMap {
                Thread.sleep(5000 - it * 1000L)
                it * 2
            }.await()
            println("Working time is ${System.currentTimeMillis() - time}")
        }.await()

        assertTrue(resultDataArray.deepEquals(result!!))
    }

    @Test
    fun concatMapTest() = runBlocking {
        val dataArray = async { listOf(1, 2, 3, 4, 5) }
        val resultDataArray = listOf(2, 4, 6, 8, 10)
        var result: Collection<Int>? = null
        GlobalScope.async {
            val time = System.currentTimeMillis()
            result = dataArray.concatMap {
                Thread.sleep(5000 - it * 1000L)
                it * 2
            }.await()
            println("Working time is ${System.currentTimeMillis() - time}")
        }.await()

        assertTrue(resultDataArray.deepEquals(result!!))
    }

    private suspend fun <T> T.toDeferred(): Deferred<T> {
        return background { this }
    }

    private suspend fun <X> background(context: CoroutineContext = Dispatchers.Default, block: suspend () -> X): Deferred<X> {
        return coroutineScope {
            async(context) {
                block.invoke()
            }
        }
    }
}