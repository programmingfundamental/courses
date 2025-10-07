---
layout: default
title: Strategy
parent: Лабораторно упражнение 11
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 1
---

# Strategy

Strategy е представител на поведенческите шаблони. Използва се когато даден проблем може да бъде разрешен по различни начини и избора за неговото решение се взема по време на изпълнението. 

За имплементация на Strategy е необходимо:
- интерфейс Strategy;
- конкретни имплементации на интерфейса - за всяка отделна стратегия;
- контекстен обект с поведение, което зависи от избраната стратегия.

Подобна имплементация прави промяната на дадена стратегия или добавянето на нова безпроблемно, без това да налага промени в контекстния обект и в клиентската част, която използва този контекстен обект. Освен това стратегиите са взаимозаменяеми.

Като недостатък на Strategy може да се посочи необходимостта клиента да е запознат с различните стратегии, за да може да ги прилага правилно. Освен това подобен подход не е много ефективен в практиката ако имаме само две или три стратегии.

Като пример по-долу е показана програма, която изчислява лице на геометрична фигура.

```
public interface ShapeStrategy {

    double calculateArea();
}
```

Необходими са конкретни имплементации:

```
public class RectangleShape implements ShapeStrategy{

    private int length;
    private int width;

    public RectangleShape(int length, int width) {
        this.length = length;
        this.width = width;
    }

    @Override
    public double calculateArea() {
        return length * width;
    }
}
```
```
public class TriangleStrategy implements ShapeStrategy{

    private int a;
    private int b;
    private int c;

    public TriangleStrategy(int a, int b, int c) {
        if ((a + b > c) && (a + c > b) && (b + c > a)) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    @Override
    public double calculateArea() {
        int p = (a + b + c)/2;
        return Math.sqrt(p * (p - a) * (p - b) * (p - c));
    }
}
```
```
public class CircleStrategy implements ShapeStrategy{

    private int radius;

    public CircleStrategy(int radius) {
        this.radius = radius;
    }

    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
}
```

След това трябва да бъде създаден и контекстен клас:

```
public class Shape {

    private ShapeStrategy shapeStrategy;

    public Shape(ShapeStrategy shapeStrategy) {
        this.shapeStrategy = shapeStrategy;
    }

    public double getArea() {
        return shapeStrategy.calculateArea();
    }
}
```

Клиентският код изглежда по следния начин:

```
public class Application {

    public static void main(String[] args) {
        Shape circle = new Shape(new CircleStrategy(2));
        System.out.println("Circle's area is: " + String.format("%.2f", circle.getArea()));

        Shape triangle = new Shape(new TriangleStrategy(5, 4, 6));
        System.out.println("Triangle's area is: " + String.format("%.2f", triangle.getArea()));

        Shape rectangle = new Shape(new RectangleShape(4, 7));
        System.out.println("Rectangle's area is: " + rectangle.getArea());
    }
}
```

