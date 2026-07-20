---
layout: default
title: Връзки между класовете
parent: Лабораторно упражнение 3
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 2
---

# Връзки между класовете

В обектно-ориентираното програмиране класовете не се разглеждат само като отделни описания на обекти. Между класовете могат да съществуват връзки, чрез които се моделира как обектите си взаимодействат, как един обект използва друг обект и дали жизненият цикъл на един обект зависи от друг.

В Java връзка между класове се реализира чрез:

- поле от тип на друг клас;
- параметър от тип на друг клас;
- върната стойност от тип на друг клас;
- наследяване чрез `extends`;
- имплементиране на интерфейс чрез `implements`;
- вложен клас.

## Връзка от тип „е“

Връзката от тип „е“ се реализира чрез наследяване. Класът наследник е специализиран вариант на родителския клас.

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

Класът `Student` е `Person`, защото наследява `Person`. Наследяването се използва само когато наследникът може да бъде разглеждан като обект от родителския тип.

## Връзка от тип „има“

Връзката от тип „има“ се реализира чрез поле, което сочи към обект от друг клас. Един клас съдържа или използва друг клас като част от своето състояние.

```java
class Engine {

    private int power;

    public Engine(int power) {
        this.power = power;
    }

    public int getPower() {
        return power;
    }
}

class Car {

    private Engine engine;

    public Car(Engine engine) {
        this.engine = engine;
    }

    public int getEnginePower() {
        return engine.getPower();
    }
}
```

Класът `Car` има поле от тип `Engine`. Това означава, че обект от тип `Car` използва обект от тип `Engine`. `Car` не е `Engine`, затова наследяване не е подходяща връзка.

## Асоциация

Асоциацията е обща връзка между два класа, при която обект от единия клас използва или познава обект от другия клас. Тя описва взаимодействие, но не определя задължително собственост или зависим жизнен цикъл.

```java
class Student {

    private String name;

    public Student(String name) {
        this.name = name;
    }

    public void enroll(Course course) {
        System.out.println(name + " enrolls in " + course.getName());
    }
}

class Course {

    private String name;

    public Course(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

Методът `enroll(Course course)` приема обект от тип `Course`. Така между `Student` и `Course` има връзка, защото обект от `Student` използва обект от `Course`.

## Агрегация

Агрегацията е връзка от тип „част - цяло“, при която частта може да съществува независимо от цялото. Обектът, който съдържа връзката, не създава задължително съставния обект и не управлява изцяло неговия жизнен цикъл.

```java
class Teacher {

    private String name;

    public Teacher(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Course {

    private String name;
    private Teacher teacher;

    public Course(String name, Teacher teacher) {
        this.name = name;
        this.teacher = teacher;
    }

    public String getInformation() {
        return name + " - " + teacher.getName();
    }
}
```

Обектът `Teacher` се създава извън `Course` и се подава към конструктора. Ако обектът `Course` вече не се използва, обектът `Teacher` може да продължи да съществува и да бъде използван от друг курс.

```java
Teacher teacher = new Teacher("Ivan Petrov");

Course first = new Course("Programming", teacher);
Course second = new Course("Databases", teacher);
```

В този пример един и същ преподавател участва в два курса. Това показва, че преподавателят не е собственост само на един курс.

## Композиция

Композицията е връзка от тип „част - цяло“, при която частта принадлежи на цялото и се създава като негова вътрешна част. Жизненият цикъл на частта зависи от жизнения цикъл на цялото.

```java
class Address {

    private String town;
    private String street;

    public Address(String town, String street) {
        this.town = town;
        this.street = street;
    }

    public String getText() {
        return town + ", " + street;
    }
}

class Building {

    private Address address;

    public Building(String town, String street) {
        this.address = new Address(town, street);
    }

    public String getAddressText() {
        return address.getText();
    }
}
```

Класът `Building` създава обекта `Address` в своя конструктор. Външният код не подава готов адрес, а подава стойности, от които сградата изгражда своя вътрешен обект. Това означава, че адресът е част от състоянието на сградата.

## Агрегация и композиция

Агрегацията и композицията моделират връзка „част - цяло“, но се различават по силата на зависимостта между обектите.

| Критерий | Агрегация | Композиция |
| -------- | --------- | ---------- |
| Създаване на частта | Частта се подава отвън | Частта се създава вътре в цялото |
| Жизнен цикъл | Частта може да съществува самостоятелно | Частта зависи от цялото |
| Споделяне | Един обект може да участва в няколко други обекта | Частта обикновено принадлежи на един обект |
| Пример | Курс има преподавател | Сграда има адрес |

Изборът между агрегация и композиция зависи от това дали съставният обект трябва да бъде самостоятелен. Ако обектът трябва да се създава и използва независимо, връзката е агрегация. Ако обектът има смисъл само като вътрешна част от друг обект, връзката е композиция.

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
