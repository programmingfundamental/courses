---
layout: default
title: Лабораторно упражнение 2
parent: Програмиране за мобилни и Интернет устройства Kotlin
has_children: true
nav_order: 2
permalink: /docs/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-2
---

# Лабораторно упражнение 2

# Класове и обекти

Декларацията на клас се състои от името на класа, прототипа на класа (като се посочат параметрите на неговия тип, първичният конструктор и т.н.) и тялото на класа, заобиколено от къдрави скоби. Както заглавката, така и тялото не са задължителни; Ако класът няма тяло, къдравите скоби могат да бъдат пропуснати.

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

1. Декларира клас с име Customer без никакви свойства или дефинирани от потребителя конструктори. Непараметризиран конструктор по подразбиране се създава от Kotlin автоматично.
2. Обявява клас с две свойства: val и var и конструктор с два параметъра
3. Създава екземпляр на класа чрез конструктора по подразбиране. Имайте предвид, че в Kotlin няма ключова дума new
4. Създава екземпляр на класа с помощта на конструктора с два аргумента.
5. Достъп до свойството
6. Актуализира стойността на свойството

# Свойства

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

# Модификатори на видимост

Публичният (public) модификатор е модификаторът по подразбиране в Kotlin. Точно като публичния модификатор на Java, това означава, че декларацията се вижда навсякъде.

Защитен (protected) модификатор в Kotlin: НЕ МОЖЕ да се зададе на декларации от първо ниво. Декларациите, които са защитени в клас, могат да бъдат достъпни само в техните подкласове.

Internal е нов модификатор, наличен в Kotlin, който не съществува в Java. Задаването на декларация като вътрешна означава, че тя ще бъде налична само в същия модул. Под модул в Kotlin имаме предвид група от файлове, които са компилирани заедно.

Частните (private) модификатори не позволяват декларациите да бъдат видими извън текущия обхват.

# Наследяване

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

1. Kotlin класовете са окончателни по подразбиране. Ако искате да разрешите наследяването на класа, маркирайте класа с модификатора open.
2. Методите на Kotlin също са окончателни по подразбиране. Както при класовете, модификаторът open позволява заместването им.
3. Класът наследява суперклас, когато посочите името му след него. Празните скоби показват извикване на конструктора по подразбиране на суперкласа.
4. Заместването на методи или атрибути изисква модификатора override

# Наследяване с параметризиран конструктор

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

Ако искате да използвате параметризиран конструктор на суперкласа, когато създавате подклас, предоставете аргументите на конструктора в декларацията на подкласа.

# Предаване на аргументи на конструктора към суперклас

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

# Абстрактни класове

Подобно на Java, ключовата дума се използва за деклариране на абстрактни класове в Kotlin e abstract. Абстрактен клас не може да бъде инициализиран. Въпреки това, можете да наследи. Членовете на абстрактния клас не са абстрактни, освен ако изрично не използвате ключова дума abstract, за да ги направите абстрактни.

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

# Интерфейси

Интерфейсите в Kotlin са подобни на интерфейсите в Java 8. Те могат да съдържат дефиниции на абстрактни методи, както и реализации на неабстрактни методи. Те обаче не могат да съдържат никакво състояние. Това означава, че интерфейсът може да има свойство, но трябва да бъде абстрактено или да предоставя имплементации на достъп.

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

# Класове данни

Класове данни улесняват създаването на класове, които се използват за съхраняване на стойности. Такива класове автоматично се предоставят с методи за копиране, получаване на представяне на низове и използване на екземпляри в колекции. Можете да замените тези методи с вашите собствени реализации в декларацията на класа.

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

1. Дефинира клас данни с модификатора data
2. Заменете метода по подразбиране, като обявите потребителите за равни, ако имат еднакъви id
3. Методът се генерира автоматично, което прави изхода да изглежда добре.
4. Нашият обичай счита два случая за равни, ако техните ids са равни.
5. Екземплярите на класове данни с точно съвпадащи атрибути имат същия hashCode
6. Автоматично генерираната функция copy улеснява създаването на нов екземпляр.
7. copy създава нов екземпляр, така че обектът и неговото копие да имат различни референции.
8. Когато копирате, можете да променяте стойностите на определени свойства. copy приема аргументи в същия ред като конструктора на класа.
9. Използвайте с именувани аргументи, за да промените стойността въпреки реда на свойствата в copy
10. Автоматично генерираните функции componentN ви позволяват да получите стойностите на свойствата по реда на декларация.

