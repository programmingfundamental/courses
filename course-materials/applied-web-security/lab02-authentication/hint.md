# Упражнение 2 — Authentication със Spring Security — насоки

## Решение на примерния проблем

В Accounts.encode винаги използваме PasswordEncoder и проверяваме байтовото ограничение на BCrypt:

```java
public String encode(String password) {
    if (password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
        throw new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.BAD_REQUEST);
    }
    return encoder.encode(password);
}
```

1. Записът съдържа {bcrypt}, salt и хеш. Старите {noop} записи не се променят автоматично: мигрираме стойността след префикса с encoder, без логване, или изискваме смяна на парола. Стойности над 72 UTF-8 байта изискват смяна. Reset е вариант само ако данните не трябва да се запазят.
2. POST /login с валиден CSRF token → 204 и сесия. /api/me със сесията → username=alice.
3. POST /logout с актуален token → 204 и невалидна сесия. Последваща заявка без сесия → 401.
4. `mvn test '-Dlab.mode=lab02' '-Dtest=WebSecurityTest#lab02*'` проверява storage, login/logout и грешни credentials при същия режим.

## Решение на самостоятелна задача 1 — Account summary

В Web добавяме DTO и endpoint. Правилото за /api/** вече изисква вход:

```java
public record AccountSummary(String username, long documentCount) {}

@GetMapping("/api/account-summary")
AccountSummary accountSummary(Authentication user) {
    Long count = db.queryForObject("SELECT COUNT(*) FROM documents WHERE owner=?",
        Long.class, user.getName());
    return new AccountSummary(user.getName(), count);
}
```

Името идва от SecurityContext чрез Authentication; не четем username от параметър и не връщаме DB entity. Login установява самоличността, а owner=? определя кои документи се броят.

Тест в WebSecurityTest с реален session login:

```java
@Test void summaryUsesAuthenticatedIdentity() throws Exception {
    mvc.perform(get("/api/account-summary")).andExpect(status().isUnauthorized());
    for (String name : new String[]{"alice", "bob"}) {
        mvc.perform(post("/login").with(csrf()).param("username", name)
            .param("password", "wrong")).andExpect(status().isUnauthorized());
        var login = mvc.perform(post("/login").with(csrf()).param("username", name)
            .param("password", "Lab-" + name + "-2026!"))
            .andExpect(status().isNoContent()).andReturn();
        var session = (MockHttpSession) login.getRequest().getSession(false);
        mvc.perform(get("/api/account-summary").session(session)
            .param("username", name.equals("alice") ? "bob" : "alice"))
            .andExpect(jsonPath("$.username").value(name))
            .andExpect(jsonPath("$.documentCount").value(name.equals("alice") ? 2 : 1))
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.role").doesNotExist());
    }
}
```

## Решение на самостоятелна задача 2 — Гранични случаи

| Случай | Решение и проверка |
|---|---|
| Unicode над 72 байта | 40 кирилски букви са 80 UTF-8 байта: регистрация → 400 и няма INSERT |
| Познат/непознат потребител | DaoAuthenticationProvider и общ failure handler връщат еднакъв 401. Timing се измерва статистически, не чрез assertion за точни милисекунди |
| Session/CSRF след login | Вземаме token преди вход и нов token след вход. Старият token с актуалната сесия → 403; новият → успешен POST |
| Стар {noop} запис | Изпълняваме миграция или смяна на парола, проверяваме DB prefix и реален вход. Рестартът сам не променя записа |

## Въпроси за анализ

1. Защо salt не трябва да се пази тайно?
2. Защо два BCrypt hashes не се сравняват директно?
3. Защо @WithMockUser не доказва login?
4. Какво означава 204 от login?
5. Защо logout е POST с CSRF?
6. Как се мигрира plaintext store?

## Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
