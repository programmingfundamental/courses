---
layout: default
title: ResponseEntity
parent: Лабораторно упражнение 7
grand_parent: Интернет технологии
nav_order: 3
---
# ResponseEntity

Обектът ResponseEntity е Spring wrapper на отговора на заявката. Съдържа целия HTTP отговор: код на състоянието, хедъри и тяло, в резултат на което можем да го използваме за пълно конфигуриране на HTTP отговора. ResponseEntity е генерик и всеки тип може да бъде използван в качеството на тяло на отговора.

```java
@GetMapping("/hello")
ResponseEntity<String> hello() {

    return new ResponseEntity<>("Hello World!", HttpStatus.OK);
}

```

Добавяне на HTTP хедъри:

```java
@GetMapping("/customHeader")
ResponseEntity<String> customHeader() {

    HttpHeaders headers = new HttpHeaders();
    headers.add("Custom-Header", "foo");
        
    return new ResponseEntity<>(
      "Custom header set", headers, HttpStatus.OK);
}

```

Наред с това ResponseEntity предоставя два вложени интерфейса за изграждане: HeadersBuilder и неговия наследник BodyBuilder. Можем да получим достъп до техните възможности чрез статичните методи на ResponseEntity. За най-използваните HTTP статус кодове са предвидени следните статични методи:

```java
BodyBuilder accepted();
BodyBuilder badRequest();
BodyBuilder created(java.net.URI location);
HeadersBuilder<?> noContent();
HeadersBuilder<?> notFound();
BodyBuilder ok();
```

Опростен пример за отговор с тяло и HTTP статус код 200:

```java
@GetMapping("/hello")
ResponseEntity<String> hello() {
    return ResponseEntity.ok("Hello World!");
}
```

Пример с добавяне на потребителски хедъри:

```java
@GetMapping("/customHeader")
ResponseEntity<String> customHeader() {
    return ResponseEntity.ok()
        .header("Custom-Header", "foo")
        .body("Custom header set");
}
```

Тъй като _BodyBuilder.body()_ връща _ResponseEntity_ вместо _BodyBuilder,_ трябва да бъде извикан последен.
