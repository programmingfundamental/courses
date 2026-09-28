---
title: Inheritance and the `super` Keyword
sidebar:
  order: 1
---

# Inheritance and the `super` Keyword

Inheritance lets one Java class be defined as a specialized version of another. A subclass can use the inheritable fields and methods of its parent class and add its own fields and methods.

Inheritance describes an “is-a” relationship. If `Student` extends `Person`, a student is a person, with additional characteristics that are not shared by all people.

## Parent Class and Subclass

The class being inherited from is called the parent class, base class, or superclass. The class that inherits is called the subclass, derived class, or child class.

```java
class Person {

    private String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

`Person` describes a general characteristic—a name—that can be used by more specialized classes.

## The `extends` Keyword

Declare inheritance with the `extends` keyword.

```java
class Student extends Person {

    private int facultyNumber;

    public Student(String name, int facultyNumber) {
        super(name);
        this.facultyNumber = facultyNumber;
    }

    public int getFacultyNumber() {
        return facultyNumber;
    }
}
```

The declaration `class Student extends Person` means that `Student` inherits from `Person`. A `Student` object has behavior defined in `Person` as well as additional behavior defined in `Student`.

```java
Student student = new Student("Ivan Petrov", 12345);

System.out.println(student.getName());
System.out.println(student.getFacultyNumber());
```

`getName()` is defined in `Person`, but can be called through a `Student` object because `Student` extends `Person`.

## What Is Inherited?

A subclass inherits the inheritable members of its parent class. Which fields and methods are accessible depends on their access modifiers.

**Members declared `private` are not inherited.** Public and protected members can be inherited, as can package-private members when the subclass is in the same package. See [Encapsulation and Access Modifiers](/courses/en/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-03/kapsulirane/) for more about visibility.

This does not mean the parent's private fields disappear: a subclass object contains the state defined by its parent class. The parent's constructor and methods work with those fields. For example, `student.getName()` uses the `name` field declared in `Person` without `Student` inheriting that private field. A private method is also not inherited and cannot be overridden. A same-named method in a subclass is a separate method.

This rule is described in the [Java Language Specification, §8.2](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.2).

Constructors are not inherited. A subclass must have its own constructor, which can call a parent constructor through `super(...)`.

```java
class Employee extends Person {

    private double salary;

    public Employee(String name, double salary) {
        super(name);
        this.salary = salary;
    }
}
```

`Employee` does not inherit the `Person(String name)` constructor. Its constructor calls the parent constructor with `super(name)`.

## Inheritance and Encapsulation

Inheritance does not remove encapsulation rules. A field declared `private` in the parent class cannot be accessed directly from a subclass.

```java
class Person {

    private String name;

    public String getName() {
        return name;
    }
}

class Student extends Person {

    public void printName() {
        System.out.println(getName());
    }
}
```

`Student` cannot access `name` directly because it is `private`. It accesses the value through the public `getName()` method.

## Adding New Members

A subclass can add fields and methods that do not exist in the parent class.

```java
class Teacher extends Person {

    private String subject;

    public Teacher(String name, String subject) {
        super(name);
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }
}
```

`Teacher` inherits `getName()` from `Person` and adds its own `subject` field and `getSubject()` method.

## Single Inheritance

In Java, a class can directly extend only one parent class.

```java
class Student extends Person {

}
```

A class cannot have two direct parent classes:

```java
// Not allowed:
// class Student extends Person, User {
//
// }
```

This rule is called single inheritance of classes.

## Multilevel Inheritance

You can create a hierarchy with several levels.

```java
class Person {

}

class Student extends Person {

}

class GraduateStudent extends Student {

}
```

`GraduateStudent` directly extends `Student` and indirectly extends `Person`.

## The `Object` Class

Every Java class directly or indirectly extends `Object`. If a class does not specify a parent with `extends`, its default parent class is `Object`.

```java
class Student {

}
```

The class above is equivalent in principle to:

```java
class Student extends Object {

}
```

This means that every Java object can also be treated as an object of type `Object`.

```java
Student student = new Student();
Object value = student;
```

The `Object` class defines common methods available to all objects. The `toString()`, `equals()`, and `hashCode()` methods are covered in more detail when we study polymorphism.

## `final` Classes and Methods

A class declared `final` cannot be extended.

```java
final class Configuration {

}

// class AppConfiguration extends Configuration { } // not allowed
```

A `final` method is inherited, but a subclass cannot replace its implementation.

```java
class Parent {

