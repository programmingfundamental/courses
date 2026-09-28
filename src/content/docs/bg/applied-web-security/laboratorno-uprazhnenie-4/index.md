---
title: "Упражнение 4 — Brute-Force атаки и защита на Authentication"
sidebar:
  order: 4
  label: "Упражнение 4"
---

# Упражнение 4 — Brute-Force атаки и защита на Authentication

## 1. Теория

### 1.1. Ограничаване на опитите за вход

1. **Brute force** изпробва кандидати; **password guessing** подбира вероятни пароли; **credential stuffing** използва вече изтекли двойки име/парола.
   - Пример: брояч за alice отказва следващ вход след достигане на прага за неуспешни опити.
2. **NAT** позволява много клиенти да споделят публичен IP адрес; **cluster** е група работещи копия на приложението.
   - Пример: ограничение само по IP може да блокира цяла зала; броячи само в един процес не се споделят с останалите копия.
3. **Атомарен брояч** обновява стойността без загуба при едновременни заявки. **Lockout DoS** е отказ на услуга чрез блокиране на чужд профил.
   - Пример: два едновременни неуспеха трябва да увеличат брояча с две, а не с едно.
4. **Injected Clock** е часовник, подаден като зависимост, който тестът може да управлява.
   - Пример: тестът премества времето с 31 секунди и проверява изтичане на блокировка PT30S, без sleep. PT30S означава продължителност 30 секунди.
5. **X-Forwarded-For** е заглавка за адреса на клиента при работа през proxy.
   - Пример: доверявайте се на стойност, зададена от управлявания proxy, защото клиентът също може да изпрати тази заглавка.

### 1.2. Политика и състояние

1. Brute force изпробва множество кандидати; password guessing подбира вероятни пароли; credential stuffing използва вече компрометирани credential pairs . Account enumeration е различаване на съществуващи accounts по response, timing или lockout behavior.

2. Rate limiting ограничава честота, throttling забавя, lockout временно отказва след праг. IP limit може да засегне NAT users и се заобикаля с различни източници; account limit защитава identity, но позволява targeted lockout. Progressive delay нараства с грешките; не блокирайте servlet threads със sleep. Lab policy брои неуспешните опити за точния username; петият failure достига прага, следващият login е blocked за 60 секунди. Отговорът остава generic 401, за да не разкрива state. Clock е dependency за детерминистични tests. Multi-instance deployment изисква споделени атомарни counters.

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

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [подготовка на средата](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Изпълнени предходните 3 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

### Инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/lab04-brute-force/resources/README.md) съдържа работна карта и очаквани наблюдения.

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

**Фокус:** AuthenticationProvider → LoginGuard → clock/counters. Съпоставете с [DFD и endpoints](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/system-overview.md); отбележете кой input е недоверен и къде се взема security решението.

## 3. Примерен проблем

Няколко последователни грешни пароли не предизвикват никакво ограничение. Трябва да ограничите guessing срещу учебен account, като не позволите permanent lockout да се превърне в лесен denial of service.

### Начален код

```java
// lab04 заобикаля limiter-а в provider:
Authentication result = delegate.authenticate(input);
return result; // няма counter, blocking или expiration
```

### Стъпки за решаване

Преди работа следвайте [стартиране и възстановяване](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Командите за Maven се изпълняват от `vulnerable-app`, а Compose командите — от корена на курса.

#### Стъпка 1

Стартирайте lab04. В resources има simulation с точно 6 wrong-password requests към localhost и един valid login. Не увеличавайте броя и не добавяйте wordlist. Наблюдавайте status/correlation ID, без да логвате пароли.

#### Стъпка 2

В уязвимия режим валидният login след грешките е 204. Изпълнете WebSecurityTest#lab04_limitIsWiredIntoLogin → red. Разграничете test за LoginGuard самостоятелно от test, който доказва wiring в authentication flow.

#### Стъпка 3

Прочетете counter state и transitions. Определете MAX_ATTEMPTS, LOCK_DURATION, RESET_AFTER_SUCCESS, началото на block interval и поведението точно на boundary time. Отхвърлете нулев/отрицателен policy.

#### Стъпка 4

Свържете failure/success/block checks във provider. Запазете generic response за known/unknown account; blocked request не удължава безкрайно срока. Audit записва outcome/correlation без credentials.

#### Стъпка 5

Пуснете LoginGuardTest с mutable Clock вместо реално изчакване. Добавете integration test за success преди прага, който reset-ва counters, и за независим Bob account.

### Анализ на причината

Сървърът разглежда всеки login изолирано и не пази история на отказите. Сигурното password hashing увеличава цената, но не задава policy за броя online опити. Неправилно reset/expire logic може да направи limiter-а неефективен или да заключи account завинаги.

Причинната верига се описва като: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/threat-model.md); аргументирайте residual risk след fix.

### Реализация на защита

Използвайте atomic update на per-account state, временна блокировка и expiry. Policy е configurable чрез MAX_ATTEMPTS, LOCK_DURATION и RESET_AFTER_SUCCESS. В предоставената реализация failed-attempt window се обновява при failure; блокираните requests се отказват преди нов failure. Map е bounded приблизително, но single-process counters не са защита за cluster. Анализирайте IP throttling като втори слой, доверен само на презаписания proxy address.

### Проверка с регресионен тест

Изпълнете:

```powershell
mvn test '-Dlab.mode=lab04' '-Dtest=WebSecurityTest#lab04*'
```

Преди поправка очаквайте assertion failure, който показва дефекта. След поправката същата команда трябва да премине. Infrastructure error не е валиден red security test.

Примерен test fragment (пълният runnable class и imports са в `vulnerable-app/src/test/java/bg/tuvarna/lab`):

```java
for (int i = 0; i < 5; i++) {
    mvc.perform(post("/login").with(csrf()).param("username", "alice")
       .param("password", "wrong")).andExpect(status().isUnauthorized());
}
mvc.perform(post("/login").with(csrf()).param("username", "alice")
   .param("password", "Lab-alice-2026!")).andExpect(status().isUnauthorized());
```

Матрица на примерните проверки:

- под прага → валиден login е приет;
- прагът е достигнат → следващ валиден login е отказан;
- blocked request → generic 401 и без изместване на срока;
- точно на expiry → разрешен нов опит;
- success reset true/false → съответните counters;
- concurrent failures → няма изгубени increments;

Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.
