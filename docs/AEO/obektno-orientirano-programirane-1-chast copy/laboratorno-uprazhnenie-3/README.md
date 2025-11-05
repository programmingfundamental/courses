---
layout: default
title: Laboratory exercise 2
parent: Object-oriented Programming - 1 part
has_children: true
nav_order: 2
permalink: /docs/object-oriented-programming-1-part/laboratory-exercise-2
---
# Laboratory exercise 2

# Inheritance

**Inheritance** is one of the basic principles of OOP. It is a mechanism by which a class can inherit the state (fields) and behavior (methods) of another (parent) class.

Through inheritance, the attributes and methods of the parent class can be reused when we create our own class – the descendant (if they have an access modifier other than private).

In Java, a class can have only one parent class.

The parent class does NOT have the variables and methods of the child class, regardless of their access modifiers.

Super/parent class – a parent class, its attributes and methods can be reused.

Sub/child class – a descendant class. It inherits all the properties of the parent class and can add its own attributes. It also inherits the behavior of the parent class and can add new behavior.

```java
class Animal { 
    void eat(){
        System.out.println("eating...");
    } 
} 
```

```java
class Dog extends Animal { 
    void bark(){
    System.out.println("barking...");
    } 
}
```

```java
class BabyDog extends Dog { 
    void weep(){
    System.out.println("weeping...");
    } 
}
```


```java
class Main { 
    public static void main(String args[]){ 
        BabyDog d=new BabyDog(); 
        d.weep(); 
        d.bark(); 
        d.eat(); 
    }
} 
```

# Keywords this and super

**this** and **super** cannot be used in static contex.

### Keyword this

The reserved word **this** refers to the current object in a method or constructor. Most often **this** is used to prevent confusion between attributes (of the class) and parameters (of the method) that have the same name.

**this** can also be used to :

· call the constructor of the current class;

· call a method of the current class;

· return an object of the current class;

· be passed as an argument to a method;

· be passed as an argument to a constructor


```java
public class Subject {
    String name;
    double finalGrade;
    int hours;

    public Subject() {
    }

    public Subject(String name, int hours) {
        this.name = name;
        this.hours = hours;
    }

    public Subject(String name, int hours, double finalGrade) {
        this(name, hours); //invokes constructor with only two parameters
        this.finalGrade = finalGrade;
    }

    public String getName(){
        return this.name;
    }

    public Double getFinalGrade(){
        return this.finalGrade;
    }

    public int getHours()
    {
        return this.hours;
    }

    public void printSubjectNameAndHours(){
        System.out.println("Subject: " + this.getName() + " : " + this.getHours()); //method invoke
    }

}
```

### Keyword super

The keyword super points to the superclass (parent class) of the object. It is used to call attributes, methods, and constructors inherited from the parent class.


```java
class Animal { // Superclass (parent)
  public void animalSound() {
    System.out.println("The animal makes a sound");
  }
}
```

```java
class Dog extends Animal { // Subclass (child)
  public void animalSound() {
    super.animalSound(); // Call the superclass method
    System.out.println("The dog says: bow wow");
  }
}
```

```java
public class Main {
  public static void main(String args[]) {
    Animal myDog = new Dog(); // Create a Dog object
    myDog.animalSound(); // Call the method on the Dog object
  }
}
```


# Encapsulation

**Encapsulation** is defined as the packaging of data into a single unit. It is the mechanism that binds the code and the data it manipulates. Encapsulation prevents access to the data unless it is in accordance with the rules set.

* Technically, in encapsulation, the variables (properties) of a class are hidden from any other class and can only be accessed through a function of the class that owns them in which they are declared.
* As in encapsulation, the data in a class is hidden from other classes using the concept of data hiding, which is achieved by making the members or methods of the class private, and the class is exposed to the end user or the world without providing any implementation details using the concept of abstraction, so it is also known as **a combination of data hiding and abstraction**.
* Encapsulation can be achieved by declaring all the variables in the class as private and writing public methods in the class to set and get the values ​​of the variables.
* It is combined with a defined setter and getter method.

## Encapsulation examples

### 1. Mutable attributes

Mutable attributes can be changed after the object is created using **setter methods**.

```java
class Person {
private String name;
private int age;

public void setName(String name) {
    this.name = name;
}

public void setAge(int age) {
    this.age = age;
}
}

public class Main {
public static void main(String[] args) {
Person p = new Person();
p.setName("Ivan"); // модификация чрез setter
p.setAge(25); // модификация чрез setter
System.out.println(p.getName() + " " + p.getAge());
}
}
```


### 2. Unmutable attributes

Unmutable attributes cannot be changed after the object is created. This is achieved by:
* Declaring the field as final.
* Lacking a setter method.

```java
class Person {
private final String name; // final прави атрибута немодифицируем
private final int age;

public Person(String name, int age) {
    this.name = name;
    this.age = age;
}

public class Main {
public static void main(String[] args) {
Person p = new Person("Maria", 30);
// p.setName("Ivan"); // Грешка, защото няма имплементиран setter
System.out.println(p.getName() + " " + p.getAge());
}
}
```

**Advantages of Encapsulation**:

* **Data hiding**: This is a way to restrict access to the properties of a class by hiding the implementation details. Encapsulation also provides a way to hide data. The user will have no idea about the internal implementation of the class. It will not be visible to the user how the class stores values ​​in the variables. The user will only know that we are passing the values ​​to a method as a setter and the variables are initialized with that value.
* **Increased flexibility:** Class variables would be read-only or write-only depending on the requirement. If there is a need to make the variables read-only then the setter methods like setName(), setAge() should be ommited or if the variables should be write-only then there will be no getters like getName(), getAge()
* **Reusability:** Encapsulation also improves the reuse and is easy to change with new requirements.
* **Test code is easy:** Encapsulated code is easy to test for unit testing.


# Access modifiers

They represent 3 keywords that are placed before:

· classes;

· variables;

· methods during declaration.

Their purpose is to set the visibility of the declared element, the way in which it can be accessed. They are:

· *private* – visibility is only within the class. If we try to access the declared type outside the class, it will not be visible;

· *protected* – visibility is in the class, in its descendants and in the package of its class;

· *public* – it is visible everywhere (in the class, in the package, in the descendants, in other packages that are within the project).

If no modifier is specified, visibility will be only in the package. The default access modifier provides greater accessibility than private, but is more limited than protected.
