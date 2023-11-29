---
layout: default
title: Laboratory exercise 6
parent: Object-oriented Programming - 1 part
has_children: true
nav_order: 6
permalink: /docs/object-oriented-programming-1-part/laboratory-exercise-6
---

# Laboratory exercise 6


### Exceptions and errors

All exceptions in Java are represented as classes, that inherit children of class Object. Class Throwable is a parent class of all exceptions. _Exception is an event that occures during program execution and appears as obstacle of its normal finishing._

Class **Error** extends Throwable. The errors of the class are critical and when such error occures, the program is forcedly finished. Such errors are thrown by _runtime system_ and usually are result of incorrect condition during program execution.

![](<../../../assets/image (148).png>)


Exceptions and errors fall into two main types: *checked* that appear during compilation and *unchecked* which appear during execution.


Checked exceptions must be catched, which is guaranteed by the compiler. These exceptions extend **java.lang.Exception**, but not **java.lang.RuntimeException**. Such exceptions are expected and well written program should recover. For example, if the program is trying to read from file that does not exist, there will be IOException thrown; this error could be processed and the user will be notified that there is no such file.
 
Unchecked exceptions should not be catched. This type of exceptions extend **java.lang.RuntimeException** and their presence usually is due to some program bugs or incorrect library usage. The error NullPointerException is example of unchecked exception and could appear when trying to call object that has no value. Catching and processing of these errors is not necessary, but it's possible.


**Throwable - methods**

Some of exception methods (class **Throwable**) are:

\-     **getMessage()** - returns textual description of the exception.

\-     **getCause()** - returns inner exception.

\-     **getStackTrace()** - returns complete stack of the exception.

\-     **printStackTrace()** - prints exception class, message and the error stack of the whole chain of method calls.

#### Try-catch clause

When given exception should be catched, the code fragment generating the exception must be put in a block surrounded with brackets after keyword _**try**_. It is followed by one or more  _**catch**_ blocks and the code in this block is executed when there is such exception catched.

_**try**_{

_some code_

}

catch _(_SomeException object_){_

some code to catch

_}_

```
public class Example {
    public static void main(String[] args) {
        int[] array = new int[2];
        try {
            System.out.println(array[5]); 
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("This will printing ever.");
        }
    }
}
```

_Output:_

_java.lang.ArrayIndexOutOfBoundsException: 5_

&#x20;       _at bg.tu\_varna.sit.examples.contr.Example.main(Example.java:7)_


The order of catch-clauses is very important; it starts from child to the parent because of the wrapper nature of the exceptions. As a rule, most common exception should be in the last clause. If the order is incorrect, there will be compilation error.

In the example above there is block with keyword *finally*, which guarantees execution of the code inside regardless of catching or no the exception.


### **Exception throwing**

When in given method occurs exception, the method could **process** the exception or "**throw**" it to the calling method. Keyword **throws** is used:

```
public class Example {
public static void main(String[] args) {
    try {
        division(100, 0);
    } catch (ArithmeticException e) {
        System.out.println("You can’t divide by zero!");
    }
}
public static void division(int a, int b) throws ArithmeticException {
    int c = a / b;
    System.out.println(c);
}
}
```

When declaring method it's possible it could throw more than one exception. They are written after the keyword *throws* separated by comma.

In some situations developers should decide themselves when to throw an exception. The example from above will be modified as follows:

```
public class Example {
```

```
    public static void main(String[] args) {
        try {
            division(100, 0);
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }
    }

    public static void division(int a, int b) throws ArithmeticException {
        if (b == 0) {
            throw new ArithmeticException();
        } else {
            int c = a / b;
            System.out.println(c);
        }
    }
} 
```

#### Custom exceptions


There could be custom exceptions created as childern of Exception. Usually such exceptions are defined when

\-      there is more specific processing needed, or

\-      there are exceptions, specific for the business logic and the work process, or the problem solved.

In the example there is custom exception defined and the exception is thrown when user's age is less than 18 years:

```
class InvalidAgeException extends Exception {
    public InvalidAgeException(String str) {
        super(str);
    }
}

public class Example {
    static void validate(int age) throws InvalidAgeException {
        if (age < 18) {
            throw new InvalidAgeException("age is not valid to vote");
        } else {
            System.out.println("welcome to vote");
        }
    }

    public static void main(String args[]) {
        try {
            validate(13);
        } catch (InvalidAgeException ex) {
            System.out.println("Caught the exception");
            System.out.println("Exception occured: " + ex);
        }
        System.out.println("rest of the code...");
    }
}
```
