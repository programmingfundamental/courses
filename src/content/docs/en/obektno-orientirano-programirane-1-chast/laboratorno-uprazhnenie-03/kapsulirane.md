---
title: Encapsulation and Access Modifiers
sidebar:
  order: 3
---

# Encapsulation and Access Modifiers

Encapsulation is an object-oriented programming principle that places an object's data and the operations on that data in one class. External code does not work directly with the object's internal state; it uses the methods provided by the class.

An object's internal state consists of the values of its fields. If these fields are directly accessible from outside, any part of the program can change them without validation. This can leave the object in an invalid state.

```java
class Student {

    String name;
    int age;
}
```

With this declaration, the fields can be changed directly:

```java
Student student = new Student();
student.age = -5;
```

An age of `-5` is invalid, but direct access allows it. Encapsulation addresses this problem by restricting access to fields.

## Private Fields

A field declared `private` is accessible only within the class where it is defined.

```java
class Student {

    private String name;
    private int age;
}
```

After this change, `name` and `age` cannot be accessed directly outside `Student`.

```java
Student student = new Student();

// student.age = -5; // does not compile
```

The object still has the `name` and `age` fields, but access to them must be provided through methods.

## Getter Methods

A getter returns the value of a field.

```java
class Student {

    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
```

The public `getName()` and `getAge()` methods let callers read the values without exposing the fields directly.

## Setter Methods

A setter assigns a new value to a field.

```java
class Student {

    private String name;
    private int age;

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        if (age >= 0) {
            this.age = age;
        }
    }
}
```

The `setAge(int age)` method checks a value before assigning it. If the supplied value is negative, the field is not changed. The class therefore controls its own state.

## Encapsulation Through a Constructor

You can also set values through a constructor. This is appropriate when an object must be created with a valid initial state.

```java
class Student {

    private String name;
    private int age;

    public Student(String name, int age) {
        this.name = name;
        setAge(age);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age >= 0) {
            this.age = age;
        }
    }
}
```

The constructor calls `setAge(age)` because the age validation is already implemented there. This keeps the validity rule in one place.

## Read-Only Objects

Sometimes values should be set when an object is created and never changed afterward. In that case, define private fields and getters but no setters.

```java
final class StudentCard {

    private final String number;
    private final String ownerName;

    public StudentCard(String number, String ownerName) {
        this.number = number;
        this.ownerName = ownerName;
    }

    public String getNumber() {
        return number;
    }

    public String getOwnerName() {
        return ownerName;
    }
}
```

The fields are `private final`. They are assigned in the constructor and cannot be assigned again.

Here, `String` is immutable, and declaring the class `final` prevents subclasses from changing its behavior. However, the absence of setters alone does not guarantee an immutable object: another method could change a field, or a getter could return a mutable array or another internal object.

## Comparing a Read-Only Class with a `record`

The same data can be described more concisely:

```java
record StudentCard(String number, String ownerName) {
}
```

These are alternative definitions and should be tried separately.

| Feature | Read-only ordinary class | `record` |
| ------- | ------------------------ | ---------- |
| Fields and constructor | Declared explicitly | Generated from components |
| Reading values | For example, `getNumber()` | `number()` |
| Changing values | Prevented by the class design | Component fields are `final` |
| Equality | Implement `equals()` and `hashCode()` if needed | Generated from components |
| Inheritance | Can be prohibited with `final` | The class is always `final` |

With either approach, a `final` reference does not make the referenced object immutable. For arrays, use defensive copies on input and output. See [Types of Classes](/courses/en/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-02/vidove-klasove/).

## `final` Fields

A field declared `final` can be assigned only once. It cannot be assigned a new value afterward.

```java
class Student {

    private final String facultyNumber;

    public Student(String facultyNumber) {
        this.facultyNumber = facultyNumber;
    }

    public String getFacultyNumber() {
        return facultyNumber;
    }
}
```

A `final` field must be initialized when it is declared or in a constructor. This lets you create objects whose state cannot be changed after creation in certain ways.

## A Class's Public Interface

A class's public interface consists of its public constructors and methods that external code can use. Private fields and private helper methods are part of its internal implementation.

Here, **interface** means how to use the class. For example, a bank account client calls `deposit(...)` and `getBalance()` without knowing how the balance is stored.

This meaning differs from the **`interface`** keyword, which declares a separate Java type and behavior contract.

```java
class BankAccount {

    private double balance;

    public BankAccount(double initialBalance) {
        if (initialBalance >= 0) {
            balance = initialBalance;
        }
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
}
```

`BankAccount` does not allow direct changes to `balance`. Changes go through `deposit(double amount)`, which accepts only positive values.

## Access Modifiers

Access modifiers determine where a class, field, constructor, or method can be used. They control visibility in Java and are a key tool for implementing encapsulation.

Java has four access levels for class members:

- `private`;
- package-private access, when no modifier is specified;
- `protected`;
- `public`.

## `private`

The `private` modifier restricts access to the class in which the member is declared.

```java
class Student {

    private int age;

    public void setAge(int age) {
        if (age >= 0) {
            this.age = age;
        }
    }
}
```

The `age` field can be used directly only inside `Student`. External code must use a public method, if the class provides one.

Use `private` for fields that represent an object's internal state and for helper methods that should not be part of the public interface.

## Package-Private Access

When no modifier is specified, the member has package-private access. It is accessible only to classes in the same package.

```java
class Student {

    String name;
}
```

The `name` field has no explicit modifier. Classes in the same package can access it, but classes in other packages cannot.

A package is a group of related classes. Introductory examples often omit a package declaration, but the same package-private rule still applies.

## `protected`

The `protected` modifier allows access from classes in the same package and from subclasses.

```java
class Person {

    protected String name;
}

class Student extends Person {

    public String getName() {
        return name;
    }
}
```

`Student` extends `Person` and can use the `name` field because it is `protected`.

For a subclass in another package, access to an instance `protected` member is restricted: it can use the member through `this`, `super`, or a reference whose type is that subclass (or one of its subclasses), but not through an arbitrary parent-type reference. Within the same package, package access also applies.

Use `protected` carefully. It exposes a parent class member to subclasses and increases the dependency between the parent class and its subclasses.

## `public`

The `public` modifier makes a member accessible from anywhere in the program, provided the class itself is accessible.

```java
public class Student {

    public String getInformation() {
        return "Student";
    }
}
```

Public methods form the way external code interacts with an object. Encapsulated fields are usually not declared `public`, because that would allow uncontrolled direct changes.

## Access Modifiers on Classes

A top-level class can be `public` or have no explicit modifier.

```java
public class Student {

}
```

A public class can be accessed from other packages. A class without a modifier is accessible only within its own package.

## Visibility Table

| Modifier | Same class | Same package | Subclass in another package | Other package |
| -------- | ---------- | ------------ | --------------------------- | ------------- |
| `private` | Yes | No | No | No |
| no modifier | Yes | Yes | No | No |
| `protected` | Yes | Yes | Yes | No |
| `public` | Yes | Yes | Yes | Yes |

Choose the most restrictive access that still lets the class do its job. Use `private` for fields unless there is a reason to expose them directly.

This table describes top-level classes. Nested classes within the same enclosing class can access its private members. This is an access privilege, not inheritance of `private` members.
