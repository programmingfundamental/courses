# Упражнение 6 — Cross-Site Scripting (XSS)

## 1. Теория

### 1.1. Контекст на браузърното съдържание

1. **Origin** е комбинацията протокол, хост и порт. **DOM** е дървото от елементи на страницата; **sink** е мястото, което използва входа.
   - Пример: innerHTML интерпретира HTML, докато textContent задава текст.
2. **Stored**, **reflected** и **DOM-based XSS** описват съответно съхранен вход, отразен вход от заявка и обработка в браузъра.
   - Пример: коментар с HTML, записан и после включен директно в страницата, създава stored XSS.
3. **Output encoding** представя символите безопасно за конкретното място; **sanitization** премахва недопустими HTML елементи.
   - Пример: в текстов възел < става &lt;. При повторно кодиране &lt; може да стане &amp;lt; — това е double encoding.
4. **Quoted attribute** е атрибут в кавички; **attribute breakout** затваря кавичката чрез входа; **URL scheme** е частта преди двоеточието.
   - Пример: за href валидирайте схемата http/https и кодирайте стойността за атрибут. javascript: остава опасна схема дори при кодирани кавички.
5. **CSP** задава разрешени източници на съдържание; **defense in depth** комбинира няколко защитни слоя.
   - Пример: script-src 'self' ограничава скриптовете, а правилното кодиране запазва коментара като текст. **Marker** е видим индикатор, например промяна на заглавието, с който се установява изпълнение на скрипт.

### 1.2. Кодиране и допълнителни защити

1. XSS означава изпълнение на недоверено browser съдържание в доверен origin. Stored XSS пази input преди output; reflected връща request input директно; DOM-based XSS възниква при unsafe client-side sinks като innerHTML. HTML text, quoted attribute, JavaScript string и URL са различни contexts.

2. Output encoding преобразува специалните символи според destination context. Sanitization премахва недопустима структура, когато rich HTML е изрично изискване; тук comments са plain text, затова не добавяме sanitizer библиотека. CSP (Content Security Policy) ограничава позволените sources/actions и е defense in depth, не замества encoding. HttpOnly пречи на script да прочете cookie, но XSS пак може да извиква same-origin actions. Base64 не обезврежда HTML, ако после го декодирате към unsafe sink.

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

Технически източници и version scope: [references](../architecture/references.md).

## 2. Подготовка

### Предварителни знания

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [подготовка на средата](../setup.md). Изпълнени предходните 5 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

### Инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](resources/README.md) съдържа работна карта и очаквани наблюдения.

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

**Фокус:** Stored comment / reflected query → HTML rendering → browser. Съпоставете с [DFD и endpoints](../architecture/system-overview.md); отбележете кой input е недоверен и къде се взема security решението.

## 3. Примерен проблем

Alice записва comment, който при преглед от Bob променя заглавието на browser tab. Сървърът третира user content като HTML и го изпълнява в origin на портала.

### Начален код

```java
// Web.render в lab06:
return text;
// След това: "<p>" + render(commentBody) + "</p>"
```

### Стъпки за решаване

Преди работа следвайте [стартиране и възстановяване](../setup.md). Командите за Maven се изпълняват от `vulnerable-app`, а Compose командите — от корена на курса.

#### Стъпка 1

Стартирайте lab06; този режим изключва CSP, за да не скрие unsafe rendering. Влезте като Alice и POST /api/comments с body `<script>document.title='LAB-XSS'</script>` и валиден CSRF token.

#### Стъпка 2

Отворете /comments като Bob в отделна browser session. Заглавието става LAB-XSS; няма network exfiltration, alerts или външни ресурси. Запишете само marker и origin.

#### Стъпка 3

Проверете reflected /search?q= със същия marker. В DevTools сравнете Network response и DOM. Намерете точния sink; обяснете защо safe SQL storage не предотвратява browser execution.

#### Стъпка 4

Приложете HtmlUtils.htmlEscape само при HTML text output, запазете original text в DB и добавете CSP към session chain. Проверете, че guided fix работи и в lab06, а не само при сменен mode.

#### Стъпка 5

Пуснете encoding regression tests и повторете browser проверката. Добавете tests за ampersand и вече encoded input, за да откриете double encoding. Browser title вече не се сменя, а literal text се показва.

### Анализ на причината

Сървърът поставя untrusted string в HTML response без context-aware encoding. Browser parser не знае, че string-ът е „само comment“. Storage, authentication и input length checks не променят parsing context.

Причинната верига се описва като: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](../architecture/threat-model.md); аргументирайте residual risk след fix.

### Реализация на защита

За зададения plain-text HTML sink използвайте HtmlUtils.htmlEscape. За DOM text използвайте textContent, а не innerHTML. Не интерполирайте user data в JavaScript source. Guided CSP: `default-src 'none'; form-action 'self'; frame-ancestors 'none'; base-uri 'none'`. При нови UI assets разрешавайте само необходимите sources; не добавяйте unsafe-inline за удобство.

### Проверка с регресионен тест

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

Матрица на примерните проверки:

- ordinary HTML characters → encoded response;
- script-like stored input → literal text, без raw script tag;
- normal Bulgarian text → правилно показан;
- CSP присъства след fix;
- browser marker не се изпълнява; отделно от MockMvc assertions;

Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.

## Самостоятелни задачи

### Задача 1

**Условие:** разширете profile с optional homepage link; unsafe starter в resources поставя стойността директно в href.

**Изисквания:** plain display name да остава безопасен, homepage да допуска само http/https URL и да се рендерира в quoted attribute.

**Ограничения:** не приемайте javascript/data schemes; не използвайте HTML text escaping като единствен URL control; без външни заявки по време на тестовете.

**Критерии за приемане:** автоматизирани tests за quoted attribute breakout, опасна scheme, relative/empty value според описана policy и нормален https URL; browser проверка без marker execution. Не е достатъчно да поправите само comments.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

### Задача 2 — Гранични случаи

- Double encoding при вече съдържащ `&lt;` input.
- Attribute value със затваряща кавичка.
- Опасна URL scheme при правилно escaped attribute.
- CSP блокира exploit, но raw vulnerable sink остава.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.
