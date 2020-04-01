package com.epam.coroutinesextensions.coroutinesoperators

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext


suspend fun <T> ReceiveChannel<T>.distinctUntilChanged(
        context: CoroutineContext = Dispatchers.Unconfined,
        scope: CoroutineScope = GlobalScope): ReceiveChannel<T> =
        scope.produce(context) {
            var prev: T? = null

            consumeEach {
                if (it != prev) {
                    send(it)
                    prev = it
                }
            }
        }

suspend fun <T> ReceiveChannel<T>.distinctUntilChanged(context: CoroutineContext = Dispatchers.Unconfined,
                                                       scope: CoroutineScope = GlobalScope,
                                                       comparator: (T, T) -> Boolean): ReceiveChannel<T> =
        scope.produce(context) {
            var prev: T = receive()
            send(prev)

            consumeEach { it ->
                if (!comparator(it, prev)) {
                    send(it)
                    prev = it
                }
            }
        }

suspend fun <T> ReceiveChannel<T>.reduce(
        context: CoroutineContext = Dispatchers.Unconfined,
        accumulator: (T, T) -> T,
        scope: CoroutineScope = GlobalScope
): ReceiveChannel<T> = scope.produce(context) {
    var result: T = receive()

    consumeEach { result = accumulator(it, result) }
    send(result)
}

suspend fun <T> ReceiveChannel<T>.reduce(
        initialValue: T,
        context: CoroutineContext = Dispatchers.Unconfined,
        accumulator: (T, T) -> T,
        scope: CoroutineScope = GlobalScope
): ReceiveChannel<T> = scope.produce(context) {
    var result = initialValue

    consumeEach { result = accumulator(it, result) }
    send(result)
}

suspend fun <T> ReceiveChannel<T>.concatWith(
        context: CoroutineContext = Dispatchers.Unconfined,
        other: ReceiveChannel<T>,
        scope: CoroutineScope = GlobalScope
): ReceiveChannel<T> = scope.produce(context) {
    consumeEach { it ->
        send(it)
    }
    other.consumeEach {
        send(it)
    }
}

suspend fun <T> concat(
        context: CoroutineContext = Dispatchers.Unconfined,
        first: ReceiveChannel<T>,
        second: ReceiveChannel<T>
): ReceiveChannel<T> = first.concatWith(context, second)

fun <T> ReceiveChannel<T>.debounce(
        wait: Long = 300,
        context: CoroutineContext = Dispatchers.Unconfined,
        scope: CoroutineScope = GlobalScope
): ReceiveChannel<T> = scope.produce(context) {
    var nextTime = 0L
    consumeEach {
        val curTime: Long = java.lang.System.currentTimeMillis()
        if (curTime < nextTime) {
            // not enough time passed from last send
            delay(nextTime - curTime)
            var mostRecent = it
            while (!isEmpty) {
                mostRecent = receive()
            } // take the most recently sent without waiting
            nextTime += wait // maintain strict time interval between sends
            send(mostRecent)
        } else {
            // big pause between original events
            nextTime = curTime + wait // start tracking time interval from scratch
            send(it)
        }
    }
}

fun <E> Iterable<E>.asReceiveChannel(context: CoroutineContext = Dispatchers.Unconfined): ReceiveChannel<E> =
        GlobalScope.produce(context) {
            for (element in this@asReceiveChannel)
                send(element)
        }

