---
layout: default
title: Laboratory lesson 1
parent: Object-oriented Programming - 2 part AEO
has_children: false
nav_order: 1
permalink: /docs/obektno-orientirano-programirane-2-chast-aeo/laboratorno-uprazhnenie-1
---

# Laboratory lesson 1

### Composition and Aggregation

In object-oriented programming, composition and aggregation are forms of association between objects that model part-whole relationships. They are used to build more complex systems by combining smaller objects.

The main purpose of composition and aggregation is:

- modeling real-world dependencies between objects

- implementing modularity and separation of responsibilities

- increasing the reusability of existing code

- reducing dependencies between objects (compared to using inheritance).

  
## Composition

Composition is used to create a strong connection (thight coupling) between objects and is suitable in cases where one object (the "part") cannot meaningfully exist without the other (the "whole"). When a composite object is destroyed, the part is also destroyed.

There are few types of composition depending on their implementation:

- classis composition, where parts are created inside the complex object

  <img width="176" height="233" alt="classicComposition" src="https://github.com/user-attachments/assets/23ab1905-0fd2-4514-82ec-066756dde617" />
  

```java
public class Address {

  private String town;
  private String streetName;
  private int number;

  // constructor, getters, setters, methods
}

public class Building {

  private Address address;

  public Building() {
    this.address = new Address(); // internal object creation
  }
        
  // methods
}

```

- multidirectional composition (1-to-many)


<img width="370" height="217" alt="multipleComposition" src="https://github.com/user-attachments/assets/2b5a9c55-5df7-4ed4-ae67-2ed3d2f7b8b5" />



```java
public class DocumentationChapter {
  private int number;
  private String chapterTitle;

  public DocumentationChapter(int number, String chapterTitle) {
    this.number = number;
    this.chapterTitle = chapterTitle;
  }
}

public class ProjectDocumentation {

  private List<DocumentationChapter> documentation = new ArrayList<>();

  public ProjectDocumentation() {
    documentation.add(new DocumentationChapter(1, "Title page"));
    documentation.add(new DocumentationChapter(2, "Project introduction"));
    documentation.add(new DocumentationChapter(3, "Project body"));
    documentation.add(new DocumentationChapter(4, "Conclusion"));
  }
}

```

- composition with access control, where the parts are encapsulated and the complex object does not allow external modifications


<img width="406" height="234" alt="controlledComposition" src="https://github.com/user-attachments/assets/20068d34-7a35-46f4-89ab-9591f766f145" />



```java
public class DocumentationChapter {

  private int number;
  private String chapterTitle;

  public DocumentationChapter(int number, String chapterTitle) {
    this.number = number;
    this.chapterTitle = chapterTitle;
  }
}

public class ProjectDocumentation {

  private List<DocumentationChapter> documentation;

  public ProjectDocumentation() {
    documentation = List.of(
      new DocumentationChapter(1, "Title page"),
      new DocumentationChapter(2, "Project introduction"),
      new DocumentationChapter(3, "Project body"),
      new DocumentationChapter(4, "Conclusion")
    );
  }

  public List<DocumentationChapter> getDocumentation() {
    return Collections.unmodifiableList(documentation);
  }
}

```



## Aggregation

Aggregation is used in cases where the relationship between objects is weaker and the part can exist independently outside the context of the complex object. An object that is used as part of a complex can be shared between different complex objects and in different contexts, thus being independent of their existence.

Likewise composition, there are different implementation-based aggregation types:

- simple aggregation with external parts that could exist independently


<img width="187" height="185" alt="simpleAggregation" src="https://github.com/user-attachments/assets/943f83f1-acd8-4f40-9500-a7a726a24eac" />



```java

public class Book {

  // properties, constructors, methods

}

public class Library {

  private List<Book> catalog;

  public Library(List<Book> catalog) {
    this.catalog = catalog; // external input
  }

}

```

- unidirectional aggregation where one of the objects "knows" about the other


<img width="216" height="234" alt="unidirectionalAggregation" src="https://github.com/user-attachments/assets/a945ce35-c9ba-4f7a-810b-b367d5bb5d13" />


```java

public class Book {

  private String title;

  public Book(String title) {
    this.title = title;
  }

  // methods

}

public class Reader {

  private String name;
  private Set<Book> books = new HashSet<>();

  public Reader(String name) {
    this.name = name;
  }

  public void borrowBook(Book book) {
    books.add(book);
  }

}

```

