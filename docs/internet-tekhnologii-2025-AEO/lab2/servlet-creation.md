---
layout: default
title: Creating servlets
parent: Laboratory excercise 2
grand_parent: Internet Technologies
nav_order: 8
---


# Creating servlets

Servlets are Java programs that serve HTTP requests and implement the jakarta.servlet.Servlet interface. Web application developers create servlets by extending the jakarta.servlet.http.HttpServlet class - an abstract class that implements the Servlet interface and is specifically designed to serve HTTP requests.

```
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/HelloJava")
public class HelloJava extends HttpServlet {
    
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse
response) throws ServletException, IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.println("<h1> Hello Java </h1>");
}
```

## Installing a servlet

According to the standard for structure and placement, servlets are placed in the `\WEB-INF\classes` directory structure. To be accessible to client requests, the servlet must be declared in the context of the application. This is done in two ways - by describing it in the `web.xml` application descriptor or by using the `@WebServlet` annotation.

### Declaration via web.xml

```
<servlet>
    <servlet-name>HelloJava</servlet-name>
    <servlet-class>HelloJava</servlet-class>
</servlet>

<servlet-mapping>
    <servlet-name>HelloJava</servlet-name>
    <url-pattern>/HelloJava</url-pattern>
</servlet-mapping>
```

The idea behind the descriptor is to create a relationship between the servlet class and the URL it will respond to. This is done in two steps:

* The first is to assign an alias to the servlet for a specific class
* The second is to bind this alias to a specific URL

### Declaration using the `@WebServlet` annotation

```
@WebServlet("/HelloJava")
public class HelloJava extends HttpServlet {
    ...
}
```

Here the servlet "HelloJava" is associated with the address /HelloJava. The names of the servlets and the addresses can be, and most often are, completely different.

#### Servlet declaration with more than one URL

```
@WebServlet(urlPatterns = {"/sendFile", "/uploadFile"})
public class UploadServlet extends HttpServlet {
    ...
}
```

#### Servlet declaration with additional information

```
@WebServlet(
    name = "MyServlet",
    description = "This is my first annotated servlet",
    urlPatterns = "/processServlet"
)
public class MyServlet extends HttpServlet {
...
}
```

#### Servlet declaration with initialization parameters

```
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
