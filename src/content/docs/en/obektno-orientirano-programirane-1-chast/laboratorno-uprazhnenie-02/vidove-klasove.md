---
title: Types of Classes
sidebar:
  order: 2
---

# Types of Classes: `record` and `enum`

A class is Java's main mechanism for defining user-defined data types. The language also provides specialized constructs for defining types: `record` and `enum`.

Use an ordinary class when you need full control over an object's data and behavior, including its fields, constructors, and methods. A `record` is primarily a concise way to represent data. An `enum` defines a fixed set of permitted values.

## Records (`record`)

A `record` is a special kind of class for objects that primarily store data. It is useful when a type contains several values that are not expected to change after the object is created.

An ordinary class requires you to declare fields, a constructor, and access methods manually, and often methods such as `toString()`, `equals()`, and `hashCode()` as well. The compiler generates much of this code automatically for a `record`.

## Declaring a `record`

```java
record Student(String name, int facultyNumber) {

}
```

Here, `Student` is a record with two components:

- `name` of type `String`;
- `facultyNumber` of type `int`.

The components in the record header describe the data it stores. The compiler automatically creates a private final field and a public accessor method for each component.

## Creating a `record` Object

Create a record object with `new`, just as you would an ordinary class object.

```java
Student student = new Student("Ivan Petrov", 12345);
```

Creation calls the generated constructor. The arguments must match the number, order, and types of the components in the record declaration.

## Accessing Component Values

Record accessors do not use the usual `getName()` form. Each accessor has the same name as its component.

```java
System.out.println(student.name());
System.out.println(student.facultyNumber());
```

The `name()` method returns the `name` component. The `facultyNumber()` method returns the `facultyNumber` component.

## Generated Members

For a `record`, the compiler automatically creates:

- private final fields for the components;
- a constructor that accepts values for every component;
- accessor methods for the components;
- a `toString()` method;
- an `equals(Object other)` method;
- a `hashCode()` method.

As a result, `equals()` compares two objects of the same record type by value. An ordinary class needs to define this behavior manually if it is required.

```java
record Point(int x, int y) {

}

public class Application {

    public static void main(String[] args) {
        Point first = new Point(10, 20);
        Point second = new Point(10, 20);

        System.out.println(first.equals(second));
        System.out.println(first);
    }
}
```

`first.equals(second)` returns `true` because the two objects have the same record type and equal component values.

## An Ordinary Class and a `record` with the Same Data

We can define an ordinary class for coordinates:

```java
final class PointClass {
    private final int x;
    private final int y;

    PointClass(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
```

The same data can be represented by a record:

```java
record PointRecord(int x, int y) {
}
```

```java
PointClass firstClass = new PointClass(2, 3);
PointClass secondClass = new PointClass(2, 3);
PointRecord firstRecord = new PointRecord(2, 3);
PointRecord secondRecord = new PointRecord(2, 3);

System.out.println(firstClass.getX());                // 2
System.out.println(firstRecord.x());                  // 2
System.out.println(firstClass.equals(secondClass));   // false
System.out.println(firstRecord.equals(secondRecord)); // true
System.out.println(firstRecord == secondRecord);      // false
System.out.println(firstRecord);                      // PointRecord[x=2, y=3]
```

The ordinary class inherits identity-based equality from `Object`. To compare values, you must explicitly implement `equals()` and a matching `hashCode()`. A record generates these methods as well as `toString()`. The ordinary class shown here stores the same data but does not yet have all the behavior of the record.

A `record` is `final`: it cannot be extended and cannot extend an arbitrary class, but it can implement interfaces. It can have its own methods, such as `distanceFromOrigin()`. Records are suitable for calculation results, coordinates, or data descriptions. Use an ordinary class when you need mutable state or a different class hierarchy.

## Immutability of a `record`

The component fields of a `record` are `final`. Their values are assigned through the constructor and cannot later be reassigned. For a reference component, this guarantees a fixed reference, but not necessarily immutable contents in the referenced object.

```java
record Product(String name, double price) {

}

Product product = new Product("Keyboard", 59.90);

// product.price = 49.90; // not allowed
```

This makes a `record` useful for passing data between parts of a program when its state should not be changed accidentally.

## Shallow Immutability and Defensive Copies

A `record` prevents its component fields from being reassigned after creation. If a component refers to a mutable object, that object's contents can still change. This is called **shallow immutability**.

```java
record Grades(int[] values) {
}

int[] source = {5, 6};
Grades grades = new Grades(source);
source[0] = 2;
System.out.println(grades.values()[0]); // 2
grades.values()[1] = 3;                // changes the same array
```

