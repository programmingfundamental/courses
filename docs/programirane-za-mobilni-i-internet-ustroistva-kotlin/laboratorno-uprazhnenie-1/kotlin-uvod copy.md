---
layout: default
title: Пример за Kotlin код
parent: Лабораторно упражнение 1
grand_parent: Програмиране за мобилни и Интернет устройства
nav_order: 2
---

## Оператори и типоведанни

Както при другите езици, Kotlin използва + , - и *, / p. Kotlin също така поддържа различни типове числа, Int, Long, Double, Float

Kotlin не преобразува имплицитно между числови типове. Винаги можете да зададете стойности от различни типове чрез кастиране.

Проверете със следните примери:

val i: Int = 6
val b1 = i.toByte()
println(b1)

val b2: Byte = 1
println(b2)

val i1: Int = b2

val i2: String = b2

val i3: Double = b2

Защо се получиха такива резултати

Променете примера:

val i4: Int = b2.toInt() 
println(i4)

val i5: String = b2.toString()
println(i5)

val i6: Double = b2.toDouble()
println(i6)

Kotlin поддържа два вида променливи: изменяеми и не неизменяеми

Неизменяеми с ```val``` можете да зададете стойност веднъж. Ако се опитате да зададете нещо отново, ще получите грешка.

Изменящи се с var можете да зададете стойност, след което да промените стойността по-късно в програмата.

Проверете следния пример:

var fish = 1
fish = 2
val aquarium = 1
aquarium = 2

Низовете в Kotlin работят почти като низове във всеки друг език за програмиране. " - се използват за низове, ' - се използва за еденични символи, + - обедидение, $ - служи за създаване на шаблони, {} - използва се за изрази в шаблон

Проверете примера:

val numberOfFish = 5
val numberOfPlants = 12
"I have $numberOfFish fish" + " and $numberOfPlants plants"

"I have ${numberOfFish + numberOfPlants} fish and plants"

## Сравняване

В тази задача научавате за булевите стойности и проверката на условията в езика за програмиране Kotlin. Подобно на други езици, Kotlin има булеви и булеви оператори като по-малко от, равно на, по-голямо от и т.н. (.<==>!=<=>=)

Пример:

val numberOfFish = 50
val numberOfPlants = 23
if (numberOfFish > numberOfPlants) {
    println("Good ratio!") 
} else {
    println("Unhealthy ratio")
}

Израс с диапазон

val fish = 50
if (fish in 1..100) {
    println(fish)
}

Допълнителни условия

if (numberOfFish == 0) {
    println("Empty tank")
} else if (numberOfFish < 40) {
    println("Got fish!")
} else {
    println("That's a lot of fish!")
}

Твърдение:

when (numberOfFish) {
    0  -> println("Empty tank")
    in 1..39 -> println("Got fish!")
    else -> println("That's a lot of fish!")
}

## Nullabel

По подразбиране променливите не могат да бъдат null

Проверете с примера:

var rocks: Int = null

Използвайте оператора за въпросителен знак след типа, за да покажете, че променливата може да бъде нула

var marbles: Int? = null

Оператор ?

Пример:

var fishFoodTreats = 6
if (fishFoodTreats != null) {
    fishFoodTreats = fishFoodTreats.dec()
}

var fishFoodTreats = 6
fishFoodTreats = fishFoodTreats?.dec()

Оператор  ?:

fishFoodTreats = fishFoodTreats?.dec() ?: 0

Оператор !!

val len = s!!.length

## Колекции

Дефиниране на списък с listOf

val school = listOf("mackerel", "trout", "halibut")
println(school)

Дефиниране на списък с mutableListOf

val myList = mutableListOf("tuna", "salmon", "shark")
myList.remove("shark")

Дефиниране на масив с arrayOf, intArrayOf

val school = arrayOf("shark", "salmon", "minnow")
println(java.util.Arrays.toString(school))

val mix = arrayOf("fish", 2)

val numbers = intArrayOf(1,2,3)

Оператор +

