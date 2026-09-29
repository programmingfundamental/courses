---
title: Лабораторно упражнение 2
sidebar:
  order: 2
---

# Лабораторно упражнение 2

## Класове и обекти

Клас се декларира с `class`, име и при необходимост параметри на конструктора и тяло във фигурни скоби. Когато няма тяло, скобите могат да се пропуснат. Всеки кодов блок е самостоятелен пример; повтарящите се имена не се добавят едновременно в един файл.

```kotlin
class Customer                                  // 1

class Contact(val id: Int, var email: String)   // 2

fun main() {

    val customer = Customer()                   // 3

    val contact = Contact(1, "mary@gmail.com")  // 4

    println(contact.id)                         // 5
    contact.email = "jane@gmail.com"            // 6
}
```

1. Декларира се `Customer` с конструктор без параметри.
2. `Contact` има първичен конструктор с два параметъра. `val id` и `var email` едновременно декларират свойства.
3. Обект се създава чрез извикване на конструктора, без ключова дума `new`.
4. На конструктора на `Contact` се подават два аргумента.
5. Прочита се `id`.
6. Променя се `email`.

## Свойства

```kotlin
class Customer(initialEmail: String) {
    var email: String = initialEmail
        get() = field
        set(value) {
            field = value.trim()
        }
}
```

`field` е полето за съхранение (backing field) на свойството. Getter прочита стойността, а setter я променя. Обръщение към самото свойство в неговия getter или setter може да предизвика рекурсивно извикване. Инициализацията използва `initialEmail` директно; `trim()` се изпълнява при последващо присвояване.

## Конструктори

Първичният конструктор (`primary constructor`) се задава след името на класа. Параметър с `val` или `var` става свойство, а параметър без тях служи за инициализация. Блокът `init` съдържа код, изпълняван при създаване на обекта. Вторичният конструктор (`secondary constructor`) се декларира с `constructor(...)`. Ако има първичен конструктор, вторичният трябва да делегира към него пряко или чрез друг вторичен конструктор с `this(...)`.

```kotlin
class Contact(val id: Int, var email: String) {
    init {
        println("Created contact $id")
    }

    constructor(email: String) : this(0, email)
}

fun main() {
    val contact = Contact("mary@gmail.com")
    println(contact.email)
}
```

## Модификатори на видимост

- `public` е модификаторът по подразбиране. Декларацията е достъпна навсякъде, където е достъпен съдържащият я тип.
- `private` ограничава достъпа до съдържащия клас; за декларация на най-горно ниво (top-level) — до същия файл.
- `protected` позволява достъп от класа и неговите подкласове. Не се използва за top-level декларации.
- `internal` позволява достъп в същия модул — група файлове, компилирани заедно.

## Наследяване

Kotlin напълно поддържа традиционния обектно-ориентиран механизъм за наследяване.

```kotlin
open class Dog {                // 1
    open fun sayHello() {       // 2
        println("wow wow!")
    }
}

class Yorkshire : Dog() {       // 3
    override fun sayHello() {   // 4
        println("wif wif!")
    }
}

fun main() {
    val dog: Dog = Yorkshire()
    dog.sayHello()
}
```

1. Класовете са окончателни по подразбиране. Модификаторът `open` разрешава наследяване.
2. Метод с `open` може да бъде предефиниран в подклас.
3. `Yorkshire` наследява `Dog`, като `Dog()` извиква конструктора на базовия клас.
4. За предефиниране на метод или свойство се използва `override`.

## Наследяване с параметризиран конструктор

```kotlin
open class Tiger(val origin: String) {
    fun sayHello() {
        println("A tiger from $origin says: grrhhh!")
    }
}

class SiberianTiger : Tiger("Siberia")                  // 1

fun main() {
    val tiger: Tiger = SiberianTiger()
    tiger.sayHello()
}
```

Аргументите за параметризирания конструктор на базовия клас се подават в декларацията на подкласа.

## Предаване на аргументи на конструктора към суперклас

```kotlin
open class Lion(val name: String, val origin: String) {
    fun sayHello() {
        println("$name, the lion from $origin says: graoh!")
    }
}

class Asiatic(name: String) : Lion(name = name, origin = "India") // 1

fun main() {
    val lion: Lion = Asiatic("Rufo")                              // 2
    lion.sayHello()
}
```

## Абстрактни класове

Абстрактен клас се декларира с `abstract` и не може да има непосредствени екземпляри. Той може да бъде наследяван. Членовете му са абстрактни само ако изрично са означени с `abstract`; останалите могат да имат реализация.

