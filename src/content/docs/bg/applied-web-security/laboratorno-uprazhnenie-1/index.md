---
title: "Упражнение 1 — Лабораторна среда и Threat Modeling"
sidebar:
  order: 1
  label: "Упражнение 1"
---

# Упражнение 1 — Лабораторна среда и Threat Modeling

## 1. Теория

### 1.1. Модел на заплахите и архитектура

1. **Модел на заплахите (threat model)** описва какво защитаваме, от кого и с какви мерки.
   - **Asset** е ценен ресурс; например документ. **Actor** е участник; например потребител без права върху документа.
   - **Attack surface** е съвкупността от достъпни входове; **entry point** е конкретен вход, например `/register`.
   - **Likelihood** е вероятност, **impact** е въздействие, а **residual risk** е рискът след защитата. При оценки 2 и 3 рискът е 6 по скалата 1–3.
2. **DFD (Data Flow Diagram)** показва потоците от данни и границите на доверие (**trust boundaries**).
   - Пример: браузър → Nginx → приложение → база данни (DB). TB1, TB2 и т.н. са означения на границите в схемата.
   - **Reverse proxy** приема заявката и я препраща. **Endpoint** е адрес и HTTP метод, например GET /health. **Endpoint inventory** е списък на тези входове.
3. **STRIDE** групира заплахите: подмяна на самоличност, промяна на данни, отричане на действие, изтичане на информация, отказ на услуга и повишаване на привилегии.
   - Пример: подаване на role=ADMIN при регистрация е опит за повишаване на привилегии; защитата задава ролята на сървъра.
4. **Authentication** установява самоличността; **authorization** определя правата. **Deny-by-default** отказва всичко, което не е изрично разрешено; **allowlist** изброява разрешеното.
   - Пример: /health е публичен, /api/me изисква вход, а неизвестен маршрут се отказва.
5. **Loopback** е адрес за достъп от същия компютър (127.0.0.1); **wildcard** адрес приема връзки през всички съответни интерфейси.
   - Docker `ports` публикува порт на хоста; `expose` описва вътрешен порт. `internal` мрежа ограничава външната свързаност.
   - **Runtime role** е потребителят на базата при работа на приложението; **DDL** са команди за структурата, например CREATE TABLE. Приложението може да има SELECT без CREATE.

### 1.2. Прилагане на понятията

1. Asset е ценност: документи, самоличност, ключ, availability. Threat е нежелано събитие, vulnerability — причиняваща слабост, exploit — конкретно използване, risk — likelihood × impact, mitigation — намаляващ риска control. Threat actor има capability и достъп; не приемайте, че anonymous и authenticated user имат еднаква позиция.

2. Trust boundary се преминава при промяна на доверие, например client → server и app → DB. Reverse proxy не валидира ownership. STRIDE групира Spoofing, Tampering, Repudiation, Information disclosure, Denial of service и Elevation of privilege. OWASP Top 10 е ориентир за пропуски, а не доказателство за сигурност. В курса използваме likelihood/impact 1–3 с изрично описани предпоставки.

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

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [подготовка на средата](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Не се изисква предварително познаване на конкретен exploit. Елементарните Java конструкции не се преговарят.

### Инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/lab01-threat-modeling/resources/README.md) съдържа работна карта и очаквани наблюдения.

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

**Фокус:** Compose, Nginx, endpoint inventory и trust boundaries. Съпоставете с [DFD и endpoints](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/system-overview.md); отбележете кой input е недоверен и къде се взема security решението.

## 3. Примерен проблем

Екипът добавя портал и твърди, че „щом е зад Nginx, е защитен“. Преди да оцените конкретен exploit, трябва да установите кой може да достигне всеки компонент и какви данни преминават през него.

### Начален код

```yaml
services:
  db:
    ports: ["5432:5432"]  # host interfaces, заобикаля app policy
```

### Стъпки за решаване

Преди работа следвайте [стартиране и възстановяване](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Командите за Maven се изпълняват от `vulnerable-app`, а Compose командите — от корена на курса.

#### Стъпка 1

Стартирайте приложението според стъпките в setup.md. Запишете `docker compose ps` и `docker compose config`; очакват се само loopback ports 8080/8081. Не копирайте secret file или environment dumps в отчета.

#### Стъпка 2

Изпълнете `curl.exe -i http://localhost:8080/health` и `/api/me`. Очаквания: 200 и 401. Влезте като Alice и повторете `/api/me` → нейното username. Съпоставете резултатите с endpoint inventory.

#### Стъпка 3

Прочетете `SecurityConfig.java`, `infra/nginx.conf` и Compose. Начертайте DFD с TB1–TB4; за всеки поток отбележете protocol, identity и sensitive data. Обяснете защо `expose` и `ports` не означават едно и също.

#### Стъпка 4

Сравнете unsafe YAML с реалната конфигурация, без да публикувате DB. Опишете заплахата с actor, entry point, asset и impact. Подредете поне пет threats по риск; добавете evidence и предложен test.

#### Стъпка 5

Изпълнете baseline test, добавете автоматизирана проверка, че `docker compose config --format json` няма публикувани DB/app ports и host_ip на proxy ports е 127.0.0.1. Проверете и отрицателен конфигурационен fixture в памет, без стартиране на unsafe topology.

### Анализ на причината

Нарушеното assumption е „вътрешният компонент е недостъпен по дефиниция“. Публикуван host port създава отделен entry point извън приложната authentication/authorization. Наличието на Nginx не променя правата върху директна DB връзка.

Причинната верига се описва като: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/threat-model.md); аргументирайте residual risk след fix.

### Реализация на защита

Публикувайте само Nginx на loopback, премахнете host ports на DB/app, използвайте internal network и отделен runtime DB role. За HTTP routes приложете explicit allowlist и deny-by-default. Докажете controls поотделно: network isolation не замества identity или object policy.

### Проверка с регресионен тест

Изпълнете:

```powershell
mvn test '-Dtest=WebSecurityTest#lab01*'
```

Началната baseline проверка трябва да е green; вашият unsafe config fixture трябва да бъде отхвърлен.

Примерен test fragment (пълният runnable class и imports са в `vulnerable-app/src/test/java/bg/tuvarna/lab`):

```java
mvc.perform(get("/api/me")).andExpect(status().isUnauthorized());
mvc.perform(get("/unlisted").with(user("alice")))
   .andExpect(status().isForbidden());
```

Матрица на примерните проверки:

- public health → 200;
- anonymous /api/me → 401;
- authenticated unknown route → 403;
- Compose DB/app ports → няма;
- unsafe config fixture → проверката трябва да го отхвърли;

Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.