val numbers = intArrayOf(1,2,3)
val numbers3 = intArrayOf(4,5,6)
val foo2 = numbers3 + numbers
println(foo2[5])

Изпробвайте различни комбинации от вложени масиви и списъци. Както и в други езици, можете да влагате масиви и списъци. Тоест, когато поставите масив в масив, имате масив от масиви, а не сплескан масив от съдържанието на двете. Елементите на масива също могат да бъдат списъци, а елементите на списъците могат да бъдат масиви.

val numbers = intArrayOf(1, 2, 3)
val oceans = listOf("Atlantic", "Pacific")
val oddList = listOf(numbers, oceans, "salmon")
println(oddList)

Инициализиране на масив 

val array = Array (5) { it * 2 }
println(java.util.Arrays.toString(array))

## Обхождане на масив

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

## Функциии

fun main(args: Array<String>) {
    println("Hello, world!")
}

fun printHello() {
    println ("Hello World")
}

printHello()

## Предаване на аргоменти в main

fun main(args: Array<String>) {
    println("Hello, ${args[0]}")
}

Примерни функции

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

## Стойност по подразбиране

fun swim(speed: String = "fast") {
   println("swimming $speed")
}

## Задължителни параметри

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

## Компактни функции

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

## Филтри

Филтрите са удобен начин да получите част от списък въз основа на някакво състояние.

Да дефинираме колекция:

val decorations = listOf ("rock", "pagoda", "plastic plant", "alligator", "flowerpot")

Как може да отпечатаме само стойностите с 'p'

## Нетърпеливи и мързеливи филтри

Филтрите по подразбиране на нетърпеливи, това означава, че всеки път когато се изполват се създава колекция.

fun main() {
    val decorations = listOf ("rock", "pagoda", "plastic plant", "alligator", "flowerpot")

    // eager, creates a new list
    val eager = decorations.filter { it [0] == 'p' }
    println("eager: $eager")
}

За да бъде един филтър мързелив трябва да се дефинира с asSequence

val filtered = decorations.asSequence().filter { it[0] == 'p' }
    println("filtered: $filtered")

val newList = filtered.toList()
    println("new list: $newList")

## Трансформиране на елементи

val lazyMap = decorations.asSequence().map {
        println("access: $it")
        it
    }

println("lazy: $lazyMap")
println("-----")
println("first: ${lazyMap.first()}")
println("-----")
println("all: ${lazyMap.toList()}")

Пример 2:

val lazyMap2 = decorations.asSequence().filter {it[0] == 'p'}.map {
        println("access: $it")
        it
    }
    println("-----")
    println("filtered: ${lazyMap2.toList()}")


## Ламбди

В допълнение към традиционните именувани функции, Kotlin поддържа ламбда. Ламбда е израз, който прави функция. Но вместо да декларирате именувана функция, вие декларирате функция, която няма име. Част от това, което прави това полезно, е, че ламбда изразът вече може да се предава като данни. В други езици ламбдите се наричат анонимни функции, функционални литерали или подобни имена.

Пример

var dirtyLevel = 20
val waterFilter = { dirty : Int -> dirty / 2}
println(waterFilter(dirtyLevel))

Дефиниране на Ламбда

val waterFilter: (Int) -> Int = { dirty -> dirty / 2 }

Направете променлива, наречена waterFilter
waterFilter може да бъде всяка функция, която приема Int и връща Int
Присвояване на ламбда на waterFilter
Ламбда връща стойността на аргумента, разделена на dirty / 2

## Функции приемащи Ламбда параметър

fun updateDirty(dirty: Int, operation: (Int) -> Int): Int {
   return operation(dirty)
}

val waterFilter: (Int) -> Int = { dirty -> dirty / 2 }
println(updateDirty(30, waterFilter))

Предаване на именна функция

fun increaseDirty( start: Int ) = start + 1

println(updateDirty(15, ::increaseDirty))

Пример

var dirtyLevel = 19;
dirtyLevel = updateDirty(dirtyLevel) { dirtyLevel -> dirtyLevel + 23}
println(dirtyLevel)