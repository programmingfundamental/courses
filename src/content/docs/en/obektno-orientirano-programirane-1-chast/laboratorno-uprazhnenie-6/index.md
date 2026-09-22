---
title: Laboratory exercise 4
sidebar:
  order: 4
---

# Laboratory exercise 4

### Abstract class and interface

Abstraction is the process of showing only “relevant” data and “hiding” unnecessary details about an object from the user (the programmer using the abstraction).

Since an abstract class allows for concrete implementation of methods, it does not provide 100% abstraction. It provides partial abstraction.

Interfaces on the other hand are used for 100% abstraction.

| Abstract class                                                                                           | Interface                                                                               |
| --------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------- |
| An abstract class can only extend one class or one abstract class at a time               | A single interface can extend any number of interfaces at once.                      |
| An abstract class can extend another concrete (regular) class or abstract class                    | An interface can only extend another interface.                                      |
| An abstract class can have both abstract and concrete methods.                                   | An interface can only have abstract methods.                                          |
| In abstract class, keyword “abstract” is mandatory to declare a method as abstract           | In interface, keyword “abstract” is optional to declare a method as abstract |
| An abstract class can have protected and public abstract methods                                    | An interface can only have public abstract methods.                                 |
| An abstract class can have a static, final, or static final variable with any access modifier. | An interface can only have a public static final (constant) variable                |
