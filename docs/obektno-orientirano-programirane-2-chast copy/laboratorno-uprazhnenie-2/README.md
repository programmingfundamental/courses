---
layout: default
title: Laboratory lesson 2
parent: Object-oriented Programming - 2 part AEO
has_children: true
nav_order: 2
permalink: /docs/obektno-orientirano-programirane-2-chast-aeo/laboratorno-uprazhnenie-2
---

# Laboratory lesson 2



# LSP - Liskov Substitution Principle

Barbara Liskov defined the principle in 1988 as:

_If for every object o1 of type S there is an object o2 of type T such that for all programs P defined with respect to T the behavior of P is unchanged when o1 is substituted for o2 then S is a subtype of T._

Or as Robert S. Martin sums it up:

_Subtypes must be overriden of their base types._

In terms of LSP, _methods_ _that use base class invocations must be able to use objects of the derived class without knowing it_. In simple words, derived classes must be substitutes for the base class for the LSP principle to apply.

Let's consider the following example with squares and rectangles. It's possible to be said that a square is in fact a rectangle. Unfortunately, there is a problem with such statement:


```
public class Rectangle {
    private int length;
    private int breadth;
    
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }
    public int getBreadth() {
        return breadth;
    }
    public void setBreadth(int breadth) {
        this.breadth = breadth;
    }
    public int getArea() {
        return this.length * this.breadth;
    }
}
```

```
public class Square extends Rectangle {
    @Override
    public void setBreadth(int breadth) {
        super.setBreadth(breadth);
        super.setLength(breadth);
    }
    @Override
    public void setLength(int length) {
        super.setLength(length);
        super.setBreadth(length);
    }
}
```

```
public class LSPDemo {
    public void calculateArea(Rectangle r) {
        r.setBreadth(2);
        r.setLength(3);

        r.getArea() == 6 : printError("area", r);

        r.getLength() == 3 : printError("length", r);
        r.getBreadth() == 2 : printError("breadth", r);
    }

    private String printError(String errorIdentifer, Rectangle r) {
        return "Unexpected value of " + errorIdentifer + "  for instance of " + r.getClass().getName();
    }

    public static void main(String[] args) {
        LSPDemo lsp = new LSPDemo();
        //
        // An instance of Rectangle is passed
        //
        lsp.calculateArea(new Rectangle());
        //
        // An instance of Square is passed
        //
        lsp.calculateArea(new Square());
}
```

The calculating method passes rectangle as argument, which breaks LSP, since according the principle the method should be able to use any derivate class.

Method _calculateArea_ should always take into consideration:

* both lengths should be equals, setLength;
* both breadths should be quals, setBreadth;
* the area is calculated as product of length and breadth.

Considering the example above, it could be said that:

* class _Square_ doesn't need method setBreadth or setLength since the square has equal parts;
* class _LSPDemo_ have to know about class Rectangle's derivates in order to correctly perform calculations. 


Some points for LSP:

* Only when child-classes are complete substitutes for parent class they could be used more than once and to be modified;
* Using this principle, methods could define preconditions and postconditions. Preconditions should be met in order to execute given method, and postconditions make sure that condition will be true.
* Child-classes should throw exceptions for the parent's properties that could not be processed.


### Rules

* #### **Signature rule for method arguments** - the overridden subtype method argument types can be identical or wider than the supertype method argument types.
* #### **Signature rule for return types** - the return type of the overridden subtype method can be narrower than the return type of the supertype method.
* #### **Signature rule for exceptions** - the subtype method can throw fewer or narrower (but not any additional or broader) exceptions than the supertype method.
* #### **Properties rule for history constraint** - subclass methods (inherited or new) shouldn’t allow state changes that the base class didn’t allow.
* #### **Methods rule - preconditions** - given subtype can weaken (but not strengthen) the precondition for a method it overrides.
* #### **Methods rule - postconditions** - given subtype can strengthen (but not weaken) the postcondition for a method it override.

####


# ISP - Interface Segregation Principle

Interface segregation means that **larger interfaces should be divided into smaller ones**. By doing this, we can ensure that classes only need to be concerned with the methods that are of interest to them.


Let's consider example for a zoo, starting with an interface:

```java
public interface BearKeeper {
    void washTheBear();
    void feedTheBear();
    void petTheBear();
}
```

