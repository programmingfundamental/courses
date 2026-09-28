---
title: "Упражнение 10 — Security Testing и интегрирана защита"
sidebar:
  order: 10
  label: "Упражнение 10"
---

# Упражнение 10 — Security Testing и интегрирана защита

## 1. Теория

### 1.1. Оценка на сигурността и доказателства

1. **Security assessment** е систематична оценка; **finding** е установен проблем; **hypothesis** е предположение; **limitation** е граница на проверката.
   - Пример: заявка като alice връща документ на bob — това е finding с evidence (доказателство). Непроверен маршрут е ограничение, а не потвърдена уязвимост.
2. **Root cause** е първопричината; **mitigation** е защитната мярка; **security contract** е проверимо правило.
   - Пример: правило „чужд документ не се връща“ се проверява с отрицателен тест; собствен документ се проверява с положителен тест.
3. **Dependency tree** показва библиотеките и версиите; **advisory** описва известна уязвимост; **affected range** е диапазонът засегнати версии; **reachability** е достижимостта на съответния код.
   - Пример: mvn dependency:tree установява версията, след което се проверява дали приложението използва засегнатата функция.
4. **Audit event** е запис за действие; **correlation ID** свързва заявка и събития; **log injection** вмъква заблуждаващ текст в журнала.
   - Пример: сървърът генерира идентификатор, записва тип на събитието и резултат, а тест проверява, че паролата не присъства в лога.
5. **Mutation testing** променя временно проверяван код, за да установи дали тестът открива промяната; **coverage** показва обхвата на проверките.
   - Пример: временно премахване на owner проверката трябва да провали теста за чужд документ; след възстановяване тестът преминава.
6. **OWASP ZAP** е инструмент за анализ на HTTP трафик; **passive scan** наблюдава съобщенията, а **active scan** изпраща допълнителни пробни заявки.
   - Пример: пасивно наблюдение на login flow показва липсващи заглавки; то не доказва правилна проверка на собствеността.

### 1.2. Слоеве на проверката

1. Security regression test пази вече фиксиран security contract. Negative testing проверява forbidden input/action, а positive control доказва, че бизнес функционалността остава работеща. Unit test на helper не доказва wiring в HTTP/filter/DB flow; integration test не е достатъчен за browser enforcement. Defense in depth комбинира независими controls, като всеки има собствена цел.

2. Dependency vulnerability review изисква конкретен component/version, advisory, affected range и reachability; scanner severity не е автоматично application risk. Configuration review покрива published ports, profiles, secrets, cookies, debug и deny-by-default. Audit event трябва да позволява разследване с correlation ID, без credentials, tokens или sensitive payloads. User-supplied log fields трябва да са ограничени/нормализирани, за да не внесат log injection.

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

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [подготовка на средата](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Изпълнени предходните 9 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

### Инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend. OWASP ZAP е незадължителен и се използва само passive срещу localhost. За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/lab10-security-testing/resources/README.md) съдържа работна карта и очаквани наблюдения.

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

**Фокус:** Цялата архитектура → evidence → fixes → regression suite. Съпоставете с [DFD и endpoints](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/system-overview.md); отбележете кой input е недоверен и къде се взема security решението.

## 3. Примерен проблем

Преди учебен release е предоставен build с няколко върнати дефекта. Вашата задача е да направите bounded security review, да защитите системата и да докажете, че findings са отстранени без счупена нормална функционалност.

### Начален код

```java
Document d = loadById(id);
return d; // липсва object policy
```

### Стъпки за решаване

Преди работа следвайте [стартиране и възстановяване](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Командите за Maven се изпълняват от `vulnerable-app`, а Compose командите — от корена на курса.

#### Стъпка 1

Стартирайте чист lab10. Направете bounded inventory само на предоставените endpoints. Изпълнете `mvn test -Dlab.mode=lab10` и разграничете assertion failures от environment/build errors.

#### Стъпка 2

Потвърдете findings с минимум една реална local request pair: разрешен flow и нарушаващ policy flow. Класифицирайте по root cause, а не само по HTTP status. Попълнете resources/finding-template.md.

#### Стъпка 3

Поправете дефектите, без да променяте LAB_MODE и без да отслабвате tests. За всеки finding добавете поне един нов edge-case regression test; повтарящите се root causes могат да имат общ control с отделни coverage checks.

#### Стъпка 4

Прегледайте Maven dependency tree, Compose bindings, DB role, secret file placement и audit format. Запишете конкретно коя версия/конфигурация сте проверили. OWASP ZAP е optional: passive-only срещу localhost, ограничен context, без външни URL и без active scan.

#### Стъпка 5

Пуснете пълния `mvn verify -Psecurity-tests`, след това нормален login → own document → comment → logout flow през proxy. Извършете deliberate mutation на един fix във временен local working copy, покажете red test, възстановете и покажете green.

### Анализ на причината

Комбинираните failures често имат различни trust assumptions: identity вместо ownership, text вместо SQL/HTML structure или конфигурация, която изключва control. Един успешен scanner pass или green mocked test не доказва, че всички тези boundaries са защитени.

Причинната верига се описва като: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/threat-model.md); аргументирайте residual risk след fix.

### Реализация на защита

Използвайте server-side object policy, parameterized SQL и context-aware output control на съответните места. За конфигурацията запазете deny-by-default и отделните chains. Logging използва server-generated correlation ID и allowlisted fields; добавете event type без request body/header dump. Dependency update се приема след repeatable build и tests, а не само промяна на version string.

### Проверка с регресионен тест

Изпълнете:

```powershell
mvn test '-Dlab.mode=lab10' '-Dtest=WebSecurityTest,JwtSecurityTest,LoginGuardTest,VaultTest,AuditTest'
```

Преди поправка очаквайте assertion failure, който показва дефекта. След поправката същата команда трябва да премине. Infrastructure error не е валиден red security test.

Примерен test fragment (пълният runnable class и imports са в `vulnerable-app/src/test/java/bg/tuvarna/lab`):

```java
mvc.perform(get("/health"))
   .andExpect(header().exists("X-Correlation-ID"))
   .andExpect(header().string("X-Content-Type-Options", "nosniff"));
// Добавете assertions за body/owner/state към всеки finding,
// не само status().is4xxClientError().
```

Матрица на примерните проверки:

- authentication и session lifecycle;
- owner/admin/non-owner policy;
- brute-force threshold/expiration;
- SQL input остава data; XSS input остава text;
- CSRF mutation отказ без state change;
- crypto roundtrip/tamper/logging;
- JWT signature/claims/scope;
- config, headers, real PostgreSQL и cookie flags;

Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.
