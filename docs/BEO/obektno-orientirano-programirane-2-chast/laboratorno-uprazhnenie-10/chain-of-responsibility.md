---
layout: default
title: Chain of Responsibility
parent: Лабораторно упражнение 10
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# Chain of Responsibility

### Проблем

В практиката често постъпват заявки, които могат да бъдат обработени от различни обекти, като предварително не е известно кой точно трябва да ги обработи.

Например в информационна система могат да постъпват административни, счетоводни или технически заявки. Всеки отдел обработва само заявките, които попадат в неговата компетентност. Ако даден отдел не може да обработи заявката, тя трябва автоматично да бъде предадена към следващото звено.

### Решение

Chain of Responsibility организира обработващите обекти във верига. Всеки обработващ обект проверява дали може да обработи заявката. Ако не може, я предава на следващия обект във веригата.

По този начин клиентският код не знае кой конкретен обект ще обработи заявката.

### Дефиниция

Chain of Responsibility е поведенчески шаблон за проектиране, който предава заявка последователно през верига от обработващи обекти, докато някой от тях я обработи или веригата приключи.

Основните участници са:
* Handler - Интерфейс или абстрактен клас, който дефинира обработката на заявката и връзката към следващото звено.
* Concrete Handler - Конкретно звено от веригата, което обработва определен тип заявки.
* Client - Създава веригата и подава заявката към първото звено.

### UML диаграма

<img width="846" height="375" alt="ChainOfResponsibility" src="https://github.com/user-attachments/assets/ff686737-df04-4b18-9339-ba11d9ca729b" />

### Примерна реализация

Заявка

```java
public class Request {

    public enum RequestType {
        ADMINISTRATIVE,
        ACCOUNTING,
        TECHNICAL
    }

    private String name;
    private RequestType requestType;

    public Request(String name, RequestType requestType) {
        this.name = name;
        this.requestType = requestType;
    }

    public RequestType getRequestType() {
        return requestType;
    }
}
```

Handler
```java
public interface RequestHandler {

    void setNextHandler(RequestHandler handler);

    String processRequest(Request request);

}
```

Concrete Handlers
```java
public class AccountingDepartment implements RequestHandler {

    private RequestHandler nextHandler;

    @Override
    public void setNextHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public String processRequest(Request request) {
        if (Request.RequestType.ACCOUNTING == request.getRequestType()) {
            return "Request has been processed by accounting department";
        } else if (Objects.nonNull(nextHandler)) {
            return nextHandler.processRequest(request);
        } else {
            return "No department to process request found!";
        }
    }
}
```
```java
public class AdministrativeDepartment implements RequestHandler {

    private RequestHandler nextHandler;

    @Override
    public void setNextHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public String processRequest(Request request) {
        if (Request.RequestType.ADMINISTRATIVE == request.getRequestType()) {
            return "Request has been processed by accounting department";
        } else if (Objects.nonNull(nextHandler)) {
            return nextHandler.processRequest(request);
        } else {
            return "No department to process request found!";
        }
    }
}
```
```java
public class TechnicalDepartment implements RequestHandler {

    private RequestHandler nextHandler;

    @Override
    public void setNextHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public String processRequest(Request request) {
        if (Request.RequestType.TECHNICAL == request.getRequestType()) {
            return "Request has been processed by accounting department";
        } else if (Objects.nonNull(nextHandler)) {
            return nextHandler.processRequest(request);
        } else {
            return "No department to process request found!";
        }
    }
}
```

Създаване на веригата
```java
public class RequestProcessor {

    private final RequestHandler firstHandler;

    public RequestProcessor() {

        RequestHandler accounting = new AccountingDepartment();
        RequestHandler administrative = new AdministrativeDepartment();
        RequestHandler technical = new TechnicalDepartment();

        accounting.setNextHandler(administrative);
        administrative.setNextHandler(technical);

        firstHandler = accounting;
    }

    public String processRequest(Request request) {
        return firstHandler.processRequest(request);
    }

}
```

Използване
```java
public class Application {

    public static void main(String[] args) {

        RequestProcessor processor = new RequestProcessor();

        System.out.println(processor.processRequest(new Request("Monthly payment",
                                Request.RequestType.ACCOUNTING)));

        System.out.println(processor.processRequest(new Request("New workstation",
                                Request.RequestType.TECHNICAL)));

    }

}
```

Интерфейсът RequestHandler дефинира общото поведение на всички звена във веригата. Освен метода за обработка на заявката той съдържа и метод за определяне на следващия обработващ обект.

Всеки отдел реализира интерфейса RequestHandler и проверява дали може да обработи постъпилата заявка. Ако може, обработката приключва. В противен случай заявката се предава към следващото звено чрез метода processRequest().

Класът RequestProcessor създава веригата от обработващи обекти и съхранява референция само към първото звено. Клиентският код винаги подава заявката към началото на веригата, без да знае кой конкретен обект ще я обработи.

> [!IMPORTANT]
> Всеки обработващ обект взема самостоятелно решение дали да обработи заявката или да я предаде към следващото
> звено. Клиентът не знае кой обект ще извърши обработката и не е свързан директно с конкретните обработващи класове.


### Предимства

* намалява зависимостите между клиента и обработващите обекти;
* позволява динамично изграждане и промяна на веригата;
* улеснява добавянето на нови обработващи обекти;
* подпомага спазването на принципа Open/Closed;
* разпределя отговорността между множество класове.

### Недостатъци

* обработката може да премине през голяма част от веригата;
* при неправилно изградена верига заявката може да остане необработена;
* голям брой обработващи обекти могат да усложнят архитектурата.

### Приложение

Chain of Responsibility е подходящ когато:
* няколко обекта могат да обработят една и съща заявка;
* предварително не е известно кой ще обработи заявката;
* обработващите обекти трябва лесно да се добавят, премахват или пренареждат;
* се цели намаляване на зависимостите между клиента и конкретните обработващи класове.
