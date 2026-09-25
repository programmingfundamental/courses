# 1. Упражнение 2 — Authentication със Spring Security

**Продължителност:** 110 минути. **Аудитория:** IV курс, бакалавър „Киберсигурност“.

> Всички offensive действия в курса се изпълняват единствено срещу предоставената локална лабораторна среда.

## 2. Security сценарий

В DB на портала са открити password стойности, които могат директно да се прочетат. Login работи, но изтичане на backup би разкрило паролите. Трябва да възстановите сигурното съхранение и да докажете реален session flow.

## 3. Учебни цели

След упражнението студентът:

- анализира filter chain и AuthenticationProvider;
- разграничава authentication от authorization;
- диагностицира plaintext password storage;
- защитава credentials чрез BCrypt;
- тества login, session и logout;
- аргументира 401 спрямо 403;

## 4. Предварителни знания

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [README](../README.md). Изпълнени предходните 1 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

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

**Фокус:** SecurityFilterChain → AuthenticationProvider → Accounts → SecurityContext. Съпоставете с [DFD и endpoints](../architecture/system-overview.md); отбележете кой input е недоверен и къде се взема security решението.

## 7. Необходима теория

Authentication проверява identity; authorization определя позволени действия. Spring Security filters обработват request преди Controller. DaoAuthenticationProvider зарежда UserDetails и използва PasswordEncoder; успешната authentication се пази в SecurityContext, а session свързва следващите browser requests с нея.

Password hashing е еднопосочна, умишлено скъпа проверка. Salt е случайна стойност за всеки hash, която пречи еднакви пароли да имат еднакви записи; не е secret. BCrypt има cost и ограничения за дължината в bytes. DelegatingPasswordEncoder записва `{bcrypt}` prefix за алгоритъма. Base64 и reversible encryption не са password storage. Basic token/Bearer authentication предава credential при request; session authentication използва server-side state и cookie. Session identifier също е secret. 401 означава липсваща/невалидна authentication, 403 — отказ за вече установен principal или CSRF rejection.

Технически източници и version scope: [references](../architecture/references.md).

## 8. Уязвим пример

```java
// Accounts.encode, активен само при LAB_MODE=lab02
return "{noop}" + password; // видимата парола е в DB
```

Примерът е умишлен учебен дефект. За възпроизвеждане използвайте `LAB_MODE=lab02`; не пренасяйте този switch в production.

## 9. Водена практическа задача

Преди работа следвайте [startup/reset](../README.md). Командите за Maven се изпълняват от `vulnerable-app`, а Compose командите — от корена на курса.

### Стъпка 1

Направете пълен reset с `LAB_MODE=lab02`, за да се seed-нат уязвимите users. Стартирайте WebSecurityTest#lab02_passwordStorage в същия режим; очаквайте failure за липсващ `{bcrypt}` prefix.

### Стъпка 2

През browser login формата влезте като Alice; DevTools показва POST /login → 204 и session cookie. GET /api/me → Alice; отделен private window → 401. Никога не прилагайте cookie стойността в отчета.

### Стъпка 3

Проследете login request през SecurityConfig/provider/Accounts. С DB query прочетете само учебния password prefix; покажете защо този запис излага password. Проверете unknown user и wrong password: еднакъв generic отказ.

### Стъпка 4

Променете Accounts.encode, така че и лабораторният code path да използва PasswordEncoder. За съществуващи users опишете reset/migration стратегия; само промяна на encoder не преобразува старите записи. За упражнението изчистете учебния volume.

### Стъпка 5

Изпълнете тестовете за истински form login и logout. Добавете registration test за BCrypt prefix и два hashes на еднаква парола, които се различават, но се match-ват. Удостоверете, че новият user не може да задава ADMIN.

Разпределение: scenario/theory 15 мин, наблюдение 15, root cause 10, guided fix 25, tests 15, самостоятелна работа 25, защита 5. Общо 110 минути.

## 10. Анализ на root cause

Login успехът не доказва безопасно съхранение. `{noop}` казва на DelegatingPasswordEncoder да сравни password без hash. Доверието е неправилно прехвърлено от login UX към защита на данните в DB. Ръчен controller, който само връща 200, също не създава автоматично persistent SecurityContext.

Предайте причинна верига: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](../architecture/threat-model.md); аргументирайте residual risk след fix.

## 11. Реализация на защита

Използвайте server-side PasswordEncoder при registration/seed и DaoAuthenticationProvider при login. Guided baseline е `PasswordEncoderFactories.createDelegatingPasswordEncoder()`. Не сравнявайте нови random hashes със string equality. Запазете CSRF върху login/logout, generic failures и default session fixation protection. Отделете legacy password migration от новото записване.

Reference branch в общия проект служи за guided comparison. За предаване променете уязвимия code path и запазете същия lab mode. Самостоятелната задача по-долу изисква собствено разширение и няма готово решение в този файл.

## 12. Security regression test

Изпълнете:

```powershell
mvn test '-Dlab.mode=lab02' '-Dtest=WebSecurityTest#lab02*'
```

Преди поправка очаквайте assertion failure, който показва дефекта. След поправката същата команда трябва да премине. Infrastructure error не е валиден red security test.

Примерен test fragment (пълният runnable class и imports са в `vulnerable-app/src/test/java/bg/tuvarna/lab`):

```java
var result = mvc.perform(post("/login").with(csrf())
    .param("username", "alice").param("password", "Lab-alice-2026!"))
    .andExpect(status().isNoContent()).andReturn();
var session = (MockHttpSession) result.getRequest().getSession(false);
mvc.perform(get("/api/me").session(session))
    .andExpect(jsonPath("$.username").value("alice"));
```

Задължителна матрица:

- valid credentials → 204 и /api/me със същата session → Alice;
- wrong password и unknown user → 401 generic;
- anonymous protected endpoint → 401;
- logout → 204 и session invalidation;
- stored password → BCrypt prefix и успешен matches;

Добавете поне един нов автоматизиран test за edge case или самостоятелната задача. Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.

## 13. Самостоятелна задача

**Problem statement:** добавете `GET /api/account-summary`, който връща username и брой собствени документи.

**Requirements:** anonymous се отказва; user identity идва само от SecurityContext; count е за текущия user.

**Constraints:** не приемайте username от query parameter; не връщайте password/hash/roles от DB entity; не използвайте Basic auth вместо зададения session flow.

**Acceptance criteria:** автоматизирани anonymous, invalid-login, valid-login и cross-user isolation tests, включително Alice count=2 и Bob count=1; обяснение защо login и policy са различни отговорности.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

## 14. Edge cases

- Парола с Unicode, която надхвърля BCrypt byte limit при допустим брой chars.
- Unknown user срещу known user: еднакъв текст, но възможни timing различия.
- Session/CSRF token преди login се сменят; клиентът трябва да вземе нов CSRF token.
- Смяна на mode без DB reset оставя legacy {noop} записи.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.

## 15. Въпроси за анализ

1. Защо salt не трябва да се пази тайно?
2. Защо два BCrypt hashes не се сравняват директно?
3. Защо @WithMockUser не доказва login?
4. Какво означава 204 от login?
5. Защо logout е POST с CSRF?
6. Как се мигрира plaintext store?

## 16. Очакван резултат

Работещ guided fix за **Authentication със Spring Security**, контролиран before/after evidence, root-cause анализ, test matrix и реализирана самостоятелна задача според acceptance criteria. Нормалният разрешен flow остава работещ. Отчетът разграничава доказаното с автоматизиран test, провереното ръчно и оставащите ограничения.

## 17. Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
