---
layout: default
title: SRP - Single Responsibility Principle
parent: Лабораторно упражнение 2
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 1
---

# SRP - Single Responsibility Principle

## **Принцип за единствена отговорност**

Принципът за единствена отговорност гласи, че един клас трябва да има една основна отговорност и една основна причина за промяна.

Това не означава, че класът трябва да има само един метод. Означава, че всички негови методи трябва да бъдат свързани с една ясно определена задача.

### Проблем

Следният пример показва клас Book, който освен че съхранява информация за книга, съдържа и метод за извеждане на текста ѝ. Това води до нарушаване на принципа SRP, тъй като класът има повече от една отговорност.

```java
public class Book {

    private String title;
    private String author;
    private String text;

    public Book(String title, String author, String text) {
        this.title = title;
        this.author = author;
        this.text = text;
    }

    public boolean containsWord(String word) {
        return text.contains(word);
    }

    public String replaceWord(String oldWord, String newWord) {
        return text.replace(oldWord, newWord);
    }

    public void printTextToConsole() {
        System.out.println(text);
    }
}
```

На пръв поглед класът изглежда коректен, но той има повече от една отговорност:
* съхранява информация за книга;
* обработва текста;
* извежда текста на конзолата.

Ако по-късно се наложи текстът да се извежда във файл, в уеб страница или в друг формат, класът Book ще трябва да бъде променян. Това нарушава SRP.

### Решение

Извеждането на текста може да бъде отделено в самостоятелен клас.

```java
public class Book {

    private String title;
    private String author;
    private String text;

    public Book(String title, String author, String text) {
        this.title = title;
        this.author = author;
        this.text = text;
    }

    public boolean containsWord(String word) {
        return text.contains(word);
    }

    public String replaceWord(String oldWord, String newWord) {
        return text.replace(oldWord, newWord);
    }

    public String getText() {
        return text;
    }
}
```

```java
public class BookPrinter {

    public void printToConsole(Book book) {
        System.out.println(book.getText());
    }
}
```

В тази реализация класът *Book* отговаря за информацията и поведението, свързани с книгата, а класът *BookPrinter* отговаря за извеждането.

### Предимства

Прилагането на SRP води до:
* по-ясна структура на класовете;
* по-лесно тестване;
* по-малко зависимости;
* по-лесна поддръжка;
* по-малък риск промяна в една функционалност да засегне друга.

### Недостатъци / трудности

При неправилно прилагане SRP може да доведе до прекалено много малки класове. Затова е важно отговорностите да се разделят логически, а не механично.

### Приложение

SRP се използва при:
* разделяне на бизнес логика от вход/изход;
* отделяне на валидирането от съхранението на данни;
* създаване на service, repository и utility класове;
* рефакториране на големи класове.
