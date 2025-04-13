---
layout: default
title: ResponseEntity
parent: Laboratory excercise 7
grand_parent: Internet Technologies
nav_order: 3
---


# ResponseEntity

The ResponseEntity object is a Spring wrapper around the request response. It contains the entire HTTP response: status code, headers, and body, so we can use it to fully configure the HTTP response. ResponseEntity is generic and any type can be used as the response body.

```java
@GetMapping("/hello")
ResponseEntity<String> hello() {

    return new ResponseEntity<>("Hello World!", HttpStatus.OK);
}

```

Adding HTTP headers:

```java
@GetMapping("/customHeader")
ResponseEntity<String> customHeader() {

    HttpHeaders headers = new HttpHeaders();
    headers.add("Custom-Header", "foo");
        
    return new ResponseEntity<>(
      "Custom header set", headers, HttpStatus.OK);
}

```

In addition, ResponseEntity provides two nested building interfaces: HeadersBuilder and its descendant BodyBuilder. We can access their capabilities through the static methods of ResponseEntity. The following static methods are provided for the most commonly used HTTP status codes:

```java
BodyBuilder accepted();
BodyBuilder badRequest();
BodyBuilder created(java.net.URI location);
HeadersBuilder<?> noContent();
HeadersBuilder<?> notFound();
BodyBuilder ok();
```

A simple example of a response with a body and HTTP status code 200:

```java
@GetMapping("/hello")
ResponseEntity<String> hello() {
    return ResponseEntity.ok("Hello World!");
}
```

Example of adding custom headers:

```java
@GetMapping("/customHeader")
ResponseEntity<String> customHeader() {
    return ResponseEntity.ok()
        .header("Custom-Header", "foo")
        .body("Custom header set");
}
```

Since _BodyBuilder.body()_ returns _ResponseEntity_ instead of _BodyBuilder,_ it should be called last.