```kotlin
abstract class Person(name: String) {

    init {
        println("My name is $name.")
    }

    fun displaySSN(ssn: Int) {
        println("My SSN is $ssn.")
    }

    abstract fun displayJob(description: String)
}

class Teacher(name: String): Person(name) {

    override fun displayJob(description: String) {
        println(description)
    }
}

fun main(args: Array<String>) {
    val jack = Teacher("Jack Smith")
    jack.displayJob("I'm a mathematics teacher.")
    jack.displaySSN(23123)
}
```

## Интерфейси

Интерфейсите могат да съдържат абстрактни методи и методи с реализация. Свойствата им нямат backing field: те са абстрактни или предоставят реализация на достъпа, която не съхранява собствена стойност.

```kotlin
interface Animal {

    val name: String

    fun move() : String

    fun hello() {
        println("Hello, I am ${this.name}")
    }
}

class Fish : Animal {

    override val name: String = "Dory"
    override fun move() = "just swim"

}

fun main(args: Array<String>) {
    val dory = Fish()

    println("name = ${dory.name}")
    print("Calling hello(): ")

    dory.hello()

    print("Calling and printing move(): ")
    println(dory.move())
}
```

## Класове за данни (`data class`)

Класът за данни (`data class`) съхранява стойности. Компилаторът генерира `equals()`, `hashCode()`, `toString()`, `componentN()` и `copy()` въз основа на свойствата от първичния конструктор. Следващият пример използва автоматично генерираното поведение.

```kotlin
data class User(val name: String, val id: Int)             // 1
fun main() {
    val user = User("Alex", 1)
    println(user)                                          // 3

    val secondUser = User("Alex", 1)
    val thirdUser = User("Max", 2)

    println("user == secondUser: ${user == secondUser}")   // 4
    println("user == thirdUser: ${user == thirdUser}")

    // hashCode() function
    println(user.hashCode())                               // 5
    println(secondUser.hashCode())
    println(thirdUser.hashCode())

    // copy() function
    println(user.copy())                                   // 6
    println(user === user.copy())                          // 7
    println(user.copy("Max"))                              // 8
    println(user.copy(id = 3))                             // 9

    println("name = ${user.component1()}")                 // 10
    println("id = ${user.component2()}")
}
```

1. `data class User` има свойствата `name` и `id`.
2. `equals()` и `hashCode()` се генерират съгласувано от двете свойства.
3. `println(user)` използва генерираната `toString()`.
4. Два обекта са равни чрез `==`, ако съвпадат и `name`, и `id`.
5. Равните обекти имат еднакъв `hashCode()`.
6. `copy()` създава нов екземпляр.
7. Операторът `===` сравнява референциите; копието е друг обект.
8. Позиционен аргумент в `copy()` променя съответното свойство в копието.
9. Именуван аргумент като `id = 3` посочва кое свойство се променя.
10. `component1()` и `component2()` връщат свойствата в реда на първичния конструктор.

## Изброими класове (`enum class`)

Изброим клас (`enum class`) представя краен набор от именувани стойности, например посоки, състояния или режими.

```kotlin
enum class State {
    IDLE, RUNNING, FINISHED                           // 1
}

fun main() {
    val state = State.RUNNING                         // 2
    val message = when (state) {                      // 3
        State.IDLE -> "It's idle"
        State.RUNNING -> "It's running"
        State.FINISHED -> "It's finished"
    }
    println(message)
}
```

1. `State` съдържа три различни константи.
2. Константа се достъпва чрез името на класа.
3. Изразът `when` е изчерпателен, защото обхваща всички константи, и не изисква `else`.

### Свойства и методи в `enum class`

Списъкът от константи се отделя от останалите членове с точка и запетая.

```kotlin
enum class Color(val rgb: Int) {                      // 1
    RED(0xFF0000),                                    // 2
    GREEN(0x00FF00),
    BLUE(0x0000FF),
    YELLOW(0xFFFF00);

    fun containsRed() = (this.rgb and 0xFF0000 != 0)  // 3
}

fun main() {
    val red = Color.RED
    println(red)                                      // 4
    println(red.containsRed())                        // 5
    println(Color.BLUE.containsRed())                 // 6
    println(Color.YELLOW.containsRed())               // 7
}
```

