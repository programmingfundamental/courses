---
title: Въведение в Kotlin
sidebar:
  order: 2
---

# Въведение в Kotlin

Кратките блокове с изрази се изпълняват поотделно в Kotlin скрипт (`.kts`) или в тялото на `main()`. Блоковете с декларация на `main()` са самостоятелни програми. Алтернативните реализации не се добавят едновременно в един файл.

## Оператори и типове данни

Аритметичните оператори са `+`, `-`, `*`, `/` и `%` (остатък от деление). Целочислените типове включват `Byte`, `Short`, `Int` и `Long`, а типовете с плаваща запетая — `Float` и `Double`. Например `1L` е литерал от тип `Long`, `1.5f` — от тип `Float`, а `1.5` — от тип `Double`.

При присвояване на стойност от един числов тип на друг Kotlin изисква явно преобразуване. Целочислен литерал като `1` може да се присвои на `Byte`, ако се побира в диапазона му.

```kotlin
val i: Int = 6
val b1 = i.toByte()
println(b1)

val b2: Byte = 1
println(b2)
```

Следният код не се компилира: стойността от тип `Byte` не се преобразува автоматично в `Int`, `String` или `Double`.

```kotlin
val b2: Byte = 1
val i1: Int = b2

val i2: String = b2

val i3: Double = b2
```

Правилен вариант с явно преобразуване:

```kotlin
val b2: Byte = 1
val i4: Int = b2.toInt() 
println(i4)

val i5: String = b2.toString()
println(i5)

val i6: Double = b2.toDouble()
println(i6)
```

Преобразуването към по-тесен числов тип, например с `toByte()`, може да загуби информация, ако стойността е извън неговия диапазон.

При декларация с `val` стойността не може да бъде присвоена повторно. Това не означава, че самият обект е неизменяем. При декларация с `var` е разрешено повторно присвояване.

Следният код не се компилира, защото `aquarium` е декларирана с `val`:

```kotlin
var fish = 1
fish = 2
val aquarium = 1
aquarium = 2
```

Ако стойността трябва да се променя, правилният вариант е:

```kotlin
var aquarium = 1
aquarium = 2
```

Низовете (`String`) се ограждат с двойни кавички, а единичните символи (`Char`) — с единични кавички. Операторът `+` обединява низове. В шаблон на низ `$name` включва стойност на променлива, а `${expression}` — резултат от израз.

```kotlin
val numberOfFish = 5
val numberOfPlants = 12
"I have $numberOfFish fish" + " and $numberOfPlants plants"

"I have ${numberOfFish + numberOfPlants} fish and plants"
```

## Сравняване

Резултатът от сравнение е от тип `Boolean`. Операторите за сравнение включват `<`, `<=`, `>`, `>=`, `==` и `!=`.

Пример:

```kotlin
val numberOfFish = 50
val numberOfPlants = 23
if (numberOfFish > numberOfPlants) {
    println("Good ratio!") 
} else {
    println("Unhealthy ratio")
}
```

### Проверка за принадлежност към диапазон

```kotlin
val fish = 50
if (fish in 1..100) {
    println(fish)
}
```

### Допълнителни условия

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
### Условен израз `when`

```kotlin
val numberOfFish = 50
when (numberOfFish) {
    0  -> println("Empty tank")
    in 1..39 -> println("Got fish!")
    else -> println("That's a lot of fish!")
}
```

## Типове, допускащи `null`

### Тип, който не допуска `null`

Типът `Int` не допуска `null`. Следният код не се компилира:

```kotlin
var rocks: Int = null
```

Валидна декларация е `var rocks: Int = 0`.

### Тип, който допуска `null` чрез `?`

Знакът `?` след името на типа разрешава стойност `null`, която означава липса на стойност, а не числото нула.

```kotlin
var marbles: Int? = null
```

### Проверка за `null`

```kotlin
var fishFoodTreats: Int? = 6
if (fishFoodTreats != null) {
    fishFoodTreats = fishFoodTreats.dec()
}
```

### Safe-call оператор `?.`

Операторът извиква метода само ако стойността е различна от `null`; в противен случай резултатът е `null`.

```kotlin
var fishFoodTreats: Int? = null
fishFoodTreats = fishFoodTreats?.dec()
```

### Elvis оператор `?:`

При резултат `null` отляво се използва стойността отдясно:

```kotlin
val fishFoodTreats: Int? = null
val remainingTreats: Int = fishFoodTreats?.dec() ?: 0
println(remainingTreats)
```

### Оператор `!!`

Операторът принудително третира стойността като различна от `null`. Ако тя е `null`, възниква `NullPointerException`. Следната функция се компилира, но извикването `textLength(null)` би предизвикало това изключение:

```kotlin
fun textLength(s: String?): Int {
    return s!!.length
}
```

## Колекции

`List<T>` предоставя достъп за четене до списък. `MutableList<T>` позволява добавяне, премахване и замяна на елементи. `Array<T>` има фиксиран размер, но елементите му могат да се променят. Специализираните масиви като `IntArray` съхраняват стойности от конкретен числов тип. `Sequence<T>` позволява отложена обработка на елементи и е разгледана след операциите върху списъци.

Дефиниране на списък с `listOf()`:

```kotlin
val school = listOf("mackerel", "trout", "halibut")
println(school)
```

Дефиниране на изменяем списък с `mutableListOf()`:

```kotlin
val myList = mutableListOf("tuna", "salmon", "shark")
myList.remove("shark")
```

Дефиниране на масив с `arrayOf()` и `intArrayOf()`:

```kotlin
val school = arrayOf("shark", "salmon", "minnow")
println(java.util.Arrays.toString(school))

val mix = arrayOf<Any>("fish", 2)

val numbers = intArrayOf(1,2,3)

```

