---
title: "Classes, Objects, Constructors, and Keywords"
sidebar:
  order: 1
---

# Classes, Objects, Constructors, and Keywords

Java uses classes and objects to describe and represent data. A class describes a data type, while an object is a specific value created from that description. A constructor participates in creating the object and sets its initial state. The `new`, `this`, and `static` keywords determine how objects are created, how the current object is accessed, and how class members are declared.

## Class

A class is a mechanism for creating a user-defined data type in Java. It describes the data and behavior that objects of that type will have.

A class is not the object itself. It describes the structure used to create concrete objects. If `Student` is a class, a particular student with a name and faculty number is an object of that class.

Declare a class with the `class` keyword, followed by its name and a body in curly braces:

```java
class Student {

}
```

A class declaration contains:

- the `class` keyword;
- the class name;
- the class body;
- class members, as needed.

Java class names start with a capital letter and use `PascalCase`, for example `Student`, `BankAccount`, `Car`, and `ProductOrder`.

## Class Members

A class body can contain fields, constructors, and methods.

Fields store an object's data. Methods describe operations that can use this data. Constructors set the initial state of a new object.

```java
class Student {

    String name;
    int facultyNumber;

    Student(String name, int facultyNumber) {
        this.name = name;
        this.facultyNumber = facultyNumber;
    }

    String getInformation() {
        return name + " - " + facultyNumber;
    }
}
```

In this example, `Student` contains:

- a `name` field;
- a `facultyNumber` field;
- a `Student(String name, int facultyNumber)` constructor;
- a `getInformation()` method that returns a text representation of the data.

## Fields

A field is a variable declared in a class body. It describes part of an object's state.

```java
class Product {

    String name;
    double price;
}
```

Each `Product` object has its own `name` and `price` values. Two objects created from the same class can therefore have different states.

## Fields, Local Variables, and Parameters

A field is declared in a class body and describes an object's state. Every object has its own copy of non-static fields.

A local variable is declared inside a method, constructor, or block. It exists only while that block is executing.

A parameter is declared in the parentheses of a method or constructor. It receives a value when the method or constructor is called.

```java
class Product {

    String name; // field

    Product(String name) { // parameter
        String trimmedName = name.trim(); // local variable
        this.name = trimmedName;
    }
}
```

Here, the class-level `name` is a field, the constructor's `name` is a parameter, and `trimmedName` is a local variable.

## Methods

A method is a named block of instructions. Methods describe the behavior of objects.

```java
class Product {

    String name;
    double price;

    String getDescription() {
        return name + " costs " + price;
    }
}
```

The `getDescription()` method uses the `name` and `price` field values and returns text. It is called on a particular `Product` object.

## Methods for Accessing Fields

You can access values through getter and setter methods. A getter returns a field's value. A setter assigns a new value to a field.

```java
class Student {

    String name;

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }
}
```

The `getName()` method returns the current value of `name`. The `setName(String name)` method assigns a new value to that field.

## A Class as a Data Type

Once a class has been declared, its name can be used as a type.

```java
Student student;
Product product;
```

These declarations create variables that can store references to objects of the corresponding type. The objects themselves have not been created yet. Use the `new` keyword to create an object.

## Object

An object is a specific instance of a class. A class describes structure and behavior; an object is an actual instance created from that description.

If `Student` is a class, a student named `Ivan Petrov` with faculty number `12345` is a specific object of that class.

An object has:

- state;
- behavior;
- identity.

The field values determine its state, and its methods determine its behavior. Identity means that each object is a separate entity in memory, even if its values match those of another object.

```java
class Student {

    String name;
    int facultyNumber;
}
```

For a `Student` object, the state consists of the values of `name` and `facultyNumber`.

## Creating an Object

Create an object with the `new` keyword. A class constructor is called during creation.

```java
Student student = new Student();
```

In this example:

- the `Student` on the left is the variable's type;
- `student` is the reference variable's name;
- `new Student()` creates an object;
- the result of `new Student()` is a reference to that object;
- the reference is stored in `student`.

## Object References

A reference is a value that lets the program access an object. A variable of a class type stores a reference, not the object itself. A reference is not a memory address that can be accessed for arithmetic operations.

```java
Student first = new Student();
Student second = first;
```

This creates one object. Both `first` and `second` contain a reference to it. If you change a field through one variable, the change is visible through the other variable as well.

```java
first.name = "Ivan";
System.out.println(second.name);
```

The result is `Ivan`, because both variables refer to the same object.

The assignment `second = first` copies the reference, while `new Student()` creates a separate object. For references, `==` checks whether they refer to the same object. `equals()` may compare content, depending on how the class implements it.

## The `null` Value

A reference variable may not refer to an object. This state is represented by `null`.

```java
Student student = null;
```

The variable `student` exists, but does not refer to a `Student` object.

`null` is not an object. You cannot access fields or methods through `null`.

```java
Student student = null;

// student.name = "Ivan"; // runtime error
```

Before using a reference that might be `null`, make sure it refers to an actual object.

## Constructor

A constructor is a special class member that runs when a new object is created. Its main purpose is to set the object's initial state.

