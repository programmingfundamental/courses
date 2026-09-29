---
title: Lab 2
sidebar:
  order: 2
---

# Lab 2

## Classes and objects

A class is declared with `class`, a name, and, if needed, constructor parameters and a body in braces. When there is no body, the braces can be omitted. Each code block is a standalone example; do not place repeated names in the same file at the same time.

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

1. `Customer` is declared with a constructor without parameters.
2. `Contact` has a primary constructor with two parameters. `val id` and `var email` also declare properties.
3. An object is created by calling the constructor, without the `new` keyword.
4. Two arguments are passed to the `Contact` constructor.
5. `id` is read.
6. `email` is changed.

## Properties

```kotlin
class Customer(initialEmail: String) {
    var email: String = initialEmail
        get() = field
        set(value) {
            field = value.trim()
        }
}
```

`field` is the property's backing field. The getter reads its value, while the setter changes it. Referring to the property itself inside its getter or setter may cause a recursive call. Initialization uses `initialEmail` directly; `trim()` runs on subsequent assignments.

## Constructors

The primary constructor is specified after the class name. A parameter with `val` or `var` becomes a property, while a parameter without them is used for initialization. An `init` block contains code executed when the object is created. A secondary constructor is declared with `constructor(...)`. If there is a primary constructor, a secondary constructor must delegate to it directly or through another secondary constructor using `this(...)`.

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

## Visibility modifiers

- `public` is the default modifier. The declaration is accessible wherever its containing type is accessible.
- `private` restricts access to the containing class; for a top-level declaration, access is restricted to the same file.
- `protected` allows access from the class and its subclasses. It is not used for top-level declarations.
- `internal` allows access within the same module: a group of files compiled together.

## Inheritance

Kotlin fully supports traditional object-oriented inheritance.

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

1. Classes are final by default. The `open` modifier allows inheritance.
2. A method marked `open` can be overridden in a subclass.
3. `Yorkshire` inherits from `Dog`, with `Dog()` calling the base class constructor.
4. Use `override` to override a method or property.

## Inheritance with a parameterized constructor

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

Arguments for the base class's parameterized constructor are passed in the subclass declaration.

## Passing constructor arguments to a superclass

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

## Abstract classes

An abstract class is declared with `abstract` and cannot be instantiated directly. It can be subclassed. Its members are abstract only if explicitly marked `abstract`; other members can have implementations.

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

## Interfaces

Interfaces can contain abstract methods and methods with implementations. Their properties have no backing fields: they are either abstract or provide accessor implementations that do not store their own values.

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

## Data classes (`data class`)

A data class stores values. The compiler generates `equals()`, `hashCode()`, `toString()`, `componentN()`, and `copy()` based on the properties in the primary constructor. The following example uses this automatically generated behavior.

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

1. `data class User` has the properties `name` and `id`.
2. `equals()` and `hashCode()` are generated consistently from both properties.
3. `println(user)` uses the generated `toString()`.
4. Two objects are equal according to `==` if both `name` and `id` match.
5. Equal objects have the same `hashCode()`.
6. `copy()` creates a new instance.
7. The `===` operator compares references; the copy is a different object.
8. A positional argument in `copy()` changes the corresponding property in the copy.
9. A named argument such as `id = 3` specifies which property changes.
10. `component1()` and `component2()` return the properties in primary constructor order.

## Enum classes (`enum class`)

An enum class represents a finite set of named values, such as directions, states, or modes.

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

1. `State` contains three distinct constants.
2. A constant is accessed through the class name.
3. The `when` expression is exhaustive because it covers all constants, so it does not require `else`.

### Properties and methods in an `enum class`

The list of constants is separated from the remaining members by a semicolon.

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

1. `Color` has an `rgb` property and a `containsRed()` method.
2. Each constant passes a value for `rgb` to the constructor.
3. The bitwise `and` operation checks whether the red component is nonzero.
4. `println(red)` prints the name `RED`.
5. The method is called on the `red` constant.
6. `Color.BLUE.containsRed()` returns `false`.
7. The red component is nonzero for `RED` and `YELLOW`, so the check returns `true`.

## Sealed classes (`sealed class`)

A sealed class restricts its direct subclasses to the same package and module. They must have names and cannot be local or anonymous classes. This allows the compiler to check whether `when` covers all possible cases.

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

`Mammal` is a sealed class, and `Cat` and `Human` are its direct subclasses. After an `is` check, the compiler recognizes the specific type (a smart cast), such as `Human`, and allows access to `job`. The `when` expression is exhaustive and does not require `else`.

## Class and object

A class describes structure and behavior, while an object is an instance of that class. Multiple objects can be created from one class:

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

1. The `LuckDispatcher` class is declared.
2. The method prints a random integer from 0 to 89.
3. Two distinct objects are created.
4. The method is called on each object.

An `object` declaration creates a single shared instance (a singleton). It is initialized on first access, and initialization is thread-safe.

## Object expressions

An `object` expression creates an anonymous object with the specified members. In this example, it stores intermediate values for calculating rent:

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

1. A function with three parameters is declared.
2. Calling it creates an anonymous object with the values for the different days.
3. The total is calculated using the object's properties.
4. The result is printed.
5. The function is called with specific numbers of days.

## Object declarations

A named `object` declaration provides a single shared object. Its members are accessed through its name, without calling a constructor. The example uses demonstration values.

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

1. Creates an object declaration.
2. Defines the object's method.
3. Calls the method. This is when the object is actually created.

## Companion objects (`companion object`)

A companion object is declared inside a class. Its members can be called through the containing class's name. They belong to the companion object, rather than individual instances of the class.

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

1. Defines a class.
2. Declares a companion object. The name `Bonger` can be omitted.
3. Defines a companion object method.
4. Calls the companion object's method through the class name.