Операторът `+` създава нов масив с елементите на двата масива:

```kotlin
val numbers = intArrayOf(1,2,3)
val numbers3 = intArrayOf(4,5,6)
val foo2 = numbers3 + numbers
println(foo2[5])
```

Масивите и списъците могат да бъдат вложени. Вложеният масив остава отделен елемент; съдържанието му не се обединява автоматично с външния масив. Елементите на масив могат да бъдат списъци и обратно.

```kotlin
val numbers = intArrayOf(1, 2, 3)
val oceans = listOf("Atlantic", "Pacific")
val oddList = listOf(numbers, oceans, "salmon")
println(oddList)
```

Инициализиране на масив 

```kotlin
val array = Array (5) { it * 2 }
println(java.util.Arrays.toString(array))
```

## Обхождане на масив

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

## Функции

```kotlin
fun main(args: Array<String>) {
    println("Hello, world!")
    printHello()
}

fun printHello() {
    println ("Hello World")
}

```

## Предаване на аргументи в `main()`

Първият аргумент се използва само ако е подаден, за да се избегне достъп извън границите на масива.

```kotlin
fun main(args: Array<String>) {
    val name = args.firstOrNull() ?: "world"
    println("Hello, $name")
}
```

### Примерни функции

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

Функцията за избор на храна може да присвоява стойност според деня:

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

Следващият вариант заменя предишната `feedTheFish()` и използва вече дефинираните `randomDay()` и `fishFood()`:

```kotlin
fun feedTheFish() {
    val day = randomDay()
    val food = fishFood(day)

    println ("Today is $day and the fish eat $food")
}
```

Алтернативна реализация на `fishFood()` с `val` и клон `else`:

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

Същата алтернатива може да върне директно резултата от `when`:

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

## Стойност по подразбиране

```kotlin
fun swim(speed: String = "fast") {
   println("swimming $speed")
}
```

## Задължителни параметри

Параметърът `day` е задължителен, а останалите имат стойности по подразбиране. Вариантът на `feedTheFish()` заменя предишния и използва `randomDay()` и `fishFood()` от горните примери.

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

## Компактни функции

Функция с един израз може да се запише чрез `=`. Следващият вариант заменя предишната `shouldChangeWater()`:

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

## Филтри

Функцията `filter` избира елементите на колекция, които удовлетворяват условие.

Примерна колекция:

```kotlin
val decorations = listOf ("rock", "pagoda", "plastic plant", "alligator", "flowerpot")
```

Да се изведат само стойностите, които започват с `'p'`.

## Незабавна и отложена обработка на колекции

Операции като `filter` и `map` върху обикновени колекции се изпълняват незабавно и създават нови колекции. При верига от такива операции обикновено се получават междинни резултати.

```kotlin
fun main() {
    val decorations = listOf ("rock", "pagoda", "plastic plant", "alligator", "flowerpot")

    // eager, creates a new list
    val eager = decorations.filter { it [0] == 'p' }
    println("eager: $eager")
}
```

Чрез `asSequence()` обработката се отлага. `filter` и `map` описват операциите, а изпълнението започва при крайна (terminal) операция като `toList()` или `first()`.

```kotlin
val decorations = listOf("rock", "pagoda", "plastic plant", "alligator", "flowerpot")
val filtered = decorations.asSequence().filter { it[0] == 'p' }
println("filtered: $filtered")

val newList = filtered.toList()
println("new list: $newList")
```

## Трансформиране на елементи

`map` преобразува всеки елемент. В примера стойността се връща непроменена, а съобщението показва кога се изпълнява обработката. Използва се колекцията `decorations` от предходния пример. `first()` обработва необходимите елементи до първия резултат, а `toList()` обхожда последователността отначало и събира всички резултати.

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

Пример 2:

```kotlin
val lazyMap2 = decorations.asSequence().filter {it[0] == 'p'}.map {
    println("access: $it")
    it
}
println("-----")
println("filtered: ${lazyMap2.toList()}")
```

## Ламбди

Ламбда изразът представлява анонимна функция, която може да бъде съхранявана в променлива, подавана като аргумент или връщана като резултат.

Пример

```kotlin
var dirtyLevel = 20
val waterFilter = { dirty : Int -> dirty / 2}
println(waterFilter(dirtyLevel))
```

Дефиниране на ламбда израз с явно зададен тип:

```kotlin
val waterFilter: (Int) -> Int = { dirty -> dirty / 2 }
```

Променливата `waterFilter` съхранява функция от тип `(Int) -> Int`: тя приема `Int` и връща `Int`. Присвоеният ламбда израз връща резултата от целочисленото деление на аргумента `dirty` на две.

## Функции, приемащи ламбда израз като аргумент

Функция от по-висок ред приема друга функция като аргумент или връща функция. В примера `updateDirty()` извиква подадената `operation`.

```kotlin
fun updateDirty(dirty: Int, operation: (Int) -> Int): Int {
   return operation(dirty)
}

val waterFilter: (Int) -> Int = { dirty -> dirty / 2 }
println(updateDirty(30, waterFilter))
```

Предаване на именувана функция към вече дефинираната `updateDirty()` чрез референция с `::`:

```kotlin
fun increaseDirty( start: Int ) = start + 1

println(updateDirty(15, ::increaseDirty))
```

Ламбда изразът може да се постави след скобите, когато е последният аргумент на `updateDirty()`:

```kotlin
var dirtyLevel = 19;
dirtyLevel = updateDirty(dirtyLevel) { dirtyLevel -> dirtyLevel + 23}
println(dirtyLevel)
```
