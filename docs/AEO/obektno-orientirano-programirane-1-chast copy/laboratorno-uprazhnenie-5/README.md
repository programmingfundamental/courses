---
layout: default
title: Laboratory exercise 3
parent: Object-oriented Programming - 1 part
has_children: true
nav_order: 3
permalink: /docs/object-oriented-programming-1-part/laboratory-exercise-3
---
# Laboratory exercise 3


# Polymorphism


The word **polymorphism** comes from a Greek word meaning having many forms. Polymorphism is an object-oriented programming concept that refers to the ability of a variable, function, or object to take on multiple forms. Through inheritance, objects can override behavior inherited from a parent class with behavior specific to the inheritor. Polymorphism allows the same method to perform different behavior in two ways:

1\.    Overloading

Compile-time polymorphism uses method overloading. Methods in a class can have the same name, but must accept a different number of parameters. Different implementations can exist depending on the number of parameters passed and the type of the parameters.

Requirements:

· Have the same name;

· Have a different number of parameters or have different types of parameters;

There cannot be more than one parameter with the same number and type of parameters.

It is impossible to have two methods with the same number and type of parameters, but return different types, because the compiler will not know which of the two methods to call.

```
class Shapes {
  public void area() {
    System.out.println("Find area ");
  }
public void area(int r) {
    System.out.println("Circle area = "+3.14*r*r);
  }
 
public void area(double b, double h) {
    System.out.println("Triangle area="+0.5*b*h);
  }
public void area(int l, int b) {
    System.out.println("Rectangle area="+l*b);
  }
 
 
}
 
class Main {
  public static void main(String[] args) {
    Shapes myShape = new Shapes();  // Create a Shapes object
     
    myShape.area();
    myShape.area(5);
    myShape.area(6.0,1.2);
    myShape.area(6,2);
     
  }
}

```

2\.    Overriding

Runtime polymorphism uses method overriding. A child class can provide a different implementation than the parent class. The compiler instruction used is @Override – it tells the compiler that this method from the parent class has a new implementation. This instruction (annotation) is used at compile time to check if the method is overridden correctly.

Requirements for overriding a method:

· Have the same name as the method in the parent class;

· Pass the same number of parameters;

· Pass parameters of the same type;

· Return the same type.

Static methods cannot be overridden because they belong to the class, while method instances (non-static) belong to the object.

```
class Shapes {
  public void area() {
    System.out.println("The formula for area of ");
  }
}
class Triangle extends Shapes {
  public void area() {
    System.out.println("Triangle is ½ * base * height ");
  }
}
class Circle extends Shapes {
  public void area() {
    System.out.println("Circle is 3.14 * radius * radius ");
  }
}
class Main {
  public static void main(String[] args) {
    Shapes myShape = new Shapes();  // Create a Shapes object
    Shapes myTriangle = new Triangle();  // Create a Triangle object
    Shapes myCircle = new Circle();  // Create a Circle object
    myShape.area();
    myTriangle.area();
    myShape.area();
    myCircle.area();
  }
}
```

# Abstract class

A class that is declared using the keyword “abstract” is known as an abstract class. It can have abstract methods (methods without a body) as well as concrete methods (regular methods with a body). A normal class (non-abstract class) cannot have abstract methods.

An abstract class cannot be instantiated, which means you are not allowed to create an object from it.

Let’s say we have a class Animal that has a method sound() and subclasses (see inheritance) of it like Dog, Lion, Horse, Cat etc. Since the sound that animals make is different, it doesn’t make sense to implement this method in the parent class. This is because each child class has to override this method to provide its own implementation details, like for example class Lion will say “Roar” in this method and class Dog will say “Woof”.

So, when we know that all the classes that inherit from Animal will have to override this method, then there is no point in implementing this method in the parent class. For this reason, making this method abstract would be the better choice, because by making this method abstract, we force all subclasses to implement this method (otherwise we get a compilation error), also we do not need to create an implementation of this method in the parent class.

Since the Animal class will have an abstract method, it must be declared as an abstract method.

When the sound() method is abstract, it is mandatory for the descendants of the Animal class to provide details about the implementation of this method. This way, we guarantee that every animal has a sound.

```
//abstract class
abstract class Animal{
   //abstract method
   public abstract void sound();
}
//Child-class of class Animal
public class Dog extends Animal{

   public void sound(){
	System.out.println("Woof");
   }
}

public class Main {
   public static void main(String args[]){
	Animal obj = new Dog();
	obj.sound();
   }
}
```

### Abstract class definition

An abstract class defines methods, but does not necessarily implement all methods.

```
//Abstract class
abstract class A{
   //abstract method with no body
   abstract void myMethod();

   //concrete method
   void anotherMethod(){
      //implemntation
   }
}
```

### Rules

* **Note 1:**

There are cases where it is difficult or often unnecessary to implement all the methods in the parent class. In these cases, the parent class can be declared abstract, making it a special class that is not fully implemented.

A class that inherits an abstract class must implement all the methods that are declared abstract in the parent class.

* **Note 2:**

An abstract class cannot be instantiated, which means that you cannot create an object of it. To use this class, you must create another class that extends this class and provides the implementation of the abstract methods, and then you can use the object of this child class to call non-abstract methods of the parent class, as well as implemented methods (those that were abstract in the parent but implemented in the derived class).

