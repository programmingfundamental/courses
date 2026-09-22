---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---
### Task


I. Create a Book Editor interface with methods
* void generateBook(String title, int numberOfPages ): creates a book with title and numberOfPages empty pages
  
* void swapPages(int firstPageNumber, int secondPageNumber): swaps pages with given as parameters page numbers; throws an exception if it cannot be executed (InvalidPageException)

II. Create a Page class implementing Comparable

Private fields:

* pageNumber

* content

II.1 Default and parameterized constructors

Methods:

II. 2 Accessors and modifiers for all attributes

II.3 Comparator interface method implementation (optional)

II.4. Method for equality (optional)

II.5 For exchanging page content, the reference to Page passed as a parameter

II.6 For tectual description

III. Class Book implementing interface BookEditor, which stores sorted by page number books. 

Class has private attributes for: bookTitle, collection with pages


III.1 Constructor by number of pages and title. Creates a book with a title and blank pages - the specified number

III.2 Constructor by title - creates a book with only 1 page

Methods: 

III.3. addPage - page is passed as parameter

III.4. changePage - page is passed as parameter

III.5. removePage - page number is passed as parameter

III.6. swapPages - pages are passed as parameters, produces an exception if it cannot be executed

III.7 For textual description

III.8 Interface methods implementation

IV. Main function

IV.1 Creates an object - III. Initialize it with a constructor.

IV.2 Print the book- IV.1 to the console output

IV.3. Add Page to - IV.1, print

IV.4 Remove Page from - IV.1

IV.5 Swap two of the pages, print

IV.6 Handle exceptions
