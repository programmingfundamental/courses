---
layout: default
title: Създаване на сървлети
parent: Лабораторно упражнение 2
grand_parent: Интернет технологии
nav_order: 8
---

# Създаване на сървлети

Сървлетите са Java програми, които обслужват HTTP заявки и имплементират jakarta.servlet.Servlet интерфейса. Разработчиците на уеб приложения създават сървлети като наследяват jakarta.servlet.http.HttpServlet класа - абстрактен клас, който имплементира Servlet интерфейса и е специално проектиран за обслужване на HTTP заявки.

При обработка на заявката контейнерът подава на сървлета два обекта – `HttpServletRequest` и `HttpServletResponse`. Чрез обекта `request` се получават данните, изпратени от клиента, а чрез `response` се генерира отговорът, който ще бъде върнат към браузъра.

```java
@WebServlet("/HelloJava")
public class HelloJava extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        out.println("Hello Java!");
    }
}
```

Методът doGet() се извиква при HTTP GET заявка. Обектът PrintWriter се използва за записване на текст в отговора.

## Получаване на параметри от заявката

Данните, изпратени от клиента, се получават чрез обекта HttpServletRequest. Стойностите на параметрите се извличат чрез метода getParameter().

При GET заявките параметрите се подават като част от URL адреса:

```
/HelloJava?name=Ivan
```

При application/x-www-form-urlencoded данните се обработват автоматично от контейнера и се достъпват чрез request.getParameter(). При application/json, application/html и application/xml съдържанието се намира в тялото на заявката като структуриран текст и трябва да бъде прочетено чрез request.getReader() и допълнително обработено с подходяща библиотека.

```java
@WebServlet("/HelloJava")
public class HelloJava extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

    
        String name = request.getParameter("name");

        PrintWriter out = response.getWriter();
        out.println("Hello, " + name);
    }
}
```

Сървлетът HelloJava обработва GET заявка, при която параметърът name се подава като част от URL адреса. Стойността му се извлича чрез request.getParameter("name") и се използва за генериране на текстов отговор.

## Инсталиране на сървлет

Според стандарта за структура и разположение, сървлетите се поставят в директорийната структура на `\WEB-INF\classes`. За да бъдат достъпен за клиентски заявки, сървлетът трябва да бъде деклариран в контекста на приложението. Това става по два начина - чрез описание в дескриптора на приложението `web.xml` или чрез анотацията `@WebServlet`.

### Декларация посредством web.xml

```xml
<servlet>
    <servlet-name>HelloJava</servlet-name>
    <servlet-class>HelloJava</servlet-class>
</servlet>

<servlet-mapping>
    <servlet-name>HelloJava</servlet-name>
    <url-pattern>/HelloJava</url-pattern>
</servlet-mapping>
```

Идеята на дескриптора е създаване на връзка между класа на сървлета и URL адреса, на който той ще отговаря. Това става на две стъпки:

* Първата е задаване на псевдоним на сървлета за конкретен клас
* Втората е обвързване на този псевдоним с определен URL

### Декларация посредством анотацията `@WebServlet`

```java
@WebServlet("/HelloJava")
public class HelloJava extends HttpServlet {
    ...
}
```

Тук сървлета „HelloJava“ е свързан с адреса /HelloJava. Имената на сървлетите и адресите могат да бъдат и най-често са напълно различни.

#### Декларация на сървлет с повече от едно URL

```java
@WebServlet(urlPatterns = {"/sendFile", "/uploadFile"})
public class UploadServlet extends HttpServlet {
    ...
}
```

#### Декларация на сървлет с допълнителна информация

```java
@WebServlet(
    name = "MyServlet",
    description = "This is my first annotated servlet",
    urlPatterns = "/processServlet"
)
public class MyServlet extends HttpServlet {
...
}
```

#### Декларация на сървлет с инициализиращи параметри

```java
@WebServlet(
    urlPatterns = "/imageUpload",
    initParams =
    {
        @WebInitParam(name = "saveDir", value = "D:/FileUpload"),
        @WebInitParam(name = "allowedTypes", value = "jpg,jpeg,gif,png")
    }
)
public class ImageUploadServlet extends HttpServlet {
    ...
}
```



