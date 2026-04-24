---
layout: default
title: Data Management
parent: Laboratory Exercise 7
grand-parent: Software Systems
nav_order: 1
#permalink: /docs/BEO/software-systems/laboratorno-uprazhnenie-07/Upravlenie-na-danni
---

# Data management with JavaFX

Graphic apps, created with JavaFX, often need storage and extraciont of information – user, settings, results as well as other structured data. As JavaFX is focused on the GUI creation only, standard Java techniques and external technologies for work with data bases are used.

Most often the standard database interface JDBC is used together with some database management system, for example H2. Such a combination allows clear separation of the user interface of the app and the database logic.

<img width="560" height="382" alt="image" src="https://github.com/user-attachments/assets/102fdbfd-ab5e-46d3-b5bf-94b4f30b6f5b" />

## JDBC layer – connection between Java and the data base

JDBC (Java Database Connectivity) is a standard programming interface for work with relational data bases in Java. It defines creation of a connection, execution of SQL queries and processing of results – no matter of which specific database management system is used.

Using JDBC the app does not communicate with the database directly. It uses a driver instead, which translates the queries to a specific database management system. Thus, the databse could me changed without modifying the business logic.

The main objects, used by JDBC, are:
- Connection – active connection to the database
- Statement / PreparedStatement – execution of SQL queries
- ResultSet – result from execution of SELECT query

## H2 – integrated relational database

Light integrated relational database management systems are usually used in desktop Java apps. Such a database is H2, which is implemented in Java entirely. H2 is used through JDBC and supports standard SQL syntax.

H2 could store data in file. This means that data are saved locally and are stored between different application runs. Hence, H2 is suitable for JavaFX apps and does need a separate SQL server.


In order to use H2 in a Maven project, the dependency to the JDBC H2 driver must be added.


```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.4.240</version>
</dependency>
```

## Creation of a database connection

The database connection is implemented in a separate class, which centralizes the configuration and allows re-use:

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

The following data are defined here:
- URL – path to H2 base in a local file (./data/librarydb)
- USER and PASSWORD – standard values for H2
- The method getConnection() returns a ready connected, which can be used by DAO or other classes.

## Example task: A catalog with books

The example demonstrates data management by creation a catalog with books. Each record contains title, author, year of publication and ISBN number. The data are extracted from the database and are visualized through JavaFX interface.

### SQL database structure

```sql
CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    year INT,
    isbn VARCHAR(20)
);

```

## Entity layer – class Book

Each row from the table books is presented in Java with entity class. This class does not contain logic for database access and serves only for transmission and presentation of the data.

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

To prevent the necessity of manually executing SQL scripts the class DatabaseInitializer is implemented. It creates a table books and inserts records inside.


```java
package bg.tu_varna.sit.ps.lab7;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Creates table books, if it does not exist
            stmt.execute("CREATE TABLE IF NOT EXISTS books (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "author VARCHAR(255) NOT NULL, " +
                    "year INT, " +
                    "isbn VARCHAR(20))");

            // Insert records
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

The upper fragment does the following:
- Creates a connection using DatabaseConnection.getConnection(). This guarantees that all SQL queries will be executed in the right database.
- Created Statement, which allows execution of SQL code.
- Created a table, if it does not exist (IF NOT EXISTS), in order to prevent rewriting existing data.
- A few records are inserted with INSERT command, which allows immediate visualization of the data in the JavaFX app.


## DAO layer – data access

The DAO class contains SQL queries and is responsible for extraction of data from the database. It returns a list of entity objects, which could be used from the JavaFX layer directly.

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

## Visualization with JavaFX

After the data are extracted by DAO, they are visualized in the graphical user interface using JavaFX TableView. Thus, users can see data ordered in a table. In accordance with the app specifics other UI controls for data visualization could be used, for example ListView, ComboBox and different panels with text fields and labels.

```java
public class LibraryApplication extends Application {

    @Override
    public void start(Stage stage) {
        DatabaseInitializer.initialize(); // A database is created and sample data are inserted 

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

The following window shoulb be visualized in case of successful execution: 

<img width="652" height="469" alt="Screenshot 2026-02-03 223238" src="https://github.com/user-attachments/assets/fbb76196-a774-46b4-a8ef-c251e2c87088" />



