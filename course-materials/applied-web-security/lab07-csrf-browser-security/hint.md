# Упражнение 7 — CSRF, Cookies и Browser Security — насоки

## Решение на примерния проблем

В session SecurityFilterChain премахваме `if(mode.vulnerable(7)) http.csrf(c->c.disable())`. Стандартната CSRF защита остава включена. Отделният stateless Bearer chain не приема session cookie и запазва своята конфигурация.

1. Влизаме като alice и вземаме актуален token от /csrf.
2. POST /api/profile без token или с подправен token → 403 и непроменено display_name.
3. Със същата сесия и валиден token → 200 и нова стойност в DB.
4. Портове 8080 и 8081 са cross-origin, но same-site: отказът се дължи на CSRF проверката, а не на SameSite=Lax.
5. Изпълняваме `WebSecurityTest#lab07*` при lab07 и CookieIT. Поведението на Secure cookie се проверява и в браузър през HTTPS.

## Решение на самостоятелна задача 1 — Форма за коментар

В Web добавяме форма под /api/**, където вече се изисква вход. Тя се отваря след login и получава token от текущата сесия:

```java
@GetMapping(value="/api/comment-form", produces="text/html")
String commentForm(CsrfToken token) {
    return "<form method=\"post\" action=\"/api/comments\">"
        + "<textarea name=\"body\"></textarea>"
        + "<input type=\"hidden\" name=\"" + HtmlUtils.htmlEscape(token.getParameterName())
        + "\" value=\"" + HtmlUtils.htmlEscape(token.getToken()) + "\">"
        + "<button type=\"submit\">Изпрати</button></form>";
}
```

POST /api/comments използва съществуващия метод и връща 201. Token се изпраща в body, без промяна на CORS или добавяне в URL.

В WebSecurityTest инжектираме `@Autowired com.fasterxml.jackson.databind.ObjectMapper mapper;`. Следният тестов фрагмент извлича реален /csrf response:

```java
var login = mvc.perform(post("/login").with(csrf()).param("username", "alice")
    .param("password", "Lab-alice-2026!"))
    .andExpect(status().isNoContent()).andReturn();
var session = (MockHttpSession) login.getRequest().getSession(false);
var response = mvc.perform(get("/csrf").session(session))
    .andExpect(status().isOk()).andReturn();
var token = mapper.readTree(response.getResponse().getContentAsString());
int before = db.queryForObject("SELECT COUNT(*) FROM comments", Integer.class);
mvc.perform(post("/api/comments").session(session)
    .header(token.get("headerName").asText(), token.get("token").asText())
    .param("body", "Нов коментар"))
    .andExpect(status().isCreated());
assertThat(db.queryForObject("SELECT COUNT(*) FROM comments", Integer.class))
    .isEqualTo(before + 1);
```

За всеки отрицателен случай запомняме броя непосредствено преди POST: липсващ token, произволен token и token, прочетен от /csrf в отделна сесия. Изпращаме ги с alice session: всеки получава 403, без нов коментар. За другата сесия правим отделен GET /csrf без alice session и вземаме response token. Тестваме и самата форма: скритото поле има правилното parameterName и подадената от него стойност позволява 201.

## Решение на самостоятелна задача 2 — Гранични случаи

- **Стар token:** вземаме token преди login, след вход изпращаме него с новата сесия → 403. Нов GET /csrf дава работещ token. След logout старият session identifier вече не удостоверява.
- **Same-site/cross-origin:** формата от 8081 без token не променя данните; положителен POST през 8080 с актуален token работи.
- **Secure през HTTP:** CookieIT проверява Set-Cookie; реален браузър през HTTPS проверява изпращането. Изключенията за localhost не доказват поведение за други хостове.
- **Ред на филтрите:** POST без CSRF може да получи 403 преди authentication. За изолирана проверка на identity използваме GET /api/me → 401 или POST с валиден CSRF.

## Въпроси за анализ

1. Защо SOP не спира формата?
2. Защо SameSite=Lax не спря локалния пример?
3. Защо CORS не е CSRF control?
4. Защо token се взема след login?
5. Кога може да се изключи CSRF за API?
6. Какво доказва CookieIT?

## Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
