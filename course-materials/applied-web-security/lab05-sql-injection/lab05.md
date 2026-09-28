# Упражнение 5 — SQL Injection

## 1. Теория

### 1.1. SQL структура и стойности

1. **Prepared statement** пази SQL структурата отделно от входните стойности; **binding** свързва стойност с параметър (placeholder).
   - Пример: `WHERE owner=? AND title=?` с параметри alice и O'Reilly търси текст, без апострофът да затваря SQL литерал.
2. **Literal** е стойност, записана в самия SQL; **boolean condition** е логическо условие, например 1=1; `--` започва SQL коментар.
   - Пример: конкатенация на вход с апостроф може да промени логическите условия на заявката.
3. **JPA** е интерфейс за работа с обекти и база; **JPQL** е езикът му за заявки; **native query** изпълнява SQL.
   - Пример: setParameter задава стойност отделно от JPQL текста. Конкатенацията е проблем и при JPA.
4. **Identifier** е име на колона или таблица. **Allowlist** съпоставя позволени входни имена с известни SQL имена.
   - Пример: sort=title избира фиксираната колона title. Параметърът ? не замества име на колона в ORDER BY.
5. **Wildcard** е шаблонен символ: % съвпада с поредица, _ с един символ. **Exact match** използва равенство вместо LIKE.
   - Пример: LIKE '%notes%' намира заглавия, съдържащи notes; title='notes' изисква точно съвпадение.
6. **NUL** е нулевият символ; **stack trace** показва веригата от извиквания при грешка; **SQL dialect** са особеностите на дадена база.
   - Пример: отказвайте недопустим вход с 400 без SQL текст; проверявайте поведението и с PostgreSQL, защото H2 може да се различава.

### 1.2. Защита на заявките

1. SQL Injection възниква когато недоверен input става част от изпълнимата структура на SQL. Prepared statement отделя структурата от bind values. Input validation ограничава допустим domain/размер, но не заменя parameterized SQL. Escaping на apostrophe не е обща защита за всички dialects/contexts.

2. JPA не прави concatenated JPQL/native SQL безопасен; setParameter/позиционни placeholders са нужни и там. Bind parameters са за стойности, не за column/table/ORDER BY identifiers; dynamic identifiers изискват server-side allowlist. LIKE wildcard `%` е search semantics, не SQL injection; parameter binding запазва wildcard поведението. Runtime DB role трябва да има минимални права, но дори read-only injection може да наруши confidentiality. Generic error handling не трябва да връща SQL/schema/stack trace.

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

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [подготовка на средата](../setup.md). Изпълнени предходните 4 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

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

**Фокус:** Documents.search → JDBC query → DB parser. Съпоставете с [DFD и endpoints](../architecture/system-overview.md); отбележете кой input е недоверен и къде се взема security решението.

## 3. Примерен проблем

Search трябва да показва само документите на текущия user. Един search string променя структурата на SQL условието и резултатите вече съдържат Bob документи при Alice session.

### Начален код

```java
String sql = "SELECT id,owner,title FROM documents WHERE owner='"
    + owner + "' AND title LIKE '%" + q + "%' ORDER BY id";
return db.query(sql, rowMapper);
```

### Стъпки за решаване

Преди работа следвайте [стартиране и възстановяване](../setup.md). Командите за Maven се изпълняват от `vulnerable-app`, а Compose командите — от корена на курса.

#### Стъпка 1

Стартирайте lab05 и влезте като Alice. GET /api/search?q=notes → само нейния документ. За сравнение empty q връща нейните два документа.

#### Стъпка 2

Използвайте локалната команда от resources с q=`' OR '1'='1' -- `, без destructive statements. В уязвимия режим резултатът включва Bob. Запишете request parameter и owner списъка като evidence.

#### Стъпка 3

Възстановете получения SQL на хартия. Посочете затворения literal, OR condition и коментара. Проверете O'Reilly: валиден текст не трябва да причинява query error.

#### Стъпка 4

Заменете конкатенацията със `owner=? AND title LIKE ?`; bind-нете principal и `%`+q+`%` като values. Ограничете размера и NUL input; не правете blacklist на SQL keywords.

#### Стъпка 5

Изпълнете lab05 regression и PostgresIT. Добавете test за `%` и `_`, като ясно документирате, че wildcard search е позволен, но owner isolation остава. Проверете, че error response няма SQL.

### Анализ на причината

q преминава от данни към синтаксис преди заявката да достигне DB parser. Authenticated identity не прави произволния search input доверен. Ownership условие в конкатениран query може да бъде заобиколено от променената boolean структура.

Причинната верига се описва като: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](../architecture/threat-model.md); аргументирайте residual risk след fix.

### Реализация на защита

Guided pattern: `db.query("SELECT ... WHERE owner=? AND title LIKE ?", mapper, principal, "%" + q + "%")`. SQL структурата остава константна. За sorting използвайте map от публично enum към фиксирани SQL identifiers; не поставяйте untrusted column name в placeholder. DB runtime user няма CREATE/DDL; обработвайте malformed inputs с 400 без SQL текст.

### Проверка с регресионен тест

Изпълнете:

```powershell
mvn test '-Dlab.mode=lab05' '-Dtest=WebSecurityTest#lab05*'
```

Преди поправка очаквайте assertion failure, който показва дефекта. След поправката същата команда трябва да премине. Infrastructure error не е валиден red security test.

Примерен test fragment (пълният runnable class и imports са в `vulnerable-app/src/test/java/bg/tuvarna/lab`):

```java
mvc.perform(get("/api/search").with(user("alice"))
    .param("q", "' OR '1'='1' -- "))
    .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
mvc.perform(get("/api/search").with(user("alice")).param("q", "O'Reilly"))
    .andExpect(jsonPath("$.length()").value(1));
```

Матрица на примерните проверки:

- normal notes → един result;
- O'Reilly → валиден result;
- structured malicious input → нула results;
- empty input → само собствени docs;
- oversized/NUL input → 400;
- wildcards → allowed semantics, без чужди owners;

Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.

## Самостоятелни задачи

### Задача 1

**Условие:** намерете втория unsafe query в `vulnerable-app/src/main/resources/exercises/UnsafeLookup.java.txt`; той е starter за нов exact-title lookup module.

**Изисквания:** интегрирайте lookup като `GET /api/lookup?title=` и поправете query construction; резултатите трябва да са само за текущия user.

**Ограничения:** snippet-ът не е активен endpoint преди задачата; не копирайте готово решение от search; exact match не е LIKE; без destructive payloads.

**Критерии за приемане:** normal title, apostrophe, empty title, oversized value и boolean structured input са автоматизирано покрити. Един и същ title при Alice/Bob не нарушава isolation. Покажете red test върху unsafe starter и green след fix.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

### Задача 2 — Гранични случаи

- `%`/`_` са allowed wildcard data, но не сменят SQL structure.
- Apostrophe и кирилица в едно заглавие.
- Dynamic sort identifier не може да се bind-не като value.
- H2 compatibility mode не гарантира PostgreSQL semantics.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.
