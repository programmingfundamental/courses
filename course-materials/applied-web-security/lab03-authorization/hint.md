# Упражнение 3 — Authorization и Broken Access Control — насоки

## Решение на примерния проблем

В Documents.get премахваме `!mode.vulnerable(3)` от условието. След зареждането на d винаги изпълняваме:

```java
boolean admin = user.getAuthorities().stream()
    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
if (!admin && !d.owner().equals(user.getName())) {
    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
}
return d;
```

@PreAuthorize остава на service метода. Очаквания: alice → документ 1: 200; bob → документ 1: 404; admin → документ 1: 200; липсващ документ: 404; без вход: 401. Проверяваме с `WebSecurityTest#lab03*` при lab03.

## Решение на самостоятелна задача 1 — Редактиране на заглавие

В Documents проверката и промяната са в една SQL операция, за да няма интервал между тях:

```java
@PreAuthorize("isAuthenticated()")
public void rename(long id, String title, Authentication user) {
    if (title == null || title.isBlank() || title.length() > 200 || title.indexOf('\0') >= 0) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
    boolean admin = user.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    int changed = admin
        ? db.update("UPDATE documents SET title=? WHERE id=?", title, id)
        : db.update("UPDATE documents SET title=? WHERE id=? AND owner=?",
            title, id, user.getName());
    if (changed != 1) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
}
```

В Web добавяме:

```java
public record TitleChange(String title) {}

@PatchMapping("/api/documents/{id}/title")
@ResponseStatus(HttpStatus.NO_CONTENT)
void rename(@PathVariable long id, @RequestBody TitleChange change, Authentication user) {
    documents.rename(id, change.title(), user);
}
```

DTO не съдържа owner и UPDATE не го променя. Read и write имат еднаква политика: USER само собствен документ, ADMIN всеки.

| Заявка с валиден CSRF | Резултат | DB |
|---|---|---|
| Без вход, id=1 | 401 | Без промяна |
| alice, id=1 | 204 | Ново заглавие |
| bob, id=1 | 404 | Без промяна |
| admin, id=1 | 204 | Ново заглавие |
| alice, id=999 | 404 | Няма нов ред |
| alice, празно/201 символа | 400 | Без промяна |

Отрицателен тестов фрагмент в WebSecurityTest:

```java
String before = db.queryForObject("SELECT title FROM documents WHERE id=1", String.class);
mvc.perform(patch("/api/documents/1/title").with(user("bob")).with(csrf())
    .contentType("application/json").content("{\"title\":\"Changed\",\"owner\":\"bob\"}"))
    .andExpect(status().isNotFound());
assertThat(db.queryForObject("SELECT title FROM documents WHERE id=1", String.class))
    .isEqualTo(before);
assertThat(db.queryForObject("SELECT owner FROM documents WHERE id=1", String.class))
    .isEqualTo("alice");
```

Положителните тестове възстановяват заглавието или използват rollback.

## Решение на самостоятелна задача 2 — Гранични случаи

- **Невалиден ID:** текст и число извън long → 400; отрицателен ID без запис → 404; DB остава непроменена.
- **Чужд/липсващ документ:** еднакъв 404 без owner или заглавие.
- **Self-invocation:** Web извиква инжектирания Documents bean, а не this.rename. SQL политиката остава необходима независимо от method-security анотацията.
- **Промяна на owner:** owner=? е в самия UPDATE. Тест променя owner на bob преди UPDATE като alice: очаква 404 и старото заглавие. За няколко свързани операции използваме транзакция със заключване или версия на реда.

## Въпроси за анализ

1. Защо UUID не поправя IDOR?
2. Каква е разликата horizontal/vertical?
3. Защо service policy е полезна?
4. Защо 404 при чужд документ?
5. Защо да проверим DB след отказ?
6. Какъв е рискът от owner в request body?

## Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