- bidirectional aggregation where both objects are aware of each other


<img width="202" height="234" alt="bDirectionalAgg" src="https://github.com/user-attachments/assets/1de35aa1-f1e4-4cfa-baf0-1a6fc8e82a13" />


```java

public class Book {

  private String title;
  private Set<Reader> readers = new HashSet<>();

  public Book(String title) {
    this.title = title;
  }

  public void addReader(Reader reader) {
    readers.add(reader);
  }

}

public class Reader {

  private String name;
  private Set<Book> books = new HashSet<>();

  public Reader(String name) {
    this.name = name;
  }

  public void borrowBook(Book book) {
    books.add(book);
    book.addReader(this);
  }
        
}

```

- multiple aggregation with mediator - objects remain independent and are feeded externaly


<img width="278" height="250" alt="aggregationWithMediator" src="https://github.com/user-attachments/assets/7d607427-3755-4695-a110-8595b2a8d99f" />


```java

public class Book {

  private String title;

  public Book(String title) {
    this.title = title;
  }

}

public class Reader {

  private String name;

  public Reader(String name) {
    this.name = name;
  }
        
}

public class Library {

  private Set<Book> books = new HashSet<>();
  private Set<Reader> readers = new HashSet<>();

  public void addBook(Book book) {
    books.add(book);
  }
        
  public void addReader(Reader reader) {
    readers.add(reader);
  }

}

```


- associative aggregation with independent objects


<img width="278" height="201" alt="associativeAggregation" src="https://github.com/user-attachments/assets/6cc9f9c9-56cc-4fa2-9a19-19e3ea6cf56b" />


```java

public class Book {

  private String title;

  public Book(String title) {
    this.title = title;
  }

}

public class Reader {

  private String name;

  public Reader(String name) {
    this.name = name;
  }
        
}

public class LoanBook {

  private Book book;
  private Reader reader;
  private LocalDate date;

  public LoanBook(Book book, Reader reader, LocalDate date) {
    this.book = book;
    this.reader = reader;
    this.date = date;
  }

}

```


## Composition vs Aggregation

Composition models structural dependency, while aggregation models logical belonging.

The table below summarizes and compares both forms:

| Criteria             | Composition | Aggregation |
|----------------------|-------------|-------------|
| connectivity degree  | strong      | weak        |
| lifecycle            | dependent   | independent |
| ownership            | yes         | no          |
| shareability         | no          | yes         |


The choice between association, aggregation, and composition is an architectural decision that determines the degree of dependency, flexibility, and maintainability of the system.

Composition and Aggregation are used in practice for:

- domain models structuring
  
- creation of microservice and layered architectures
  
- dependency injection
  
- creation of flexible and replaceble components.

Their use supports the implementation of principles such as the Single Responsibility Principle, Dependency Inversion Principle, and Low Coupling / High Cohesion.


# Tasks 

### Task 1

Look at lab's examples. Modify the code as follows:

- convert multidirectional composition to aggregation

- convert unidirectional aggregation to composition.


### Task 2

Look at the code below. What type of relation is implemented? Refactor the code to implement the other type and create main function to test the logic.

```java
public class Student {

    private String fNumber;
    private String fullName;
    private int course;
    private String specialty;

    public Student(String fNumber, String fullName, int course, String specialty) {
        this.fNumber = fNumber;
        this.fullName = fullName;
        this.course = course;
        this.specialty = specialty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return course == student.course && Objects.equals(fNumber, student.fNumber) && Objects.equals(fullName, student.fullName) && Objects.equals(specialty, student.specialty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fNumber, fullName, course, specialty);
    }

    @Override
    public String toString() {
        return "Student{" +
                "fNumber='" + fNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", course=" + course +
                ", specialty='" + specialty + '\'' +
                '}';
    }
}

public class University {

    private Set<Student> students = new HashSet<>();

    public void addStudent(Student student) {
        students.add(student);
    }

    public String getStudentInfo() {
        StringBuilder info = new StringBuilder();
        for (Student s : students) {
            info.append(s.toString()).append("\n");
        }
        return info.toString();
    }
}
```

### Task 3

Write a program for an online platform (OnlinePlatform). The platform has users (User), each of whom has a user profile (Profile) and can create posts (Post). Users can also comment on existing posts (Comment). What type of relationships between the User-Profile, User-Post and Post-Comment objects will be used and why?
