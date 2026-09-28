---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---

## Independent Study Exercises

## Task 1 — Blog System

Create a blog system.

It must include:

- A `Version` class with fields for the creator and the person who edited the item. Store the creator's name and the editor's name. Follow encapsulation principles when creating objects of this class.
- A `VersionException` class that extends `Exception` and accepts an error message in its constructor. Use it when setting fields in `Version`: handle `null` and empty creator or editor values, and throw exceptions with the messages `"Created by cannot be null"` and `"Modified by cannot be null"`.
- A `Comment` class that extends `Version` and adds a content field. Follow encapsulation principles when creating objects of this class.
- A `CommentException` class that extends `Exception` and accepts an error message in its constructor. Use it when setting the `Comment` content; handle `null` and empty values and throw an exception with the message `"Comment cannot be empty"`.
- An `Article` class that extends `Version` and adds title, content, and an array of comments (up to 50). Follow encapsulation principles when creating objects of this class.
- An `ArticleException` class that extends `Exception` and accepts an error message in its constructor. Use it when setting fields in `Article`. Handle `null` and empty values by throwing exceptions with the messages `"Article title cannot be empty"`, `"Article content cannot be empty"`, and `"Comment cannot be null"`.
- A `Blog` class with a static array of 1,000 articles and an author name. Its constructor accepts the author's name. It must have:
  - a method for adding an article that passes through exceptions from `Article`;
  - a method for adding a comment by article title and comment. If there is no such article, throw `"Missing article"`; pass through exceptions from `Comment`.

In `main`, create two `Blog` objects with different authors, add articles by both authors, and add comments to those articles. Handle exceptions by printing their messages to the console.

## Task 2 — Tracing `finally`

Write a `readGrade(String text)` method that converts text to an integer, accepts grades from 2 through 6, and returns the grade. For a number outside the range, use `throw new IllegalArgumentException(...)`. Print `"Validation complete"` from `finally`.

In `main`, call the method with `"6"`, `"9"`, and `"abc"`. Handle `NumberFormatException` before `IllegalArgumentException`, because the former is a subclass of the latter. Before running the program, write down the expected order of messages. Verify that `finally` runs both when the method returns and when an exception occurs. Explain why you should not use `return` in this block.

## Task 3 — Checked Exception and `record`

Create `InvalidTitleException extends Exception` with a message constructor, and a `validateTitle(String title) throws InvalidTitleException` method. It must throw the exception if `title` is `null` or empty after `trim()`.

Separately, create `record Book(String title, double price)` with a compact constructor that rejects a negative price with `IllegalArgumentException`. Compare the caller's obligation to handle these two exception types. Test valid data, price `-1`, an empty title, and `null`.

## Task 4 — Exception Hierarchy and Resource Cleanup

Place `IOException`, `NumberFormatException`, `StackOverflowError`, and the custom exceptions from the tasks in the exception hierarchy. Identify which are checked and unchecked, and which are caught by `catch (Exception ...)`, without actually exhausting runtime resources.

Implement `DemoResource implements AutoCloseable`, which prints a message from `close()`. Use it with `try-with-resources` once with normal completion and once with an exception in the body. Verify that closing happens before the outer `catch`.
