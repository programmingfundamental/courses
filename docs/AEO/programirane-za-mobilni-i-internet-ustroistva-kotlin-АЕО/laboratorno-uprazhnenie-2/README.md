---
layout: default
title: Lab 2
parent: Mobile and Internet Programming Kotlin
has_children: true
nav_order: 2
permalink: /docs/programirane-za-mobilni-i-internet-ustroistva-kotlin-аео/laboratorno-uprazhnenie-2
---

# Lab 2

# Classes and objects

A class declaration consists of the class name, the class prototype (specifying its type parameters, primary constructor, etc.), and the class body, surrounded by curly brackets. Both the header and body are optional; if the class does not have a body, the curly brackets can be skipped.

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

1. Class Customer without any constructor or properties is declared. Kotlin automatically declares default constructor;
2. Class Contact with two properties (val and var) and constructor by that properties is declared;
3. Creates class instance with default constructor (there is no keyword "new" in Kotlin);
4. Creates class instance with parameterized constructor;
5. Property accessor;
6. Property modifier.

# Properties

```kotlin

class Customer {

    constructor(email: String) {
        this.email = email
    }

    private var email: String
        get() = this.email
        set(value) {
            this.email = field
        }
}

```

# Access modifiers

Public modifier is a default access modifier in Kotlin, meaning such declaration could be accessed from everywhere.

Protected modifier in Kotlin declares access in the same class and its sub-classess.

Internal modifier declares access on a module level, i.e. group of files compiled together.

Private modifier declares visibility within current class.

# Inheritance

Kotlin supports traditional object-oriented approach of inheritance.

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

1. Kotlin classes are final by default. If there is a need a class to be later extended, it should be declared with the keyword "open";
2. Kotlin methods are also final by default and declaring them with "open" makes them extendable;
3. Class extends super class when its name is present after declaration of the new class name. Empty brackets indicate default constructor call;
4. Redefinition of methods should be preceeded with modifier "override".

# Inheritance with parameterized constructor

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

If there is a need of parameterized constructor from the super class to be used, declaration of the sub-class should have the necessary parameters.

# Passing arguments through paret class constructor

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

# Abstract classes

Similar to Java, the keyword used to declare abstract classes in Kotlin is abstract. An abstract class cannot be initialized but can be extended. Members of an abstract class are not abstract unless there is explicit declaration.

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

# Interfaces

Interfaces in Kotlin are similar to interfaces in Java 8. They can contain definitions of abstract methods as well as implementations of non-abstract methods. However, they cannot contain any state. This means that an interface can have a property, but it must be abstract or provide accessor implementations.

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

# Data classes

Data classes make it easy to create classes that are used to store values. Such classes automatically provide methods for copying, getting string representations, and using instances in collections. These methods can be replaced with custom implementation in the class declaration.

```kotlin
data class User(val name: String, val id: Int) {           // 1
    override fun equals(other: Any?) =
        other is User && other.id == this.id               // 2
}
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

1. Declares data class with specific modifier;
2. Replaces default method and users with same id a considered as equal;
3. Automatic method generation;
4. Such case consideres two instances equal when they have same id;
5. Data classes with same attributes have same result from method hashCode();
6. Automatically created function copy makes easier to create new instance;
7. Using copy creates new instance so the references of such objects are different;
8. When using copy, new values for class parameters could be used. The copy accepts arguments in the same order as declared constructor;
9. Using named arguments could change value despite attribute order in the copy;
10. Generated function componentN allows to get attribute values according declaration order.

# Enum classes

Enum classes are used to model types that represent a finite set of different values, such as directions, states, modes, etc.

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

1. Declares enum class with three constants (should be different);
2. Accesses enum value;
3. When using enums, there is no need of else.

# Enums can contain properties and methods like other classes, separated from the list of enum constants by a semicolon.

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

1. Defines an enum class with a property and a method.
2. Each enum constant must pass a parameter argument to the constructor.
3. Members of the enum class are separated from the constant definitions by a semicolon.
4. By default, returns the name of the constant, here .toString"RED"
5. Calls a method on an enum constant.
6. Calls a method via an enum class name.
7. The RGB values ​​of and share the first bits (), so this prints "true".REDYELLOWFF

# Sealed classes

Sealed classes restrict the use of inheritance. Once you declare a class as sealed, it can only be subclassed from the same package where the sealed class is declared. It cannot be subclassed outside the package where the sealed class is declared.
Sealed classes are used when there is a fixed set of related types that have to be handeled exhaustively.

```kotlin

sealed class Mammal(val name: String)                                                   // 1

class Cat(val catName: String) : Mammal(catName)                                        // 2
class Human(val humanName: String, val job: String) : Mammal(humanName)

fun greetMammal(mammal: Mammal): String {
    when (mammal) {                                                                     // 3
        is Human -> return "Hello ${mammal.name}; You're working as a ${mammal.job}"    // 4
        is Cat -> return "Hello ${mammal.name}"                                         // 5     
    }                                                                                   // 6
}

fun main() {
    println(greetMammal(Cat("Snowy")))
}

```

1. Defines a sealed class.
2. Defines subclasses. Note that all subclasses must be in the same package.
3. Uses an instance of the sealed class as an argument in an expression.
4. Performs a smartcast, casting to .
5. Performs a smartcast, casting to .
6. The -case is not needed here, since all possible subclasses of the sealed class are covered. With an unsealed superclass, it would be required.

# Object keyword

Classes and objects in Kotlin work the same way as in most object-oriented languages: a class is a blueprint, and an object is an instance of a class. Typically, you define a class and then create multiple instances of that class:

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

1. Defines a class.
2. Defines a method.
3. Creates instances.
4. Calls the instance method.


In Kotlin there is also the keyword object. It is used to get a data type with a single implementation (similar to Singleton in Java).
To achieve this in Kotlin, you just have to declare: no class, no constructor, just a lazy instance - meaning it will be created once when the object is accessed.

# object Expression

Here's a basic, typical use of an expression: a simple object/property structure. There is no need this to be done in the class declaration: there is single object creation with its members and what is the access written in a function. Objects like this are often created in Java as anonymous instances of classes.

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

1. Creates a function with parameters.
3. Creates an object to use in calculating the result value.
4. Accesses the object's properties.
5. Prints the result.
6. Calls the function. This is when the object is actually created.

# object Declaration

OBject declaration could be also used. This is not expression and cannot be assigned to variable. It is used for direct access to object members:

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

# Companion Objects

The declaration of an object in a class defines another useful case: the companion object. It is syntactically similar to static methods in Java:  members of the object are called using its class name as a qualifier.
Companion objects are used when there is a need of static methods, constants, or factory methods associated with a class.

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
2. Defines a companion. Its name can be omitted.
3. Defines a method on a companion object.
4. Calls the companion object method using the class name.
