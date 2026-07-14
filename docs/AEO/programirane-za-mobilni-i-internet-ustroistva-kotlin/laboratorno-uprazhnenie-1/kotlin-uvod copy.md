---
layout: default
title: Introduction
parent: Lab 1
grand_parent: Mobile and Internet Programming Kotlin
nav_order: 2
---

## Operators and data types

As other programming languages, Kotlin uses + , - and *, / p. Kotlin also supports different numeric types: Int, Long, Double, Float.

There is no implicit conversion between numeric types in Kotlin.

Above statement could be checkd with following examples:

```kotlin
val i: Int = 6
val b1 = i.toByte()
println(b1)

val b2: Byte = 1
println(b2)

val i1: Int = b2

val i2: String = b2

val i3: Double = b2
```

Consider obtained results.

Change the example:

```kotlin
val i4: Int = b2.toInt() 
println(i4)

val i5: String = b2.toString()
println(i5)

val i6: Double = b2.toDouble()
println(i6)
```

There are two types of variable in Kotlin: mutable and immutable.
Immutable variables: with ```val``` a value could be once declared. There will be an error when trying to chnage that value.

Mutable variables: with ```var``` a value could be declared and later that value could be changed.

Condier following example:

```kotlin
var fish = 1
fish = 2
val aquarium = 1
aquarium = 2
```

Working with strings in Kotlin is similar to other programming languages. " - is used for strings, ' - is used for chars, + - concatenation, $ - pattern creation, {} - used for pattern expressions.

Example:

```kotlin
val numberOfFish = 5
val numberOfPlants = 12
"I have $numberOfFish fish" + " and $numberOfPlants plants"

"I have ${numberOfFish + numberOfPlants} fish and plants"
```

## Comparison

Similar to other languages, Kotlin supports boolean operators like less than, equal to, greater than, etc. (.<==>!=<=>=)

Example:

```kotlin
val numberOfFish = 50
val numberOfPlants = 23
if (numberOfFish > numberOfPlants) {
    println("Good ratio!") 
} else {
    println("Unhealthy ratio")
}
```

Range expression

```kotlin
val fish = 50
if (fish in 1..100) {
    println(fish)
}
```

Nested conditions
```kotlin
if (numberOfFish == 0) {
    println("Empty tank")
} else if (numberOfFish < 40) {
    println("Got fish!")
} else {
    println("That's a lot of fish!")
}
```
Conditional statement:
```kotlin
when (numberOfFish) {
    0  -> println("Empty tank")
    in 1..39 -> println("Got fish!")
    else -> println("That's a lot of fish!")
}
```

## Nullable

Varibles in Kotlin cannot be null by default.

Try following code line:

```kotlin
var rocks: Int = null
```

In order to define that variable could be null, quotation mark should be used after the data type.

```kotlin
var marbles: Int? = null
```

Example:

```kotlin
var fishFoodTreats = 6
if (fishFoodTreats != null) {
    fishFoodTreats = fishFoodTreats.dec()
}
```

Operator  (?:) - takes the right-hand value if the left-hand value is null.

```kotlin
var fishFoodTreats = 6
fishFoodTreats = fishFoodTreats?.dec()

fishFoodTreats = fishFoodTreats?.dec() ?: 0
```



Operator !! - asserts that an expression is non-nullable.

```kotlin
val len = s!!.length
```

## Collections

List definition with listOf:

```kotlin
val school = listOf("mackerel", "trout", "halibut")
println(school)
```

List definition with mutableListOf:

```kotlin
val myList = mutableListOf("tuna", "salmon", "shark")
myList.remove("shark")
```

Array definition with arrayOf, intArrayOf:

```kotlin
val school = arrayOf("shark", "salmon", "minnow")
println(java.util.Arrays.toString(school))

val mix = arrayOf("fish", 2)

val numbers = intArrayOf(1,2,3)

```

Operator +

```kotlin
val numbers = intArrayOf(1,2,3)
val numbers3 = intArrayOf(4,5,6)
val foo2 = numbers3 + numbers
println(foo2[5])
```

Try different combinations of nested arrays ans lists. As in other programming languages, arrya elements could be lists and viceversa.

```kotlin
val numbers = intArrayOf(1, 2, 3)
val oceans = listOf("Atlantic", "Pacific")
val oddList = listOf(numbers, oceans, "salmon")
println(oddList)
```

Arrays initialization:

```kotlin
val array = Array (5) { it * 2 }
println(java.util.Arrays.toString(array))
```

## Array iterations

```kotlin
val school = arrayOf("shark", "salmon", "minnow")
for (element in school) {
    print(element + " ")
}

for ((index, element) in school.withIndex()) {
    println("Item at $index is $element\n")
}

for (i in 1..5) print(i)

for (i in 5 downTo 1) print(i)

for (i in 3..6 step 2) print(i)

for (i in 'd'..'g') print (i)

var bubbles = 0
while (bubbles < 50) {
    bubbles++
}

println("$bubbles bubbles in the water\n")

do {
    bubbles--
} while (bubbles > 50)
println("$bubbles bubbles in the water\n")

repeat(2) {
    println("A fish is swimming")
}
```

## Functions

```kotlin
fun main(args: Array<String>) {
    println("Hello, world!")
}

fun printHello() {
    println ("Hello World")
}

printHello()
```

## Parameter passing in main method

