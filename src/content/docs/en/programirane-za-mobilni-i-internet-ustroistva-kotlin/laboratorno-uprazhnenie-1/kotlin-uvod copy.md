---
title: Introduction to Kotlin
sidebar:
  order: 2
---

# Introduction to Kotlin

Run the short expression blocks separately in a Kotlin script (`.kts`) or inside `main()`. Blocks that declare `main()` are standalone programs. Do not place alternative implementations in the same file at the same time.

## Operators and data types

The arithmetic operators are `+`, `-`, `*`, `/`, and `%` (remainder). Integer types include `Byte`, `Short`, `Int`, and `Long`; floating-point types are `Float` and `Double`. For example, `1L` is a `Long` literal, `1.5f` is a `Float` literal, and `1.5` is a `Double` literal.

Kotlin requires explicit conversion when assigning a value of one numeric type to another. An integer literal such as `1` can be assigned to a `Byte` if it fits within its range.

```kotlin
val i: Int = 6
val b1 = i.toByte()
println(b1)

val b2: Byte = 1
println(b2)
```

The following code does not compile: a `Byte` value is not automatically converted to `Int`, `String`, or `Double`.

```kotlin
val b2: Byte = 1
val i1: Int = b2

val i2: String = b2

val i3: Double = b2
```

A correct version with explicit conversion:

```kotlin
val b2: Byte = 1
val i4: Int = b2.toInt() 
println(i4)

val i5: String = b2.toString()
println(i5)

val i6: Double = b2.toDouble()
println(i6)
```

Converting to a narrower numeric type, for example with `toByte()`, may lose information if the value is outside its range.

A value declared with `val` cannot be reassigned. This does not mean that the object itself is immutable. A declaration with `var` allows reassignment.

The following code does not compile because `aquarium` is declared with `val`:

```kotlin
var fish = 1
fish = 2
val aquarium = 1
aquarium = 2
```

If the value needs to change, the correct version is:

```kotlin
var aquarium = 1
aquarium = 2
```

Strings (`String`) use double quotes, while individual characters (`Char`) use single quotes. The `+` operator concatenates strings. In a string template, `$name` inserts the value of a variable, while `${expression}` inserts the result of an expression.

```kotlin
val numberOfFish = 5
val numberOfPlants = 12
"I have $numberOfFish fish" + " and $numberOfPlants plants"

"I have ${numberOfFish + numberOfPlants} fish and plants"
```

## Comparisons

A comparison produces a `Boolean` result. Comparison operators include `<`, `<=`, `>`, `>=`, `==`, and `!=`.

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

### Checking whether a value belongs to a range

```kotlin
val fish = 50
if (fish in 1..100) {
    println(fish)
}
```

### Additional conditions

```kotlin
val numberOfFish = 50
if (numberOfFish == 0) {
    println("Empty tank")
} else if (numberOfFish < 40) {
    println("Got fish!")
} else {
    println("That's a lot of fish!")
}
```
### The `when` conditional expression

```kotlin
val numberOfFish = 50
when (numberOfFish) {
    0  -> println("Empty tank")
    in 1..39 -> println("Got fish!")
    else -> println("That's a lot of fish!")
}
```

## Nullable types

### A non-nullable type

The `Int` type does not allow `null`. The following code does not compile:

```kotlin
var rocks: Int = null
```

A valid declaration is `var rocks: Int = 0`.

### A nullable type using `?`

A `?` after the type name allows `null`, which represents the absence of a value, rather than the number zero.

```kotlin
var marbles: Int? = null
```

### Checking for `null`

```kotlin
var fishFoodTreats: Int? = 6
if (fishFoodTreats != null) {
    fishFoodTreats = fishFoodTreats.dec()
}
```

### The safe-call operator `?.`

The operator calls the method only if the value is not `null`; otherwise, the result is `null`.

```kotlin
var fishFoodTreats: Int? = null
fishFoodTreats = fishFoodTreats?.dec()
```

### The Elvis operator `?:`

If the result on the left is `null`, the value on the right is used:

```kotlin
val fishFoodTreats: Int? = null
val remainingTreats: Int = fishFoodTreats?.dec() ?: 0
println(remainingTreats)
```

### The `!!` operator

This operator forces a value to be treated as non-null. If it is `null`, a `NullPointerException` is thrown. The following function compiles, but calling `textLength(null)` would throw this exception:

```kotlin
fun textLength(s: String?): Int {
    return s!!.length
}
```

## Collections

`List<T>` provides read-only access to a list. `MutableList<T>` allows elements to be added, removed, and replaced. `Array<T>` has a fixed size, but its elements can be changed. Specialized arrays such as `IntArray` store values of a specific numeric type. `Sequence<T>` allows lazy processing of elements and is covered after list operations.

Defining a list with `listOf()`:

```kotlin
val school = listOf("mackerel", "trout", "halibut")
println(school)
```

Defining a mutable list with `mutableListOf()`:

```kotlin
val myList = mutableListOf("tuna", "salmon", "shark")
myList.remove("shark")
```

