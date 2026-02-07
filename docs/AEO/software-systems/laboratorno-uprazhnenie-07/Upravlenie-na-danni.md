---
layout: default
title: Data Management
parent: Laboratory Exercise 7
grand-parent: Software Systems
nav_order: 1
permalink: /docs/BEO/software-systems/laboratorno-uprazhnenie-07/Upravlenie-na-danni
---

# Управление на данни с JavaFX

Графичните приложения, изградени с JavaFX, често се нуждаят от съхранение и извличане на информация – потребители, настройки, резултати или други структурирани данни. Тъй като JavaFX се фокусира единствено върху изграждането на потребителския интерфейс, за работата с бази данни се използват стандартните механизми на Java и външни технологии.

За тази цел най-често се комбинират JDBC като универсален интерфейс за достъп до бази данни и конкретна система за управление на бази данни, например H2. Тази комбинация позволява ясно разделение между визуалната част на приложението и логиката за работа с данни.

<img width="560" height="382" alt="image" src="https://github.com/user-attachments/assets/102fdbfd-ab5e-46d3-b5bf-94b4f30b6f5b" />

## JDBC слой – връзка между Java и базата данни

JDBC (Java Database Connectivity) представлява стандартен интерфейс за работа с релационни бази данни в Java. Той дефинира начин за създаване на връзка, изпълнение на SQL заявки и обработка на резултатите, независимо от конкретната база данни.

Чрез JDBC приложението не комуникира директно с базата, а използва драйвер, който превежда заявките към конкретната система за управление на бази данни. Това позволява смяна на базата без промяна на бизнес логиката.

Основните обекти, с които работи JDBC, са:
- Connection – активна връзка към базата данни
- Statement / PreparedStatement – изпълнение на SQL заявки
- ResultSet – резултат от SELECT заявка

## H2 – вградена релационна база данни

За настолни Java приложения често се използват леки вградени бази данни. H2 е такава база, реализирана изцяло на Java, която се използва чрез JDBC и поддържа стандартен SQL синтаксис.

H2 може да работи във файл, което означава, че данните се съхраняват локално и се запазват между стартиранията на приложението. Това я прави подходяща за JavaFX приложения без нужда от външен сървър.

За да бъде използвана в Maven проект, е необходимо да се добави зависимостта към H2 JDBC драйвера.

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.4.240</version>
</dependency>
```

## Създаване на връзка към базата данни

Връзката към базата се реализира в отделен клас, който централизира конфигурацията и позволява повторна употреба:

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:h2:./data/librarydb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```

Тук се задават:
- URL – пътя до H2 база в локален файл (./data/librarydb)
- USER и PASSWORD – стандартни стойности за H2
- Методът getConnection() връща готова връзка за използване от DAO или други класове.

## Примерна задача: Каталог с книги

За демонстрация на управлението на данни да се създаде каталог с книги. Всеки запис да съдържа заглавие, автор, година на издаване и ISBN номер. Данните да се извличат от базата и да се визуализират с JavaFX интерфейс.

### SQL структура на базата данни

```sql
CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    year INT,
    isbn VARCHAR(20)
);

```

## Entity слой – клас Book

Всеки ред от таблицата books се представя в Java чрез entity клас. Този клас не съдържа логика за достъп до база данни и служи само за пренос и представяне на данните.

```java
package bg.tu_varna.sit.ps.lab7;

public class Book {

    private int id;
    private String title;
    private String author;
    private int year;
    private String isbn;

    public Book(int id, String title, String author, int year, String isbn) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.isbn = isbn;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }
    public String getIsbn() { return isbn; }
}

```

## Database Initializer

За да не се налага ръчно изпълнение на SQL скриптове, се създава клас DatabaseInitializer. Той създава таблица books и вкарва записи.


```java
package bg.tu_varna.sit.ps.lab7;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Създаваме таблицата books, ако не съществува
            stmt.execute("CREATE TABLE IF NOT EXISTS books (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "author VARCHAR(255) NOT NULL, " +
                    "year INT, " +
                    "isbn VARCHAR(20))");

            // Вкарваме няколко примерни книги
            stmt.execute("INSERT INTO books (title, author, year, isbn) VALUES " +
                    "('Clean Code', 'Robert C. Martin', 2008, '9780132350884')," +
                    "('Effective Java', 'Joshua Bloch', 2018, '9780134685991')," +
                    "('Design Patterns', 'Gamma et al.', 1994, '9780201633610')");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

Какво се прави тук:
- Създава се връзка чрез DatabaseConnection.getConnection(). Това гарантира, че всички SQL заявки ще се изпълнят в правилната база.
- Създава се Statement, който позволява изпълнение на SQL код.
- Създава се таблица само ако не съществува (IF NOT EXISTS), за да не се презаписват вече съществуващи данни.
- Вкарват се начални записи чрез INSERT, което позволява веднага да се визуализират примерни данни в JavaFX приложението.


## DAO слой – достъп до данните

DAO класът съдържа SQL заявките и отговаря за извличането на данни от базата. Той връща списък от entity обекти, които могат директно да бъдат използвани от JavaFX слоя.

```java
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDao {

    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("year"),
                        rs.getString("isbn")
                );
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }
}
```

## Визуализация с JavaFX

След като данните се извлекат от DAO, те се визуализират в графичния интерфейс чрез JavaFX TableView. Това позволява потребителят да вижда записите подредени в таблица. В зависимост от нуждите на приложението, могат да се използват и други контроли за визуализация на данните, като ListView, ComboBox или различни панели с текстови полета и етикети.

```java
public class LibraryApplication extends Application {

    @Override
    public void start(Stage stage) {
        DatabaseInitializer.initialize(); // Създава се база и се вкарват примерни данни

        BookDao dao = new BookDao();
        List<Book> books = dao.findAll();

        TableView<Book> tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList(books));

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, Integer> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        tableView.getColumns().addAll(titleCol, authorCol, yearCol, isbnCol);

        VBox layout = new VBox(10, tableView);
        layout.setPadding(new javafx.geometry.Insets(20));

        Scene scene = new Scene(layout, 600, 400);
        stage.setTitle("Library Catalog");
        stage.setScene(scene);
        stage.show();
    }
```

При успешно стартиране, трябва да се визуализира следният прозорец: 

<img width="652" height="469" alt="Screenshot 2026-02-03 223238" src="https://github.com/user-attachments/assets/fbb76196-a774-46b4-a8ef-c251e2c87088" />


