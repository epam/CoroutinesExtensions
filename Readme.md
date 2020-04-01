# Coroutines Operators

## Preview
Kotlin Coroutines is growing in Android programming as a new way to create asynchronous work. Comparing them with RxJava, we can see that RxJava has a lot functions that helps to modify data, but Coroutines still has not got those functions. Coroutines Operators is library that created to fix those problems and provide users posibility to use same functions as in RxJava, but in Kotlin Coroutines.

Library contains extensions for `Deferred` and `ReceiveChannel` classes.

#### Note

Coroutines Cache works only on `kotlin-coroutines:0.26.0` and above.

## List of extended functions

### Deferred

* zip() - combine items from two Deferreds together via a specified function and return items based on the results of this function
* zipWith() - instance version of zip()
* map() - transform the items from Deferred by applying some function
* flatMap() - transform the Collection items in Deferred into Deferreds, then flatten this into a single Collection
* concatMap() - transform the Collection items in Deferred into Deferreds, then flatten this into a single Collection, without interleaving

### ReceiverChannels

* distinctUntilChanged() - suppress duplicate consecutive items emitted by the ReceiverChannel
* reduce() - apply a function to each emitted item, sequentially, and emit only the final accumulated value
* concat() - concatenate two ReceiverChannels sequentially
* concatWith() - instance version of concat()
* debounce() - only emit an item from the source ReceiverChannel after a particular timespan has passed without the ReceiverChannel emitting any other items


## Examples

### zip

Combine items from two Deferreds together via a specified function and return items based on the results of this function

```kotlin
    val firstName = async { "James" }
    val secondName = async { "Kirk" }
    println(zip(first, second, BiFunction { first: String, second: String -> "$first $second" }))
    // prints "James Kirk"
```

### zipWith

Instance version of [zip()](#zip)

```kotlin
    val firstName = async { "James" }
    val secondName = async { "Kirk" }
    println(first.zipWith(second, BiFunction { first: String, second: String -> "$first $second" }))
    // prints "James Kirk"
```

### map

Transform the items from Deferred by applying some function

```kotlin
    val hello = async { "Hello" }
    println(hello.map { item: String -> "$item world"})
    // prints "Hello world"
```

### flatMap

Transform the Collection items in Deferred into Deferreds, then flatten this into a single Collection

```kotlin
    val time = System.currentTimeMillis()
    val dataArray = async { listOf(1, 2, 3, 4, 5) }
    val result = dataArray.flatMap ({
        Thread.sleep(5000 - it * 1000L)
        it * 2
    }).toList()
    println(result)
    println("Working time is ${System.currentTimeMillis() - time}")
    // prints [10, 8, 6, 4, 2] Working time is 4097
```
### concatMap

Transform the Collection items in Deferred into Deferreds, then flatten this into a single Collection, without interleaving

```kotlin
    val time = System.currentTimeMillis()
    val dataArray = async { listOf(1, 2, 3, 4, 5) }
    val result = dataArray.concatMap ({
        Thread.sleep(5000 - it * 1000L)
        it * 2
    }).toList()
    println(result)
    println("Working time is ${System.currentTimeMillis() - time}")
    // prints [2, 4, 6, 8, 10] Working time is 4303
```

### distinctUntilChanged

Suppress duplicate consecutive items emitted by the ReceiverChannel

```kotlin
    val list = listOf(1, 1, 1, 2, 2, 2, 3, 4, 4, 4)
    print(list.asReceiveChannel().distinctUntilChanged().toList())
    // prints [1, 2, 3, 4]
```

Version with comparator
```kotlin
    val list = listOf(1, 1, 2, 6, 8, 3, 4, 6, 8)
    print(list.asReceiveChannel().distinctUntilChanged(comparator = { first, second -> first % 2 == second % 3 }).toList())
    // prints [1, 2, 6, 3]
```

### reduce

Apply a function to each emitted item, sequentially, and emit only the final accumulated value

```kotlin
    val list = listOf(1, 2, 3, 4)
    print(list.asReceiveChannel().reduce(accumulator = { first, second -> first + second }).toList())
    // prints [10]
```

Version with initial value

```kotlin
    val list = listOf(1, 2, 3, 4)
    print(list.asReceiveChannel().reduce(initialValue = 5, accumulator = { first, second -> first + second }).toList())
    // prints [15]
```

### concat

Concatenate two ReceiverChannels sequentially

```kotlin
    val list1 = listOf(1, 2, 3, 4)
    val list2 = listOf(5, 6, 7)
    print(concat(first = list1.asReceiveChannel(), second = list2.asReceiveChannel()).toList())
    // prints [1, 2, 3, 4, 5, 6, 7]
```

### concatWith

Instance version of [concat()](#concat)
```kotlin
    val list1 = listOf(1, 2, 3, 4)
    val list2 = listOf(5, 6, 7)
    print(list1.asReceiveChannel().concatWith(other = list2.asReceiveChannel()).toList())
    // prints [1, 2, 3, 4, 5, 6, 7] 
```
