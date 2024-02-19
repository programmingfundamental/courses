---
layout: default
title: Laboratory exercise 10
parent: Object-oriented Programming - 1 part
has_children: true
nav_order: 10
permalink: /docs/object-oriented-programming-1-part/laboratory-exercise-10
---

# Laboratory exercise 10

**Входно-изходни операции в Java**



Input-output operations in Java are based on the concept of streams, where "stream" refers to a sequence of data.

A Java application uses an input stream to read data from a source and an output stream to write that data to a destination. The source and destination can be a file, console, another application, or some peripheral device, as shown in the figure below.



![](<../../assets/image (118).png>)



Streams support various types of data (bytes, primitive data types, characters, objects), where some streams simply pass the data, while others process and transform it into a suitable format for a specific case.

**Byte streams. ByteStream classes**

As the name suggests, these classes read and write data in 8-bit format. They belong to the packet java.io.

Each byte stream class inherits class InputStream or class OutputStream.

Examples of classes that extends InputStream:

\-          BufferedInputStream – reads bytes from buffer;

\-          ByteArrayInputStream – reads bytes from an array;

\-          DataInputStream – reads simple format data;

\-          FileInputStream – reads bytes from file;

\-          ObjectInputStream – reads objects;

\-          other.

Some of the InputStream class methods:

·         public abstract int read() – reads byte from input data, returns -1 when end of stream is reached;

·         public int available() – returns how many bytes could be read;

·         public void close() – closes current inout stream.

Likewise, some of OutputStream children are:

\-          BufferedOutputStream – writes bytes to buffer;

\-          ByteArrayOutputStream – writes bytes to an array;

\-          DataOutputStream – writes simple format data;

\-          FileOutputStream – writes in file;

\-          ObjectOutputStream – writes objects;

\-          other.

Some of the methods in OutputStream:

·         public void write(int i) – writes passed byte to the current output stream;

·         public void write(byte\[]) – writes passed byte array to the current output stream;

·         public void flush() – clears current output stream;

·         public void close() – closes current output stream.



It is important to remember that every input-output stream must be closed.

Byte streams are used for reading/writing primitive data types. There are other types of streams for more complex data, but all of them are based on byte streams.

_Example of byte strean:_



![](<../../assets/image (105).png>)

**Symbol streams. CharacterStream classes**

Classes for working with byte streams can operate only with one byte and are not suitable for direct manipulation of Unicode characters, which are 16-bit. For handling streams of such characters, character streams are used accordingly.

Similar to the classes for working with byte streams, the classes for working with character streams also come in two main types: subclasses of the abstract class Reader or the abstract class Writer.

Reader subclasses:

\-          BufferedReader;

\-          FileReader;

\-          InputStreamReader – has methods for byte to symbol conversion;

\-          StringReader;

\-          other.

Class Reader and all his children/subclasses has similar to InputStream methos as _**int read()**_ and _**void close()**_.

Writer subclasses:

\-          BufferedWriter;

\-          FileWriter;

\-          OutputStreamWriter;

\-          StringWriter;

\-          др.

Class Writer and all his children/subclasses has similar to OutputStream methos as: _**void write()**_, _**void write(int i)**_ writes one symbol, _**void flush()**_ and _**void close()**_.


Often, character streams act as "wrappers" for byte streams. For example, FileReader uses FileInputStream, while FileWriter uses FileOutputStream.

Character streams support all end-of-line characters - "\r", "\n", and "\r\n". This allows working with text files.

_Examples of symbol streams:_

The two examples shown perform the same task: they read from one file and write the read content to another. In the first example, only the FileReader and FileWriter classes are used, while the second accomplishes the same using BufferedReader and PrintWriter.



![](<../../assets/image (92).png>)

![](<../../assets/image (133).png>)

**Class Scanner**

This class enables working with text by providing methods for its conversion into various primitive data types. Unlike input-output streams, the class resides in the java.util package.

The Scanner class breaks the input into tokens using a delimiter (by default, this is the whitespace). The tokens obtained from this separation can be converted into values of different types, thanks to the various next methods.

Commonly used methods from the Scanner class include:

·         boolean hasNext() – returns true if there is next token;

·         boolean hasNextInt() – returns true if the next token could be considered as integer number;

·         boolean hasNextDouble() – returns true if the next token could be considered as double number;

·         boolean hasNextLong() – returns true if the next token could be considered as long number;