Defining an array with `arrayOf()` and `intArrayOf()`:

```kotlin
val school = arrayOf("shark", "salmon", "minnow")
println(java.util.Arrays.toString(school))

val mix = arrayOf<Any>("fish", 2)

val numbers = intArrayOf(1,2,3)

```

The `+` operator creates a new array containing the elements of both arrays:

```kotlin
val numbers = intArrayOf(1,2,3)
val numbers3 = intArrayOf(4,5,6)
val foo2 = numbers3 + numbers
println(foo2[5])
```

Arrays and lists can be nested. A nested array remains a separate element; its contents are not automatically merged into the outer array. Array elements can be lists, and vice versa.

```kotlin
val numbers = intArrayOf(1, 2, 3)
val oceans = listOf("Atlantic", "Pacific")
val oddList = listOf(numbers, oceans, "salmon")
println(oddList)
```

Initializing an array

```kotlin
val array = Array (5) { it * 2 }
println(java.util.Arrays.toString(array))
```

## Iterating over an array

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
    printHello()
}

fun printHello() {
    println ("Hello World")
}

```

## Passing arguments to `main()`

The first argument is used only if it has been supplied, to avoid accessing an element outside the array bounds.

```kotlin
fun main(args: Array<String>) {
    val name = args.firstOrNull() ?: "world"
    println("Hello, $name")
}
```

### Example functions

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
    return week[kotlin.random.Random.nextInt(week.size)]
}
```

The function that selects food can assign a value based on the day:

```kotlin
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
```

The following version replaces the previous `feedTheFish()` and uses the previously defined `randomDay()` and `fishFood()`:

```kotlin
fun feedTheFish() {
    val day = randomDay()
    val food = fishFood(day)

    println ("Today is $day and the fish eat $food")
}
```

An alternative implementation of `fishFood()` with `val` and an `else` branch:

```kotlin
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
```

The same alternative can return the result of `when` directly:

```kotlin
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

The `day` parameter is required, while the other parameters have default values. This version of `feedTheFish()` replaces the previous one and uses `randomDay()` and `fishFood()` from the examples above.

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

A single-expression function can be written using `=`. The following version replaces the previous `shouldChangeWater()`:

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

The `filter` function selects collection elements that satisfy a condition.

Example collection:

```kotlin
val decorations = listOf ("rock", "pagoda", "plastic plant", "alligator", "flowerpot")
```

Print only the values that start with `'p'`.

## Eager and lazy collection processing

Operations such as `filter` and `map` on ordinary collections run eagerly and create new collections. Chaining these operations usually produces intermediate results.

```kotlin
fun main() {
    val decorations = listOf ("rock", "pagoda", "plastic plant", "alligator", "flowerpot")

    // eager, creates a new list
    val eager = decorations.filter { it [0] == 'p' }
    println("eager: $eager")
}
```

With `asSequence()`, processing is deferred. `filter` and `map` describe the operations, and execution begins with a terminal operation such as `toList()` or `first()`.

```kotlin
val decorations = listOf("rock", "pagoda", "plastic plant", "alligator", "flowerpot")
val filtered = decorations.asSequence().filter { it[0] == 'p' }
println("filtered: $filtered")

val newList = filtered.toList()
println("new list: $newList")
```

## Transforming elements

`map` transforms each element. In this example, the value is returned unchanged, while the message shows when processing occurs. It uses the `decorations` collection from the previous example. `first()` processes the elements needed to obtain the first result, while `toList()` traverses the sequence again from the beginning and collects all results.

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

## Lambdas

A lambda expression is an anonymous function that can be stored in a variable, passed as an argument, or returned as a result.

Example

```kotlin
var dirtyLevel = 20
val waterFilter = { dirty : Int -> dirty / 2}
println(waterFilter(dirtyLevel))
```

Defining a lambda expression with an explicit type:

```kotlin
val waterFilter: (Int) -> Int = { dirty -> dirty / 2 }
```

The `waterFilter` variable stores a function of type `(Int) -> Int`: it takes an `Int` and returns an `Int`. The assigned lambda expression returns the result of integer division of the `dirty` argument by two.

## Functions that take a lambda expression as an argument

A higher-order function takes another function as an argument or returns a function. In this example, `updateDirty()` calls the supplied `operation`.

```kotlin
fun updateDirty(dirty: Int, operation: (Int) -> Int): Int {
   return operation(dirty)
}

val waterFilter: (Int) -> Int = { dirty -> dirty / 2 }
println(updateDirty(30, waterFilter))
```

Passing a named function to the previously defined `updateDirty()` using a `::` reference:

```kotlin
fun increaseDirty( start: Int ) = start + 1

println(updateDirty(15, ::increaseDirty))
```

A lambda expression can be placed after the parentheses when it is the last argument of `updateDirty()`:

```kotlin
var dirtyLevel = 19;
dirtyLevel = updateDirty(dirtyLevel) { dirtyLevel -> dirtyLevel + 23}
println(dirtyLevel)
```
