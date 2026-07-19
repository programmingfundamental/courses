---
layout: default
title: Наследяване и ключова дума super
parent: Лабораторно упражнение 3
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 2
---

# Наследяване и ключова дума `super`

Наследяването е механизъм в Java, чрез който един клас се дефинира като специализиран вариант на друг клас. Класът наследник получава достъп до наследимите полета и методи на родителския клас и може да добавя нови полета и методи.

Наследяването описва връзка от тип „е“. Ако `Student` наследява `Person`, това означава, че студентът е човек, но има и допълнителни характеристики, които не са общи за всички хора.

## Родителски клас и клас наследник

Класът, от който се наследява, се нарича родителски клас, базов клас или суперклас. Класът, който наследява, се нарича клас наследник, производен клас или подклас.

```java
class Person {

    private String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

Класът `Person` описва обща характеристика: име. Тази характеристика може да бъде използвана от по-специализирани класове.

## Ключова дума `extends`

Наследяването се декларира чрез ключовата дума `extends`.

```java
class Student extends Person {

    private int facultyNumber;

    public Student(String name, int facultyNumber) {
        super(name);
        this.facultyNumber = facultyNumber;
    }

    public int getFacultyNumber() {
        return facultyNumber;
    }
}
```

Декларацията `class Student extends Person` означава, че `Student` наследява `Person`. Обект от тип `Student` има поведение, дефинирано в `Person`, и допълнително поведение, дефинирано в `Student`.

```java
Student student = new Student("Ivan Petrov", 12345);

System.out.println(student.getName());
System.out.println(student.getFacultyNumber());
```

Методът `getName()` е дефиниран в `Person`, но може да се извика чрез обект от тип `Student`, защото `Student` наследява `Person`.

## Какво се наследява

Класът наследник получава наследимите членове на родителския клас. Това включва достъпните полета и методи според модификаторите за достъп.

Конструкторите не се наследяват. Класът наследник трябва да има собствен конструктор, който при нужда извиква конструктор на родителския клас чрез `super(...)`.

```java
class Employee extends Person {

    private double salary;

    public Employee(String name, double salary) {
        super(name);
        this.salary = salary;
    }
}
```

В примера `Employee` не наследява конструктора `Person(String name)`. Затова конструкторът на `Employee` извиква родителския конструктор чрез `super(name)`.

## Наследяване и капсулация

Наследяването не премахва правилата на капсулацията. Ако поле в родителски клас е `private`, то не може да се достъпва директно в класа наследник.

```java
class Person {

    private String name;

    public String getName() {
        return name;
    }
}

class Student extends Person {

    public void printName() {
        System.out.println(getName());
    }
}
```

Класът `Student` не може да използва директно полето `name`, защото то е `private`. Достъпът се извършва чрез публичния метод `getName()`.

## Добавяне на нови членове

Класът наследник може да добавя нови полета и методи, които не съществуват в родителския клас.

```java
class Teacher extends Person {

    private String subject;

    public Teacher(String name, String subject) {
        super(name);
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }
}
```

Класът `Teacher` наследява `getName()` от `Person` и добавя собствено поле `subject` с метод `getSubject()`.

## Предефиниране на метод

Класът наследник може да дефинира метод със същата сигнатура като метод от родителския клас. Това се нарича предефиниране на метод.

```java
class Person {

    public String getInformation() {
        return "Person";
    }
}

class Student extends Person {

    @Override
    public String getInformation() {
        return "Student";
    }
}
```

Анотацията `@Override` указва, че методът предефинира метод от родителски клас. Ако сигнатурата е написана грешно, компилаторът ще отчете грешка.

```java
Person person = new Person();
Student student = new Student();

System.out.println(person.getInformation());
System.out.println(student.getInformation());
```

Първото извикване използва реализацията от `Person`. Второто извикване използва реализацията от `Student`.

## Единично наследяване

В Java един клас може да наследява директно само един родителски клас.

```java
class Student extends Person {

}
```

Не е позволено един клас да има два директни родителски класа.

```java
// не е позволено
// class Student extends Person, User {
//
// }
```

Това правило се нарича единично наследяване на класове.

## Многостепенно наследяване

Може да се създаде йерархия от няколко нива.

```java
class Person {

}

class Student extends Person {

}

class GraduateStudent extends Student {

}
```

Класът `GraduateStudent` наследява директно `Student` и косвено `Person`.

## Клас `Object`

В Java всеки клас пряко или непряко наследява класа `Object`. Ако даден клас не посочи родителски клас чрез `extends`,
негов родителски клас по подразбиране е `Object`.

```java
class Student {

}
```

Горният клас е еквивалентен на следната идея:

```java
class Student extends Object {

}
```

Това означава, че всеки обект в Java може да бъде разглеждан и като обект от тип `Object`.

```java
Student student = new Student();
Object value = student;
```

Класът `Object` дефинира общи методи, които са налични за всички обекти. По-подробно методите `toString()`,
`equals()` и `hashCode()` се разглеждат при полиморфизма и предефинирането на методи.

## Вложени класове

Вложен клас е клас, деклариран в тялото на друг клас. Външният клас създава логически контекст, а вложеният клас описва тип, който е тясно свързан с него.

```java
class Bank {

    static class Account {

        private String iban;

        public Account(String iban) {
            this.iban = iban;
        }

        public String getIban() {
            return iban;
        }
    }
}
```

Класът `Account` е деклариран в тялото на класа `Bank`. Понеже е деклариран като `static`, той принадлежи на класа `Bank`, а не на конкретен обект от `Bank`.

Обект от статичен вложен клас се създава чрез името на външния клас.

```java
Bank.Account account = new Bank.Account("BG00BANK0000000000");
System.out.println(account.getIban());
```

Статичният вложен клас се използва, когато даден тип има смисъл основно като част от друг тип. Той не получава автоматичен достъп до нестатичните полета на външния клас, защото не е свързан с конкретен обект от външния клас.

## `final` клас и `final` метод

Клас, деклариран с `final`, не може да бъде наследяван.

```java
final class Configuration {

}

