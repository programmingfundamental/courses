# Прихващане на грешки

Когато сървлет генерира грешка, уеб контейнера търси в конфигурационния файл (web.xml) декларацията на този тип грешка. Необходимо е да има деклариран error-page елемент, който да окаже кой сървлет да бъде извикан в отговор на настъпила грешка.

Например, ако при различните видове грешки се изисква извикването на един и същи сървлет, това се постига със следната web.xml конфигурация:

```
<!-- servlet definition -->
<servlet>
    <servlet-name>ErrorHandler</servlet-name>
    <servlet-class>ErrorHandler</servlet-class>
</servlet>
<!-- servlet mappings -->
<servlet-mapping>
    <servlet-name>ErrorHandler</servlet-name>
    <url-pattern>/ErrorHandler</url-pattern>
</servlet-mapping>
<!-- error-code related error pages -->
<error-page>
    <error-code>404</error-code>
    <location>/ErrorHandler</location>
</error-page>
<error-page>
    <error-code>403</error-code>
    <location>/ErrorHandler</location>
</error-page>
<!-- exception-type related error pages -->
<error-page>
    <exception-type>
        javax.servlet.ServletException
    </exception-type>
    <location>/ErrorHandler</location>
</error-page>
<error-page>
    <exception-type>java.io.IOException</exception-type>
    <location>/ErrorHandler</location>
</error-page>
```

Ако се изисква по общо решение с не толкова подробно описание, то може да се използва следната декларация:

```
<error-page>
    <exception-type>java.lang.Throwable</exception-type>
    <location>/ErrorHandler</location>
</error-page>
```

| Атрибути на заявката                 | значение                                          | Тип данни             |
| ------------------------------------ | ------------------------------------------------- | --------------------- |
| `javax.servlet.error.status_code`    | дава статус код                                   | `java.lang.Integer`;  |
| `javax.servlet.error.exception_type` | дава информация за вид изключение                 | `java.lang.Class`;    |
| `javax.servlet.error.message`        | дава информация за точното съобщение за грешка    | `java.lang.String`    |
| `javax.servlet.error.request_uri`    | дава информация за URL-то, което извиква сървлета | `java.lang.String`    |
| `javax.servlet.error.exception`      | дава информация за полученото изключение          | `java.lang.Throwable` |
| `javax.servlet.error.servlet_name`   | дава името на сървлет                             | `java.lang.String`    |
