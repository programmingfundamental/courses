---
layout: default
title: Laboratory exercise 5
parent: Object-oriented Programming - 1 part
has_children: true
nav_order: 5
permalink: /docs/object-oriented-programming-1-part/laboratory-exercise-5
---
# Laboratory exercise 5

# String

#### ASCII AND UNICODE table ASCII is the predecessor of the UNICODE table

The ASCII table stores 128 or 256 characters (7 or 8 bit) - numbers, uppercase and lowercase letters and special characters. When it is necessary to work with characters other than Latin, the ASCII table cannot be used.

Java uses the 16-bit UNICODE character table, storing 216=65536 characters, and when combining 2 characters to obtain a specific new character, we can form up to 100,000 characters.

#### String

The java.lang.String class allows for the manipulation of character strings in Java.

The internal representation of the String class is an array of characters (char).

The String class follows the principles of object-oriented programming: values ​​are stored in dynamic memory, and variables keep a reference to the memory (a reference to an object in dynamic memory). It has an important feature - sequences of characters stored in a variable of the class are immutable. Once stored, the contents of the variable are not changed directly - if we try to change the value, it will be stored in a new location in dynamic memory, and the variable will start pointing to it.

Example: If several variables point to the same area in memory with a given value, this value cannot be changed directly. The change will only affect the variable through which the value was edited, as this will create a new value in dynamic memory and point the variable in question to it, while the other variables will point to the old location.

#### String declaration

A string declaration is a declaration of an object of the String class. This is not equivalent to creating a variable and allocating memory for it. With the declaration, the compiler is informed that it will use the variable name and the expected type for it is String. The variable is not created in memory and it is not yet available for processing (it has a null value, which means no value) and attempting to manipulate such a string will generate an error (a NullPointerException exception)!

#### Declaration, creation and initialization

In order for a declared variable to be processed, it must be created and initialized.

Creating a class variable (also known as instantiation) is the process of allocating a region in dynamic memory. Initializing variables:

* via symbolic constant

  ```java
  String helloWorld = "Hello, world!";
  ```
* by assigning the value to another string

Assigning a value is equivalent to pointing a String variable to another variable of the same type. An example of this is the following snippet:

  ```java
  String source = "Some source";  
  String assigned = source;
  ```


* The assigned variable takes the value of source. Since the String class is a reference type, "Some source" is written to the heap, pointed to by the first variable. Thus, the two objects have the same value:
  
  ![](<../../assets/image (1).png>)
  
* by passing the value to an operation that returns a string. This can be the result of a method that validates data; collecting the values ​​of several constants and variables, converting an existing variable, etc. Example of an expression that returns a string after concatenation:

```java
String email = "some@email.bg";
String info = "My mail is: " + email + "."; // My mail is: some@email.bg.
```

#### Printing

Data display is done through the System.out output stream.:

```java
System.out.println("Your name is: " + name);
```
Using the println(…) method, we print the message: Your name is:, accompanied by the value of the variable name. After the end of the message, a newline character is added, and the next message will be displayed on the next line of the console. If we want to avoid the newline character and have the messages displayed on the same line, then we resort to the print(…) method. In case we need more precise formatted output, the printf(…) method comes to the rescue.: 

```java
System.out.printf("Hello, %s, have a nice reading!", name);
```

#### Escaping 

If we want to use quotes in the content, then we need to put a slash before them to instruct the compiler. 

```java
String quote = "Book’s title is \"Intro to Java\""; // Book's title is "Intro to Java"
```

The quotes this time are part of the text. In the variable, they are added by placing them after the escaping character, a backslash. This way, the compiler understands that the quotes do not serve as the beginning or end of a character string, but are part of the data. The slash is used for characters that play a special role in the text (in this case, the quotes) or to define an action that cannot be expressed with a symbol. Examples of the second case are the designation of a newline character (\n), tabulation (\t), selection of a character by its Unicode (\uXXXX, where X denotes the code), etc.

* \’ - Single quote
* \”- Double quotation mark
* \ - Slash

#### String equality methods

* equals() – differentiates between lowercase and uppercase letters
* equalsIgnoreCase() – ignores case, compares only the contents of the string.

```java
String word1 = "Java";
String word2 = "JAVA";
System.out.println(word1.equals(word2)); // false
System.out.println(word1.equalsIgnoreCase(word2)); // true
```

#### Alphabetical string comparison

* compareTo(…) – compares case-sensitively.
* compareToIgnoreCase(…) – compares case-insensitively.

compareTo(…) returns a positive number, a negative number, or 0 depending on the lexicographical order of the character strings being compared. The calculation is done by taking the unique code from the Unicode table for each character and calculating the difference. 

```java
String str1= "abc";
String str2 = "aBcd";
System.out.println(str1.compareTo(str2)); //32
System.out.println(str2.compareTo(str1)); //-32
System.out.println(str1.compareToIgnoreCase(str2)); //-1
System.out.println(str2.compareToIgnoreCase(str1)); //1
```

#### String concatenation

The process of joining strings together to form a new string is called concatenation. It can be done in two ways::
* using concat(…):

```java
String greet = "Hello, ";
String name = "reader!";
String result = greet.concat(name); // Hello, reader!
```

