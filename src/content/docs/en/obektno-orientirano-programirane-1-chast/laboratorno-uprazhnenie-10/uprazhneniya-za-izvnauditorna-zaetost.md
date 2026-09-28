---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---

## Independent Study Exercises

## Task 1

Define a `Page` class representing a book page, with `number` of type `int` and `content` of type `String`. Add a constructor, accessors and mutators, `toString()`, and `compareTo(Page other)`. Implement `Comparable<Page>` so pages are compared by number.

## Task 2

Define `InvalidPageException` for an invalid page number or an impossible page operation. Extend `RuntimeException` and provide a `InvalidPageException(String message)` constructor.

## Task 3

Define a `BookEditor` interface with `generateBook(String title, int numberPages)`, `swapPages(int firstPageNumber, int secondPageNumber)`, `updatePage(int pageNumber, String content)`, and `removePage(int pageNumber)`. Throw `InvalidPageException` when an operation cannot be completed.

## Task 4

Define a `Book` class implementing `BookEditor`. It must contain `title` of type `String` and `pages` of type `Map<Integer, Page>`. Use a `Map` implementation that iterates over pages in page-number order.

Implement `Book(String title, int numberPages)`, `addPage(Page page)`, the `BookEditor` methods, and `toString()`. `generateBook` should create a book with empty pages. `swapPages` must swap the contents of two pages, not their page numbers.

## Task 5

Create an `Application` class demonstrating book creation, adding a page, changing its content, removing a page, swapping two pages, and handling `InvalidPageException`. Use `try-catch` for operations that may throw it.

## Task 6 — Working with `Map.Entry`

In `Book`, create a report by traversing `pages.entrySet()`. Each line must include the key, the page number from the `Page` object, and the content length. Verify that the key matches the page number. Do not call `get(key)` when the value is already available through `entry.getValue()`.

Create a separate `HashMap<String, Integer>` of book stock counts. Increase every count by 1 using `Map.Entry.setValue` during traversal. Compare this with a separate pair created using `Map.entry(...)`: it does not modify a map and does not support `setValue`. Check initial values of 0, 1, and 10.

Copy the entries into a list and sort it by value using `Map.Entry.comparingByValue()`. Explain why sorting the list does not change the iteration order of the original `HashMap`.
