---
layout: default
title: Интерфейси и абстрактни класове
parent: Лабораторно упражнение 6
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 1
---

# Интерфейси и абстрактни класове

## Интерфейси и сравнение с абстрактни класове

Интерфейсът в Java описва договор за поведение. Той задава какви методи трябва да бъдат реализирани от класовете, които го имплементират. Интерфейсът не описва конкретен обект самостоятелно, а описва способност или роля, която различни класове могат да имат.

```java
interface Printable {

    void print();
}
```

Клас имплементира интерфейс чрез ключовата дума `implements`.

```java
class Document implements Printable {

    @Override
    public void print() {
        System.out.println("Printing document");
    }
}
```

Методите от интерфейс са публични като договор. При реализацията им в клас трябва да се използва `public`, защото не може да се намалява видимостта на наследеното поведение.

## Интерфейс като тип

Интерфейсът може да се използва като тип на променлива, параметър или масив.

```java
Printable printable = new Document();
printable.print();
```

Променливата има тип `Printable`, а реалният обект е `Document`. Това позволява различни класове да се обработват еднакво, ако имплементират един и същ интерфейс.

## Няколко интерфейса

Един клас може да имплементира повече от един интерфейс.

```java
interface Movable {

    void move();
}

interface Chargeable {

    void charge();
}

class ElectricCar implements Movable, Chargeable {

    @Override
    public void move() {
        System.out.println("Moving");
    }

    @Override
    public void charge() {
        System.out.println("Charging");
    }
}
```

Това е възможно, защото интерфейсите описват поведение, а не наследяване на състояние от няколко класа.

## Съдържание на интерфейс

Интерфейс може да съдържа:

- абстрактни методи;
- `default` методи;
- `static` методи;
- константи.

```java
interface Identifiable {

    int MIN_ID = 1;

    int getId();

    default boolean hasValidId() {
        return getId() >= MIN_ID;
    }

    static String getTypeName() {
        return "Identifiable";
    }
}
```

Поле в интерфейс е константа. То се третира като `public static final`, дори когато тези модификатори не са изписани.

## Абстрактен клас и интерфейс

Абстрактният клас се използва, когато класовете наследници имат обща основа, общо състояние или обща частична реализация. Интерфейсът се използва, когато трябва да се опише поведение, което може да бъде реализирано от несвързани класове.

```java
abstract class Animal {

    private String name;

    public Animal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String sound();
}

interface Trainable {

    void train();
}

class Dog extends Animal implements Trainable {

    public Dog(String name) {
        super(name);
    }

    @Override
    public String sound() {
        return "Bark";
    }

    @Override
    public void train() {
        System.out.println("Training dog");
    }
}
```

Класът `Dog` наследява общото състояние и поведение от `Animal` и едновременно с това имплементира способността `Trainable`.

## Избор между абстрактен клас и интерфейс

Абстрактен клас се използва, когато има обща йерархия от тип „е“. Интерфейс се използва, когато различни класове трябва да поддържат еднакво действие, без непременно да имат общ родителски клас.

Например `Dog` е `Animal`, затова наследяването от абстрактен клас е подходящо. `Document`, `Image` и `Report` могат да бъдат `Printable`, но не е необходимо да наследяват един и същ родителски клас.
