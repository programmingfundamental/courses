---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---
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
