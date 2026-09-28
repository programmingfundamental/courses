# Упражнение 6 — Cross-Site Scripting (XSS) — насоки

## Решение на примерния проблем

Web.render винаги връща `HtmlUtils.htmlEscape(text)`. В SecurityConfig премахваме условието за lab06 при задаване на CSP, така че политиката да важи във всеки режим.

В DB пазим първоначалния текст и го кодираме при извеждане в HTML. Коментар със script се връща с &lt;script&gt; и се показва като текст. `WebSecurityTest#lab06*` проверява кодирането, CSP и нормална кирилица; браузърната проверка отделно потвърждава липса на изпълнен marker.

## Решение на самостоятелна задача 1 — Homepage link

1. Добавяме nullable `homepage VARCHAR(2048)` към app_users в schema.sql и infra/init.sql. За съществуваща PostgreSQL база owner изпълнява еднократно `ALTER TABLE app_users ADD COLUMN homepage VARCHAR(2048)`.
2. POST /api/profile приема и `@RequestParam(defaultValue="") String homepage`. Празно поле премахва връзката; относителен URL се отказва.
3. В Web добавяме валидатор:

```java
private String validatedHomepage(String value) {
    if (value == null || value.isBlank()) return null;
    if (value.length() > 2048 || value.chars().anyMatch(c -> c < 32 || c == 127)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
    try {
        java.net.URI uri = new java.net.URI(value);
        String scheme = uri.getScheme();
        if (!("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                || uri.getHost() == null || uri.getUserInfo() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return uri.toASCIIString();
    } catch (java.net.URISyntaxException ex) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
```

След check(displayName,200) записваме двете полета едновременно:

```java
String link = validatedHomepage(homepage);
db.update("UPDATE app_users SET display_name=?, homepage=? WHERE username=?",
    displayName, link, user.getName());
```

GET /profile извлича двете стойности за user.getName(). След прочитането им генерира:

```java
String html = "<h1>" + render(displayName) + "</h1>";
if (homepage != null) {
    String href = validatedHomepage(homepage);
    if (href != null) html += "<a href=\"" + HtmlUtils.htmlEscape(href) + "\">Homepage</a>";
}
return html;
```

displayName и homepage тук са стойностите от DB. Не правим HTTP заявка към homepage.

| Вход | Резултат |
|---|---|
| https://example.com/profile?a=1&b=2 | Успех; href съдържа &amp;, браузърът възстановява оригиналния URL |
| javascript:alert(1) или data:text/html,... | 400, DB не се променя |
| URL със сурова кавичка и onmouseover | 400 при URI parsing |
| /relative/path | 400 |
| Празна стойност | Успех, няма anchor |
| displayName с HTML | Показва се като текст |

В MockMvc тестовете изпращаме POST с user(alice) и csrf(), проверяваме статуса, DB и последващия GET. Отговорът няма injected attribute. В браузъра проверяваме липса на marker execution, без да следваме външния URL.

## Решение на самостоятелна задача 2 — Гранични случаи

- **Double encoding:** пазим първоначалния текст. Вход &lt; се кодира като &amp;lt; в изхода и се вижда буквално като &lt;; не декодираме повторно преди render.
- **Затваряща кавичка:** URI parsing отказва сурова кавичка, а HtmlUtils.htmlEscape защитава quoted атрибута. Проверяваме, че няма втори атрибут.
- **Опасна scheme:** javascript: се отказва независимо дали кавичките са HTML encoded.
- **CSP прикрива пропуск:** тестът проверява самия HTML за raw script/инжектирани атрибути. Само липса на изпълнение при активна CSP не доказва правилно encoding.

## Въпроси за анализ

1. Защо HTML encoding не е универсално?
2. Какво не доказва MockMvc?
3. Защо пазим original comment в DB?
4. Кога е нужен sanitizer?
5. Достатъчно ли е HttpOnly?
6. Може ли CSP да скрие bug?

## Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
