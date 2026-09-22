# 1. Упражнение 7 — CSRF, Cookies и Browser Security

**Продължителност:** 110 минути. **Аудитория:** IV курс, бакалавър „Киберсигурност“.

> Всички offensive действия в курса се изпълняват единствено срещу предоставената локална лабораторна среда.

## 2. Security сценарий

Alice е влязла в портала. Друга локална страница съдържа форма, която променя display name през нейната session. Приложението проверява кой е user, но не проверява дали request идва от легитимния flow.

## 3. Учебни цели

След упражнението студентът:

- възпроизвежда локален CSRF с browser cookie;
- анализира SOP, origin и site;
- разграничава CORS от CSRF protection;
- защитава session mutations с CSRF token;
- тества липсващ/невалиден token и cookie flags;
- аргументира Secure, HttpOnly, SameSite и session fixation;

## 4. Предварителни знания

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [README](../README.md). Изпълнени предходните 6 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

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

**Фокус:** Browser cookie policy → CsrfFilter → state-changing Controller. Съпоставете с [DFD и endpoints](../architecture/system-overview.md); отбележете кой input е недоверен и къде се взема security решението.

## 7. Необходима теория

CSRF (Cross-Site Request Forgery) използва автоматично приложените browser credentials за нежелано действие. Same-Origin Policy (SOP) ограничава четенето на cross-origin responses; не забранява всички изпращания, например HTML form POST. Origin включва scheme, host и port, докато site не се различава само по port. localhost:8080 и localhost:8081 тук са cross-origin, но same-site, затова SameSite=Lax не спира демонстрацията.

HttpOnly ограничава достъпа до cookie през JavaScript. Secure изисква secure transport в нормалната browser policy; SameSite ограничава cross-site изпращания, но не е заместител на CSRF token. CORS определя кои origins могат да четат/използват responses през script; не доказва намерение на user. CSRF token е unpredictable session-bound стойност, която server сравнява при mutation. Session fixation означава запазване на идентификатор, наложен преди login; Spring сменя session identity при authentication.

Технически източници и version scope: [references](../architecture/references.md).

## 8. Уязвим пример

```java
// Умишлено само в lab07; дефект за session chain:
http.csrf(csrf -> csrf.disable());
```

Примерът е умишлен учебен дефект. За възпроизвеждане използвайте `LAB_MODE=lab07`; не пренасяйте този switch в production.

## 9. Водена практическа задача

Преди работа следвайте [startup/reset](../README.md). Командите за Maven се изпълняват от `vulnerable-app`, а Compose командите — от корена на курса.

### Стъпка 1

Стартирайте lab07 и влезте като Alice в localhost:8080/login. Отворете /profile. После в същия browser отворете `http://localhost:8081/attacker.html` и натиснете учебния бутон.

### Стъпка 2

Върнете се на /profile: display name е CSRF-LAB-MARKER. В DevTools установете POST target и наличието на session cookie, без да записвате стойността му. Не използвайте file:// и не сменяйте localhost с 127.0.0.1 между стъпките.

### Стъпка 3

Включете default CSRF protection в session chain. Bearer-only chain остава stateless и без browser cookie authentication. Повторете формата от 8081: очаквайте 403 и непроменени данни.

### Стъпка 4

Използвайте lab-client.ps1 за легитимен POST с актуален token. GET /csrf създава/зарежда session token; след login вземете нов. Тествайте missing и invalid token отделно от authorization.

### Стъпка 5

Проверете HttpOnly/SameSite в DevTools. Локалният HTTP profile има Secure=false; пуснете CookieIT, който задава Secure=true и проверява реален Set-Cookie. Не твърдете, че този HTTP header test доказва HTTPS browser enforcement.

Разпределение: scenario/theory 15 мин, наблюдение 15, root cause 10, guided fix 25, tests 15, самостоятелна работа 25, защита 5. Общо 110 минути.

## 10. Анализ на root cause

Автоматично прикрепената session cookie доказва кой browser притежава session, но не доказва произход/намерение на state-changing action. CORS/SOP често спират четенето на резултата, след като нежеланата промяна вече е изпълнена.

Предайте причинна верига: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](../architecture/threat-model.md); аргументирайте residual risk след fix.

## 11. Реализация на защита

В session SecurityFilterChain запазете default CSRF. Използвайте server-provided token в form parameter `_csrf` или върнатия headerName. GET не трябва да променя бизнес state. Session cookie: HttpOnly, SameSite=Lax; Secure=true в реален TLS profile, false само за описаната HTTP лаборатория. Не изключвайте CSRF глобално само защото приложението има и JWT endpoints.

Reference branch в общия проект служи за guided comparison. За предаване променете уязвимия code path и запазете същия lab mode. Самостоятелната задача по-долу изисква собствено разширение и няма готово решение в този файл.

## 12. Security regression test

Изпълнете:

```powershell
mvn test '-Dlab.mode=lab07' '-Dtest=WebSecurityTest#lab07*'
```

Преди поправка очаквайте assertion failure, който показва дефекта. След поправката същата команда трябва да премине. Infrastructure error не е валиден red security test.

Примерен test fragment (пълният runnable class и imports са в `vulnerable-app/src/test/java/bg/tuvarna/lab`):

```java
mvc.perform(post("/api/profile").with(user("alice"))
    .param("displayName", "forged")).andExpect(status().isForbidden());
mvc.perform(post("/api/profile").with(user("alice")).with(csrf())
    .param("displayName", "Allowed")).andExpect(status().isOk());
```

Задължителна матрица:

- валиден token и authenticated session → 200 и update;
- missing token → 403 и без update;
- invalid token → 403 и без update;
- token от друга session → 403;
- real Set-Cookie → HttpOnly, SameSite=Lax; Secure=true в CookieIT;

Добавете поне един нов автоматизиран test за edge case или самостоятелната задача. Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.

## 13. Самостоятелна задача

**Problem statement:** защитете втория mutation flow — създаване на comment през собствена HTML форма.

**Requirements:** формата да използва server-provided CSRF token; след login да работи с актуалната session; server отказът да оставя comments count непроменен.

**Constraints:** без глобално изключване на CSRF/CORS wildcard; token не се поставя в URL; не използвайте cookies като Bearer fallback.

**Acceptance criteria:** legitimate comment → 201; missing, forged и token от друга session → 403; поне един test извлича реален `/csrf` response вместо само `.with(csrf())`; проверка за липса на DB insert при отказ.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

## 14. Edge cases

- Token от предишна session след login/logout.
- Same-site cross-origin request от различен localhost port.
- Secure cookie върху plain HTTP и browser-specific localhost exceptions.
- POST без CSRF връща 403 преди authentication, затова anonymous тестовете за identity използват GET.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.

## 15. Въпроси за анализ

1. Защо SOP не спира формата?
2. Защо SameSite=Lax не спря локалния пример?
3. Защо CORS не е CSRF control?
4. Защо token се взема след login?
5. Кога може да се изключи CSRF за API?
6. Какво доказва CookieIT?

## 16. Очакван резултат

Работещ guided fix за **CSRF, Cookies и Browser Security**, контролиран before/after evidence, root-cause анализ, test matrix и реализирана самостоятелна задача според acceptance criteria. Нормалният разрешен flow остава работещ. Отчетът разграничава доказаното с автоматизиран test, провереното ръчно и оставащите ограничения.

## 17. Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