// class AppConfiguration extends Configuration { } // не е позволено
```

Метод, деклариран с `final`, се наследява, но не може да бъде предефиниран в клас наследник.

```java
class Parent {

    public final void printType() {
        System.out.println("Parent");
    }
}

class Child extends Parent {

    // public void printType() { } // не е позволено
}
```

`final` се използва, когато наследяването или промяната на конкретно поведение трябва да бъде забранена.

| Употреба | Предназначение |
| -------- | -------------- |
| `final` поле | Полето получава стойност само веднъж |
| `final` метод | Методът не може да бъде предефиниран в клас наследник |
| `final` клас | Класът не може да бъде наследяван |

## Кога се използва наследяване

Наследяване се използва, когато между два класа има ясна връзка „е“. Например `Dog` е `Animal`, `Student` е `Person`, `SavingsAccount` е `BankAccount`.

Ако връзката е „има“, наследяване не е подходящият механизъм. Например `Car` има `Engine`, но `Car` не е `Engine`.

## Ключова дума `super`

Ключовата дума `super` се използва в клас наследник за достъп до непосредствения родителски клас. Тя може да се използва за извикване на конструктор, метод или поле от родителския клас.

`super` има смисъл само при наследяване. В клас, който не наследява друг потребителски клас, не се използва `super` за достъп до собствените членове.

## Извикване на конструктор чрез `super(...)`

Конструкторът на родителския клас не се наследява от класа наследник. Ако родителският клас изисква стойности за своите полета, конструкторът на наследника трябва да извика подходящ родителски конструктор.

```java
class Person {

    private String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Student extends Person {

    private int facultyNumber;

    public Student(String name, int facultyNumber) {
        super(name);
        this.facultyNumber = facultyNumber;
    }
}
```

Изразът `super(name)` извиква конструктора `Person(String name)`. Така полето `name`, което принадлежи на родителската част на обекта, се инициализира от родителския клас.

Извикването на родителски конструктор чрез `super(...)` трябва да бъде първата инструкция в конструктора на класа наследник.

```java
public Student(String name, int facultyNumber) {
    super(name);
    this.facultyNumber = facultyNumber;
}
```

След извикването на родителския конструктор могат да се инициализират полетата, които принадлежат на класа наследник.

## Ред на инициализация при наследяване

При създаване на обект от клас наследник първо се инициализира частта, която принадлежи на родителския клас. След това
се изпълнява конструкторът на класа наследник.

```java
class Parent {

    Parent() {
        System.out.println("Parent constructor");
    }
}

class Child extends Parent {

    Child() {
        System.out.println("Child constructor");
    }
}
```

При създаване на обект от `Child` резултатът е:

```text
Parent constructor
Child constructor
```

Този ред гарантира, че наследената част от обекта е създадена преди кодът в конструктора на наследника да започне да я
използва.

## Автоматично извикване на `super()`

Ако в конструктор на клас наследник не е написано изрично извикване на родителски конструктор, компилаторът се опитва автоматично да добави `super()`.

```java
class Person {

    public Person() {
        System.out.println("Person created");
    }
}

class Student extends Person {

    public Student() {
        System.out.println("Student created");
    }
}
```

Конструкторът на `Student` се обработва така, сякаш е написано:

```java
public Student() {
    super();
    System.out.println("Student created");
}
```

Ако родителският клас няма конструктор без параметри и наследникът не извика друг родителски конструктор чрез `super(...)`, програмата няма да се компилира.

```java
class Person {

    public Person(String name) {

    }
}

class Student extends Person {

    public Student() {
        // грешка: липсва извикване super(name)
    }
}
```

## Извикване на родителски метод

Когато клас наследник предефинира метод от родителския клас, чрез `super` може да се извика оригиналната реализация.

```java
class Person {

    public String getInformation() {
        return "Person";
    }
}

class Student extends Person {

    private int facultyNumber;

    public Student(int facultyNumber) {
        this.facultyNumber = facultyNumber;
    }

    @Override
    public String getInformation() {
        return super.getInformation() + ", faculty number: " + facultyNumber;
    }
}
```

Изразът `super.getInformation()` извиква метода `getInformation()` от `Person`. След това резултатът се допълва с информацията от `Student`.

## Достъп до родителско поле

Ако родителският клас и класът наследник съдържат поле с едно и също име, чрез `super` може да се достъпи полето от родителския клас.

```java
class Person {

    protected String name = "Unknown";
}

class Student extends Person {

    private String name = "Ivan";

    public void printNames() {
        System.out.println(super.name);
        System.out.println(this.name);
    }
}
```

`super.name` означава полето `name` от родителския клас. `this.name` означава полето `name` от текущия клас.

Деклариране на полета с еднакви имена в родителски и наследен клас създава неяснота и не трябва да се използва без ясна причина. По-често `super` се използва за конструктори и за разширяване на родителски методи.

## `super` и `this`

`this` обозначава текущия обект. `super` обозначава родителската част на текущия обект.

```java
class Student extends Person {

    private int facultyNumber;

    public Student(String name, int facultyNumber) {
        super(name);
        this.facultyNumber = facultyNumber;
    }
}
```

В примера `super(name)` извиква конструктор от родителския клас. `this.facultyNumber` достъпва поле от текущия клас.

`super` не може да се използва в статичен контекст, защото статичният метод не се изпълнява върху конкретен обект.

```java
class Student extends Person {

    public static void print() {
        // super.getName(); // не се компилира
    }
}
```