# Enum класове

Enum класове се използват за моделиране на типове, които представляват краен набор от различни стойности, като посоки, състояния, модове и т.н.

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

1. Дефинира клас enum с три enum константи. Броят на константите винаги е краен и всички те са различни.
2. Осъществява достъп до enum константа чрез името на класа.
3. При enums when може да заключи дали -изразът е изчерпателен, така че да не се нуждаете от else

# Enums могат да съдържат свойства и методи като други класове, отделени от списъка с enum константи с точка и запетая.

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

1. Дефинира enum клас със свойство и метод.
2. Всяка enum константа трябва да предава аргумент за параметъра на конструктора.
3. Членовете на класа enum са отделени от константните дефиниции с точка и запетая.
4. По подразбиране връща името на константата, тук .toString"RED"
5. Извиква метод върху enum константа.
6. Извиква метод чрез име на клас enum.
7. RGB стойностите на и споделят първите битове (), така че това отпечатва "true".REDYELLOWFF

# Затворени класове

Затворените класове ограничават използването на наследството. След като обявите клас за затворен, той може да бъде подкласиран само от същия пакет, където е деклариран затворения клас. Той не може да бъде подкласиран извън опаковката, в която е деклариран затворения клас.

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

1. Дефинира запечатан клас.
2. Дефинира подкласове. Имайте предвид, че всички подкласове трябва да са в един и същ пакет.
3. Използва екземпляр на запечатания клас като аргумент в израз.
4. Извършва се смарткаст, замятане до .
5. Извършва се смарткаст, замятане до .
6. Тук не е необходим случаят -case, тъй като са обхванати всички възможни подкласове на запечатания клас. С незапечатан суперклас ще се изисква.

# Ключова дума за обект

Класовете и обектите в Kotlin работят по същия начин, както в повечето обектно-ориентирани езици: класът е план, а обектът е екземпляр на клас. Обикновено дефинирате клас и след това създавате няколко екземпляра на този клас:

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

1. Дефинира чертеж.
2. Дефинира метод.
3. Създава екземпляри.
4. Извиква метода на екземпляри.



В Kotlin имате и Ключова дума обект. Използва се за получаване на тип данни с една реализация.
Ако сте потребител на Java и искате да разберете какво означава "единичен", можете да се сетите за модела Singleton: Той ви гарантира, че е създаден само един екземпляр от този клас, дори ако 2 нишки се опитат да го създадат.
За да постигнете това в Kotlin, трябва само да декларирате : няма клас, няма конструктор, само мързелив екземпляр. Защо мързелив? Защото ще бъде създаден веднъж при достъп до обекта. В противен случай дори няма да бъде създаден.

# object Expression

Ето една основна типична употреба на израз: проста структура обект/свойства. Не е необходимо да го правите в декларацията на класа: създавате един обект, декларирате членовете му и осъществявате достъп до него в рамките на една функция. Обекти като този често се създават в Java като анонимни екземпляри на класове.

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

1. Създава функция с параметри.
3. Създава обект, който да се използва при изчисляване на стойността на резултата.
4. Осъществява достъп до свойствата на обекта.
5. Отпечатва резултата.
6. Извиква функцията. Това е моментът, в който обектът действително е създаден.

# object Declaration

Можете също да използвате декларацията. Това не е израз и не може да се използва в присвояване на променлива. Трябва да го използвате за директен достъп до неговите членове:

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

# Companion Objects

Декларацията на обекта в клас дефинира друг полезен случай: придружаващия обект. Синтактично е подобно на статичните методи в Java: извиквате членове на обекта, като използвате името на класа му като квалификатор.

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
2. Определя спътник. Името му може да бъде пропуснато.
3. Дефинира метод на придружаващ обект.
4. Извиква метода придружаващ обект чрез името на класа.
