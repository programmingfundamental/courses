---
layout: default
title: Laboratory lesson 8
parent: Object-oriented Programming - 2 part AEO
has_children: true
nav_order: 8
permalink: /docs/obektno-orientirano-programirane-2-chast-aeo/laboratorno-uprazhnenie-8
---

# Laboratory lesson 8

# Composite

Composite is example of structural design patterns. It is used when there is need of hierarchy in some group of objects or when group of objects needs to be treated as a single objects.

The Composite needs the following elements: 

1. Base component - interface for all objects from the composition, could be also abstract class;
2. Collection - defines composite elements behaviour.
3. Composite - contains composite elements and performs operations in the component..

Example: 

*Base component*

```
public interface Shape {
	
	public void draw(String fillColor);
}
```

*Composite element*

There could be many "leaf" elements, that implement the interface

```

public class Triangle implements Shape {

	@Override
	public void draw(String fillColor) {
		System.out.println("Drawing Triangle with color "+fillColor);
	}

}
```


```

public class Circle implements Shape {

	@Override
	public void draw(String fillColor) {
		System.out.println("Drawing Circle with color "+fillColor);
	}

}
```

*Composite object*

The coposite object contains group of components and usually adds some specific behaviour.

```

import java.util.ArrayList;
import java.util.List;

public class Drawing implements Shape{

	
	private List<Shape> shapes = new ArrayList<Shape>();
	
	@Override
	public void draw(String fillColor) {
		for(Shape sh : shapes)
		{
			sh.draw(fillColor);
		}
	}
	
	
	public void add(Shape s){
		this.shapes.add(s);
	}
	
	
	public void remove(Shape s){
		shapes.remove(s);
	}
	
	
	public void clear(){
		System.out.println("Clearing all the shapes from drawing");
		this.shapes.clear();
	}
}
```

```
public class Application {

	public static void main(String[] args) {
		Shape tri = new Triangle();
		Shape tri1 = new Triangle();
		Shape cir = new Circle();
		
		Drawing drawing = new Drawing();
		drawing.add(tri1);
		drawing.add(tri1);
		drawing.add(cir);
		
		drawing.draw("Red");
		
		drawing.clear();
		
		drawing.add(tri);
		drawing.add(cir);
		drawing.draw("Green");
	}

}
```

The output of the main function written above is:

```
Drawing Triangle with color Red
Drawing Triangle with color Red
Drawing Circle with color Red
Clearing all the shapes from drawing
Drawing Triangle with color Green
Drawing Circle with color Green
```

#### When to use Composite:

* when group of objects should have behaviour of a single object, or
* when there is a need to create tree structure.





# Flyweight

Structural design pattern Flyweight is used to decrease number of objects created which leads to better application structure and decreases the time for object creation. Flyweight is also suitable when lot of similar objects should be created. The flyweight-object is a shared object that can be used in different contexts simultaneously.

In order to implement Flyweight there needs to be:

- interface;
- one or more concrete implementations of the declared interface;
- class with factory method that is responsible for creation and caching of the objects.

Let's consider the following example:

```
public interface StudentInformation {

    void display();
}
```

There are two concrete implementations of the interface - BachelorStudent и MasterStudent: 

```
public class BachelorStudent implements StudentInformation{

    private String facultyNumber;
    private String firstName;
    private String lastName;

    public BachelorStudent(String facultyNumber, String firstName, String lastName) {
        this.facultyNumber = facultyNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BachelorStudent that = (BachelorStudent) o;
        return Objects.equals(facultyNumber, that.facultyNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facultyNumber);
    }

    @Override
    public String toString() {
        return "BachelorStudent{" +
                "facultyNumber='" + facultyNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    @Override
    public void display() {
        System.out.println(this);
    }
}
```

```
public class MasterStudent implements StudentInformation{

    private String facultyNumber;
    private String firstName;
    private String lastName;

    public MasterStudent(String facultyNumber, String firstName, String lastName) {
        this.facultyNumber = facultyNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BachelorStudent that = (BachelorStudent) o;
        return Objects.equals(facultyNumber, that.facultyNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facultyNumber);
    }

    @Override
    public String toString() {
        return "MasterStudent{" +
                "facultyNumber='" + facultyNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    @Override
    public void display() {
        System.out.println(this);
    }
}
```

There have to be a class with factory method:

```
public class StudentFactory {

    private static Map<String, StudentInformation> students = new HashMap<>();

    public static StudentInformation getInfo(String type, String facultyNumber, String firstName,
                                             String lastName) {
        StudentInformation student = students.get(facultyNumber);

        if (student == null) {
            student = type.equalsIgnoreCase("bachelor")
                    ? new BachelorStudent(facultyNumber, firstName, lastName)
                    : new MasterStudent(facultyNumber, firstName, lastName);
            students.put(facultyNumber, student);
        }

        return student;
    }

    public static long getStudentsNumber() {
        return students.size();
    }
}
```

The collecction has the student faculty number as key. It's important to remember that once the object is created and added to the collection, it's immutable, i.e. can not be changed.

The client code is as follows:

```
public class Application {

    public static void main(String[] args) {
        StudentInformation firstStudent = StudentFactory.getInfo("bachelor", "123456", "John", "Doe");
        StudentInformation secondStudent = StudentFactory.getInfo("master", "112233", "Jane", "Doe");
        StudentInformation thirdStudent = StudentFactory.getInfo("master", "123456", "Jake", "Doe");

        firstStudent.display();
        secondStudent.display();
        thirdStudent.display();

        System.out.println(StudentFactory.getStudentsNumber());
    }
}
```

Despite creation of three objects with the factory method, the collection contains only two. The reson is the same key of two objects.


Flyweight implementation could make an application more complex, but this could be justified if the system should be scalable and have to deal with large number of objects.

While Flyweight usage optimizes memory usage, this design pattern could lead to redundant performance costs. Constant searches and shared state management could slow down the operations in cases when the performance is critical.



**PRACTICE**

*Task 1*. Create application for online shop catalogue.

Each item has unique id, name, base price and profit. As behaviour has final price calculation and return of item' information.

Client could make an order with one or more items inside it.

Use Composite pattern for implementation.


*Task 2*. Create program for bank accounts.

Base account has id and balance, and the options of deposit, withdraw and check account information. Account is created only if it doesn't exist.

Each account could be extended with properties as account holder (with two names), currency (BGN, EUR, USD) and interest.

Use Decorator and Flyweight patterns for implementation.


