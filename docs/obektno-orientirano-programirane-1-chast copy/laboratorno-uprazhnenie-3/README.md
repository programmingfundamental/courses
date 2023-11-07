---
layout: default
title: Laboratory exercise 3
parent: Object-oriented Programming - 1 part
has_children: true
nav_order: 3
permalink: /docs/object-oriented-programming-1-part/laboratory-exercise-3
---

# Laboratory exercise 3


**Polymorphism**

Polymorphism indicates that object/unit has many forms. In OOP context, polymorphism addresses the fact that variable, method or object could have many forms. Using inheritance, objects could override behaviour adopted from their parent class and therefore acting in a different way. Given method could perform different behaviour using two different approaches:

1\.    Overloading

Overloading is example of compile time polymorphism. This means that methods could have same name, but different arguments. Different implementations should vary in number of arguments or their type.

Requirements:

·        Have the same name;

·        Have different number of arguments or different type of arguments;

It's impossible to have more than one method with same number and type of arguments.
It's also impossible to have two methods with same number and type of arguments, but with different returning type, because the compiler will be unsure which method to be called.


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

Overriding is example of runtime polymorphism and means that child class could offer different implementation than its parent class. The annotation *@Override* is used to inform the compiler that already defined in a parent class method has new implementation. The annotation is also used in compile time to check if the method is correctly overriden.

Requirements for method overriding:

·        To have the same name;

·        To have same number of arguments;

·        To have same type of arguments;

·        To return same type of result.

Static methods could not be overriden since they belong to the class, whereas non-static methods belong to the object.

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

Class which is declared with keyword "abstract" is called abstract class. Such class could have concrete methods and abstract methods - methods without body, while normal classes has only concrete methods. One of specifics of abstact classes is that **such class could not be instantiated**, i.e. there is no way to create instance of abstract class.
Usually, abstract methods describe common behaviour, where each child class has their own implementation (see the example below). Each child class must implement the abstract method (or methods) from its parent class, elsewhere there will be a compilation error.


```
//abstract class
abstract class Animal {
   //abstract method
   public abstract void sound();
}
//Child class of Animal
public class Dog extends Animal {

@Override
public void sound() {
	  System.out.println("Woof");
   }
}

public class Main {
   public static void main(String args[]) {
	  Animal obj = new Dog();
	  obj.sound();
   }
}
```

### Abstract class declaration

The abstract class defines all methods, but not all methods are implemented.

```
// Class declaraton
abstract class A {
   // abstract method without body
   abstract void myMethod();

   // concrete method
   void anotherMethod() {
      // method implementation
   }
}
```

### Remarks

*   **Remark 1:**

    There are cases when it's hard or not necessary to implement all method in a parent class. In such case, the parent class is declared as abstract, which means the class is not completely implemented.
    Child class of abstract parent class MUST implement all abstract methods, declared in its superclass.
    
*   **Remark 2:**

    It is impossible to have instances of an abstract class. In order to use such class, there must be child classes (at least one) which offer implementation of all abstract methods.
    
*   **Remark 3:**

    If a child class does not implement all abstract methods from its abstract parent, the child could be also declared abstract.



### Difference between *abstract* and *concrete* class

In the ecample above, Animal is abstract class, but Cat, Dog & Lion are concrete classes.

Differences:

* Abstract class could not be used until it is extened by another class;
* There are no abstract methods in concrete classes. But it is possible to have abstract class without abstract methods;
* Abstract class could have both abstract and concrete methods.


# Interface

Interface looks like class, but it's not. It could have methods and properties, but by default all declared methods are abstract. All declared properties in interface must be public, static and final (also by default).
Since interface methods do not have body, they should be implemented by some class in order to be accessed. If a class implements given interface, it should implement all of its methods, not just some of them. Single class could implement more than one interface.


```
interface ExampleInterface {
   /* All methods are abstract by default,
    * so they do not have bodies
    */
   public void method1();
   public void method2();
}

class Demo implements ExampleInterface {
   /* Classes that implement given interface
    * should have all methods implemented
    */
   public void method1() {
	  System.out.println("implementation of method1");
   }
   public void method2() {
	  System.out.println("implementation of method2");
   }
}

class Main {
   public static void main(String arg[]) {
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
    * should implement all its methods, including
    * methods from parent interface
    */
    public void method1() {
	    System.out.println("method1");
    }
    public void method2() {
	    System.out.println("method2");
    }

public static void main(String args[]){
	    Inf2 obj = new Demo();
	    obj.method2();
    }
}
```

### Marker interfaces

Empty interfaces, with no methods or constants declared inside, are known as marker interfaces or tagging interfaces. Example of such interface is Serializable or EventListener.

### Important

1. An interface could not be instantiated, i.e. no interface object could be created.
2. Interface provides full abstraction since all declared methods do not have body.
3. Interface is implemented using **implements** keyword.
4. If given class implements interface, it should implement all of its methods - elsewhere the class should be declared abstract.
5. Interface could not be declared as private or protected.
6. By default, all methods in interface are abstract and public.
7. Properties, declared in interface, are public, static and final by default, i.e. they are constants.



###

