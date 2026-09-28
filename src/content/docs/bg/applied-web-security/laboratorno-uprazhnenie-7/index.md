---
title: "Упражнение 7 — CSRF, Cookies и Browser Security"
sidebar:
  order: 7
  label: "Упражнение 7"
---

# Упражнение 7 — CSRF, Cookies и Browser Security

## 1. Теория

### 1.1. Заявки, сесии и произход

1. **CSRF** е нежелана заявка, която използва автоматично изпратените данни за сесията на потребителя.
   - Пример: HTML форма от друг origin изпраща POST към /api/profile. Сървърът проверява token преди промяна.
2. **Origin** включва протокол, хост и порт; **site** се определя от схемата и регистрируемия домейн, без порта.
   - Пример: http://localhost:8080 и http://localhost:8081 са различни origins, но са same-site.
3. **SOP** ограничава четенето между origins; **CORS** разрешава определени origins за браузърни заявки от скрипт.
   - Пример: form POST може да бъде изпратен, въпреки че изпращащата страница няма право да прочете отговора.
4. Cookie атрибутите **HttpOnly**, **Secure** и **SameSite** управляват достъп от скрипт, транспорт и изпращане между sites.
   - Пример: SameSite=Lax не спира заявка между двата localhost порта; token проверката е отделна.
5. **Session fixation** запазва предварително наложен идентификатор; **mutation** е операция, променяща данни.
   - Пример: след login се сменя сесията и се взема актуален CSRF token. При отказ на POST броят коментари в базата остава същият.

### 1.2. Политики на браузъра

1. CSRF (Cross-Site Request Forgery) използва автоматично приложените browser credentials за нежелано действие. Same-Origin Policy (SOP) ограничава четенето на cross-origin responses; не забранява всички изпращания, например HTML form POST. Origin включва scheme, host и port, докато site не се различава само по port. localhost:8080 и localhost:8081 тук са cross-origin, но same-site, затова SameSite=Lax не спира демонстрацията.

2. HttpOnly ограничава достъпа до cookie през JavaScript. Secure изисква secure transport в нормалната browser policy; SameSite ограничава cross-site изпращания, но не е заместител на CSRF token. CORS определя кои origins могат да четат/използват responses през script; не доказва намерение на user. CSRF token е unpredictable session-bound стойност, която server сравнява при mutation. Session fixation означава запазване на идентификатор, наложен преди login; Spring сменя session identity при authentication.

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

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [подготовка на средата](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Изпълнени предходните 6 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

### Инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/lab07-csrf-browser-security/resources/README.md) съдържа работна карта и очаквани наблюдения.

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

**Фокус:** Browser cookie policy → CsrfFilter → state-changing Controller. Съпоставете с [DFD и endpoints](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/system-overview.md); отбележете кой input е недоверен и къде се взема security решението.

## 3. Примерен проблем

Alice е влязла в портала. Друга локална страница съдържа форма, която променя display name през нейната session. Приложението проверява кой е user, но не проверява дали request идва от легитимния flow.

### Начален код

```java
http.csrf(csrf -> csrf.disable());
```

### Стъпки за решаване

Преди работа следвайте [стартиране и възстановяване](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Командите за Maven се изпълняват от `vulnerable-app`, а Compose командите — от корена на курса.

#### Стъпка 1

Стартирайте lab07 и влезте като Alice в localhost:8080/login. Отворете /profile. После в същия browser отворете `http://localhost:8081/attacker.html` и натиснете учебния бутон.

#### Стъпка 2

Върнете се на /profile: display name е CSRF-LAB-MARKER. В DevTools установете POST target и наличието на session cookie, без да записвате стойността му. Не използвайте file:// и не сменяйте localhost с 127.0.0.1 между стъпките.

#### Стъпка 3

Включете default CSRF protection в session chain. Bearer-only chain остава stateless и без browser cookie authentication. Повторете формата от 8081: очаквайте 403 и непроменени данни.

#### Стъпка 4

Използвайте lab-client.ps1 за легитимен POST с актуален token. GET /csrf създава/зарежда session token; след login вземете нов. Тествайте missing и invalid token отделно от authorization.

#### Стъпка 5

Проверете HttpOnly/SameSite в DevTools. Локалният HTTP profile има Secure=false; пуснете CookieIT, който задава Secure=true и проверява реален Set-Cookie. Не твърдете, че този HTTP header test доказва HTTPS browser enforcement.

### Анализ на причината

Автоматично прикрепената session cookie доказва кой browser притежава session, но не доказва произход/намерение на state-changing action. CORS/SOP често спират четенето на резултата, след като нежеланата промяна вече е изпълнена.

Причинната верига се описва като: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/threat-model.md); аргументирайте residual risk след fix.

### Реализация на защита

В session SecurityFilterChain запазете default CSRF. Използвайте server-provided token в form parameter `_csrf` или върнатия headerName. GET не трябва да променя бизнес state. Session cookie: HttpOnly, SameSite=Lax; Secure=true в реален TLS profile, false само за описаната HTTP лаборатория. Не изключвайте CSRF глобално само защото приложението има и JWT endpoints.

### Проверка с регресионен тест

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

Матрица на примерните проверки:

- валиден token и authenticated session → 200 и update;
- missing token → 403 и без update;
- invalid token → 403 и без update;
- token от друга session → 403;
- real Set-Cookie → HttpOnly, SameSite=Lax; Secure=true в CookieIT;

Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.
