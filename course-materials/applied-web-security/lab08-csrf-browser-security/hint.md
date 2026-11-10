# Упражнение 8 — CSRF защита на сесиите и формите в Task Manager — решения и насоки


## Решение на примерния проблем

Добавяме beans HttpSessionCsrfTokenRepository и SessionAuthenticationStrategy. SecurityConfig ползва същия repository чрез http.csrf(c -> c.csrfTokenRepository(repository)). Стратегията е CompositeSessionAuthenticationStrategy с ChangeSessionIdAuthenticationStrategy и CsrfAuthenticationStrategy(repository). AuthController.login приема и HttpServletResponse; предава го на AuthService.login. След успешно authenticate извикваме strategy.onAuthentication(authentication,httpRequest,httpResponse), след това запазваме context в сесията. Премахваме ръчното changeSessionId от упражнение 3, защото вече е в стратегията.

AuthController добавя:

```java
@GetMapping("/csrf")
public java.util.Map<String,String> csrf(org.springframework.security.web.csrf.CsrfToken token) {
    return java.util.Map.of("token",token.getToken(),"headerName",token.getHeaderName(),"parameterName",token.getParameterName());
}
```

Маршрутът е permitAll, докато /auth/me остава authenticated. Сценарий: GET csrf → POST login с token → GET csrf в новия контекст → PATCH task с headerName/token. Запазваме summary преди всеки отказ и четем от DB след него. При wrong/missing/other-session token статусът е 403 и summary е същото. Bearer в същата верига не отменя CSRF изискването; отделяне има в упражнение 10.

## Решение на самостоятелна задача 1

GET /ui/tasks/new получава CsrfToken и връща form method=post action=/ui/tasks с textarea description, input summary, datetime-local deadline и hidden поле с token.getParameterName()/getToken(), кодирани за quoted attributes. TaskRequestDto получава @Setter. POST адаптерът е:

```java
@PostMapping(value="/ui/tasks", consumes="application/x-www-form-urlencoded")
public org.springframework.http.ResponseEntity<TaskResponseDto> createForm(
        @jakarta.validation.Valid @ModelAttribute TaskRequestDto dto) {
    return org.springframework.http.ResponseEntity.status(201).body(service.create(dto));
}
```

TaskService задава owner, не формата. Ръчните проверки изпращат валиден future deadline, summary/description минимум 10 символа; липсващ и чужд token=403 без нов ред, правилен=201 с текущ owner. Form route е под authenticated /ui/**, CSP form-action self го допуска.

## Решение на самостоятелна задача 2

Token преди login със сесията след login се отказва, защото CsrfAuthenticationStrategy го изчиства. Нов GET /auth/csrf дава актуалния token. Logout инвалидира сесията. За cross-origin използваме форма от друг localhost порт без token и следим DB, не само видимия отговор. Secure се включва в HTTPS конфигурация и се проверява в браузър; Postman показва header, но не доказва browser enforcement. За identity използваме GET /tasks или POST с валиден CSRF; иначе CSRF filter може да върне 403 преди authentication.


## Въпроси за анализ

1. Защо JWT не отменя CSRF при приемана сесия?
2. Кой сменя CSRF token при custom login?
3. Как се доказва, че отказът няма DB ефект?

## Checklist

- [ ] Примерният проблем има работеща реализация в Task Manager.
- [ ] Самостоятелните задачи имат код/анализ и проверими резултати.
- [ ] Ръчните проверки включват разрешен и отказан сценарий.
- [ ] Отказаната операция не променя DB.
- [ ] Ръчните API проверки с Postman, DB наблюденията и браузърните проверки са разграничени.
- [ ] Отчетът не съдържа пароли, raw tokens или поверителни бележки.

## Тест за Microsoft Forms

**Упражнение 8 — CSRF защита на сесиите и формите в Task Manager.** Втората колона е ключът за Word файла. Номерацията в последната колона е от [учебния материал](lab08.md); „задача“ означава самостоятелна задача. Наблюденията в условията са учебни сценарии.

| Въпрос | Отговор | Обосновка | Къде е обяснено |
| --- | --- | --- | --- |
| 1 | B | Нужни са актуалната сесия и изпратената за нея стойност. Издаването на token или смяната само на cookie не заменя изпращането на правилната двойка. | §1.1, т. 2 и 5; §3, стъпки 2–4. |
| 2 | C | Отказът на променящата заявка може да настъпи в CSRF филтъра. Анонимен GET към защитения списък изолира изискването за удостоверяване. | §3, стъпки 4–5; задача 2, последно изречение. |
| 3 | A | Портът участва в origin. Изпращането на форма и достъпът на скрипт до отговора са отделни действия. | §1.1, т. 3–4; задача 2. |
| 4 | D | До разделянето на веригите в упражнение 10 променящите Bearer заявки в общата верига също изискват CSRF. Статусът и състоянието се проверяват отделно. | §2 „Начален проект и надграждане“; §1.2, т. 2; §3, стъпка 4. |
| 5 | A и C | JSON и формата използват два предвидени канала за един и същ вид проверка. Създаването минава през общата услуга, която определя собственика от контекста. | §1.1, т. 2; §3, стъпки 2–3; задача 1. |
| 6 | B и D | Secure се отнася до транспорта, а HttpOnly — до достъпа на JavaScript до cookie. Нито наблюдение само по себе си не потвърждава ротация или намерение. | §1.1, т. 1–5; задача 2. |
| 7 | C и E | Сесията, методът и обектът трябва да са едни и същи. Смяната само на token различава работещата от остарялата стойност; GET не изисква такава проверка. | §1.1, т. 2 и 5; §1.2, т. 2–3; §3, стъпки 2–4; задача 2. |
| 8 | A и D | За отказа е нужно състояние преди/след. За поведението на клиента е нужно наблюдение на действително изпратените браузърни заявки, а не само на записан флаг. | §1.1, т. 4; §1.2, т. 2–4; задачи 1 и 2. |
| 9 | 7 | Успяват първият и четвъртият POST. Чуждата стойност и липсващата стойност дават 403 без INSERT. Началните 5 задачи стават 7. | §1.1, т. 2; §3, стъпки 3–4; задача 1. |
| 10 | 2 | Едно извличане е нужно преди login. След удостоверяване старата стойност се изчиства; вторият token служи и за PATCH, и за logout при посочените условия. | §1.1, т. 2 и 5; §3, стъпки 2, 3 и 5. |

За импорт използвайте `web-security-lab08-ms-forms.docx` и изберете **Quiz**. Проверете по ключа въпроси 1–4, включете **Multiple answers** за 5–8 и задайте точните текстови отговори за 9–10. Сравнете текстовете, ако вариантите са разбъркани. В студентския преглед не трябва да се виждат редове `ANSWER:`. Файлът с ключа е предназначен за преподавателя.