To protect the array, copy both the input and the value returned by the accessor:

```java
record SafeGrades(int[] values) {
    SafeGrades {
        values = values.clone();
    }

    public int[] values() {
        return values.clone();
    }
}
```

This example assumes a non-null array reference. Copying an `int[]` is sufficient because its elements are primitive values. For an array of mutable objects, consider whether the elements themselves also need to be copied. The generated `equals()` method for a record with an array compares array references, not their elements; defensive copying does not change this rule.

The declared components and generated members are described in the [documentation for record classes](https://docs.oracle.com/en/java/javase/21/language/records.html).

## Compact Constructors in a `record`

A `record` can declare a compact constructor. Use it to run additional code when an object is created without writing the full parameter list.

```java
record Product(String name, double price) {

    Product {
        name = name.trim();
    }
}
```

A compact constructor does not assign values to the fields manually. After the additional code runs, the compiler automatically assigns the parameters to their corresponding components.

## When to Use a `record`

A `record` is suitable when:

- the type primarily stores data;
- all main values are known when the object is created;
- the values do not need to change afterward;
- you want to reduce boilerplate for constructors, accessors, and equality.

A `record` is not suitable when an object needs mutable state, a complex lifecycle, or many operations that change its internal data.

## Enumerated Types (`enum`)

An `enum` is a special kind of class that defines a fixed set of named constants. Use it when a value must be selected from a known set of permitted values.

Example:

```java
enum Status {

    NEW,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED

}
```

Here, `Status` is a type, and `NEW`, `IN_PROGRESS`, `COMPLETED`, and `CANCELLED` are its permitted values. You cannot create an arbitrary `Status` value that is not listed in the declaration.

## Using an `enum`

```java
Status status = Status.NEW;
```

Access enum values through the type name and constant name. This makes code easier to read and reduces errors that can occur with ordinary strings.

```java
if (status == Status.COMPLETED) {
    System.out.println("The task is completed.");
}
```

You can compare enum values with `==` because each constant is a unique, pre-created instance of its enum type.

## Using an `enum` Instead of Strings

Strings are often used when there is no `enum`:

```java
String status = "COMPLETED";
```

This approach allows errors because the compiler cannot check whether the text is a valid status.

```java
String status = "COMPLETEDD"; // valid String, but not a valid status
```

An `enum` lets the compiler detect this kind of error:

```java
Status status = Status.COMPLETED;
```

## Built-in `enum` Methods

| Method | Purpose |
| ------ | ------- |
| `values()` | Returns an array of all constants. |
| `valueOf(String)` | Returns a constant by its name. |
| `ordinal()` | Returns the constant's position. |
| `name()` | Returns the constant's name. |

Example:

```java
for (Status status : Status.values()) {
    System.out.println(status);
}
```

`values()` returns every constant in declaration order. `valueOf(String)` returns the constant with the exact specified name. If no such name exists, a runtime error occurs.

```java
Status status = Status.valueOf("NEW");
System.out.println(status.name());
System.out.println(status.ordinal());
```

`name()` returns the constant's name as text. `ordinal()` returns its position, starting at `0`. Avoid making program logic depend on `ordinal()`, because reordering the constants changes their numeric positions.

## Fields, Constructors, and Methods in an `enum`

An enum can contain fields, constructors, and methods because it is a special kind of class.

```java
enum UserRole {

    ADMIN("Administrator"),
    MODERATOR("Moderator"),
    USER("Standard user");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    String getDescription() {
        return description;
    }
}
```

Each constant in this example has its own `description`. The enum constructor is called automatically when the constants are created. External code does not call it with `new`.

```java
System.out.println(UserRole.ADMIN.getDescription());
```

## Enum Characteristics

An enum constructor cannot be public because its constants are defined in the enum declaration itself. You cannot create additional values outside that type.

Using an enum:

- restricts the allowed values;
- makes code easier to read;
- provides type checking at compile time;
- reduces the likelihood of errors.

An `enum` is suitable for an order status, day of the week, season, payment type, user role, or another limited set of values.

## Comparing `class`, `record`, and `enum`

| Construct | Main purpose | Creating values |
| --------- | ------------ | --------------- |
| `class` | Describes objects with data and behavior. | With `new`, using the declared constructors. |
| `record` | Concisely describes data with final fields; immutability is shallow. | With `new`, using a generated or explicitly declared constructor. |
| `enum` | Describes a fixed set of named values. | Only through the constants declared in the `enum`. |
