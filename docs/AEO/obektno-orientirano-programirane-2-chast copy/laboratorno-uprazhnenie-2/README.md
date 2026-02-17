---
layout: default
title: Laboratory lesson 2
parent: Object-oriented Programming - 2 part AEO
has_children: true
nav_order: 2
permalink: /docs/obektno-orientirano-programirane-2-chast-aeo/laboratorno-uprazhnenie-2
---

# Laboratory lesson 2

### SOLID principles

The SOLID principles were introduced by Robert C. Martin in his work from 2000, "Design Principles and Design Patterns". This concept was later refined by Michael Feathers, who introduced the acronym SOLID. Over the past 20 years, these five principles have revolutionized the world of object-oriented programming, changing the way software is written.

What is SOLID, and how does it help in writing better code?

Put simply, Martin's and Feather's design principles encourage the creation of software that is easier to maintain, understand, and flexible. Therefore, despite the inevitability of applications growing in size, their complexity decreases!

The SOLID principles indicate how functions and data structures should be arranged in classes and how these classes should be interconnected.

The use of the term "class" does not mean that these principles only apply to object-oriented programming. A class is simply a related grouping of functions and data. Every software system has such groupings, whether they are called classes or not. The SOLID principles apply to these groups of functions and data, and even to a lower level such as a function or data structure, which we will refer to as elements of the software.

The following five concepts constitute the SOLID principles: 

**S** Single Responsibility principle

**O** Open-Closed principle

**L** Liskov Substitution principle

**I** Interface Segregation principle

**D** Dependency Inversion principle


# SRP - Single Responsibility Principle


This principle states that **an element should have only one responsibility**. Furthermore, it should have only one reason to change, which is a change in the requirement for that element to fulfill.

Applying this principle allows:

*Easier Testing* - A class with a single responsibility will have far fewer test cases.

*Fewer Connections* - Less functionality in one class will result in fewer dependencies.

*Better Organization* - Smaller, well-organized classes are easier to understand than monolithic ones.

The principle that "a function should do one and only one thing" is used when refactoring large functions into smaller ones; it is used at the lowest levels.

```java
public static void calculateSum() {
     System.out.println("Enter the Two numbers for addtion: ");  
     Scanner readInput = new Scanner(System.in);  
     
     int a = readInput.nextInt();  
     int b = readInput.nextInt();
     int sum = a + b;
     
     System.out.println("The sum of numbers is: "+sum);     
}
```

Written in this way, such function cannot accept arguments from different sources, e.g. file, database, another function, sensors, etc. 


The SOLID principle of Single Responsibility (SRP) is similar to the principle mentioned above, but it also has its differences. While the principle "One function, one responsibility" applies at a low level, the higher you go (towards higher levels of abstraction), the more challenging it becomes to recognize responsibilities.

Every class may contain more than one method or field/attribute. In such situation, the approach cannot be applied directly as with functions. Instead, the context must be considered, where this combination of methods and variables will find application.

This principle can be understood and realized by considering various ways in which it can be violated.

Example: 

Let's consider salary application, where we have classс Employee with three methods: calculatePay(), reportHours() и save().

```java
public class Employee {
     public void calculatePay() {
          //method body
     }
     public void reportHours() {
          //method body
     }
     public void save() {
          //method body
     }
}
```

Method calculatePay() calculates salary for accounting department employee.

Method reportHours() is for human resources department.

Method save() is for database admin.

Let's assume functions calculatePay() and reportHours() has common calculating algorithm for working hours with no overtime.

How the code should be organized?

It will be logical to put this common algorithm in a function *regularHours()*, avoiding repeating code.

Combining these 3 methods into one class can lead to interdependencies between different departments. This can cause the actions of one department to affect those of another, which is not always necessary.

Example:


```java
public class Book {

    private String name;
    private String author;
    private String text;

    //constructor, getters and setters

    // methods that directly relate to the book properties
    public String replaceWordInText(String word){
        return text.replaceAll(word, text);
    }

    public boolean isWordInText(String word){
        return text.contains(word);
    }
}
```

In this code fragment there are properties and two methods declared in a class called _Book_. The code works properly and could store as many books in application as necessary.

Later appears requirement to display book information, which leads to some refactoring:

```java
public class Book {
    //...

    void printTextToConsole(){
        // our code for formatting and printing the text
    }
}
```

This method will have access to the system output resources that will introduce new dependency - the class will store information about a book and will present this information.

