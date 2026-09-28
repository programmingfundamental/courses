# Упражнение 5 — SQL Injection — насоки

## Решение на примерния проблем

В Documents.search премахваме клона с конкатенация. Запазваме проверката за null, над 100 символа и NUL. Единствената заявка е:

```java
return db.query(
    "SELECT id,owner,title FROM documents WHERE owner=? AND title LIKE ? ORDER BY id",
    (r,n) -> new Document(r.getLong(1), r.getString(2), r.getString(3)),
    owner, "%" + q + "%");
```

owner идва от user.getName() в Web. O'Reilly остава текст; `' OR '1'='1' -- ` не променя SQL структурата. Празно q връща само собствените документи. % и _ запазват LIKE семантиката, без да премахват owner условието. Изпълняваме `WebSecurityTest#lab05*` при lab05 и PostgresIT чрез security-tests.

## Решение на самостоятелна задача 1 — Точно заглавие

В Documents добавяме:

```java
public List<Document> lookup(String title, String owner) {
    if (title == null || title.length() > 200 || title.indexOf('\0') >= 0) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
    return db.query(
        "SELECT id,owner,title FROM documents WHERE owner=? AND title=? ORDER BY id",
        (r,n) -> new Document(r.getLong(1), r.getString(2), r.getString(3)), owner, title);
}
```

В Web добавяме:

```java
@GetMapping("/api/lookup")
List<Documents.Document> lookup(@RequestParam String title, Authentication user) {
    return documents.lookup(title, user.getName());
}
```

Празно заглавие е допустим вход с празен резултат при началните данни; липсващ параметър → 400. Използваме равенство вместо LIKE.

| Вход като alice | Очакване |
|---|---|
| Alice notes | Един резултат, owner=alice |
| O'Reilly guide | Един резултат, без SQL грешка |
| notes или празен текст | Нула резултати |
| 201 символа или NUL | 400 |
| ' OR '1'='1' -- | Нула резултати |
| % или _ | Буквално съвпадение, без wildcard поведение |
| Еднакво заглавие за alice и bob | Всяка сесия получава само собствения ред |

Фрагмент за нов тест в WebSecurityTest:

```java
db.update("INSERT INTO documents VALUES (?,?,?)", 101L, "alice", "Shared title");
db.update("INSERT INTO documents VALUES (?,?,?)", 102L, "bob", "Shared title");
try {
    mvc.perform(get("/api/lookup").with(user("alice")).param("title", "Shared title"))
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(101))
        .andExpect(jsonPath("$[0].owner").value("alice"));
    mvc.perform(get("/api/lookup").with(user("bob")).param("title", "Shared title"))
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(102));
} finally {
    db.update("DELETE FROM documents WHERE id IN (101,102)");
}
```

Първо интегрираме заявката от UnsafeLookup.java.txt и показваме провален отрицателен тест; заменяме я с binding и повтаряме същия тест. Проверяваме и anonymous → 401, както и липса на SQL/stack trace в error response.

## Решение на самостоятелна задача 2 — Гранични случаи

- **%/_:** search позволява шаблони, но всеки върнат owner е текущият потребител; lookup третира символите буквално заради =.
- **Апостроф и кирилица:** записваме „Бележки O'Reilly“; lookup връща точно този запис без грешка.
- **Динамично сортиране:** `Map.of("title", "title", "id", "id")` избира фиксиран SQL идентификатор; неизвестна стойност → 400. Binding остава за данните. Основният lookup използва фиксирано ORDER BY id.
- **H2/PostgreSQL:** повтаряме матрицата чрез PostgresIT с реален PostgreSQL и сравняваме съдържание/owners, а не само status.

## Въпроси за анализ

1. Защо validation не заменя binding?
2. Какво е wrong при concatenated JPA native query?
3. Защо injection може да е опасен без DELETE?
4. Как се поддържа dynamic ORDER BY?
5. Защо O'Reilly е важен тест?
6. Защо PostgreSQL test е отделен?

## Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