By calling the method, we will append the variable name, which is passed as an argument, to the variable greet. The resulting string will have the value "Hello, reader!". 

* using operators + and +=.

The above example can be implemented without any problems in both ways, for example: 

```java
String greet = "Hello, ";
String name = "reader!";
String result = greet + name; // Hello, reader!
```

All appends to strings do not change existing variables, but return a new variable as a result.

#### String search

```java
String str = "First java class";
int index = str.indexOf("java");
System.out.println(index); // index = 6 (starts from 0)
```

#### Substring

```java
String str = "First java class";
String subStr = str.substring(11, 16); // subStr = "class"
```

Calling the substring(index1, index2) method extracts a substring of a given variable that is between index1 and (index2– 1) inclusive. The character at the specified position – index2– is not taken into account! If we specify substring(11, 16), the characters between index 10 and 15 inclusive will be extracted, not between 10 and 16.!

#### Split

```java
String listOfAnimals = "dog, cat, lion, pork";
String[] animalsArr = listOfAnimals.split("\[ ,]");
for(String animal : animalsArr) {
  if(!animal.equals("")) {
      System.out.println(animal);
  }
}
```

#### Replace

```java
String helloWorld = "Hello, java.";
String fixedGreeting = helloWorld.replace("java", " world");
System.out.println(fixedGreeting);
```

#### Switching between uppercase and lowercase letters

```java
String helloWorld = "Hello, java.";
System.out.println(helloWorld.toLowerCase());
System.out.println(helloWorld.toUpperCase());
```

#### Trim

```java
String helloWorld = "Hello, java. ";
String withoutWhiteSpace = helloWorld.trim();
System.out.println(helloWorld.length());
System.out.println(withoutWhiteSpace.length());
```


#### StringBuilder

StringBuilder is a class that is used to build and modify character strings. It overcomes the performance problems that arise when concatenating strings of type String. The class is built in the form of an array of characters and that. The information in it is not immutable - changes that are imposed on variables of type StringBuilder are made in the same area of ​​memory (buffer), which saves time and resources. To change the content, a new object is not created, but simply the current one is changed. 

```java
StringBuilder sb = new StringBuilder(15);
sb.append("Hello, World! ");
sb.append("It’s a great day!");
```

* capacity() – returns the size of the entire buffer (the total number of occupied and free characters)
* length() – returns the length of the string stored in the variable
* charAt(int index) – returns the character at the specified position
* append(…) – appends a string, number, or other value after the last character written to the buffer
* delete(int start, int end) – removes string at specified start and end position
* insert(int offset, String str) – inserts a given string at a given position
* replace(int start, int end, String str) – replaces the written string between the start and end positions with the value of the variable str
* toString() – returns the information stored in the StringBuilder object as a result of type String, which can be stored in a String variable.

#### StringBuffer

StringBuffer in Java is a class that represents a mutable string. This class is part of the Java Standard Edition and provides the ability to manipulate strings through various operations such as adding characters, inserting, deleting, and others.

StringBuffer vs StringBuilder:

* Mutable vs Immutable:
 
StringBuffer is part of the Java Standard Edition and is mutable. This means that you can change its contents after it is created. StringBuilder is also mutable and is a newer version of StringBuffer. It was introduced in Java 5 and is preferred in modern applications due to some optimizations and improvements..

* Thread safety:

StringBuffer is thread-safe, which means it is suitable for use in multithreaded applications because its methods are synchronized. StringBuilder is not synchronized, so it is faster, but should be used with caution in multithreaded environments..

* Performance:
    
Due to its synchronization, StringBuffer can be slower than StringBuilder, especially in non-threaded applications where synchronization is not needed..

* Methods:
    
Both classes provide similar methods for manipulating strings, such as append(), insert(), delete(), etc. The syntax and operation of these methods are similar between the two classes..

* Usage:
    
If you are working in a multithreaded environment and need thread safety, you can use StringBuffer.
If thread safety is not needed, StringBuilder is usually preferred as it is faster.


# Tasks

### Task 1

Create a program for employees in a company.

To do this, you will need:

* Employee class, which will describe an employee and his three names, basic salary and position. Methods for accessing the class fields. Method for text representation. (The employee's full name)
* Company class, which will describe the company with its employees and names. In a constructor that accepts the number of employees in the company, initialize the array. Create two methods
* getInfo() - which will return as a result the information about the company with all employees of the company. Use string concatenation
* getInformation() - which will return as a result the information about the company with all employees of the company. Use a string buffer
* getEmployees(String name) - Returns a list of the names of employees in which the given name exists

Check the execution of the methods for different numbers of employees in the company with System.out.println(java.time.LocalDateTime.now()); at the beginning and end of the execution of the methods is there a difference between String and StringBuilder?

### Task 2

Write a program for car parking:

For this purpose you will need:

* class Car with number, width and length
* class Truck with number, width and length, load capacity
* class Bus with number, width and length, number of seats

Consider whether the classes should have a common parent class?

* class Parking, with a list of 1000 vehicles.
* constructor that accepts an input parameter, a formatted string
* Car:В4747КК,4,6;Truck:В4747КК,4,6,3;Bus:В4747КК,4,6,59
* Parse the string and for each type of vehicle create an object in the array
* Text representation of the parking lot, use string concatenation or string buffer, depending on who performed better in the first task
