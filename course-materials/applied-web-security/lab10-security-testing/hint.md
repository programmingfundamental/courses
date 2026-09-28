# Упражнение 10 — Security Testing и интегрирана защита — насоки

## Решение на примерния проблем

LabMode активира в lab10 проблемите от упражнения 3, 5 и 6. Прилагаме трите решения едновременно:

1. Documents.get винаги проверява owner или ROLE_ADMIN; чужд/липсващ документ → 404.
2. Documents.search използва само параметризирана SQL заявка с owner и q.
3. Web.render винаги прилага HtmlUtils.htmlEscape; SecurityConfig задава CSP без условието за режим.
4. Изпълняваме `mvn test -Dlab.mode=lab10` и `mvn verify -Psecurity-tests -Dlab.mode=lab10`. Повтаряме нормален login → собствен документ → comment → logout през Nginx.
5. В отделно работно копие временно премахваме owner проверката: тестът за чужд документ трябва да се провали. Възстановяваме я и тестът преминава.

| Проблем | Доказателство преди поправката | Очакване след нея |
|---|---|---|
| IDOR | alice вижда Bob invoice чрез id=2 | 404 без съдържание на bob |
| SQL Injection | q променя SQL логиката и връща чужд owner | Няма чужди данни; O'Reilly работи |
| XSS | HTML съдържа необработен вход | Encoded HTML и липса на marker execution в браузъра |

Преглеждаме и dependency tree, мрежови публикации, runtime DB role и secret файла. Без конкретен advisory, версия и достижимост не обозначаваме зависимост като потвърдена уязвимост.

## Решение на самостоятелна задача 1 — Registration/profile assessment

### Три обосновани наблюдения

| Finding/наблюдение | Risk и Evidence | Root Cause | Mitigation | Regression Test и остатъчен риск |
|---|---|---|---|---|
| Потвърден дефект: raw HTML в profile при lab10 | POST на markup като displayName и GET връща необработен таг; възможно действие през сесията | Условно изключено output encoding | Безусловно HtmlUtils.htmlEscape и CSP | Encoded отговор, без raw tag, нормално име работи; другите контексти се проверяват отделно |
| Потвърдена липса на регистрационен лимит | /register не използва guard; няколко различни валидни имена се приемат. Това не е измерен DoS | Login лимитът не обхваща регистрацията | Отделен лимит по доверен адрес и общ капацитет; над праг → 429 | С управляван Clock: до праг 201, над праг 429 без INSERT, след срок 201; остава разпределен abuse |
| Пропуск в password validation | 40 кирилски букви преминават length≤64, но са 80 UTF-8 байта | Character count вместо BCrypt byte limit | Проверка ≤72 UTF-8 байта преди encoder; отказ 400 | 40 кирилски букви → 400 без INSERT; допустима парола → 201; качеството на паролата е отделна политика |

Приоритети: profile XSS — висок; регистрационен лимит — среден според достъпност и капацитет; byte validation — среден за надежден отказ на невалидния вход. Изтичане през error response е хипотеза, докато не се провери точният отговор. За password решението използваме Accounts.encode от упражнение 2; за profile — render от упражнение 6. За регистрационния лимит отделяме брояча от LoginGuard, така че регистрации да не заключват чужди профили.

### Audit event и корелация

Във finally на AuditFilter.doFilterInternal заменяме съществуващото log извикване. Избираме event само от фиксирани стойности по точен method/path, без raw URI, body, параметри или headers:

```java
String event = "HTTP_REQUEST";
if ("POST".equals(request.getMethod())) {
    if ("/register".equals(request.getServletPath())) event = "REGISTRATION_RESULT";
    else if ("/api/profile".equals(request.getServletPath())) event = "PROFILE_UPDATE_RESULT";
}
log.info("security_event type={} correlation={} status={}", event, id, response.getStatus());
```

id остава генерираният от сървъра UUID в X-Correlation-ID. Event описва HTTP резултат; не твърди, че има успешна транзакция само защото е постъпила заявка.

Тест в AuditTest с неговия OutputCaptureExtension:

```java
@Test void profileAuditContainsCorrelationWithoutInput(CapturedOutput output) throws Exception {
    String marker = "NEVER-LOG-PROFILE-INPUT";
    var result = mvc.perform(post("/api/profile").servletPath("/api/profile")
        .with(user("alice")).with(csrf())
        .header("X-Correlation-ID", "CLIENT-SUPPLIED-ID")
        .param("displayName", marker))
        .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
        .andReturn();
    String id = result.getResponse().getHeader("X-Correlation-ID");
    assertThat(id).isNotBlank().isNotEqualTo("CLIENT-SUPPLIED-ID");
    java.util.UUID.fromString(id);
    assertThat(output.getAll()).contains("type=PROFILE_UPDATE_RESULT", "correlation=" + id)
        .doesNotContain(marker, "CLIENT-SUPPLIED-ID");
}
```

Аналогичен тест за /register проверява REGISTRATION_RESULT и липса на паролата. Отказан POST без CSRF дава status=403 в event и не променя DB. Съществуващите тестове за credentials, token и sensitive field остават. Използваме отделни данни или възстановяваме състоянието след всеки тест.

### Нов regression test извън готовата suite

В WebSecurityTest добавяме:

```java
@Test void profileMarkupStaysText() throws Exception {
    String before = db.queryForObject(
        "SELECT display_name FROM app_users WHERE username='alice'", String.class);
    try {
        mvc.perform(post("/api/profile").with(user("alice")).with(csrf())
            .param("displayName", "<b>PROFILE-MARKER</b>"))
            .andExpect(status().isOk());
        mvc.perform(get("/profile").with(user("alice")))
            .andExpect(content().string(containsString("&lt;b&gt;PROFILE-MARKER&lt;/b&gt;")))
            .andExpect(content().string(not(containsString("<b>PROFILE-MARKER</b>"))));
        mvc.perform(post("/api/profile").with(user("alice")).with(csrf())
            .param("displayName", "Алиса")).andExpect(status().isOk());
        mvc.perform(get("/profile").with(user("alice")))
            .andExpect(content().string(containsString("Алиса")));
    } finally {
        db.update("UPDATE app_users SET display_name=? WHERE username='alice'", before);
    }
}
```

Тестът се проваля с предишния render при lab10 и преминава след поправката. За registration положителният тест проверява role=USER и BCrypt запис, а не само 201.

## Решение на самостоятелна задача 2 — Гранични случаи

- **Грешен profile/base URL:** отчетът съдържа LAB_MODE, commit, адрес и конфигурация; тестът проверява очакваните данни и настройките на стартираната среда. Успех срещу друга инстанция не доказва поправката.
- **Пропуснат Docker test:** във failsafe-reports проверяваме изпълнени PostgresIT/CookieIT и skipped=0. Липсващ report/Docker означава непроверен обхват.
- **Debug logging:** marker тестът се изпълнява с използваната logging конфигурация; не включваме body/header/query logging. Нови настройки се проверяват повторно.
- **Proxy промяна:** повтаряме разрешени и отказани HTTP операции през Nginx и проверяваме status, cookies, headers и DB state. MockMvc сам не доказва proxy поведението.

## Въпроси за анализ

1. Какво е доказателство за поправка?
2. Защо severity не е равна на risk?
3. Какво прави finding actionable?
4. Защо correlation ID е server-generated?
5. Кога scanner pass е недостатъчен?
6. Кога review е приключил?

## Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
