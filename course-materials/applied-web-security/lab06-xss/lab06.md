# 1. Упражнение 6 — Cross-Site Scripting (XSS)

**Продължителност:** 110 минути. **Аудитория:** IV курс, бакалавър „Киберсигурност“.

> Всички offensive действия в курса се изпълняват единствено срещу предоставената локална лабораторна среда.

## 2. Security сценарий

Alice записва comment, който при преглед от Bob променя заглавието на browser tab. Сървърът третира user content като HTML и го изпълнява в origin на портала.

## 3. Учебни цели

След упражнението студентът:

- възпроизвежда безопасен stored XSS marker;
- разграничава stored, reflected и DOM-based XSS;
- идентифицира HTML/attribute/JavaScript contexts;
- защитава rendering с output encoding;
- тества raw response и CSP;
- аргументира ограниченията на HttpOnly и browser tests;

## 4. Предварителни знания

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [README](../README.md). Изпълнени предходните 5 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

## 5. Необходими инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](resources/README.md) съдържа работна карта и очаквани наблюдения.

## 6. Архитектурен контекст

```text
Browser
   |
Reverse Proxy (Nginx)
   |
Spring Security
   |
Controller
   |
Service
   |
Database (PostgreSQL)
```

**Фокус:** Stored comment / reflected query → HTML rendering → browser. Съпоставете с [DFD и endpoints](../architecture/system-overview.md); отбележете кой input е недоверен и къде се взема security решението.

## 7. Необходима теория

XSS означава изпълнение на недоверено browser съдържание в доверен origin. Stored XSS пази input преди output; reflected връща request input директно; DOM-based XSS възниква при unsafe client-side sinks като innerHTML. HTML text, quoted attribute, JavaScript string и URL са различни contexts.

Output encoding преобразува специалните символи според destination context. Sanitization премахва недопустима структура, когато rich HTML е изрично изискване; тук comments са plain text, затова не добавяме sanitizer библиотека. CSP (Content Security Policy) ограничава позволените sources/actions и е defense in depth, не замества encoding. HttpOnly пречи на script да прочете cookie, но XSS пак може да извиква same-origin actions. Base64 не обезврежда HTML, ако после го декодирате към unsafe sink.

Технически източници и version scope: [references](../architecture/references.md).

## 8. Уязвим пример

```java
// Web.render в lab06:
return text;
// След това: "<p>" + render(commentBody) + "</p>"
```

Примерът е умишлен учебен дефект. За възпроизвеждане използвайте `LAB_MODE=lab06`; не пренасяйте този switch в production.

## 9. Водена практическа задача

Преди работа следвайте [startup/reset](../README.md). Командите за Maven се изпълняват от `vulnerable-app`, а Compose командите — от корена на курса.

### Стъпка 1

Стартирайте lab06; този режим изключва CSP, за да не скрие unsafe rendering. Влезте като Alice и POST /api/comments с body `<script>document.title='LAB-XSS'</script>` и валиден CSRF token.

### Стъпка 2

Отворете /comments като Bob в отделна browser session. Заглавието става LAB-XSS; няма network exfiltration, alerts или външни ресурси. Запишете само marker и origin.

### Стъпка 3

Проверете reflected /search?q= със същия marker. В DevTools сравнете Network response и DOM. Намерете точния sink; обяснете защо safe SQL storage не предотвратява browser execution.

### Стъпка 4

Приложете HtmlUtils.htmlEscape само при HTML text output, запазете original text в DB и добавете CSP към session chain. Проверете, че guided fix работи и в lab06, а не само при сменен mode.

### Стъпка 5

Пуснете encoding regression tests и повторете browser проверката. Добавете tests за ampersand и вече encoded input, за да откриете double encoding. Browser title вече не се сменя, а literal text се показва.

Разпределение: scenario/theory 15 мин, наблюдение 15, root cause 10, guided fix 25, tests 15, самостоятелна работа 25, защита 5. Общо 110 минути.

## 10. Анализ на root cause

Сървърът поставя untrusted string в HTML response без context-aware encoding. Browser parser не знае, че string-ът е „само comment“. Storage, authentication и input length checks не променят parsing context.

Предайте причинна верига: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](../architecture/threat-model.md); аргументирайте residual risk след fix.

## 11. Реализация на защита

За зададения plain-text HTML sink използвайте HtmlUtils.htmlEscape. За DOM text използвайте textContent, а не innerHTML. Не интерполирайте user data в JavaScript source. Guided CSP: `default-src 'none'; form-action 'self'; frame-ancestors 'none'; base-uri 'none'`. При нови UI assets разрешавайте само необходимите sources; не добавяйте unsafe-inline за удобство.

Reference branch в общия проект служи за guided comparison. За предаване променете уязвимия code path и запазете същия lab mode. Самостоятелната задача по-долу изисква собствено разширение и няма готово решение в този файл.

## 12. Security regression test

Изпълнете:

```powershell
mvn test '-Dlab.mode=lab06' '-Dtest=WebSecurityTest#lab06*'
```

Преди поправка очаквайте assertion failure, който показва дефекта. След поправката същата команда трябва да премине. Infrastructure error не е валиден red security test.

Примерен test fragment (пълният runnable class и imports са в `vulnerable-app/src/test/java/bg/tuvarna/lab`):

```java
mvc.perform(get("/search").with(user("alice")).param("q", "<>&\""))
   .andExpect(content().string(containsString("&lt;&gt;&amp;&quot;")))
   .andExpect(header().exists("Content-Security-Policy"));
```

Задължителна матрица:

- ordinary HTML characters → encoded response;
- script-like stored input → literal text, без raw script tag;
- normal Bulgarian text → правилно показан;
- CSP присъства след fix;
- browser marker не се изпълнява; отделно от MockMvc assertions;

Добавете поне един нов автоматизиран test за edge case или самостоятелната задача. Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.

## 13. Самостоятелна задача

**Problem statement:** разширете profile с optional homepage link; unsafe starter в resources поставя стойността директно в href.

**Requirements:** plain display name да остава безопасен, homepage да допуска само http/https URL и да се рендерира в quoted attribute.

**Constraints:** не приемайте javascript/data schemes; не използвайте HTML text escaping като единствен URL control; без външни заявки по време на тестовете.

**Acceptance criteria:** автоматизирани tests за quoted attribute breakout, опасна scheme, relative/empty value според описана policy и нормален https URL; browser проверка без marker execution. Не е достатъчно да поправите само comments.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

## 14. Edge cases

- Double encoding при вече съдържащ `&lt;` input.
- Attribute value със затваряща кавичка.
- Опасна URL scheme при правилно escaped attribute.
- CSP блокира exploit, но raw vulnerable sink остава.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.

## 15. Въпроси за анализ

1. Защо HTML encoding не е универсално?
2. Какво не доказва MockMvc?
3. Защо пазим original comment в DB?
4. Кога е нужен sanitizer?
5. Достатъчно ли е HttpOnly?
6. Може ли CSP да скрие bug?

## 16. Очакван резултат

Работещ guided fix за **Cross-Site Scripting (XSS)**, контролиран before/after evidence, root-cause анализ, test matrix и реализирана самостоятелна задача според acceptance criteria. Нормалният разрешен flow остава работещ. Отчетът разграничава доказаното с автоматизиран test, провереното ръчно и оставащите ограничения.

## 17. Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