This will break single responsibility principle since the class will have more than one functionality.

In order to follow SRP a new class should be introduced with the only resposibility of presenting book information:

```java
public class BookPrinter {

    // methods for outputting text
    void printTextToConsole(String text){
        //our code for formatting and printing the text
    }

    void printTextToAnotherMedium(String text){
        // code for writing to any other location..
    }
}
```

Since there is single class for printing information, the printing could be done in different places and sources.

**In order to follow SRP in a program is necessary to know the responsibility of each class.**

Following the SRP principle, classes will adhere to a single functionality. The purpose of methods and data will be clear. This means that the code will exhibit high cohesion, as well as resistance to changes, which together reduce errors.

Although the name of the principle is indicative, its application is not always easy. When developing a project, it is necessary to correctly delineate the responsibilities of each class and pay attention to cohesion.

# OCP - Open-Closed Principle

As the name suggests, this principle states that **software objects should be open for extension but closed for modification**. As a result, when business requirements change, the object can be extended but not modified.

Example:

```
public class Guitar {
    private String make;
    private String model;
    private int volume;

    //Constructors, getters & setters
}
```

In the beginning, this class describes classic guitar, but later decoration should be added.

One way to do this is simply adding *decoration* property to the existing class _Guitar_, but this will require additional modifications.

Instead, it's better to extend class _Guitar_:


```
public class SuperCoolGuitarWithFlames extends Guitar {

    private String flameColor;
 
    //constructor, getters + setters
}
```

Written like that, existing application won't be affected, there will be only additional functionality.

**One way to follow this principle is using interfaces.**

### **1.** Example not following OCP

**Addition and Subtraction Calculator.**


```
public interface CalculatorOperation {}
```

**Class **_**"Addition**_**", which sums two numbers and implements **_**CalculatorOperation**_**:**

```java
public class Addition implements CalculatorOperation {
    private double left;
    private double right;
    private double result = 0.0;

    public Addition(double left, double right) {
        this.left = left;
        this.right = right;
    }

    // getters and setters

}
```

We also need class _Subtraction_:

```java
public class Subtraction implements CalculatorOperation {
    private double left;
    private double right;
    private double result = 0.0;

    public Subtraction(double left, double right) {
        this.left = left;
        this.right = right;
    }

    // getters and setters
}
```

**Execution class:**

```java
public class Calculator {

    public void calculate(CalculatorOperation operation) {
        if (operation == null) {
            throw new InvalidParameterException("Can not perform operation");
        }

        if (operation instanceof Addition) {
            Addition addition = (Addition) operation;
            addition.setResult(addition.getLeft() + addition.getRight());
        } else if (operation instanceof Subtraction) {
            Subtraction subtraction = (Subtraction) operation;
            subtraction.setResult(subtraction.getLeft() - subtraction.getRight());
        }
    }
}
```

Although this code looks fine, this is not good example of OCP. Adding new operation will require refactoring of already existing method _calculate_, meaning the code breaks OCP.

### 2. Same example following OCP

In order to follow OCP, the method _calculate_ will be placed on a higher level of abstraction.

Possible solution is each operation to be defined in a class:

```java
public interface CalculatorOperation {
    void perform();
}
```

```java
public class Addition implements CalculatorOperation {
    private double left;
    private double right;
    private double result;

    // constructor, getters and setters

    @Override
    public void perform() {
        result = left + right;
    }
}
```

Adding new operation means simply creating new class and implementing the same interface:

```java
public class Division implements CalculatorOperation {
    private double left;
    private double right;
    private double result;

    // constructor, getters and setters
    @Override
    public void perform() {
        if (right != 0) {
            result = left / right;
        }
    }
}
```

**With such organization, there will be no need of method modification when new operation is added:**


```java
public class Calculator {

    public void calculate(CalculatorOperation operation) {
        if (operation == null) {
            throw new InvalidParameterException("Cannot perform operation");
        }
        operation.perform();
    }
}
```
In this way, the class is _closed_ for modification but _open_ for extension.


### 3. Conclusion

The principle of open for extension and closed for modification objects improves work on projects by reducing errors that may arise when modifying classes and facilitates testing by encapsulating existing logic and providing the opportunity to focus on the new logic of the application.




**PRACTICE**

# OCP tasks

### Task 1

Consider the calculator example that follows OCP from the lesson. What else could be done to optimize the code?

### Task 2

Look at the following code:

```
public class Cuboid {
// Member variables of this class
public double length;
public double breadth;
public double height;
}
```

```
public class Sphere {
// Storing radius of a sphere
public double radius;
}
```

```
public class Application {
// Returning the total volume of the geometric objects
public double getTotalVolume(Cuboid[] cGeoObjects, Sphere[] sGeoObjects)
{
	// Variable used to store total volume
	double volSum = 0;

	// Iteratively calculating the volume of each Cuboid
	// and adding it to the total volume

	// Iterating using for each loop to
	// calculate the volume of a cuboid
	for (Cuboid geoObj : cGeoObjects) {

		volSum += geoObj.length * geoObj.breadth
				* geoObj.height;
	}

	// Iterating using for each loop to
	// calculate the volume of a cuboid
	for (Sphere geoObj : sGeoObjects) {

		// Iteratively calculating the volume of each
		// Sphere and adding it to the total volume
		volSum += (4 / 3) * Math.PI * geoObj.radius
				* geoObj.radius * geoObj.radius;
	}

	// Returning the to total volume
	return volSum;
}
}
```

```
public class GFG {
public static void main(String args[])
{
	// Initializing a cuboid one as well as declaring
	// its dimensions.
	Cuboid cb1 = new Cuboid();
	cb1.length = 5;
	cb1.breadth = 10;
	cb1.height = 15;

	// Initializing a cuboid two as well as declaring
	// its dimensions.
	Cuboid cb2 = new Cuboid();
	cb2.length = 2;
	cb2.breadth = 4;
	cb2.height = 6;

	////Initializing a cuboid three as well as declaring
	/// its dimensions.
	Cuboid cb3 = new Cuboid();
	cb3.length = 3;
	cb3.breadth = 12;
	cb3.height = 15;

	// Initializing and declaring an array of cuboids
	Cuboid[] cArr = new Cuboid[3];
	cArr[0] = cb1;
	cArr[1] = cb2;
	cArr[2] = cb3;

	// Initializing a sphere one as well as declaring
	// its dimension.
	Sphere sp1 = new Sphere();
	sp1.radius = 5;

	// Initializing a sphere two as well as declaring
	// its dimension.
	Sphere sp2 = new Sphere();
	sp2.radius = 2;

	// Initializing a sphere three as well as declaring
	// its dimension.
	Sphere sp3 = new Sphere();
	sp3.radius = 3;

	// Initializing and declaring an array of spheres
	Sphere[] sArr = new Sphere[3];
	sArr[0] = sp1;
	sArr[1] = sp2;
	sArr[2] = sp3;

	// Initializing Application class
	Application app = new Application();

	// Getting the total volume
	// using get_total_volume
	double vol = app.getTotalVolume(cArr, sArr);

	// Print and display the total volume
	System.out.println("The total volume is " + vol);
}
}
```

Does it follow the OCP?

What should be done?

Implement the solution.

### Task 3

Create a program for coffee machines with basic and premium functions. The code have to follow the OCP.

# SRP tasks

### Task 1

Look at the following code:

```
public class TextManipulator { 
    private String text;
    
    public TextManipulator(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
    public void appendText(String newText) {
        text = text.concat(newText);
    }
    
    public String findWordAndReplace(String word, String replacementWord) {
        if (text.contains(word)) {
            text = text.replace(word, replacementWord);
        }
        return text;
    }
    
    public String findWordAndDelete(String word) {
        if (text.contains(word)) {
            text = text.replace(word, "");
        }
        return text;
    }
    
    public void printText() {
        System.out.println(text);
    }

    public void printOutEachWordOfText() {
        System.out.println(Arrays.toString(text.split(" ")));
    }

    public void printRangeOfCharacters(int startingIndex, int endIndex) {
        System.out.println(text.substring(startingIndex, endIndex));
    }
}
```

Does it follow the SRP?

What should be done?

Implement the solution.

### Task 2

Create application for food delivery that accepts orders, calculates the bill and deliver the order. Follow SRP while implementing the code.



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



# Bonus task
Apply the SOLID principles when writing a program that creates new text files. When an error occur, they are handled and written to a file named 'LocalErrors.txt'.
There are two types of files - those with textual content and those with metadata (the metadata includes the author).
Text files are stored in the 'base' directory, while files with metadata are stored in the 'meta' directory.
If the text of the file starts with 'Author', a metadata file is created.
Each file should be able to be opened for reading.