This interface defines many responsibilities, which is not fine.

In order to correct this, we could divide the interface above in three:

```java
public interface BearCleaner {
    void washTheBear();
}

public interface BearFeeder {
    void feedTheBear();
}

public interface BearPetter {
    void petTheBear();
}
```

In this case, these interfaces could be implemented when necessary:

```java
public class BearCarer implements BearCleaner, BearFeeder {

    public void washTheBear() {
        //I think we missed a spot...
    }

    public void feedTheBear() {
        //Tuna Tuesdays...
    }
}
```


# DI - Dependency Inversion Principle

The applications should follow given rules in order to be in accordance with Dependecy Inversion Principle:

* high-level modules should not depend on low-level modules; instead, both should depend on abstractions;
* abstractions should not depend on details, but details should depend on abstractions.


Let's consider the following example:

```
public class StringProcessor {
    private final StringReader stringReader;
    private final StringWriter stringWriter;

    public StringProcessor(StringReader stringReader, StringWriter stringWriter) {
        this.stringReader = stringReader;
        this.stringWriter = stringWriter;
    }

    public void printString() {
        stringWriter.write(stringReader.getValue());
    }
}
```

Implementation could be completed in a different ways:

1. _**StringReader**_ and _**StringWriter**_, which are components from lower level, are hardcoded in a same package. _StringProcessor_, a higher level component, is declared in a different package. _StringProcessor_ depends on _StringReader_ and _StringWriter_. There is no dependency inversion and _StringProcessor_ can not be used in a different context.
2. _**StringReader**_ and _**StringWriter**_ are interfaces, declared in a package with the implementing class. _StringProcessor_ depends on abstractions, but lower level components do not and still there is no dependency inversion achieved.
3. _**StringReader**_ and _**StringWriter**_ are interfaces declared in the same package with _**StringProcessor**_. Now, _StringProcessor_ has ownership over the abstractions. _StringProcessor, StringReader_ and _StringWriter_ depends on abstractions. There is dependecy inversion achieved and _StringProcessor_ could be reused in a different context.
4. _**StringReader**_ and _**StringWriter**_ are interfaces, declared in a different package from _**StringProcessor**_. There is dependency inversion and in a such way is easier to replace _StringReader_ and _StringWriter_ implementations. _StringProcessor_ is again reusable in various contexts.


The implementation from 3 represents direct DIP application, **where the high level component and abstractions are in the same package.** This means that the high level component owns abstractions.

Implemetation from 4 is a better DIP application, since neither high level component nor lower level components has ownership over the abstractions. The abstractions are placed in a different layer which makes easier switching between lower level components. At the same time, all components are isolated from each other, which ensures better encapsulation.


# Tasks LSP

### Task 1

Create a program for shop shelfs. The shelf stores list of products and each product has storage temperature. The shop has list of shelfs and possibility to add products.

Later the shop introduce a shelf for cold products, which stores only products with storage temperature below 15 degrees.

# Задачи ISP

### Task 1

Define following interface:

```java
public interface Vehicle {    
    public void drive();    
    public void stop();
    public void refuel();
    public void openDoors();
}
```

The interface should be implemented by classes Bike, Car and Truck, so ISP should be implemented.

### Задача 2

Define following interface:

```
public interface Payments {
    
    public bool payMoney(double amount);
    
    public ScratchCard getScratchCard();
    
    public double getCashBackAsCreditBalance();
}
```

Implement class GooglePay with possibilities for:

* bill payment, balance check and balance decrease;
* gives scratch card with a prize:
  * if balance is over 100, there is 10% chance of winning
  * if balance is over 1000, there is 20% chance of winning card
  * if balance is over 3000, there is 30% winning chance
  * if balance is over 5000, there is 50% winning chance
  * if balance is over 10000, there is 100% chance of winning
* return of 10% payment when there is 50% winning chance and increase balance.

Implement class Paytm with following behaviour:
* bill payment, balance check and balance decreasing in a five days;.
* return of 5% payment when there is 70% winning chance and increase balance.

# Tasks DI

### Task 1

Create two implementations of the PrintInfoToMedia interface, where one implementation prints to the console and the other to a file. Execute the program once with the implementation for console output and once with the file output. Make it so that the two implementations can be switched by just changing the package.