    public final void printType() {
        System.out.println("Parent");
    }
}

class Child extends Parent {

    // public void printType() { } // not allowed
}
```

Use `final` when inheritance or changing a particular behavior must be prohibited.

| Usage | Purpose |
| ----- | ------- |
| `final` field | The field can be assigned only once. |
| `final` method | A subclass cannot replace the implementation. |
| `final` class | The class cannot be extended. |

## When to Use Inheritance

Use inheritance when there is a clear “is-a” relationship between two classes. For example, a `Dog` is an `Animal`, a `Student` is a `Person`, and a `SavingsAccount` is a `BankAccount`.

If the relationship is “has-a”, inheritance is not appropriate. A `Car` has an `Engine`, but a car is not an engine.

## The `super` Keyword

Use `super` in a subclass to access its immediate parent class. It can call a constructor, method, or field from the parent.

`super` is meaningful only with inheritance. A class that does not extend another user-defined class does not use `super` to access its own members.

## Calling a Constructor with `super(...)`

The subclass does not inherit its parent's constructors. If the parent requires values for its fields, the subclass constructor must call an appropriate parent constructor.

```java
class Person {

    private String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Student extends Person {

    private int facultyNumber;

    public Student(String name, int facultyNumber) {
        super(name);
        this.facultyNumber = facultyNumber;
    }
}
```

`super(name)` calls `Person(String name)`. The parent class initializes `name`, which belongs to the parent part of the object.

A call to a parent constructor using `super(...)` must be the first statement in the subclass constructor.

```java
public Student(String name, int facultyNumber) {
    super(name);
    this.facultyNumber = facultyNumber;
}
```

After the parent constructor runs, the subclass constructor can initialize its own fields.

## Initialization Order in Inheritance

When a subclass object is created, the parent-class part is initialized first. Then the subclass constructor runs.

```java
class Parent {

    Parent() {
        System.out.println("Parent constructor");
    }
}

class Child extends Parent {

    Child() {
        System.out.println("Child constructor");
    }
}
```

Creating a `Child` object prints:

```text
Parent constructor
Child constructor
```

This order ensures that the inherited part of the object is initialized before the subclass constructor starts using it.

## Automatic `super()` Calls

If a subclass constructor does not explicitly call a parent constructor, the compiler tries to insert `super()` automatically.

```java
class Person {

    public Person() {
        System.out.println("Person created");
    }
}

class Student extends Person {

    public Student() {
        System.out.println("Student created");
    }
}
```

The `Student` constructor is treated as if it were written as follows:

```java
public Student() {
    super();
    System.out.println("Student created");
}
```

If the parent class has no no-argument constructor and the subclass does not call another parent constructor with `super(...)`, the program will not compile.

```java
class Person {

    public Person(String name) {

    }
}

class Student extends Person {

    public Student() {
        // Error: missing call to super(name)
    }
}
```

## Calling a Parent Method

Use `super` to call an accessible method from the parent class. This is useful when a subclass method needs to reuse an operation already defined in the parent.

```java
class Person {

    private String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Student extends Person {

    private int facultyNumber;

    public Student(String name, int facultyNumber) {
        super(name);
        this.facultyNumber = facultyNumber;
    }

    public String getStudentInformation() {
        return super.getName() + ", faculty number: " + facultyNumber;
    }
}
```

`super.getName()` calls `Person`'s `getName()` method. Its result is combined with information from `Student`.

## Accessing a Parent Field

If the parent class and subclass have fields with the same name, use `super` to access the parent's field.

```java
class Person {

    protected String name = "Unknown";
}

class Student extends Person {

    private String name = "Ivan";

    public void printNames() {
        System.out.println(super.name);
        System.out.println(this.name);
    }
}
```

`super.name` refers to the parent class's `name` field. `this.name` refers to the current class's `name` field.

Declaring fields with the same name in a parent and subclass can cause confusion and should be avoided unless there is a clear reason. More often, `super` is used for constructors and to extend parent methods.

## `super` and `this`

`this` refers to the current object. `super` refers to the parent-class part of the current object.

```java
class Student extends Person {

    private int facultyNumber;

    public Student(String name, int facultyNumber) {
        super(name);
        this.facultyNumber = facultyNumber;
    }
}
```

Here, `super(name)` calls a parent-class constructor. `this.facultyNumber` accesses a field in the current class.

You cannot use `super` in a static context because a static method does not run on a particular object.

```java
class Student extends Person {

    public static void print() {
        // super.getName(); // does not compile
    }
}
```