* **Note 3:**

If a child does not implement all the abstract methods of the abstract parent class, then the child class must also be declared abstract.

Since these classes are incomplete because they have abstract methods that do not have a body, so if it is allowed to create objects of these classes, then if someone calls the abstract method using such an object, there will be no actual implementation of the method.

### Differences between abstract and concrete class

A class that is not abstract is called a concrete class. In the above example, Animal is an abstract class, and Cat, Dog & Lion are concrete classes.

Key points:

* An abstract class cannot be used until it is extended by another class.
* If you declare an abstract method in a class, then you must also declare the class abstract. You cannot have an abstract method in a concrete class. The reverse is not always true: if a class does not have an abstract method, it can also be marked as abstract.
* It can also have a non-abstract method (concrete).


# Interface

An interface looks like a class, but it is not a class. An interface can have methods and variables just like a class, but methods declared in an interface are abstract by default. Also, variables declared in an interface are public static final by default.

Since methods in interfaces have no body, they must be implemented by a class before they can be accessed. The class that implements the interface must implement all the methods of that interface. In Java, inheritance from more than one class is not allowed, but you can implement more than one interface from a single class.

```
interface ExampleInterface {
   /* All methods are public and abstract by default
    * these methos have no body
    */
   void method1();
   void method2();
}
class Demo implements ExampleInterface {
   /* Class that implements given interface 
    * must have implementation of all its methods
    */
   public void method1()
   {
	System.out.println("implementation of method1");
   }
   public void method2()
   {
	System.out.println("implementation of method2");
   }
   public static void main(String arg[])
   {
	MyInterface obj = new Demo();
	obj.method1();
   }
}
class Main {
   public static void main(String arg[])
   {
	MyInterface obj = new Demo();
	obj.method1();
   }
}
```

### Interface and inheritance

```
interface Inf1 {
   public void method1();
}
interface Inf2 extends Inf1 {
   public void method2();
}
public class Demo implements Inf2 {
   /* Class that implements given interface
    * must have implementation of all interface methods
    * and of its parent methods (if there are)
    */
    public void method1(){
	System.out.println("method1");
    }
    public void method2(){
	System.out.println("method2");
    }
    public static void main(String args[]){
	Inf2 obj = new Demo();
	obj.method2();
    }
}
```

### Markup interfaces

An empty interface is known as a tag or marker interface. For example, Serializable, EventListener, Remote(java.rmi.Remote) are marker interfaces. These interfaces have no field and methods in them.

### Important

1. We cannot instantiate an interface in java. This means we cannot create an object of an interface
2. An interface provides complete abstraction as none of its methods have a body.
3. **implements** keyword is used by classes to implement an interface.
4. To provide an implementation in a class of any method of an interface, the method must be public.
5. The class that implements an interface must implement all the methods of that interface, otherwise the class must be abstract.
6. An interface cannot be declared as private, protected.
7. All methods of an interface are abstract and public by default.
8. Variables declared in an interface are public, static and final by default.



# Tasks

#### Task 1:

Create a class Employee that has the following attributes:

· Company name (static)

· Number of employees (static) (incremented when creating a new employee)

· First name (visible to the successor)

· Last name (visible to the successor)

· Personal identification number (private)

· City (visible to the successor)

· Salary (private)

And methods:

· accessor methods, depending on the access modifier;

· constructors;

· method for text representation of the object;

· salary, which returns 0;

· static method that returns the total number of employees, accepting an array of employees as a parameter.


#### Task 2:

Create a class Manager that inherits the Worker class and has the following attributes:

· Sector (private)

Methods:

· Accessor methods, depending on the access specifier;

· Constructors;

· method for text representation of the object;

· Method "salary" that overrides the salary method of the parent class, returning the value of the salary attribute.

· Method "earnings", which accepts two parameters - number of working days and earnings for one working day;

· Method "earnings", which accepts one parameter - earnings for one working day and returns the multiplication of the earnings for one working day by 22 working days;


#### Task 3:

Create a class Clerk with the following attributes:

· Additional percentage;

· Sector (private);

The following methods:

· Access methods, depending on the access specifier;

· Constructors;

· method for text representation of the object;

· Method “salary” that overrides the salary method of the parent class, returning the value of the salary attribute with the additional percentages added to the salary.

· Method for total work experience without a parameter, which returns the work experience in years (calculated based on the year of birth of the clerk, assuming that he started working at 23 years old);

· Method for total work experience with one parameter of type String, which indicates how many years the clerk has not worked (these years must be subtracted from the total work experience);

· Method for total work experience with one parameter of type double , which indicates how many years the clerk has not worked (these years must be subtracted from the total work experience);


#### Task 4:

Create a class that contains a main method:

\- Create 2 managers and display the text representation of the objects;

\- Calculate the manager's salary;

\- Calculate his earnings for 18 working days and a full working month

\- Create 4 clerks and display the text representation of the objects;

\- Calculate the salary of each of the clerks

\- Display the length of service of each employee, with two of them having missed 2 and 3 years respectively (use the three different methods)

\- Display how many employees there are in the entire company

\- Display the name of the company where all the employees work.
