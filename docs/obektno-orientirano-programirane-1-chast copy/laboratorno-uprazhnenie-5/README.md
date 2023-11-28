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

#### String

Class java.lang.String makes possible processing of a symbol sets in Java. The inner representation of a string is char array.
The class String follows object-oriented programming principles: the values are written into dynamic memory and the variables store dynamic memory reference. Once the string is assigned to a class variable it becomes immutable, i.e. it cannot be changed - trying assigning new value will result in storing new value in new location. 


#### String declaration

Variables of string type could be declared as objects from class java.lang.String, for example *String str*. It is important to understand that it is not the same as creating variable and allocating memory for it. When a string variable is declared, the compiler is informed that variable with given name is created and expected type for this variable is String. Still this variable is unavailable for any manipulation since its value is null.


#### String declaration, creation and initialization

In order to perform any manipulation over declared variable, it must be created and initialized.
Creating string class variable is a process of dynamic memory allocation. Examples:

* by symbolic constant: String helloWorld = "Hello, world!";
* by assigning value of another string:  String source = "Some source"; String assigned = source;
 Variable *assigned* accepts the value of variable *source*. Since String is referent type, "Some source" is stored into the dynamic memory (heap). In this way both variables have same value.

![](<../../../assets/image (1).png>)

* by passing value of operation wgich results in string. This could be result of data validating method, aggregation of different variables or constants, variable transformation, etc. Example:
  String email = "some@email.bg";
  String info = "My mail is: " + email + "."; // My mail is: some@email.bg.

#### Printing strings