```kotlin
fun main(args: Array<String>) {
    println("Hello, ${args[0]}")
}
```

Examples:
```kotlin
fun feedTheFish() {
    val day = randomDay()
    val food = "pellets"
    println ("Today is $day and the fish eat $food")
}

fun main(args: Array<String>) {
    feedTheFish()
}

fun randomDay() : String {
    val week = arrayOf ("Monday", "Tuesday", "Wednesday", "Thursday",
            "Friday", "Saturday", "Sunday")
    return week[Random().nextInt(week.size)]
}

fun fishFood (day : String) : String {
    var food = ""
    when (day) {
        "Monday" -> food = "flakes"
        "Tuesday" -> food = "pellets"
        "Wednesday" -> food = "redworms"
        "Thursday" -> food = "granules"
        "Friday" -> food = "mosquitoes"
        "Saturday" -> food = "lettuce"
        "Sunday" -> food = "plankton"
    }
    return food
}

fun feedTheFish() {
    val day = randomDay()
    val food = fishFood(day)

    println ("Today is $day and the fish eat $food")
}

fun fishFood (day : String) : String {
    val food : String
    when (day) {
        "Monday" -> food = "flakes"
        "Wednesday" -> food = "redworms"
        "Thursday" -> food = "granules"
        "Friday" -> food = "mosquitoes"
        "Sunday" -> food = "plankton"
        else -> food = "nothing"
    }
    return food
}

fun fishFood (day : String) : String {
    return when (day) {
        "Monday" -> "flakes"
        "Wednesday" -> "redworms"
        "Thursday" -> "granules"
        "Friday" -> "mosquitoes"
        "Sunday" -> "plankton"
        else -> "nothing"
    }
}
```

## Default values

```kotlin
fun swim(speed: String = "fast") {
   println("swimming $speed")
}
```

## Required parameters

```kotlin
fun shouldChangeWater (day: String, temperature: Int = 22, dirty: Int = 20): Boolean {
    return when {
        temperature > 30 -> true
        dirty > 30 -> true
        day == "Sunday" ->  true
        else -> false
    }
}

fun feedTheFish() {
    val day = randomDay()
    val food = fishFood(day)
    println ("Today is $day and the fish eat $food")
    println("Change water: ${shouldChangeWater(day)}")
}
```

## Compact functions

```kotlin
fun isTooHot(temperature: Int) = temperature > 30

fun isDirty(dirty: Int) = dirty > 30

fun isSunday(day: String) = day == "Sunday"

fun shouldChangeWater (day: String, temperature: Int = 22, dirty: Int = 20): Boolean {
    return when {
        isTooHot(temperature) -> true
        isDirty(dirty) -> true
        isSunday(day) -> true
        else  -> false
    }
}
```

## Filters

Filters are an easy way to get a part of list based on some condition (or state).

Let's define collection:

```kotlin
val decorations = listOf ("rock", "pagoda", "plastic plant", "alligator", "flowerpot")
```

<span style="color:blue">How to print symbols with 'p'<span>

## Eager and lazy filters

By default, filters are eager meaning every time a collection is created when filter is used.

```kotlin
fun main() {
    val decorations = listOf ("rock", "pagoda", "plastic plant", "alligator", "flowerpot")

    // eager, creates a new list
    val eager = decorations.filter { it [0] == 'p' }
    println("eager: $eager")
}
```

In order to have a lazy filter, asSequence should be used for definition:

```kotlin
val filtered = decorations.asSequence().filter { it[0] == 'p' }
    println("filtered: $filtered")

val newList = filtered.toList()
    println("new list: $newList")
```

## Element transformers

Example 1:

```kotlin
val lazyMap = decorations.asSequence().map {
        println("access: $it")
        it
    }

println("lazy: $lazyMap")
println("-----")
println("first: ${lazyMap.first()}")
println("-----")
println("all: ${lazyMap.toList()}")
```

Example 2:

```kotlin
val lazyMap2 = decorations.asSequence().filter {it[0] == 'p'}.map {
        println("access: $it")
        it
    }
    println("-----")
    println("filtered: ${lazyMap2.toList()}")
```

## Lambda expressions

In addition to traditional functions, Kotlin supports lambdas. A lambda is an expression that performs a function, but instead of declaring a named function, unnamed function is declared. The lambda expression can be passed as data. In other programming languages, lambdas are called anonymous functions, function literals, or similar names.

Example:

```kotlin
var dirtyLevel = 20
val waterFilter = { dirty : Int -> dirty / 2}
println(waterFilter(dirtyLevel))
```

Lambda definition:

```kotlin
val waterFilter: (Int) -> Int = { dirty -> dirty / 2 }
```

In example above, variable waterFilter is created. This variable could be any function with int input and output. Lambda function is assigned to that variable, returning argument value divided by dirty/2.

## Functions with Lambda parameter

```kotlin
fun updateDirty(dirty: Int, operation: (Int) -> Int): Int {
   return operation(dirty)
}

val waterFilter: (Int) -> Int = { dirty -> dirty / 2 }
println(updateDirty(30, waterFilter))
```

Named function passed

```kotlin
fun increaseDirty( start: Int ) = start + 1

println(updateDirty(15, ::increaseDirty))
```

Example
```kotlin
var dirtyLevel = 19;
dirtyLevel = updateDirty(dirtyLevel) { dirtyLevel -> dirtyLevel + 23}
println(dirtyLevel)
```
