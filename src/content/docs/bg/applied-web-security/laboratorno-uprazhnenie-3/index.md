---
title: "Упражнение 3 — Authorization и Broken Access Control"
sidebar:
  order: 3
  label: "Упражнение 3"
---

# Упражнение 3 — Authorization и Broken Access Control

## 1. Теория

### 1.1. Права върху обекти

1. **RBAC (Role-Based Access Control)** определя достъп по роли; **authority** е отделно разрешение; **ownership** е собствеността на ресурс.
   - Пример: USER чете своя документ, а ADMIN има разрешение за всеки документ.
2. **IDOR** е достъп чрез сменяем идентификатор без необходимата проверка за права.
   - Пример: alice сменя /api/documents/1 с /api/documents/2. Сървърът сравнява собственика с principal преди връщане на данните.
3. **Method security proxy** е обвивка, която проверява извикване на метод, например чрез @PreAuthorize.
   - **Self-invocation** е извикване през this в същия обект и може да пропусне обвивката. Извикване през управляван service позволява проверката да се приложи.
4. **TOCTOU** е промяна между проверката и използването на ресурс.
   - Пример: owner се сменя преди UPDATE. Условие за owner в самия UPDATE или подходяща транзакция свързва проверката с промяната.
5. **Existence disclosure** разкрива съществуването на обект; **UUID** е идентификатор с голямо пространство от стойности.
   - Пример: еднакъв 404 за чужд и липсващ документ намалява разкриването. Трудният за отгатване UUID все пак изисква проверка на права.

### 1.2. Нива на проверка

1. Role е груба роля, authority — конкретно разрешение. RBAC е управление по роли; ownership добавя връзка между principal и resource. IDOR (Insecure Direct Object Reference) възниква, когато сменяем идентификатор води до object без проверка на тази връзка. Horizontal escalation пресича users на едно ниво, vertical escalation — user/admin права.

2. Endpoint-level проверка от типа authenticated е необходима, но недостатъчна. Method-level @PreAuthorize покрива service entry, а object policy трябва да проверява заредения owner или да ограничи самия query. В този курс USER чете само собствен, ADMIN — всички по ID, anonymous — никой. 404 при чужд и липсващ ID намалява existence disclosure; това е договор, не заместител на policy. UUID усложнява guessing, но не е authorization.

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

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [подготовка на средата](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Изпълнени предходните 2 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

### Инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/lab03-authorization/resources/README.md) съдържа работна карта и очаквани наблюдения.

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

**Фокус:** Controller → Documents.get → object ownership policy. Съпоставете с [DFD и endpoints](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/system-overview.md); отбележете кой input е недоверен и къде се взема security решението.

## 3. Примерен проблем

Alice отваря собствен документ с ID=1. При промяна на URL към ID=2 вижда фактура на Bob. Login е правилен; дефектът е в решението дали конкретната identity има право върху конкретния object.

### Начален код

```java
// Уязвимата логика в Documents.get:
Document d = loadById(id);
return d; // authenticated != owner
```

### Стъпки за решаване

Преди работа следвайте [стартиране и възстановяване](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Командите за Maven се изпълняват от `vulnerable-app`, а Compose командите — от корена на курса.

#### Стъпка 1

Стартирайте `lab03`, login като Alice и GET /api/documents/1 → 200. Отбележете principal и owner, не само status.

#### Стъпка 2

GET /api/documents/2 със същата session → 200 и Bob owner в уязвимия режим. Това е цялата контролирана reproduction; използвайте само IDs 1–3.

#### Стъпка 3

Прочетете SecurityConfig и Documents.get. Посочете коя проверка доказва authentication и къде липсва relation principal–object. Изпълнете test матрицата: different user case трябва да е red.

#### Стъпка 4

Реализирайте policy в service code path: ownership или ADMIN, иначе същия 404 като missing resource. Owner никога не се приема от клиентски parameter. Проверете отделно /admin/status с USER → 403.

#### Стъпка 5

Добавете тест за директно service извикване през Spring proxy и за ID=-1. Запазете положителните owner/admin tests и повторете лабораторния режим без да изключвате флага.

### Анализ на причината

Приложението вярва, че щом потребителят знае ID и е authenticated, той е authorized. ID принадлежи на client-controlled входа. Role проверка само на /admin не покрива object-level достъп на нормалните users.

Причинната верига се описва като: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/threat-model.md); аргументирайте residual risk след fix.

### Реализация на защита

Извлечете principal от Authentication, сравнете owner server-side и позволете изричен admin override. Използвайте service boundary, за да няма алтернативен controller без policy. За операции read-modify-write обсъдете transaction/TOCTOU; lookup-then-update може да има race, ако ownership се променя.

### Проверка с регресионен тест

Изпълнете:

```powershell
mvn test '-Dlab.mode=lab03' '-Dtest=WebSecurityTest#lab03*'
```

Преди поправка очаквайте assertion failure, който показва дефекта. След поправката същата команда трябва да премине. Infrastructure error не е валиден red security test.

Примерен test fragment (пълният runnable class и imports са в `vulnerable-app/src/test/java/bg/tuvarna/lab`):

```java
mvc.perform(get("/api/documents/1").with(user("bob")))
   .andExpect(status().isNotFound());
mvc.perform(get("/api/documents/1").with(user("admin").roles("ADMIN")))
   .andExpect(status().isOk());
```

Матрица на примерните проверки:

- anonymous → 401;
- owner → 200 с правилния object;
- other user → 404, без чуждо съдържание;
- ADMIN → 200;
- missing ID → 404; nonnumeric → 400; USER admin route → 403;

Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.
