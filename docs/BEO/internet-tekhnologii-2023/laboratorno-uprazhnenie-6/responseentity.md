---
layout: default
title: ResponseEntity
parent: Лабораторно упражнение 6
grand_parent: Интернет технологии
nav_order: 4
---
# ResponseEntity

ResponseEntity е клас в Spring Framework, който представя пълен HTTP отговор. Съдържа: код на състоянието, хедъри и тяло, в резултат на което можем да го използваме за пълно конфигуриране на HTTP отговора. ResponseEntity е генерик и всеки тип може да бъде използван като тяло на отговора.

```java
@GetMapping("/hello")
public ResponseEntity<String> hello() {

    return new ResponseEntity<>("Hello World!", HttpStatus.OK);
}
```

Този код създава REST endpoint на url /hello. Методът връща ResponseEntity<String> с тяло "Hello World!" и статус код 200 (OK).

Добавяне на HTTP хедъри:

```java
@GetMapping("/customHeader")
public ResponseEntity<String> customHeader() {

    HttpHeaders headers = new HttpHeaders();
    headers.add("Custom-Header", "foo");
        
    return new ResponseEntity<>(
      "Custom header set", headers, HttpStatus.OK);
}
```

Тук се създава endpoint /customHeader. Създаваме обект HttpHeaders и добавяме персонализиран хедър с име Custom-Header и стойност foo. След това връщаме ResponseEntity с тяло "Custom header set", включени хедъри и статус код 200.

Наред с всичко това ResponseEntity предоставя два вложени интерфейса за изграждане: HeadersBuilder и неговия наследник BodyBuilder. Можем да получим достъп до техните възможности чрез статичните методи на ResponseEntity. За най-използваните HTTP статус кодове са предвидени следните статични методи:

```java
BodyBuilder accepted();
BodyBuilder badRequest();
BodyBuilder created(java.net.URI location);
HeadersBuilder<?> noContent();
HeadersBuilder<?> notFound();
BodyBuilder ok();
```

## Примери за използване:

```java
@GetMapping("/hello")
public ResponseEntity<String> hello() {
    return ResponseEntity.ok("Hello World!");
}
```

В този пример използваме статичния метод ResponseEntity.ok(). Той връща отговор със статус код 200 и тяло "Hello World!":

```java
@GetMapping("/customHeader")
public ResponseEntity<String> customHeader() {
    return ResponseEntity.ok()
        .header("Custom-Header", "foo")
        .body("Custom header set");
}
```

Този код създава endpoint /customHeader със статус код 200. Методът .header("Custom-Header", "foo") добавя персонализиран хедър. След това .body("Custom header set") задава тялото на отговора и завършва изграждането на ResponseEntity:

Тъй като _BodyBuilder.body()_ връща _ResponseEntity_ вместо _BodyBuilder,_ трябва да бъде извикан последен.

```java
@PostMapping("/hello")
public ResponseEntity<String> hello(@RequestParam String name) {
    String message = "Hello, " + name + "!";
    return ResponseEntity.status(201).body(message);
}
```

Този endpoint /hello с метод POST връща 201 Created, когато успешно създаваме ресурс. Отговорът връща съобщението, което включва името, подадено като параметър.


```java
@PostMapping("/checkName")
public ResponseEntity<String> checkName(@RequestParam String name) {
    if (name == null || name.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Name cannot be empty");
    }
    return ResponseEntity.ok("Hello " + name + "! Name is valid");
}
```

Endpoint /checkName проверява дали параметърът name е празен. Ако е празен, връща 400 Bad Request, в противен случай – 200 OK с персонализирано съобщение. Методът .body() задава съдържанието на HTTP отговора, което се връща на клиента.