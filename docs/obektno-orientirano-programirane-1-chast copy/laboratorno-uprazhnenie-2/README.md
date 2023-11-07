---
layout: default
title: Laboratory exercise 2
parent: Object-oriented Programming - 1 part
has_children: true
nav_order: 2
permalink: /docs/object-oriented-programming-1-part/laboratory-exercise-2
---

# Laboratory exercise 2

**Inheritance**

Inheritance is one of the main principles of OOP. It's a mechanism by which given class could inherit the state (attributes) and the behaviour (methods) of another - parent - class.

Inheritance makes possible the re-using in the child class of already defined properties and methods in the parent class (if the access modifier is different than "private").

In Java, given class can have **only one** parent class.


The parent class have no access nor information about the properties and methods of its child class regardless of their access modifiers.

Super/parent class – its attributes and methods could be re-used.

Sub/child class – adopts all attributes from the parent class and could add its own properties. Same is valid for the behaviour (methods), the child class could add different methods the the already adopted from the parent class.

```
class Animal { 
    void eat(){
        System.out.println("eating...");
    } 
}
```

```
class Dog extends Animal jav{ 
    void bark(){
    System.out.println("barking...");
    } 
}
```

```
class BabyDog extends Dog { 
    void weep(){
        System.out.println("weeping...");
        } 
}
```

```
class Main { 
    public static void main(String args[]){ 
        BabyDog d=new BabyDog(); 
        d.weep(); 
        d.bark(); 
        d.eat(); 
    }
}
```

**Keywords "this" and "super"**

The keywords "this"and "super"could not be used in a static context. 

*Keyword this*

The keyword points to the current object in a method or in a constructor. Usually "this" is used to distinguish the attributes (of a class) and parameters (of a method) with the same name.

Keyword "this" could be also used for:

· calling the current class constructor;

· calling the current class method;

· returning object from the current class;

· passing as a method argument;

· passing as a constructor argument.

```
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

*Keyword "super"*

Запазената дума супер сочи към супер класа (родителския клас) на обекта. Използва се, за да се извикват атрибути,методи и конструктор наследени от родителския клас.
The keyword "super" points to the parent (super) class of the current object. It's used for calling attributes, methods and constructor, inherited from its parent.

```
class Animal { // Superclass (parent)
  public void animalSound() {
    System.out.println("The animal makes a sound");
  }
}

class Dog extends Animal { // Subclass (child)
  public void animalSound() {
    super.animalSound(); // Call the superclass method
    System.out.println("The dog says: bow wow");
  }
}

public class Main {
  public static void main(String args[]) {
    Animal myDog = new Dog(); // Create a Dog object
    myDog.animalSound(); // Call the method on the Dog object
  }
}
```


**Encapsulation**

Encapsulation is packing data in a single unit. It represents mechanism for unifying code and the data manupulated. Encapsulation prevents data access except it follows predefined rules.
Technically speaking, encapsulation states that attributes of a given class are hidden for every other class and could be accessed only through methods, declared in its owner class. This means that in single class, all attributes are declared as "private" and the methods (accessors in particular) are declared as "public".

Encapsulation advantages:

- Hiding data: the access to class attributes is limited and implementation details are unknown.
- Flexibility: class attributes could be visible only for reading or only for modifying depending on requirements. If the requirement expects only reading possibility, setters should be skipped and not declared, and viceversa - if there is need only for modifying/storing values, there will be no getters present.
- Reiterated usage: encapsulation makes easier re-using of existing and introducing of new code.
- Easy testing: such code is easier to be tested via unit testing.


**Access modifiers**

Access modifiers are keywords and they are define during declaration and before:

· classes;

· properties;

· methods.

The purpose of access modifiers is to define what visibility has the element, as well as the access. Modifiers are:

· private – visibility is within current class; non-visible outside the class;

· protected – visibility is within current class, its child classes and inside its package;

· public – visible from everywhere.

If there is no modifier specified, the visibility - by default - is within the package.