A constructor is not an ordinary method. It has no return type, does not use `void`, and always has the same name as the class.

```java
class Student {

    String name;
    int facultyNumber;

    Student(String name, int facultyNumber) {
        this.name = name;
        this.facultyNumber = facultyNumber;
    }
}
```

Here, `Student(String name, int facultyNumber)` is a constructor. It accepts two values and assigns them to the new object's fields.

## No-Argument Constructor

A no-argument constructor accepts no arguments. You can define it explicitly and use it to set initial field values.

```java
class Student {

    String name;
    int facultyNumber;

    Student() {
        name = "Unknown";
        facultyNumber = 0;
    }
}
```

Calling this constructor creates an object with the values assigned in its body:

```java
Student student = new Student();
```

## Default Constructor

If a class declares no constructors, the Java compiler automatically provides a default constructor. It has no parameters and contains no additional logic.

```java
class Student {

    String name;
    int facultyNumber;
}
```

You can use the class like this:

```java
Student student = new Student();
```

This works because the compiler supplies a default constructor. It has the form of a no-argument constructor, but does not appear in the source code.

If you declare at least one constructor yourself, the compiler does not automatically add a default constructor.

```java
class Student {

    String name;

    Student(String name) {
        this.name = name;
    }
}
```

The following statement will not compile:

```java
Student student = new Student();
```

The class has a constructor with a parameter, but no no-argument constructor.

## Constructor with Parameters for All Fields

A constructor with parameters for all fields accepts values when an object is created. It uses these values to initialize all fields that describe the object's initial state.

```java
class Student {

    String name;
    int facultyNumber;

    Student(String name, int facultyNumber) {
        this.name = name;
        this.facultyNumber = facultyNumber;
    }
}
```

Create an object:

```java
Student student = new Student("Ivan Petrov", 12345);
```

The value `"Ivan Petrov"` is passed to `name`, and `12345` is passed to `facultyNumber`. Each field receives its value when the object is created.

## Constructors and Initial State

A constructor sets initial field values. The values supplied to it should make sense for the object.

```java
class Product {

    String name;
    double price;

    Product(String name, double price) {
        this.name = name;
        this.price = price;
    }
}
```

The parameter values `name` and `price` are assigned to the new object's fields. The object therefore has an initial state as soon as the constructor is called.

## The `new` Keyword

Use the `new` keyword to create an object in memory. It connects the class and its constructor: the class determines the object's type, and the constructor determines how it is initialized.

Using `new` performs several actions:

- memory is allocated for a new object;
- an appropriate constructor is called;
- fields receive initial values;
- a reference to the object is returned.

General syntax:

```java
ClassName variableName = new ClassName(arguments);
```

Example:

```java
Student student = new Student("Ivan Petrov", 12345);
```

`new Student("Ivan Petrov", 12345)` creates a new `Student` object and calls the constructor with two parameters. The resulting reference is stored in `student`.

Every `new` expression creates a new object:

```java
Student first = new Student("Ivan Petrov", 12345);
Student second = new Student("Ivan Petrov", 12345);
```

This creates two separate objects. Their field values may be identical, but they are distinct objects in memory.

## Passing an Object Reference to a Method

Java always passes arguments **by value**. For an object, the value being copied is the reference. A method can change the object through this copy, but assigning a new reference to the parameter does not change the caller's variable.

```java
class StudentOperations {
    static void rename(Student student) {
        student.name = "Anna";
    }

    static void replace(Student student) {
        student = new Student("Georgi", 54321);
    }
}
```

Calling `StudentOperations.rename(first)` changes the object's name. Calling `StudentOperations.replace(first)` leaves the caller's `first` variable referring to the same object.

## The `this` Keyword

The `this` keyword is a reference to the current object. The current object is the object on which a non-static method or constructor is running.

Use `this` to refer explicitly to a member of the current object. This is especially useful when a parameter and a field have the same name.

```java
class Student {

    String name;
    int facultyNumber;

    Student(String name, int facultyNumber) {
        this.name = name;
        this.facultyNumber = facultyNumber;
    }
}
```

In this example:

- `this.name` refers to the current object's `name` field;
- `name` refers to the constructor parameter;
- `this.facultyNumber` refers to the current object's `facultyNumber` field;
- `facultyNumber` refers to the constructor parameter.

Without `this`, `name = name;` would assign the parameter to itself, leaving the object's field unchanged.

## Calling a Method with `this`

You can also use `this` when calling a non-static method on the current object. If you call a method without explicitly naming an object, Java assumes that the current object is the target.

```java
class Student {

    String name;

    Student(String name) {
        this.name = name;
    }

    String getName() {
        return this.name;
    }

    String getInformation() {
        return this.getName();
    }
}
```

In `getInformation()`, `this.getName()` calls `getName()` on the same object. In this example, `getName()` and `this.getName()` produce the same result. Writing `this` makes it explicit that the method belongs to the current object.

## Passing `this` as a Method Argument

You can pass `this` to a method that accepts an object of a compatible type. A declaration describes the **parameter**; when the method is called, `this` is the **argument**—a reference to the current object.

