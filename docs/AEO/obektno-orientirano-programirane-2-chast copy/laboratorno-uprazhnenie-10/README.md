---
layout: default
title: Laboratory lesson 10
parent: Object-oriented Programming - 2 part AEO
has_children: true
nav_order: 10
permalink: /docs/obektno-orientirano-programirane-2-chast-aeo/laboratorno-uprazhnenie-10
---

# Strategy


Strategy belongs to behavioural patterns group. It is used when a problem could be resolved in more than one way and the choise of decision method is made during execution.

In order to implement Strategy there have to be:
- inreface defining strategy method;
- concrete implementations for each strategy;
- context object with behavioour dependent on the chosen strategy.

Such organization allows seamless strategy change or addition of another since there is no need to modify context object and client code. It is important to point out that all strategies are mutually replaceble.

As disadvantage of Strategy is the need client to be familiar with all strategies in order to use them properly. Such approach is not very effective if there are only a few strategies available.

The example below shows area calculation for different shapes.

```
public interface ShapeStrategy {

    double calculateArea();
}
```

Some concrete implementations are nedeed:

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

The last step is to implement the context object:

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

Client code:

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

**PRACTICE**

Task 1. Create a program that processes payments using different payment methods (e.g., credit card, PayPal, Bitcoin) using the Strategy Pattern.

Task 2. Enhance the written program from task 1 using Builder pattern.
