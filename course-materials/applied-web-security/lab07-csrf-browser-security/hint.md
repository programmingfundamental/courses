# Упражнение 7 — CSRF защита на сесиите и формите в Task Manager — решения и насоки


## Решение на примерния проблем

Добавяме beans HttpSessionCsrfTokenRepository и SessionAuthenticationStrategy. SecurityConfig ползва същия repository чрез http.csrf(c -> c.csrfTokenRepository(repository)). Стратегията е CompositeSessionAuthenticationStrategy с ChangeSessionIdAuthenticationStrategy и CsrfAuthenticationStrategy(repository). AuthController.login приема и HttpServletResponse; предава го на AuthService.login. След успешно authenticate извикваме strategy.onAuthentication(authentication,httpRequest,httpResponse), след това запазваме context в сесията. Премахваме ръчното changeSessionId от упражнение 2, защото вече е в стратегията.

AuthController добавя:

```java
@GetMapping("/csrf")
public java.util.Map<String,String> csrf(org.springframework.security.web.csrf.CsrfToken token) {
    return java.util.Map.of("token",token.getToken(),"headerName",token.getHeaderName(),"parameterName",token.getParameterName());
}
```

Маршрутът е permitAll, докато /auth/me остава authenticated. Сценарий: GET csrf → POST login с token → GET csrf в новия контекст → PATCH task с headerName/token. Запазваме summary преди всеки отказ и четем от DB след него. При wrong/missing/other-session token статусът е 403 и summary е същото. Bearer в същата верига не отменя CSRF изискването; отделяне има в упражнение 9.

## Решение на самостоятелна задача 1

GET /ui/tasks/new получава CsrfToken и връща form method=post action=/ui/tasks с textarea description, input summary, datetime-local deadline и hidden поле с token.getParameterName()/getToken(), кодирани за quoted attributes. TaskRequestDto получава @Setter. POST адаптерът е:

```java
@PostMapping(value="/ui/tasks", consumes="application/x-www-form-urlencoded")
public org.springframework.http.ResponseEntity<TaskResponseDto> createForm(
        @jakarta.validation.Valid @ModelAttribute TaskRequestDto dto) {
    return org.springframework.http.ResponseEntity.status(201).body(service.create(dto));
}
```

TaskService задава owner, не формата. Тестовете изпращат валиден future deadline, summary/description минимум 10 символа; липсващ и чужд token=403 без нов ред, правилен=201 с текущ owner. Form route е под authenticated /ui/**, CSP form-action self го допуска.

## Решение на самостоятелна задача 2

Token преди login със сесията след login се отказва, защото CsrfAuthenticationStrategy го изчиства. Нов GET /auth/csrf дава актуалния token. Logout инвалидира сесията. За cross-origin използваме форма от друг localhost порт без token и следим DB, не само видимия отговор. Secure се включва в HTTPS конфигурация и се проверява в браузър; MockMvc проверява header, не browser enforcement. За identity използваме GET /tasks или POST с валиден CSRF; иначе CSRF filter може да върне 403 преди authentication.


## Въпроси за анализ

1. Защо JWT не отменя CSRF при приемана сесия?
2. Кой сменя CSRF token при custom login?
3. Как се доказва, че отказът няма DB ефект?

## Checklist

- [ ] Примерният проблем има работеща реализация в Task Manager.
- [ ] Самостоятелните задачи имат код/анализ и проверими резултати.
- [ ] Тестовете включват разрешен и отказан сценарий.
- [ ] Отказаната операция не променя DB.
- [ ] Изпълнените H2/PostgreSQL и браузърни проверки са разграничени.
- [ ] Отчетът не съдържа пароли, raw tokens или поверителни бележки.