·         boolean hasNextLine() – returns true if there are more line/s;

·         String next() – finds and returns next token;

·         boolean nextBoolean() – finds and returns next token that could be considered as boolean value;

·         int nextInt() - finds and returns next token taht could be considered as integer value;

·         double nextDouble() - finds and returns next token taht could be considered as double value;

·         long nextLong() - finds and returns next token taht could be considered as long value;

·         Scanner useDelimeter(String pattern) – defines delimeter;

·         void close().

_Examples with class Scanner_:

The example below reads an input string and then outputs each word on a separate line.



![](<../../assets/image (138).png>)

Next example reads from file and sums double numbers (skips any other type of data).



![](<../../assets/image (114).png>)

**Standard streams. Class Console**

Java supports three standard streams:

\-          System.in – system input (keyboard);

\-          System.out – system output (screen/console);

\-          System.err – system stream for errors.

None of these streams needs to be declared; they are created automatically.

System.out and System.err are defined as PrintWriter objects, i.e., character streams. On the other hand, System.in is defined as a byte stream.

An alternative to the standard streams is the Console class. It provides the functionality of the standard streams as well as additional features. The Console object provides input and output character streams through its reader() and writer() methods.

_Example:_



![](<../../assets/image (86).png>)



**Data streams. DataInput and DataOutput interfaces**

Data streams support input-output operations on all primitive data types, as well as on character strings. All data streams implement either the DataInput interface or the DataOutput interface.

Methods of the DataInput interface include:

·         boolean readBoolean();

·         byte readByte();

·         char readChar();

·         double readDouble();

·         int reading();

·         String readLine();

·         other.

All these methods has write() method coming from DataOutput interface: void writeBoolean(boolean value), void writeByte(byte value) и т.н.

Classes which implement DataInput: DataInputStream, FileImageInputStream, ObjectInputStream, RandomAccessFile.

Classes which implement DataOutput: DataOutputStream, FileImageOutputStream, ObjectOutputStream, RandomAccessFile.

_Examples:_

The example reads a file using RandomAccessFile and populates a collection of objects. Each line in the file contains one object.

![](<../../assets/image (134).png>)

![](<../../assets/image (117).png>)



**Task**


Create program for real estate agency.

Define:
-	Interface **PriceCalculator** with method calculatePrice();
  
-	Enumeration **PropertyType** with FOR_RENT and FOR_SALE values;
  
-	Enumeration **Exposure** with all possible values 9east, west, etc.);
  
-	Abstract class **Property**:
  
  •	Implements PriceCalculator and Comparable;
  
  •	Properties for type, exposure, area, pricePerSquareMeter and address that follow encapsulation principle;
  
  •	Parameterized constructor by all fields;
  
  •	Methods: getters, equals (by address) and hashCode;
  
  •	Interface methods: one returns area multiplied by price per square meter, the other compares by area;
  
-	Class **Apartment**:

  •	Extends Property with fields floor and hasParkingPlace that follow encapsulation principle;
  
  •	Parameterized constructor by all fields;
  
  •	Methods: getters and toString;
  
  •	Interface method returns the price increased with 10% if the apartment has parking place and is up to 3rd floor, and with 15% if the apartment is for sale;
  
-	Class **House**:
  
  •	Extends Property with field hasGarden that follow encapsulation principle;
  
  •	Parameterized constructor by all fields;
  
  •	Methods: getter and toString;
  
  •	Interface method returns price increased with 20% if there is garden;
  
-	Class **RealEstateAgency**:
  
  •	Field for properties – collection of unique properties that follow encapsulation principle;
  
  •	Parameterized constructor by file name - reads and loads the collection;
  
  •	Methods:
  
    o	addProperty – adds property passed as parameter to the existing collection;
    
    o	printCollection – writes collection passed as parameter to another file;
    
    o	sortByArea – orders collection by property’s area and prints it to the console;
    
    o	sortByPricePerSquareMeter – orders collection by price per square meter and prints it to the console;
    
    o	sortByAddress – orders alphabetically collection by property’s address and displays it to the console;
    
    o	calculateAveragePropertyPrice – calculates and returns the average price for the available properties;
    
    o	getPropertiesForRentNumber – finds and returns the number of available properties for rent;
    
    o	getPropertiesByExposure – finds and returns list of available properties with given exposure (passed as method parameter)
    
-	Class **Application** with main function that writes results from defined methods to a file.
