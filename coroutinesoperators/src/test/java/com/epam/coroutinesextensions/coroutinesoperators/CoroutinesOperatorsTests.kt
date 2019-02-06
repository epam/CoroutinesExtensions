package com.epam.coroutinesextensions.coroutinesoperators

import jdk.nashorn.internal.objects.Global
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.flatMap
import kotlinx.coroutines.channels.produce
import org.junit.Test
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

class CoroutinesOperatorsTests {

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

    /*@Test
    fun distinctUntilChangedTest() = runBlocking {
        val list = listOf(1, 1, 1, 2, 2, 2, 3, 4, 4, 4)
        val result = listOf(1, 2, 3, 4)
        assertEquals(result, list.asReceiveChannel().distinctUntilChanged().toList())
    }

    @Test
    fun distinctUntilChangedWithComparatorTest() = runBlocking {
        val list = listOf(1, 1, 2, 6, 8, 3, 4, 6, 8)
        val result = listOf(1, 2, 3, 4)
        assertEquals(result, list.asReceiveChannel().distinctUntilChanged(comparator = { first, second -> first % 2 == second % 2 }).toList())
    }

    @Test
    fun asyncFlatMapTest() = runBlocking {
        val list = listOf(1, 2, 3, 4)
        val result = listOf(2, 4, 6, 8)
        assertTrue(result.deepEquals(list.asReceiveChannel().asyncFlatMap(mapper = { listOf(it * 2).asReceiveChannel() }).toList()))
    }

    @Test
    fun asyncConcatMapTest() = runBlocking {
        val list = listOf(1, 2, 3, 4)
        val result = listOf(2, 4, 6, 8)
        assertEquals(result, list.asReceiveChannel().asyncConcatMap(mapper = { listOf(it * 2).asReceiveChannel() }).toList())
    }

    @Test
    fun asyncMapTest() = runBlocking {
        val list = listOf(1, 2, 3, 4)
        val result = listOf(2, 4, 6, 8)
        assertEquals(result, list.asReceiveChannel().asyncMap(mapper = { it * 2 }).toList())
    }

    @Test
    fun reduceTest() = runBlocking {
        val list = listOf(1, 2, 3, 4)
        val result = listOf(10)
        assertEquals(result, list.asReceiveChannel().reduce(accumulator = { first, second -> first + second }).toList())
    }

    @Test
    fun reduceWithInitialValueTest() = runBlocking {
        val list = listOf(1, 2, 3, 4)
        val result = listOf(15)
        assertEquals(result, list.asReceiveChannel().reduce(initialValue = 5, accumulator = { first, second -> first + second }).toList())
    }

    @Test
    fun concatWithTest() = runBlocking {
        val list1 = listOf(1, 2, 3, 4)
        val list2 = listOf(5, 6, 7, 8)
        val result = listOf(1, 2, 3, 4, 5, 6, 7, 8)
        assertEquals(result, list1.asReceiveChannel().concatWith(other = list2.asReceiveChannel()).toList())
    }

    @Test
    fun concatTest() = runBlocking {
        val list1 = listOf(1, 2, 3, 4)
        val list2 = listOf(5, 6, 7, 8)
        val result = listOf(1, 2, 3, 4, 5, 6, 7, 8)
        assertEquals(result, concat(first = list1.asReceiveChannel(), second = list2.asReceiveChannel()).toList())
    }*/

    private fun <T> Collection<T>.deepEquals(other: Collection<T>) = this.containsAll(other) && other.containsAll(this)

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