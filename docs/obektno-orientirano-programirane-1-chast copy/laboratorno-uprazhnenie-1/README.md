---
layout: default
title: Laboratory exercise 1
parent: Object-oriented Programming - 1 part
has_children: true
nav_order: 1
permalink: /docs/object-oriented-programming-1-part/laboratory-exercise-1
---

# Laboratory exercise 1


# Class

**Class** in OOP context represents an object from the world around us. It's a template that describes possible **states** and **behaviour** of a particular object. Often a class represents vast category objects with common properties.

The class defines **new data type**. It declares what attributes will an instance of this object have, but there are no values assigned for particular object. The classes could also define functions that are called "methods" and are available only for the current class' instances. These methods are declared in the class and present some action.

Class declaration contains:

1. **Declaration (class)** - starts with keyword "class".
2. **Class identifier -** - class name must start with capital letter; if the name contains more than one word, they are written together, with no spaces between.
3. **Class body** - class body and content is surrounded with brackets, "**{**" and "**}**".

```
class Dog {
    // Class body
}
```

#### Properties (attributes)

The properties are variables that describe the objects of a given class and are used in its methods. Could be called also fields.

```
class Dog {
    // Class properties
    String name;
    String breed;
    int age;
    String color;
}
```

#### Constructor

The constructor is a **special type** of method, used for initialization of new objects from a given class. The constructor is called when a new object is created.

#### Requirements:

* The constructor name is the same as the class name;
* There is no type of result returned;
* Could have or no arguments.

Type of constructors:

Default constructor - there are no arguments and all the attributes have default values assigned. There is just one default constructor for each class.
In the example below there are some default values assigned. It is possible that the default constructor has an empty body - in this case, all the attributes have default values depending on their data type assigned.

```
class Dog {
    // Properties
    String name;
    String breed;
    int age;
    String color;
 
    // Default constructor
    public Dog() {
        this.name = "no name";
        this.breed = "dog";
        this.age = 0;
        this.color = "black";
    }
}
```

Parameterized constructor - constructor with arguments that are used for initialization of the class properties with some values. Single class could have more than one parameterized constructor.

```
class Dog {
    // Properties
    String name;
    String breed;
    int age;
    String color;
 
    // Parameterized constructor
    public Dog(String name, String breed, int age, String color) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.color = color;
    }
}
```

If there is no declared constructor in a class, Java creates itself a default constructor for the class. **If there is a constructor declared, there is no default constructor available unless it is declared explicitly!**

#### Behaviour (methods)

The methods define class and its objects' behaviour.

* Access methods, accessors - as the name suggests, these methods give access to the class attributes.
* Read method – getter:  the method returns the propery value. Getters do not have parameters.

```
class Dog {
    // Properties
    String name;
    String breed;
    int age;
    String color;
 
    // Parameterized constructor
    public Dog(String name, String breed, int age, String color) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.color = color;
    }
 
    // Getter for "name" property
    public String getName() {
        return name;
    }
 
    // Getter for "breed" property
    public String getBreed(){
        return breed;
    }
 
    // Getter for "age" property
    public int getAge() {
        return age;
    }
 
    // Getter for "color" property
    public String getColor() {
        return color;
    }
}
```

* Write method – setter: modifies an attribute value. Setters do not return result and have one argument - the vallue that will be assigned.

```
class Dog {
    // Properties
    String name;
    String breed;
    int age;
    String color;
 
    // Parameterized constructor
    public Dog(String name, String breed, int age, String color) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.color = color;
    }
 
    // Setter for "name" property
    public void setName(String name) {
        this.name = name;
    }
 
    // Setter for "breed" property
    public void setBreed(String breed) {
       this.breed = breed;
    }
 
    // Setter for "age" property
    public void setAge(int age) {
        this.age = age;
    }
 
    // Setter for "color" property
    public void setColor(String color) {
        this.color = color;
    }
}
```

**Method toString()**

This method returns the textual representation of an object. Possible implementation with some formatting is shown below:

```
public String toString() {
 return ("My name is "+ this.getName()+
        ".\nMy breed, age and color are " +
        this.getBreed()+"," + this.getAge()+
        ","+ this.getColor());
}
```

* equals - standard method for Java and OOP in general. Used for comparison of objects vlaues and defines criteria for parity, i.e. when two objects from the same class could be considered the same. In the example below, two dogs will be the same if they are from the same breed.

```
public boolean equals(Object o) {
    if (this == o) return true;
    Dog dog = (Dog) o;
    return breed.equals(dog.breed);
}
```

Keyword "new"

Keyword "new" in Java:

    Used for new instance creation;
    Reserves memory in runtime;
    Reserves memory in the heap and returns reference to it;
    Calls the object's constructor.

Person firstPerson = new Person();

Person secondPerson = new Person(“Plamen”, 20);


####

Object

Object is an instance created by a class definition. When a class is created according to a particular class definition, it's said that the object is from "class name" type.

Object is the basic unit in OOP. Usually an OOP-program contains many objects that interacts through method calls. An object is defined by:

    State - represents objects' properties;
    Behaviour - defined by object's methods;
    Identifier - gives unique name to the object and makes possible the interaction with other objects.

Object initialization

A class is initialized when an object of this type is created. All instances of a class share the same attributes and behaviour, but the values of the attributes are unique for each object. Given class could have countless instances.

public static void main(String[] args)
{
    Dog tuffy = new Dog("tuffy","papillon", 5, "white");
    System.out.println(tuffy.toString());
}

Object comparison

There are two ways of comparing objects - by reference and by value.

Reference comparison checks the objects addresses in the memory:

k9 == rex;

Value comparison checks and compares value parity of class attributes:

k9.equals(rex);


**Keyword "static"**

Запазената дума static в Java се използва основно за управление на паметта. Static принадлежи повече на класа отколкото на инстанцията на класа. Можем да прилагаме „static“ на:
The keyword "static" is used mainly for memory management in Java. The word could be used for:

· Variables;

§ Static variables are used for common property between all objects.
Example: COmpany name for all of its employees.
Static variable is stored only once in the memory of the class-area when memory for the class is reserved.

· Methods;

§ Static methods could not use non-static attributes or to call non-static methods.

· Blocks;

§ Static blocks are executed before the main method, during the classes' load.

· Nested classes:

class Student{ 
   static String college ="ITS"; // static variable
   int fn;
   String name; 
   public Student(int num, String n){ 
   fn = num; 
   name = n; 
   }
  static void changeCollegeName(String newName){  // static method
     college = newName; 
  }    
   void display (){
System.out.println(name + ": " + fn );
} 
} 
 
public class Main{ 
 public static void main(String args[]){ 
 Student s1 = new Student(123, "Plamen"); 
 Student s2 = new Student(124, "Gergana"); 
 s1.display(); 
 s2.display(); 
 System.out.println(Student.college);//invoke static variable
 Student.changeCollegeName("TU Varna"); //invoke static method
 System.out.println(Student.college);
 } 
} 