```java
class Student {
    String name;

    Student(String name) {
        this.name = name;
    }

    void printCard() {
        CardPrinter.print(this);
    }
}

class CardPrinter {
    static void print(Student student) {
        System.out.println("Student: " + student.name);
    }
}
```

Calling `new Student("Ivan").printCard()` passes the same student object to `print`; it does not create a copy. The receiving method can change the object's accessible state. You cannot use `this` in a static method because it has no current object.

## Calling Another Constructor with `this()`

You can also use `this(...)` to call another constructor in the same class. This call must be the first statement in the constructor.

This is useful when a no-argument constructor should use the same initial-state logic as the constructor with parameters for all fields.

```java
class Student {

    String name;
    int facultyNumber;

    Student() {
        this("Unknown", 0);
    }

    Student(String name, int facultyNumber) {
        this.name = name;
        this.facultyNumber = facultyNumber;
    }
}
```

The no-argument constructor delegates to the constructor with parameters for all fields. This keeps the initialization logic in one constructor.

You can use `this` only in a non-static context. It is not available in static methods or static blocks because there is no current object there.

```java
class Example {

    static void print() {
        // this cannot be used here
    }
}
```

## The `static` Keyword

Use the `static` keyword to declare members that belong to a class rather than to individual objects.

The following can be static:

- fields;
- methods;
- initialization blocks;
- nested classes.

The difference between static and non-static members is ownership. A non-static field belongs to a particular object. A static field belongs to the class and has one shared value for that class.

## Common Uses of `static`

| Member | Purpose |
| ------ | ------- |
| static field | Stores a value shared by the entire class. |
| static method | Belongs to the class and can be called without creating an object. |
| static block | Runs when the class is initialized. |

## Static Fields

A static field belongs to the class. One value is shared by all objects.

```java
class Student {

    static String university = "Technical University of Varna";

    String name;

    Student(String name) {
        this.name = name;
    }
}
```

The `university` field belongs to `Student`, not to a particular student. Access it using the class name:

```java
System.out.println(Student.university);
```

Accessing a static field through an object is syntactically possible, but does not clearly show that the field belongs to the class:

```java
Student student = new Student("Ivan");
System.out.println(student.university); // not recommended
```

Use the class name because `university` belongs to `Student`.

The `name` field is non-static, so each `Student` object has its own value:

```java
Student first = new Student("Ivan");
Student second = new Student("Maria");

System.out.println(first.name);
System.out.println(second.name);
System.out.println(Student.university);
```

## Constants with `static final`

Use `static final` for a value that belongs to the class and must not change.

```java
class MathConstants {

    static final double PI = 3.14159;
}
```

`static` means the value belongs to the class rather than to an individual object. `final` means it cannot be assigned a new value after initialization.

Constant names are conventionally written in uppercase, with words separated by underscores.

| Usage | Purpose |
| ----- | ------- |
| `final` variable | The value cannot be changed after initialization. |
| `static final` field | The value belongs to the class and is used as a constant. |

## Static Methods

A static method is called through the class name and does not require an object.

```java
class Calculator {

    static int square(int number) {
        return number * number;
    }
}
```

Call it like this:

```java
int result = Calculator.square(5);
```

A static method can directly access only static members of the same class. It has no current object, so it cannot use `this`.

A static method also cannot use `super`, which requires a current object of a subclass. There is no specific object in a static context on which the method could run.

```java
class Example {

    String name;
    static int counter;

    static void printCounter() {
        System.out.println(counter);
        // System.out.println(this.name); // does not compile
    }
}
```

If a static method needs to use a non-static field, it must receive an object as a parameter or create one.

## The `main` Method

The `main` method is usually static because the Java Virtual Machine must be able to call it without first creating an object of the class.

```java
public class Application {

    public static void main(String[] args) {
        System.out.println("Program started.");
    }
}
```

## Static Initialization Blocks

A static block is a block of instructions that runs when a class is initialized, for example before its first object is created or its first static method is called. Class loading and class initialization are separate stages.

```java
class Configuration {

    static String applicationName;

    static {
        applicationName = "Student System";
    }
}
```

Static blocks are useful for initializing static fields when initialization requires more than one statement.

## Complete Example

The following example combines a class, objects, a constructor, `new`, `this`, and `static`.

```java
class Student {

    static String university = "Technical University of Varna";

    String name;
    int facultyNumber;

    Student(String name, int facultyNumber) {
        this.name = name;
        this.facultyNumber = facultyNumber;
    }

    String getInformation() {
        return name + " - " + facultyNumber + " - " + university;
    }
}

public class Application {

    public static void main(String[] args) {
        Student first = new Student("Ivan Petrov", 12345);
        Student second = new Student("Maria Ivanova", 67890);

        System.out.println(first.getInformation());
        System.out.println(second.getInformation());
    }
}
```

Here, `Student` is a class. The `first` and `second` variables refer to two different objects. The `new` expressions create those objects and call the parameterized constructor. In the constructor, `this.name` and `this.facultyNumber` refer to the current object's fields. The static `university` field belongs to `Student`, so all objects of this class share its value.
