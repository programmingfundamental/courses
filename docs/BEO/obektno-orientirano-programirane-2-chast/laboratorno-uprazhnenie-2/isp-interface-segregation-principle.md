---
layout: default
title: ISP - Interface Segregation Principle
parent: Лабораторно упражнение 2
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 4
---

# ISP - Interface Segregation Principle

## Принцип за разделяне на интерфейсите

Принципът за разделяне на интерфейсите гласи, че един клас не трябва да бъде принуждаван да имплементира методи, които не използва.

По-добре е да съществуват няколко малки и специализирани интерфейса, отколкото един голям общ интерфейс.

### Проблем

Следният интерфейс описва многофункционално устройство.

```java
public interface Machine {

    void print();

    void scan();

    void fax();
}
```
Модерен принтер може да поддържа всички операции:
```java
public class MultiFunctionPrinter implements Machine {

    @Override
    public void print() {
        System.out.println("Printing...");
    }

    @Override
    public void scan() {
        System.out.println("Scanning...");
    }

    @Override
    public void fax() {
        System.out.println("Sending fax...");
    }
}
```
Но обикновен принтер не може да сканира и да изпраща факс:
```java
public class SimplePrinter implements Machine {

    @Override
    public void print() {
        System.out.println("Printing...");
    }

    @Override
    public void scan() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fax() {
        throw new UnsupportedOperationException();
    }
}
```
Това нарушава ISP, защото класът SimplePrinter е принуден да реализира методи, които не са част от неговото реално поведение.

### Решение

Големият интерфейс може да бъде разделен на няколко по-малки интерфейса.

```java
public interface Printable {

    void print();
}
```
```java
public interface Scannable {

    void scan();
}
```
```java
public interface Faxable {

    void fax();
}
```
```java
public class SimplePrinter implements Printable {

    @Override
    public void print() {
        System.out.println("Printing...");
    }
}
```
```java
public class MultiFunctionPrinter implements Printable, Scannable, Faxable {

    @Override
    public void print() {
        System.out.println("Printing...");
    }

    @Override
    public void scan() {
        System.out.println("Scanning...");
    }

    @Override
    public void fax() {
        System.out.println("Sending fax...");
    }
}
```
При тази имплементация всеки клас реализира само интерфейсите, които отговарят на реалното му поведение.

### Предимства

Прилагането на ISP води до:
* по-малки и ясни интерфейси;
* по-малко ненужни зависимости;
* по-лесна поддръжка;
* по-гъвкави реализации.

### Недостатъци / трудности

При прекалено раздробяване могат да се появят твърде много интерфейси, което усложнява структурата на проекта.

### Приложение

ISP се използва при:
* проектиране на API;
* създаване на интерфейси за услуги;
* работа с различни реализации;
* шаблони като Adapter, Facade, Proxy.
