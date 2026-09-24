---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---

## Independent Study Exercises

## Task 1

Define an `Article` class representing a blog post, with `title`, `author`, and `editor` of type `String`, and `comments` and `editorsHistory` of type `List<String>`. Add a constructor and accessors, as well as `addComment(String comment)`, `edit(String editor, String newTitle)`, `getCommentsCount()`, and `getEditsCount()`.

`edit` must update the title and record the editor's name in `editorsHistory`. If the author and editor are the same person, do not count the change as an external edit.

## Task 2

Define a `Blog` class that manages articles in a collection. It must contain a static collection, `private static List<Article> articles;`, and static methods `addArticle(Article article)`, `editArticle(String title, String editor, String newTitle)`, `findAuthorWithMostComments()`, `findAuthorWithMostArticles()`, `findEditorWithMostEdits()`, and `printArticles()`.

Use appropriate collection traversal. Calculate the author or editor with the most occurrences from the collection data rather than hard-coding the result.

## Task 3

Create a program that adds at least five articles by at least three authors, adds differing numbers of comments, edits some articles using different editors, prints all articles, and reports the author with the most comments, the author with the most articles, and the editor with the most edits.

Choose an appropriate collection for each operation. Use `ArrayList` for articles and, if needed, `HashSet` for unique names. `Map` is covered in the next lab.

## Task 4 — Sorting and method references

Add `getAuthor()`, `getTitle()`, and `getCommentsCount()` to `Article`. Sort copies of the list by comment count in three ways: a named `Comparator`, a lambda expression, and `Comparator.comparingInt(Article::getCommentsCount)`. Verify that all methods produce the same order for the same initial data.

Add a secondary title criterion with `thenComparing(Article::getTitle)` and print results using `forEach(System.out::println)`. Implement a useful `toString()` in `Article`. Include equal comment counts, different titles, and an empty list. For each method reference, write the corresponding lambda expression.

## Task 5 — Four forms of method reference

Use `Integer::parseInt`, `System.out::println`, `String::length`, and `StringBuilder::new` with suitable functional interfaces from the lesson. For each example, identify the input parameters, result, and when the method is actually called. Use the resulting `StringBuilder` to build a short article report.
