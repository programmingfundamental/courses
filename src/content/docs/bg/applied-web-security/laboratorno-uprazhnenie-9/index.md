---
title: "Упражнение 9 — JWT Security и Token Manipulation"
sidebar:
  order: 9
  label: "Упражнение 9"
---

# Упражнение 9 — JWT Security и Token Manipulation

## 1. Теория

### 1.1. Устройство и проверка на токен

1. **JWT** е формат с header, payload и signature; **Base64url** кодира байтовете за използване в URL; **claim** е твърдение в payload.
   - Пример: sub=alice назовава потребителя, iss — издателя, aud — получателя, exp — срока на валидност.
2. **Parsing** прочита структурата; **signature validation** проверява подписа с доверен ключ; **RS256** е RSA подпис със SHA-256.
   - Пример: SignedJWT.parse не доказва кой е издателят. NimbusJwtDecoder валидира с конфигуриран публичен ключ и алгоритъм.
3. **Scope** описва разрешен достъп; **authority mapping** го превръща в разрешение на Spring Security.
   - Пример: documents.read става SCOPE_documents.read. Валиден токен без него получава 403; невалидният токен получава 401.
4. **Clock skew** е допустимото отклонение между часовници; **expiry boundary** е границата на срока.
   - Пример: при нулево отклонение проверете токена точно при exp чрез управляван Clock и очаквайте отказ при изтекъл срок.
5. **Replay** е повторно използване на токен; **refresh token** служи за получаване на нов токен; **revocation** отнема валидност преди срока.
   - Пример: краткият срок ограничава времето за replay, а смяната на доверения ключ обезсилва старите подписи. **TLS** защитава преноса, например при HTTPS.
6. **alg** посочва алгоритъм, **kid** — идентификатор на ключ.
   - Пример: сървърът избира ключ само от своя доверен набор; стойността kid не е произволен адрес за изтегляне.

### 1.2. Проверка на твърденията

1. JWT съдържа Base64url header, payload и signature. Claims като sub (subject), iss (issuer), aud (audience) и exp (expiration) са недоверени преди cryptographic validation. Signing удостоверява integrity/origin при доверен key; не криптира payload. Header alg/kid също не трябва произволно да определя algorithm или key location.

2. Издателят на токени е локалният Tokens service; `https://issuer.lab.invalid` е exact identifier, не адрес за network calls. NimbusJwtDecoder използва локален RSA public key и RS256; проверява timestamp, issuer, audience, непразен subject и задължителен exp. Scope се map-ва към authority `SCOPE_documents.read`. Валиден token без required scope води до 403; invalid signature/claims — 401. Bearer token позволява replay до expiry; TLS, кратък живот, минимални claims и revocation strategy ограничават риска.

### 1.3. Автоматизирани проверки: понятия и пример

1. **Security regression test** е автоматизиран тест, който проверява правило за сигурност и открива повторната поява на поправен проблем.
   - **Negative test** проверява отказана операция; **positive control** проверява нормална разрешена операция. **Security invariant** е правило, което трябва винаги да е изпълнено.
   - Пример: без вход GET /api/me трябва да върне 401, а след успешен вход трябва да върне името на текущия потребител.
2. **Assertion** сравнява очаквано и получено; **red → green** означава провалена проверка преди поправка и успешна проверка след нея.
   - Грешка при компилиране или недостъпна база е проблем на средата, а не доказателство, че проверката е открила нарушено правило.
3. **Unit test** проверява отделна единица; **integration test** проверява взаимодействието на компоненти; **test suite** е набор от тестове.
   - **JUnit** изпълнява Java тестовете; **MockMvc** подава HTTP заявки през Spring без браузър; **Testcontainers** стартира зависимости като PostgreSQL в Docker.
   - Пример: MockMvc проверява HTTP отговор, но изпълнението на JavaScript и поведението на cookies се проверяват в браузър. **Fixture** е наборът входни данни или конфигурация на теста.
4. **Test matrix** е таблица от случаи и очаквания; **edge case** е граничен случай; **test report** е отчетът от изпълнението.
   - Пример: липсващ вход → 401, собствен ресурс → успех, чужд ресурс → отказ. Проверявайте и съдържанието и състоянието в базата.

В съществуващия тестов клас WebSecurityTest полето mvc е MockMvc. Следният фрагмент подава заявка без сесия и проверява отказа:

```java
mvc.perform(get("/api/me"))
   .andExpect(status().isUnauthorized());
```

От vulnerable-app командата `mvn test '-Dtest=WebSecurityTest#lab01*'` изпълнява методите с префикс lab01. След промяна повторете същия тест, без да променяте очакването, и изпълнете положителния случай. Maven запазва отчета в target/surefire-reports. Профилът `mvn verify -Psecurity-tests` добавя интеграционните проверки; **profile** е именуван набор от настройки.

### 1.4. Работа с материалите и резултатите

- **Code diff** показва промените в кода; **evidence** е доказателство като резултат от заявка или тест.
  - Пример: предайте разликата в метода и отчета от теста, който проверява промяната.
