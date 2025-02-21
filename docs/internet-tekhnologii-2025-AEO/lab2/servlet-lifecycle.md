---
layout: default
title: Servlet lifecycle
parent: Laboratory excercise 2
grand_parent: Internet Technologies
nav_order: 9
---


# Servlet Lifecycle

The life cycle of a servlet can be defined as the time from its creation to its removal from memory. The stages that a servlet goes through are as follows:

* the servlet is initialized by calling its init() method;
* the servlet calls its service() method to service client requests;
* the servlet is deactivated by calling its destroy() method from the container;
* the servlet is removed from memory by the Java Virtual Machine (JVM) garbage collector.

## The `init()` method

The method is designed to be called only once. It is called when the servlet is created and is not called again. It is used for initial initialization. The servlet is usually created when the client first accesses the URL it corresponds to, but it can also be created when the server starts. When a client accesses the servlet, a single instance of it is created, and each client request is handled in a separate thread. The init() method also creates or loads various auxiliary data necessary for the servlet to operate. The method definition is as follows:

```
public void init() throws ServletException{
    // initialization code...
}
```

## The `service()` method

This is the main method of the servlet, which does the real work. The servlet container calls this method to service client requests and generate a formatted response for the client. Each time the server receives a request for a servlet, it creates a new thread and calls service(). The method checks the type of request (GET, POST, PUT, DELETE, etc.) and calls the appropriate method implemented in the servlet.

```
public void service(ServletRequest request, ServletResponse response)
throws ServletException, IOException {
    …
}
```

The doGet() and doPost() methods are the most commonly used methods in any client request.

### The doGet() method

The method processes an incoming GET request from the client, which is the result of a normal call to the servlet URL or from an HTML form that does not specify `method`. A GET request transmits data to the server using `Query string` URL parameters.

Query string parameters are listed after the URL, starting with `?`. They consist of a key and a value, and when listing multiple parameters, they are separated by the `&` symbol.

`http://example.com?key1=value1&key2=value2`

```
public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    // servlet code
}
```

### The doPost() method

The method processes an incoming POST request from the client, which is the result of an HTML form that specifically specifies `method="POST"`. The POST request transmits data to the server via the HTTP packet body, which does not modify the URL and allows for the transmission of larger amounts of data.

POST request parameters consist of a key and a value, which are grouped in the body of the HTTP packet.

```
public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // servlet code
}
```

## The destroy() method

The method is called only once during the servlet's life cycle. This method allows the servlet to perform some finalization operations such as closing a database connection, writing a cookie, or some cleanup operation. After its call, the servlet is marked for cleanup.

```
public void destroy(){
    // finalizing code...
}
```
