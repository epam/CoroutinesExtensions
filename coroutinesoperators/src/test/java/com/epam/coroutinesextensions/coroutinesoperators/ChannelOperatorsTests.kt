package com.epam.coroutinesextensions.coroutinesoperators

import junit.framework.Assert
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ChannelOperatorsTests {

    @Test
    fun distinctUntilChangedTest() = runBlocking {
        val list = listOf(1, 1, 1, 2, 2, 2, 3, 4, 4, 4)
        val result = listOf(1, 2, 3, 4)
        Assert.assertEquals(result, list.asReceiveChannel().distinctUntilChanged().toList())
    }

    @Test
    fun distinctUntilChangedWithComparatorTest() = runBlocking {
        val list = listOf(1, 1, 2, 6, 8, 3, 4, 6, 8)
        val result = listOf(1, 2, 3, 4)
        Assert.assertEquals(result, list.asReceiveChannel().distinctUntilChanged(comparator = { first, second -> first % 2 == second % 2 }).toList())
    }

    @Test
    fun reduceTest() = runBlocking {
        val list = listOf(1, 2, 3, 4)
        val result = listOf(10)
        Assert.assertEquals(result, list.asReceiveChannel().reduce(accumulator = { first, second -> first + second }).toList())
    }

    @Test
    fun reduceWithInitialValueTest() = runBlocking {
        val list = listOf(1, 2, 3, 4)
        val result = listOf(15)
        Assert.assertEquals(result, list.asReceiveChannel().reduce(initialValue = 5, accumulator = { first, second -> first + second }).toList())
    }

    @Test
    fun concatWithTest() = runBlocking {
        val list1 = listOf(1, 2, 3, 4)
        val list2 = listOf(5, 6, 7, 8)
        val result = listOf(1, 2, 3, 4, 5, 6, 7, 8)
        Assert.assertEquals(result, list1.asReceiveChannel().concatWith(other = list2.asReceiveChannel()).toList())
    }

    @Test
    fun concatTest() = runBlocking {
        val list1 = listOf(1, 2, 3, 4)
        val list2 = listOf(5, 6, 7, 8)
        val result = listOf(1, 2, 3, 4, 5, 6, 7, 8)
        Assert.assertEquals(result, concat(first = list1.asReceiveChannel(), second = list2.asReceiveChannel()).toList())
    }
}