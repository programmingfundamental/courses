---
layout: default
title: Chain of Responsibility
parent: Лабораторно упражнение 10
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# Chain of Responsibility

Моделът на веригата на отговорност се използва за постигане на слабо свързване на обекти в софтуерния дизайн, където заявка от клиент се предава на верига от обекти, за да ги обработи. След това обектът във веригата сам ще реши кой ще обработва заявката и дали искането трябва да бъде изпратено до следващия обект във веригата или не.

Пример за модел на верига на отговорност в JDK може да видим в множество catch блокове в блоков код try-catch. Тук всеки catch блок е един вид процесор за обработка на конкретно изключение. Така че, когато възникне изключение в блока try, то се изпраща до първия блок catch за обработка. Ако catch блокът не може да го обработи, той препраща заявката към следващия обект във веригата, т.е. следващия catch блок. Ако дори последният catch блок не може да го обработи, изключението се хвърля извън веригата към извикващата програма.

Класически пример за използването на шаблона Chain of Responsibility е обработката на заявка, постъпваща на входа на система, като предварително не е известно кой ще я обработи. Посоченото по-долу решение показва базова реализация на подобна задача.

Всяко едно звено от веригата трябва да обработва заявка, която има следната структура:

```
public class Request {

    public enum RequestType { ADMINISTRATIVE, ACCOUNTING, TECHNICAL }

    private String name;
    private RequestType requestType;

    public Request(String name, RequestType requestType) {
        this.name = name;
        this.requestType = requestType;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    @Override
    public String toString() {
        return "Request{" +
                "name='" + name + '\'' +
                ", requestType=" + requestType +
                '}';
    }
}
```

Базовият интерфейс трябва да има метод за определяне на следващото звено от веригата и метод, който ще обработва постъпилата заявка:

```
public interface RequestHandler {

    void setNextHandler(RequestHandler requestHandler);

    String processRequest(Request request);
}
```

Трябва да се създадат различни класове, описващи отделите, обработващи заявките. Тъй като в конкретния случай имаме три типа заявки, ще имаме три конкретни имплементации на дефинирания интерфей:

```
public class AccountingDepartment implements RequestHandler {

    private RequestHandler requestHandler;

    @Override
    public void setNextHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public String processRequest(Request request) {
        if (Request.RequestType.ACCOUNTING == request.getRequestType()) {
            return "Request has been processed by accounting department";
        } else if (Objects.nonNull(requestHandler)) {
            return requestHandler.processRequest(request);
        } else {
            return "No department to process request found!";
        }
    }
}
```

```
public class AdministrativeDepartment implements RequestHandler {

    private RequestHandler requestHandler;

    @Override
    public void setNextHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public String processRequest(Request request) {
        if (Request.RequestType.ADMINISTRATIVE == request.getRequestType()) {
            return "Request has been processed by administrative department";
        } else if (Objects.nonNull(requestHandler)) {
            return requestHandler.processRequest(request);
        } else {
            return "No department to process request found!";
        }
    }
}
```

```
public class TechnicalDepartment implements RequestHandler {

    private RequestHandler requestHandler;

    @Override
    public void setNextHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public String processRequest(Request request) {
        if (Request.RequestType.TECHNICAL == request.getRequestType()) {
            return "Request has been processed by technical department";
        } else if (Objects.nonNull(requestHandler)) {
            return requestHandler.processRequest(request);
        } else {
            return "No department to process request found!";
        }
    }
}
```

Важното, е да се отбележи, че всеки метод за обработка на заявка се опитва да я обработи в зависимост от нейния тип. Ако конкретното звено не може да обработи постъпилата заявака, то изпраща заявката към следващото звено за обработка - при наличието на такова. При достигане на края на веригата без да е намерено подходящо звено, в конкретния пример се връща съобщение за грешка.

Веригата трябва да се създаде внимателно, в противен случай е възможно получаването на циклична зависимост. Класът RequestProcessor създава веригата в своя дефолтен конструктор, а заявката постъпва винаги на входа на първото звено:

```
public class RequestProcessor {

    private AccountingDepartment accountingDepartment = new AccountingDepartment();
    private AdministrativeDepartment administrativeDepartment = new AdministrativeDepartment();
    private TechnicalDepartment technicalDepartment = new TechnicalDepartment();

    public RequestProcessor() {
        // setting the chain
        accountingDepartment.setNextHandler(administrativeDepartment);
        administrativeDepartment.setNextHandler(technicalDepartment);
    }

    public String processRequest(Request request) {
        // request enters first handler of the chain
        return accountingDepartment.processRequest(request);
    }
}
```

Демонстрация на реализирания шаблон е показана в следващия кодов фрагмент:

```
	public class Application {
    public static void main(String[] args) {
        RequestProcessor processor = new RequestProcessor();

        System.out.println(processor.processRequest(new Request("Monthly payment", Request.RequestType.ACCOUNTING)));
        System.out.println(processor.processRequest(new Request("Setting new account", Request.RequestType.TECHNICAL)));
    }
}
```

Заключение:

Клиентът не знае коя част от веригата ще обработва заявката и ще изпрати заявката до първия обект във веригата. 

Всеки обект във веригата ще има собствена реализация за обработка на заявката или за изпращането й до следващия обект във веригата.

Всеки обект във веригата трябва да има препратка към следващия обект във веригата, към който да препрати заявката, постигната чрез java композиция.

Внимателното създаване на веригата е много важно, за да се избегне случай, в който заявката никога няма да бъде препратена към конкретно звено или няма обекти във веригата, които да могат да обработват заявката.

Шаблонът за проектиране на веригата на отговорността е добър за решаване на проблема за загуба на свързване, но идва с компромиса от наличието на много класове за внедряване и проблеми с поддръжката, ако по-голямата част от кода е общ във всички реализации.
