---
title: "Упражнение 2 — Authentication със Spring Security"
sidebar:
  order: 2
  label: "Упражнение 2"
---

# Упражнение 2 — Authentication със Spring Security

## 1. Теория

### 1.1. Самоличност, пароли и сесии

1. **Principal** е установеният потребител; **credentials** са данните за удостоверяване. **Plaintext** е текст без криптографска защита.
   - Пример: след вход като alice приложението получава името от SecurityContext, а не от параметър username.
2. **SecurityFilterChain** е поредица от проверки преди контролера; **AuthenticationProvider** проверява подадените данни; **UserDetails** представя профила за Spring Security.
   - Пример: provider зарежда alice и сравнява паролата чрез PasswordEncoder.matches.
3. **BCrypt cost** определя изчислителната трудност на хеширането. Ограничението на BCrypt е 72 байта; Unicode символ може да заема няколко байта.
   - Пример: проверете UTF-8 дължината, преди да приемете паролата; `{noop}` означава запис без хеширане, а `{bcrypt}` избира BCrypt.
4. **Session** пази състояние на сървъра; **cookie** пренася идентификатора ѝ в браузъра. При logout сесията се обезсилва.
   - **CSRF token** е непредсказуема стойност, обвързана със сесията, за проверка на заявки, които променят данни. След login клиентът взема нов token.
   - **Basic authentication** изпраща кодирани име и парола; **Bearer authentication** изпраща токен. Base64 е кодиране, от което първоначалните данни могат да се възстановят.
5. **Account enumeration** разкрива дали име съществува; **timing difference** е измерима разлика във времето за отговор.
   - Пример: еднаквото съобщение за неизвестен потребител и грешна парола ограничава разкриването на профили.

### 1.2. Механизми на Spring Security

1. Authentication проверява identity; authorization определя позволени действия. Spring Security filters обработват request преди Controller. DaoAuthenticationProvider зарежда UserDetails и използва PasswordEncoder; успешната authentication се пази в SecurityContext, а session свързва следващите browser requests с нея.

2. Password hashing е еднопосочна, изчислително скъпа проверка. Salt е случайна стойност за всеки hash, която пречи еднакви пароли да имат еднакви записи; не е secret. BCrypt има cost и ограничения за дължината в bytes. DelegatingPasswordEncoder записва `{bcrypt}` prefix за алгоритъма. Base64 и reversible encryption не са password storage. Basic token/Bearer authentication предава credential при request; session authentication използва server-side state и cookie. Session identifier също е secret. 401 означава липсваща/невалидна authentication, 403 — отказ за вече установен principal или CSRF rejection.

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

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [подготовка на средата](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Изпълнени предходните 1 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

### Инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/lab02-authentication/resources/README.md) съдържа работна карта и очаквани наблюдения.

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

**Фокус:** SecurityFilterChain → AuthenticationProvider → Accounts → SecurityContext. Съпоставете с [DFD и endpoints](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/system-overview.md); отбележете кой input е недоверен и къде се взема security решението.

## 3. Примерен проблем

В DB на портала са открити password стойности, които могат директно да се прочетат. Login работи, но изтичане на backup би разкрило паролите. Трябва да възстановите сигурното съхранение и да докажете реален session flow.

### Начален код

```java
// Accounts.encode, активен само при LAB_MODE=lab02
return "{noop}" + password; // видимата парола е в DB
```

### Стъпки за решаване

Преди работа следвайте [стартиране и възстановяване](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Командите за Maven се изпълняват от `vulnerable-app`, а Compose командите — от корена на курса.

#### Стъпка 1

Направете пълен reset с `LAB_MODE=lab02`, за да се seed-нат уязвимите users. Стартирайте WebSecurityTest#lab02_passwordStorage в същия режим; очаквайте failure за липсващ `{bcrypt}` prefix.

#### Стъпка 2

През browser login формата влезте като Alice; DevTools показва POST /login → 204 и session cookie. GET /api/me → Alice; отделен private window → 401. Никога не прилагайте cookie стойността в отчета.

#### Стъпка 3

Проследете login request през SecurityConfig/provider/Accounts. С DB query прочетете само префикса на записа за паролата; покажете защо този запис излага password. Проверете unknown user и wrong password: еднакъв generic отказ.

#### Стъпка 4

Променете Accounts.encode, така че и лабораторният code path да използва PasswordEncoder. За съществуващи users опишете reset/migration стратегия; само промяна на encoder не преобразува старите записи. За упражнението изчистете тома на базата данни.

#### Стъпка 5

Изпълнете тестовете за истински form login и logout. Добавете registration test за BCrypt prefix и два hashes на еднаква парола, които се различават, но се match-ват. Удостоверете, че новият user не може да задава ADMIN.

### Анализ на причината

Login успехът не доказва безопасно съхранение. `{noop}` казва на DelegatingPasswordEncoder да сравни password без hash. Доверието е неправилно прехвърлено от login UX към защита на данните в DB. Ръчен controller, който само връща 200, също не създава автоматично persistent SecurityContext.

Причинната верига се описва като: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/threat-model.md); аргументирайте residual risk след fix.

### Реализация на защита

Използвайте server-side PasswordEncoder при registration/seed и DaoAuthenticationProvider при login. Guided baseline е `PasswordEncoderFactories.createDelegatingPasswordEncoder()`. Не сравнявайте нови random hashes със string equality. Запазете CSRF върху login/logout, generic failures и default session fixation protection. Отделете legacy password migration от новото записване.

### Проверка с регресионен тест

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

Матрица на примерните проверки:

- valid credentials → 204 и /api/me със същата session → Alice;
- wrong password и unknown user → 401 generic;
- anonymous protected endpoint → 401;
- logout → 204 и session invalidation;
- stored password → BCrypt prefix и успешен matches;

Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.
