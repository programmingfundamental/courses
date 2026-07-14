---
layout: default
title: Лабораторно упражнение 1
parent: Обектно-ориентирано програмиране - 2 част
has_children: true
nav_order: 1
---

# Лабораторно упражнение 1

### Асоциация. Композиция и агрегация

## Асоциация

Асоциацията представлява връзка между два или повече обекта, при която те взаимодействат помежду си. Тя описва логическа зависимост между обектите, без задължително да определя начина, по който се управлява техният жизнен цикъл.

В UML асоциацията се представя чрез *плътна линия* между участващите класове.

Композицията и агрегацията представляват специални случаи на асоциация, чрез които се моделират връзки от тип „част – цяло“. Те се използват за изграждане на по-сложни системи чрез комбиниране на по-малки обекти.

Основните приложения на композицията и агрегацията са:
* моделиране на реални зависимости между обектите;
* реализиране на модулност и разделяне на отговорностите;
* увеличаване възможността за преизползване на съществуващ код;
* намаляване на зависимостите между обектите (в сравнение с използването на наследяване).


## Композиция

Композицията се използва за създаване на силна връзка между обектите и е подходяща в случаите, когато единия обект ("частта") не може да съществува смислено без другия ("цялото"). При унищожаване на композитния обект, частта също се унищожава.

В UML композицията се обозначава със *запълнен ромб*, разположен откъм страната на обекта-цяло.

Съществуват различни типове композиция според начина на тяхната реализация:

- класическа композиция

При класическата композиция съставните обекти се създават вътре в композитния обект и не могат да бъдат споделяни.

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
    this.address = new Address(); // обектът се създава вътре
  }
        
  // methods
}

```

- композиция с множественост (1-to-many)

При тази разновидност композитният обект притежава множество части, които се създават и управляват единствено от него.

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

- композиция с контрол върху достъпа

При този подход съставните обекти са капсулирани и достъпът до тях се осъществява по контролиран начин. Външният код може да ги разглежда, но не и да ги модифицира директно.

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


## Агрегация

Агрегацията се използва в случаите, когато връзката между обектите е по-слаба и съставният обект може да съществува независимо от обекта, който го използва. Един и същ обект може да участва в множество агрегиращи обекти едновременно и неговият жизнен цикъл не зависи от никой от тях.

В UML агрегацията се обозначава с *незапълнен ромб*, разположен откъм страната на агрегиращия обект.

Аналогично на композицията, според различната реализация съществуват няколко типа агрегация:

- проста агрегация

При простата агрегация съставните обекти се подават отвън и съществуват независимо от агрегиращия обект.


<img width="187" height="185" alt="simpleAggregation" src="https://github.com/user-attachments/assets/943f83f1-acd8-4f40-9500-a7a726a24eac" />



```java

public class Book {

  // properties, constructors, methods

}

public class Library {

  private List<Book> catalog;

  public Library(List<Book> catalog) {
    this.catalog = catalog; // подадени отвън
  }

}

```

- еднопосочна агрегация

При еднопосочната агрегация само единият обект поддържа информация за връзката.

В разглеждания пример класът *Reader* съхранява информация за заеманите книги, докато класът *Book* не знае кои читатели използват дадена книга.


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

- двупосочна агрегация

При двупосочната агрегация и двата обекта поддържат информация за връзката помежду си. При промяна на връзката е необходимо състоянието на двата обекта да бъде синхронизирано.


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

- множествена агрегация чрез посредник

При този подход връзките между обектите се управляват от трети обект (посредник). Самите обекти остават независими един от друг и могат да бъдат използвани и в други контексти.


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


- агрегация чрез асоциативен клас (асоциативна агрегация)

При асоциативната агрегация връзката между два независими обекта се представя чрез отделен клас. Освен самата връзка, този клас може да съхранява и допълнителна информация за нея.

В примера класът *LoanBook* съдържа информация както за книгата и читателя, така и за датата на заемането.


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


## Композиция vs Агрегация

Композицията моделира **силна структурна зависимост**, докато агрегацията описва **логическа принадлежност**, при която съставните обекти могат да съществуват самостоятелно.

Таблицата по-долу обобщава и сравнява двете форми по няколко критерия.

| Критерий             | Композиция | Агрегация |
|----------------------|------------|-----------|
| степен на свързаност | силна      | слаба     |
| жизнен цикъл         | зависим    | независим |
| собственост          | има        | няма      |
| споделяемост         | не         | да        |

Изборът между асоциация, агрегация и композиция зависи от конкретния проблем и начина, по който обектите трябва да взаимодействат. При по-малки приложения това решение обикновено се взема от програмиста, а при по-големи системи често е част от цялостното архитектурно проектиране.

В практиката композицията и агрегацията се използват за:

- структуриране на домейн модели
  
- изграждане на микросървисни и слоести архитектури
  
- dependency injection
  
- създаване на гъвкави и заменяеми компоненти.

Тяхното използване подпомага прилагането на принципи като Single Responsibility Principle, Dependency Inversion Principle и Low Coupling / High Cohesion.

