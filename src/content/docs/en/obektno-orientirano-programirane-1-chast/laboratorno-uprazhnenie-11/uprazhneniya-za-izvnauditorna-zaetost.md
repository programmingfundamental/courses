---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---

## Independent Study Exercise

Create a bookstore program that reads data from a file, processes it as objects, and writes results to a new file.

## Data model

Define `InvalidDataException` for invalid data.

Define a `CoverType` interface with `boolean isHardCover()`.

Define an abstract `Person` class with `firstName` and `lastName` fields of type `String`. Add a constructor, accessors, `equals()`, `hashCode()`, and `toString()`.

Define `Author` extending `Person` with `country` and `genre` fields of type `String`.

Define `Book` implementing `CoverType` with `title` of type `String`, an `Author`, `publishingYear` of type `int`, `quantity` of type `int`, and `price` of type `double`.

`quantity` must be greater than 5 and `price` greater than 9.99. Throw `InvalidDataException` for an invalid value. `isHardCover()` returns `true` if the book was published before 2000 and its price is greater than 14 BGN.

## File input

Read book data from a text file, with one book per line. Fields are separated by `;`. Titles containing spaces may use underscores to simplify parsing.

Example:

```text
Under_the_Yoke;Ivan;Vazov;Bulgaria;classic;1985;12;15.00
East_of_Eden;John;Steinbeck;USA;classic;1998;18;12.58
```

## The `BookStore` class

Define a `BookStore` class with a `name` and a collection of unique books. Choose an appropriate collection. Its constructor accepts a bookstore name and a filename, then loads the books from that file.

Implement `addBook(Book book)`, `calculateTotalPrice()`, `calculateAveragePriceByGenre(String genre)`, `findAuthorWithMostBooks()`, `countAuthorsAfterYear(int year)`, `getBooksSortedByAuthorCountry()`, `countHardCoverBooks()`, and `toString()`.

## File output

The `Application` class should create a `BookStore` using an input file, add at least two new books, calculate all method results, and write them to an output file.

Handle exceptions while reading and writing. Use `try-with-resources` for file resources.

## Additional task — `Scanner`

After implementing the solution with text streams, read the same input file line by line using `Scanner` and compare the resulting books. Use UTF-8 and close the file resource with `try-with-resources`.

Also create a short example that reads a book count with `nextInt()` and a title with `nextLine()`. Explain why the remainder of the current line must be consumed. Check a valid integer, a non-numeric token, and a title containing spaces.