Printing of given string is achieved via system output, System.out:
* System.out.println("Your name is: "+ name); - the output represents concatenation of existing string with the value of variable *name*. At the end of resulting string is added end of line symbol, so next message will be printed on the next (new) line.
* System.out.print("Your name is: " + name); - the output represents concatenation of existing string with the value of variable *name*, but next message will be printed on the same line;
* System.out.printf("Hello, %s, have a nice reading!), name); - the output represent already existing string with the value of variable *name* in it.


#### Escaping of strings

If there are quotation marks in the string, there must be backslash ( \ ) before them. For example, "Book's title is "Intro in Java"" is a valid string and expected output is *Book's title is "Intro in Java"*. So before inner quotation marks there must be backslash. The backslash signals to the complier the inner quotation marks are part of a string, not start and end of a string. The backslash is used for some special symbols or to represent action that cannot be described with a symbol, like new line (\n), tabulation (\t), carrige return (\r), etc.

#### Identity comparison

Two strings could be compared using one of the options:

* equals() – case sensitive
* equalsIgnoreCase() – case insensitive, compares strings' content.
  
  String word1 = "Java"; String word2 = "JAVA";
  System.out.println(word1.equals(word2)); // false
  System.out.println(word1.equalsIgnoreCase(word2)); // true


#### Concatenation

Concatenation is process of joining strings in new one. This could be done in two ways:
* by method *concat()*

String greet = "Hello, "; String name = "reader!";
String result = greet.concat(name); // Hello, reader!

* by operators *+* or *+=*


String greet = "Hello, "; String name = "reader!";
String result = greet + name; // Hello, reader!

Concatenation doesn't change existing variables but returns new string as a result.

#### Search into string

   String str = "First java class";

   int index = str.indexOf("java");

   System.out.println(index); // index = 6 (starts from 0)

#### Extracting partial string

Method *substring(index1, index2)* returns part of the string variable. It's important to remember that index1 is inclusive, but index2 is exclusive, i.e. the result will include symbols from index1 to (index2 -1).


String str = "First java class"; String subStr = str.substring(11, 16); // subStr = "class".

#### Dividing string by separator

This is done using metod *split("separator")*.

```

String listOfAnimals = "dog, cat, lion, pig"; 
String[] animalsArr = listOfAnimals.split(","); 
for(String animal : animalsArr) {
 if(!animal.equals("")) { 
   System.out.println(animal); 
 }
}

```

#### Replacing strings

String helloWorld = "Hello, java."; 
String fixedGreeting = helloWorld.replace("java", " world"); 
System.out.println(fixedGreeting); // Hello, world.

#### Change of letter case

String helloWorld = "Hello, java."; 


System.out.println(helloWorld.toLowerCase()); // hello, java.


System.out.println(helloWorld.toUpperCase()); // HELLO, JAVA.

#### Space removal at the beginning and the end of string

String helloWorld = "Hello, java. ";


String withoutWhiteSpace = helloWorld.trim(); 


System.out.println(helloWorld.length()); // 13


System.out.println(withoutWhiteSpace.length()); // 12


# Enum

*Enumeration* is a special type of data, which limits variable values to pre-defined constants. Since the values are constants, there must be written with capital letters.
In order to define enumeration in Java, there must be keyword *enum* used. The example below shows enumeration for days of week:

```
public enum Day {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY,
    THURSDAY, FRIDAY, SATURDAY 
}
```

# STRINGBUILDER AND STRINGBUFFER

**StringBuilder** is class for building and modifying strings. It solves performance problems of concatenation, which is slow and expensive operation. Information into StringBuilder is mutable, i.e. could be changed. Changes are made in the same memory range (buffer) thus saving time and resources. In order to change string's value there is no new object created, but the already existing is changed instead.


Example:


StringBuilder sb = new StringBuilder(15); 


sb.append(“Hello,World! “); 


sb.append(“It’s a great day!”);


Some of the methods of StringBuilder:


capacity() – returns buffer size (total number of occupied and available symbols)


length() – returns the length of variable's string


charAt(int index) – returns the symbol at the given index


append(…) – concatenates the argument to the last symbol in the buffer


delete(int start, int end) – removes the string defined by start and end


insert(int offset, String str) – inserts given string at the *offset* position


replace(int start, int end, String str) – replaces into already existing string between start and end with given variable *str*


toString() – returns the information from the StringBuilder object.


**StringBuffer** is class that represents mutable string. It is part of Java Standard Edition and allows string operations like symbol addition, inserts, deletes and more.

*StringBuilder vs StringBuffer*

Mutable vs Immutable:
Both objects represent mutable strings. StringBuilder is newer version of StringBuffer and is more frequently used in modern applications because of some optimizations and improvements.

Thread safety:
StringBuffer is synchronized, while StringBuilder is not. This means that StringBuffer is to be used in multi-thread application, where many threads could call methods and modify object at the same time. In case of single-thread application it's better StringBuilder to be used.

Performance:
Since StringBuffer is synchronized, it's slower than StringBuilder, especially in single-thread applications.

Методи:
Both classes provide similar methods for string operations like *append()*, *insert()*, *delete()* and more. The syntax and work performed are also the same.



# Practice

### Task 1

Create program for company employees.

Define:

* class Employee with properties for first, middle and last name; access methods for all fields and method for textual description (returns full name of employee)
* клас Фирма, който да описва фирмата с нейните служители и имена. В конструктор който приема броя на служителите във фирмата, инициализирайте масива. Създайте два метода&#x20;
* class Company which describes all its employees. Define constructor with number of employees and inside initialize an array of employees. Class Company has following methods:
  * getInfo() - returns information for all employees, uses concatenation
  * getInformation() - returns information for all employees, uses StringBuffer
  * getEmployees(String name) - returns list of employees with the name present.

Check the behavior with different number of employees and put System.out.println(java.time.LocalDateTime.now()) at the beginning and at the end of method calls to see the difference.

### Task 2

Create program for parking lot:

Define:

* Class Car with number, width and length
* Class Truck with number, width, length and load
* Class Bus with number, width, length and number of seats

Decide if the classes could have common parent class.

* Class ParkingLot with 1000 vehicles
* Constructor with string parameter
  * Car:В4747КК,4,6;Truck:В4747КК,4,6,3;Bus:В4747КК,4,6,59
  * Divide the input string and for each part define an object into the array
* Method for textual description with concatenation or StringBuilder by choice.





