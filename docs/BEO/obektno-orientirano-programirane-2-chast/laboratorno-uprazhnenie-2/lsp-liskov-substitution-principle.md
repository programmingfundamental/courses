---
layout: default
title: LSP - Liskov Substitution Principle
parent: Лабораторно упражнение 2
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 3
---

# LSP - Liskov Substitution Principle

## Принцип за заместване на Лисков

Принципът за заместване на Лисков гласи, че обектите от клас наследник трябва да могат да заменят обектите от родителския клас, без това да нарушава правилното поведение на програмата.

С други думи, ако даден метод работи с обект от базов клас, той трябва да работи коректно и с обект от всеки негов наследник.

### Проблем

Класически пример за нарушение на LSP е връзката между правоъгълник и квадрат.

На пръв поглед квадратът е специален случай на правоъгълник, но при моделиране чрез наследяване възниква проблем.

```java
public class Rectangle {

    private int width;
    private int height;

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getArea() {
        return width * height;
    }
}
```
```java
public class Square extends Rectangle {

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        super.setHeight(width);
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        super.setWidth(height);
    }
}
```
```java
public class AreaCalculator {

    public void resize(Rectangle rectangle) {
        rectangle.setWidth(5);
        rectangle.setHeight(4);

        System.out.println(rectangle.getArea());
    }
}
```
При обект от тип Rectangle резултатът е 20.

При обект от тип Square резултатът е 16, защото задаването на височина променя и ширината. Така наследникът не може да замести коректно родителския клас.

### Решение

В този случай квадратът и правоъгълникът не трябва да бъдат моделирани чрез наследяване, ако поведението им при промяна на размерите е различно.

По-подходящо е да се използва обща абстракция.

```java
public interface Shape {

    int getArea();
}
```
```java
public class Rectangle implements Shape {

    private int width;
    private int height;

    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getArea() {
        return width * height;
    }
}
```
```java
public class Square implements Shape {

    private int side;

    public Square(int side) {
        this.side = side;
    }

    @Override
    public int getArea() {
        return side * side;
    }
}
```
И двата класа могат да бъдат използвани като Shape, без да нарушават очакваното поведение.

### Предимства

Прилагането на LSP води до:
* по-коректни йерархии;
* по-надежден полиморфизъм;
* по-малко неочаквани грешки;
* по-добра заменяемост на класовете.

### Недостатъци / трудности

LSP често е труден за прилагане, защото изисква не само синтактично правилно наследяване, а и логическа съвместимост между базовия клас и наследниците.

### Приложение

LSP се използва при:
* проектиране на йерархии от класове;
* използване на полиморфизъм;
* работа с абстрактни класове и интерфейси;
* проверка дали наследяването е подходящо решение.