1. `Color` има свойство `rgb` и метод `containsRed()`.
2. Всяка константа подава стойност за `rgb` на конструктора.
3. Побитовата операция `and` проверява дали червеният компонент е ненулев.
4. `println(red)` извежда името `RED`.
5. Методът се извиква върху константата `red`.
6. `Color.BLUE.containsRed()` връща `false`.
7. Червеният компонент е ненулев при `RED` и `YELLOW`, затова проверката връща `true`.

## Запечатани класове (`sealed class`)

Запечатаният клас (`sealed class`) ограничава преките си наследници до същия пакет и модул. Те трябва да имат име и не могат да бъдат локални или анонимни класове. Това позволява на компилатора да провери дали `when` обхваща всички възможни случаи.

```kotlin

sealed class Mammal(val name: String)                                                   // 1

class Cat(val catName: String) : Mammal(catName)                                        // 2
class Human(val humanName: String, val job: String) : Mammal(humanName)

fun greetMammal(mammal: Mammal): String =
    when (mammal) {
        is Human -> "Hello ${mammal.name}; You are working as a ${mammal.job}"
        is Cat -> "Hello ${mammal.name}"
    }

fun main() {
    println(greetMammal(Cat("Snowy")))
}

```

`Mammal` е запечатан клас, а `Cat` и `Human` са неговите преки наследници. След проверка с `is` компилаторът разпознава конкретния тип (smart cast), например `Human`, и разрешава достъп до `job`. Изразът `when` е изчерпателен и не изисква `else`.

## Клас и обект

Класът описва структура и поведение, а обектът е негов екземпляр. От един клас могат да се създадат няколко обекта:

```kotlin

import java.util.Random

class LuckDispatcher {                    //1
    fun getNumber() {                     //2
        var objRandom = Random()
        println(objRandom.nextInt(90))
    }
}

fun main() {
    val d1 = LuckDispatcher()             //3
    val d2 = LuckDispatcher()

    d1.getNumber()                        //4
    d2.getNumber()
}

```

1. Декларира се клас `LuckDispatcher`.
2. Методът извежда случайно цяло число от 0 до 89.
3. Създават се два различни обекта.
4. Методът се извиква върху всеки обект.

Декларацията `object` създава един споделен екземпляр (singleton). Той се инициализира при първи достъп, като инициализацията е безопасна при работа с няколко нишки.

## Израз `object`

Изразът `object` създава анонимен обект с посочените членове. В примера той съхранява междинните стойности за изчисляване на наем:

```kotlin

fun rentPrice(standardDays: Int, festivityDays: Int, specialDays: Int): Unit {  //1

    val dayRates = object {                                                     //2
        var standard: Int = 30 * standardDays
        var festivity: Int = 50 * festivityDays
        var special: Int = 100 * specialDays
    }

    val total = dayRates.standard + dayRates.festivity + dayRates.special       //3

    print("Total price: $$total")                                               //4

}

fun main() {
    rentPrice(10, 2, 1)                                                         //5
}

```

1. Декларира се функция с три параметъра.
2. При извикване се създава анонимен обект със стойностите за отделните дни.
3. Сумата се изчислява чрез свойствата на обекта.
4. Резултатът се извежда.
5. Функцията се извиква с конкретен брой дни.

## Декларация `object`

Именуваната декларация `object` предоставя един споделен обект. До членовете му се достига чрез името му, без извикване на конструктор. Примерът използва демонстрационни стойности.

```kotlin

object DoAuth {                                                 //1
    fun takeParams(username: String, password: String) {        //2
        println("input Auth parameters = $username:$password")
    }
}

fun main(){
    DoAuth.takeParams("foo", "qwerty")                          //3
}

```

1. Създава декларация на обект.
2. Определя метода на обекта.
3. Извиква метода. Това е моментът, в който обектът действително е създаден.

## Придружаващ обект (`companion object`)

Придружаващият обект (`companion object`) се декларира в клас. Членовете му могат да се извикват чрез името на съдържащия клас. Те принадлежат на придружаващия обект, а не на отделните екземпляри на класа.

```kotlin

class BigBen {                                  //1
    companion object Bonger {                   //2
        fun getBongs(nTimes: Int) {             //3
            for (i in 1 .. nTimes) {
                print("BONG ")
            }
        }
    }
}

fun main() {
    BigBen.getBongs(12)                         //4
}

```

1. Дефинира клас.
2. Декларира придружаващ обект. Името `Bonger` може да се пропусне.
3. Дефинира метод на придружаващ обект.
4. Извиква метода на придружаващия обект чрез името на класа.
