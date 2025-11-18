---
layout: default
title: Laboratory exercise 6
parent: Object-oriented Programming - 1 part
has_children: true
nav_order: 6
permalink: /docs/object-oriented-programming-1-part/laboratory-exercise-6
---

# Laboratory exercise 6


# Exception handling

### Exceptions and errors

In Java, all exceptions are represented in classes that extends the parent class of all classes – Object. In turn, the Throwable class is the parent of all exceptions. _Exception is an event that occurs during program execution and prevents its normal completion._

The **Error** class is a child-class of the Throwable class. Errors of this class are critical for program execution and when they occur, the program cannot be restored and must be forcibly terminated. Errors of this class are “thrown” by the _runtime system_ and are usually a consequence of some incorrect condition that occurred during program execution.


<p align="center"> <img width="729" height="491" alt="0_XrfSEZ-iZxaT3-qB" src="https://github.com/user-attachments/assets/edb74579-ec7f-4ca6-9c58-e1b3fa79546c" /> </p>

Exceptions and errors are divided into two main types: checked, occurring at compile time, and unchecked, occurring at runtime..

**Checked** are exceptions that **must** **have** to follow the "catch or throw" principle and this is guaranteed by the compiler. These exceptions inherit the **java.lang.Exception** class, but do not inherit **java.lang.RuntimeException**.

Checked are exceptions that a well-written program should expect and should be able to recover from. If we try to read from a file that does not exist, the program will throw an IOException, this error can be handled and the user can be informed that there is no such file

**Unchecked** exceptions are exceptions that are not obliged to follow the "catch or throw" principle. These exceptions inherit the **RuntimeException** class. The occurrence of such an exception most often means a bug in the program or incorrect use of a library.

The **NullPointerException** error is a typical representative of unchecked exceptions. It can occur inadvertently when we address an object that has no value. Catching and handling such problems is not mandatory, but it is possible.

**Throwable – methods**

Here are the most basic methods of exceptions (class **Throwable**):

\- The **getMessage()** method returns a text description of the exception. Each exception decides for itself what message to return. Most often, the thrower of the exception is allowed to set this description.

\- The **getCause()** method returns the inner / wrapped exception.

\- The **getStackTrace()** method returns the entire stack that is stored in the exception. It has been available since Java version 1.4.

\- The **initCause()** method was used before Java 1.4 to set the inner / wrapped exception. Now constructors are used, but this method still works.

\- The **printStackTrace()** method prints the exception class, the message and the error stack and the stack of the entire chain of called methods, along with the entire chain of nested exceptions.

#### Catching an exception

When we need to catch an exception, the code that throws that exception must be placed in a block enclosed in curly braces after the word _**try**_.

We then use one or more _**catch**_ blocks, where the code in that block is executed when an exception of that class is caught.

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

       _at Example.main(Example.java:7)_



The code that expects an exception to occur is placed in the _**try**_ block.

It is important to mention that the order of exception handling is extremely important, due to their wrapping nature. This means that exceptions should be caught from a more specific class to a more general class (from descendant to parent). If an exception is not caught, but a more general exception is caught, the more specific one will be caught by the block of the more general one. If we swap their places, a compilation error will occur.

In the given program, we are trying to access a field outside the array. This exception is caught in the try block and handled in the catch block. The finally keyword guarantees that the block after it will be executed regardless of whether the exception is caught or not.

### **Throwing exceptions**

When an exception occurs in a method, the method can either **handle** the exception or “**throw**” it to the calling method. This means that the exception will be handled in the calling method. Exceptions are “thrown” using the _**throws.**_ keyword.

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



In the program, the main method calls the division method, which we have declared to throw an exception of type ArithmeticException. Since throws is present in the method declaration, it follows that wherever we call it, the given exception will have to be caught and handled or re-thrown. When we declare a method and it is likely to cause multiple exceptions, they are declared, **listed with a comma after throws**.

In certain situations, it is necessary for the programmer to decide when to throw a given exception. This is possible using the keyword **throw**. The method again needs to indicate that it throws an exception of a certain type. If we rewrite the above example:

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

#### Custom exceptions:

In Java, we can create our own exceptions that extend the Exception class. These exceptions are called custom exceptions, they are created to meet the requirements of the user. When is it necessary to create custom exceptions:

\- When we want to provide more specific handling of already existing exceptions in Java.

\- When we define exceptions that correspond to the business logic and workflow of the problem we are solving.

In order to be able to create custom exceptions, the class must inherit from Exception. In this example, we create a new error InvalidAgeException, which is generated when the user is less than 18 years old.

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


# Task

Create a blog system, for this you will need:

- class Version, the class should have fields for creator and editor, in which the names of the author and the names of the author who made the edit are stored. Follow the principles of encapsulation when creating objects of the class.
- class for exceptions that occur in the Version class (VersionException). The exception class should inherit the base class Exception and accept an error message as a parameter to its constructor. Implement the exception class VersionException when setting values ​​for the fields. Handle null and empty values ​​for the creator and editor fields, throwing an exception with the text "Created by cannot be null" and "Modified by cannot be null"
- class Comment, the class should inherit Version and have a content field. Follow the principles of encapsulation when creating objects of the class.
- class for exceptions that occur in the Comment class (CommentException). The exception class should inherit the base class Exception and accept an error message as a parameter to its constructor. Implement the exception class CommentException when setting the field values. Handle null and empty field values ​​by throwing an exception with the text "Comment cannot be empty".
- class Article, the class should inherit Version and have fields for title, content and an array of comments up to 50 comments. Follow the principles of encapsulation when creating objects of the class.
- class for exceptions that occur in the Article class (ArticleException). The exception class should inherit the base class Exception and accept an error message as a parameter to its constructor. Implement the exception class ArticleException when setting the field values. Handle null and empty field values ​​by throwing an exception with the text "Article title cannot be empty", "Article content cannot be empty", "Comment cannot be null".
- class Blog with a static array of 1000 articles and an author name. The constructor takes the author name as a parameter and has:

* Method to add an article, passing in exceptions from the Article class
* Method to add a comment, by article title and comment, if there is no such article, throw a "Missing article" exception, and pass in exceptions from the Comment class

In main, create two Blog objects with different authors and add articles from both authors and comments for these articles. Handle the exceptions by printing error messages to the console.
