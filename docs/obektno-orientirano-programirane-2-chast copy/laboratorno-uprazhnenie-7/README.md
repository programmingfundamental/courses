---
layout: default
title: Laboratory lesson 7
parent: Object-oriented Programming - 2 part AEO
has_children: true
nav_order: 7
permalink: /docs/obektno-orientirano-programirane-2-chast-aeo/laboratorno-uprazhnenie-7
---

# Laboratory lesson 7


# Decorator

Decorator is member of structural design pattern group. It is used for dynamically add attributes and behavior to already existing objects without changinh their structure. Decorator plays role of wrapper class for the already existing one and implements the same interface.

Let's consider the following example:

```
public interface Book {
    String decorateBook();
}
```

There is an implementation necessary, BookImpl:

```
public class BookImpl implements Book{
    @Override
    public String decorateBook() {
        return "Book has: ";
    }
}
```

Next step is creating abstract class BookDecorator with interface object as attribute:

```
public abstract class BookDecorator implements Book {
    private Book book;
    public BookDecorator(Book book) {
        this.book = book;
    }
    @Override
    public String decorateBook() {
        return book.decorateBook();
    }
}
```

The abstract class above is the base decorator and all other decorators will extend it.

```
public class HardCoverDecorator extends BookDecorator {
    public HardCoverDecorator(Book book) {
        super(book);
    }
    @Override
    public String decorateBook() {
        return super.decorateBook() + addHardCover();
    }
    private String addHardCover() {
        return "\n hard cover";
    }
}
```

```
public class CoverIllustrationsDecorator extends BookDecorator {
    public CoverIllustrationsDecorator(Book book){
        super(book);
    }
    @Override
    public String decorateBook() {
        return super.decorateBook() + addCoverIllustrations();
    }
    private String addCoverIllustrations() {
        return "\ncover illustrations";
    }
}
```

```
public class BookIllustrationsDecorator extends BookDecorator {
    public BookIllustrationsDecorator(Book book) {
        super(book);
    }
    @Override
    public String decorateBook() {
        return super.decorateBook() + addBookIllustrations();
    }
    private String addBookIllustrations() {
        return "\n book illustrations";
    }
}
```

```
public class VolumeDecorator extends BookDecorator {
    public VolumeDecorator(Book book) {
        super(book);
    }
    @Override
    public String decorateBook() {
        return super.decorateBook() + addVolume();
    }
    private String addVolume() {
        return "\n more than one volume";
    }
}
```


When to use Decorator:

* in order to dynamically add new behavior to a given object and at the same time this new behavior to not affect another objects;
* when it's necessary to add functionalities which might be later modified;
* when addition of subclasses is not practical.

Decorator advantages:

* offers better flexibility than inheritance;
* extends and changes object behavior without using subclasses;
* gives possibility to combine different behavior by wrapping given object with different decorators.

As disadvantage of Decorator could be considered the dependency of its behavior on the calling order.



# Bridge

The Bridge design pattern is a structural pattern used to separate abstraction from its implementation. In this way, both abstraction and implementation could be changed and vary independently. The Bridge also hides execution details from the abstraction. The pattern also uses composition instead of inheritance.


Let's consider example with two interfaces, Shape and Color, where both has few implementations.

<figure><img src="../../../assets/image (95).png" alt=""><figcaption></figcaption></figure>

The code will be:

```java
public interface Color {
    public void applyColor();
}
```

```java
public abstract class Shape {
	//Composition - implementor
	protected Color color;
	
	//constructor with implementor as input argument
	public Shape(Color c){
		this.color=c;
	}
	
	abstract public void applyColor();
}
```

The bridge between abstraction and implementation here is by composition:

```java
public class Triangle extends Shape{

	public Triangle(Color c) {
		super(c);
	}

	@Override
	public void applyColor() {
		color.applyColor();
	} 
}
```

```java
public class Pentagon extends Shape{

	public Pentagon(Color c) {
		super(c);
	}

	@Override
	public void applyColor() {
		color.applyColor();
	} 
}
```
```java
public class RedColor implements Color{

	public void applyColor(){
		System.out.println("red.");
	}
}
```

```java
public class GreenColor implements Color{

	public void applyColor(){
		System.out.println("green.");
	}
}
```

```java
public class Application {

	public static void main(String[] args) {
		Shape tri = new Triangle(new RedColor());
		tri.applyColor();
		
		Shape pent = new Pentagon(new GreenColor());
		pent.applyColor();
	}

}
```


**PRACTICE**


### Task 1

Finish the example for Decorator from the lesson and add behavior for the attributes. Modify existing decorators to reflect the attributes.


### Task 2

Use Decorator and Bridge to write a program for new books creation.

Each book has pages, each page has page number and content; in the beginning the book has base price and annotation.

Later there is media added to the book - electronic book or physical book. The physical book has hard cover or paper cover; hard cover has binding (leather, paper, cardboard) and weight, paper cover has weight.

Every addition to the base book changes the price.