- **Acceptance criteria** са проверимите условия за приемане; **constraints** са ограниченията на решението.
  - Пример: отказана промяна не трябва да обновява запис в базата.
- **Baseline** е началното състояние за сравнение; **LAB_MODE** избира конфигурация при стартиране.
  - Пример: след промяна на кода повторете теста със същата конфигурация, за да сравните поведението.

- **Root cause** е първопричината; **control** е защитна мярка; **policy** е правило за достъп или поведение.
  - Пример: липсваща проверка на owner е първопричина; сравняването му с текущия потребител прилага правилото за собственост.
- **Audit** е журнал на действията; **correlation ID** свързва заявката със записите за нея.
  - Пример: запис с тип LOGIN_FAILURE и идентификатор на заявката позволява проследяване на отказан вход без записване на паролата.
- **State** е състоянието на системата; **persistence** е запазването на данни; **migration** преобразува вече записани данни.
  - Пример: след отказана промяна записът в базата остава непроменен; смяна на формата на пароли изисква и обработка на старите записи.

Технически източници и version scope: [references](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/references.md).

## 2. Подготовка

### Предварителни знания

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [подготовка на средата](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Изпълнени предходните 8 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

### Инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/lab09-jwt-security/resources/README.md) съдържа работна карта и очаквани наблюдения.

### Архитектурен контекст

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

**Фокус:** Bearer token → JwtDecoder → claim validators → authorities. Съпоставете с [DFD и endpoints](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/system-overview.md); отбележете кой input е недоверен и къде се взема security решението.

## 3. Примерен проблем

API endpoint приема identity от декодиран JWT payload, без да проверява подписа. Локална промяна на sub от alice към admin се приема като нова identity, въпреки че signature вече не съответства.

### Начален код

```java
SignedJWT parsed = SignedJWT.parse(raw);
return new Jwt(raw, null, null,
    parsed.getHeader().toJSONObject(), parsed.getJWTClaimsSet().getClaims());
// parse не прави verify!
```

### Стъпки за решаване

Преди работа следвайте [стартиране и възстановяване](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Командите за Maven се изпълняват от `vulnerable-app`, а Compose командите — от корена на курса.

#### Стъпка 1

Стартирайте lab09. Вземете session и CSRF, POST /token, после GET /token-api/documents с Authorization: Bearer. Очаквайте subject=alice. Не качвайте token в онлайн decoder.

#### Стъпка 2

В resources има локален PowerShell decoder/manipulator. Променете само sub към admin, запазете старата signature и подайте токена само към localhost. Уязвимият decoder приема променената identity.

#### Стъпка 3

Прочетете Tokens и tokenChain. Отделете parse, signature verification, claim validation и authorization като четири решения. Изпълнете JwtSecurityTest#modifiedPayloadAndForeignSignature → red.

#### Стъпка 4

Реализирайте verification с NimbusJwtDecoder и фиксиран trusted RSA key/RS256 и в lab09 branch. Не създавайте собствен signature алгоритъм и не приемайте jku/x5u от токена.

#### Стъпка 5

За guided fix добавете modified-payload и чужд signing key tests. Самостоятелно завършете claim/scope policy и допълнете tests за липсващ exp и непразен sub; baseline validators са справка, а новите negative tests трябва да конструират реално подписани tokens.

### Анализ на причината

Приложението третира decoding като доказателство за authenticity. Base64url е обратимо без secret; attacker контролира payload bytes. Дори валидният подпис не означава, че token е предназначен за този API, не е изтекъл или има необходимите permissions.

Причинната верига се описва като: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/threat-model.md); аргументирайте residual risk след fix.

### Реализация на защита

Използвайте стандартния resource server и Nimbus verifier с доверен public key, фиксиран algorithm и fail-closed behavior. Разделете session chain от stateless Bearer chain. Guided целта е криптографското отхвърляне на modified bytes; application policy за issuer/audience/time/scope се защитава отделно. Не log-вайте raw Authorization header.

### Проверка с регресионен тест

Изпълнете:

```powershell
mvn test '-Dlab.mode=lab09' '-Dtest=JwtSecurityTest'
```

Преди поправка очаквайте assertion failure, който показва дефекта. След поправката същата команда трябва да премине. Infrastructure error не е валиден red security test.

Примерен test fragment (пълният runnable class и imports са в `vulnerable-app/src/test/java/bg/tuvarna/lab`):

```java
mvc.perform(get("/token-api/documents")
    .header("Authorization", "Bearer " + modifiedToken))
    .andExpect(status().isUnauthorized());
mvc.perform(get("/token-api/documents")
    .header("Authorization", "Bearer " + validToken))
    .andExpect(status().isOk());
```

Матрица на примерните проверки:

- valid signed token → 200 и правилен subject;
- expired token → 401;
- modified payload / чужда signature → 401;
- wrong issuer / audience → 401;
- missing required scope → 403;
- malformed / missing token → 401;

Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.
