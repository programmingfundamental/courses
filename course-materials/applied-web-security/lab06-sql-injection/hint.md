# Упражнение 6 — Безопасно търсене на задачи със Spring Data JPA — решения и насоки


## Решение на примерния проблем

TaskRepository добавя:

```java
@org.springframework.data.jpa.repository.Query("""
    select t from Task t where (:admin = true or t.owner.username = :username)
    and t.summary like concat('%', :q, '%') order by t.id
    """)
java.util.List<Task> search(@org.springframework.data.repository.query.Param("q") String q,
    @org.springframework.data.repository.query.Param("username") String username,
    @org.springframework.data.repository.query.Param("admin") boolean admin);
```

Service валидира q, извиква repository.search(q,policy.currentUser(),policy.isAdmin()) и преобразува към TaskResponseDto. Controller добавя @GetMapping("/search") и @RequestParam(defaultValue="") String q. Не приема username/admin като параметри. При q="' OR '1'='1' --" получаваме само действителни текстови съвпадения, обичайно празен списък; апострофът не променя структурата.

## Решение на самостоятелна задача 1

Втора JPQL заявка заменя LIKE с `t.summary = :summary` и запазва owner/admin предиката. Service отказва null, >255 и NUL с 400. Empty summary дава List.of(). Controller е @GetMapping("/lookup") с @RequestParam String summary. Два различни users с еднакво summary получават всеки своя ID; ADMIN получава и двата. %/_ са буквални при =. Ръчна POST заявка в Postman създава данните през API, след което GET доказва isolation. HTTP 200 без проверка на owners/IDs не е достатъчен.

## Решение на самостоятелна задача 2

Премахваме фиксираното ORDER BY от search @Query и добавяме параметър org.springframework.data.domain.Sort sort към repository метода. В service избираме:

```java
String field = switch (sort) {
    case "summary" -> "summary";
    case "deadline" -> "deadline";
    default -> throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST);
};
var ordering = org.springframework.data.domain.Sort.by(field).ascending().and(
    org.springframework.data.domain.Sort.by("id"));
```

Подаваме ordering като последния repository параметър. sort="summary desc;..." се отказва с 400; разрешените две полета дават предвидима подредба с id при равенство и не променят owner филтъра. Използваме фиксирани бъдещи дати за примерните данни и сравняваме ръчно последователността на ID от Postman с работната PostgreSQL база.


## Въпроси за анализ

1. Защо @Query с параметри пази структурата?
2. Защо LIKE wildcard не означава SQL injection?
3. Защо sort не се предава като произволен SQL текст?

## Checklist

- [ ] Примерният проблем има работеща реализация в Task Manager.
- [ ] Самостоятелните задачи имат код/анализ и проверими резултати.
- [ ] Ръчните проверки включват разрешен и отказан сценарий.
- [ ] Отказаната операция не променя DB.
- [ ] Ръчните API проверки с Postman, DB наблюденията и браузърните проверки са разграничени.
- [ ] Отчетът не съдържа пароли, raw tokens или поверителни бележки.
