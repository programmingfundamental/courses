---
title: "Подготовка и стартиране"
sidebar:
  order: 0
---

# Подготовка и стартиране

Изтеглете [учебния проект](https://github.com/programmingfundamental/courses/tree/main/applied-web-security) чрез Git:

```sh
git clone https://github.com/programmingfundamental/courses.git
cd courses/applied-web-security
```


Практически курс за IV курс, бакалавър, специалност „Киберсигурност“: 10 занятия по 110 минути. Общото приложение е учебен портал с registration, login, profile, comments, search, документи, admin секция и REST/JWT API. Данните и паролите са синтетични. Материалите са на български; стандартните security термини са обяснени при въвеждането им.

**Всички offensive действия в курса се изпълняват единствено срещу предоставената локална лабораторна среда.**

## Предварителни знания и резултати

Необходими са добро програмиране, Java, основи на Spring Boot, HTTP, SQL, клиент–сървър архитектура, Linux, Docker и мрежи. След курса студентът анализира trust boundaries, възпроизвежда локално vulnerability, аргументира риск, реализира server-side mitigation и пише отрицателни и положителни regression tests. Authentication доказва самоличност; authorization решава кои действия са разрешени.

## Единен работен модел

```text
Vulnerable system → Observe behavior → Reproduce vulnerability
→ Identify root cause → Implement mitigation → Write regression test
→ Verify normal behavior
```

Използвайте отделен commit за evidence/test и за fix. Всеки студент предава кратък отчет, diff и машинния test report. Поправката трябва да работи и при избран уязвим лабораторен режим; превключване към `secure`, изтриване на тест или промяна на очаквания статус не е решение.

## Архитектура и версии

```text
Browser/curl → localhost:8080 → Nginx → Spring Security → Controller
                                                    → Service → PostgreSQL
Browser → localhost:8081 → локална CSRF демонстрационна страница
```

Java 21, Maven 3.9+, Spring Boot 3.5.7 / Spring Security 6.5.6, PostgreSQL 17.6, Nginx 1.28.0. Това са фиксирани учебни версии, не твърдение за най-нови или production-safe зависимости. Преди нов семестър преподавателят проверява advisories и тества актуализацията. Няма нужда от Node, Postman или външен identity provider. H2 е само бърз локален/test backend; PostgreSQL е реалният Compose backend.

Вижте [архитектурата](/courses/bg/ueb-sigurnost/materiali/architecture/system-overview/), [threat model](/courses/bg/ueb-sigurnost/materiali/architecture/threat-model/), [източниците](/courses/bg/ueb-sigurnost/materiali/architecture/references/) и [проверка на комплекта](https://github.com/programmingfundamental/courses/blob/main/applied-web-security/VALIDATION.md).

## Стартиране с Docker

Командите по-долу са PowerShell, от `applied-web-security`. За curl използвайте `curl.exe` в Windows, за да избегнете alias на PowerShell. Нужни са работещ Docker Desktop с Linux containers и свободни портове 8080/8081.

```powershell
./scripts/init-secrets.ps1
$env:LAB_MODE='secure'
docker compose config --quiet
docker compose up -d --build
docker compose ps
curl.exe -i http://localhost:8080/health
```

Изчакайте `/health` да върне 200 и `{"status":"UP"}`; кратък 502 по време на стартиране означава, че app още не е готово. Отворете `http://localhost:8080/login` за генерираната login форма. Успешният login връща 204; след това отворете `/api/me` или `/profile`.

Публикувани са само `127.0.0.1:8080` и `127.0.0.1:8081`. App и DB нямат host ports. Backend network е `internal: true`; само Nginx участва и във frontend bridge за host port publication. Не променяйте bind адреса, не създавайте tunnel и не публикувайте образа като публична услуга. Nginx презаписва forwarding headers. Runtime DB потребителят няма DDL/CREATE права; init се изпълнява от отделен owner.

При заети ports задайте `LAB_HTTP_PORT` и `LAB_CSRF_PORT` с други локални стойности преди Compose startup. Адаптирайте URLs в client scripts и action в lab07 HTML fixture към същите ports; не прекратявайте чужди services. За стандартните упражнения се използват 8080/8081.

За Bash: `mkdir -p .secrets; umask 077; openssl rand -base64 32 > .secrets/field-key.txt` **само при първоначално създаване**; после `LAB_MODE=secure docker compose up -d --build`. Не генерирайте нов ключ върху съществуваща БД с encrypted данни.

### Учебни credentials

| User | Password | Role | Собствени документи |
|---|---|---|---|
| alice | Lab-alice-2026! | USER | 1, 3 |
| bob | Lab-bob-2026! | USER | 2 |
| admin | Lab-admin-2026! | ADMIN | вижда всички по ID |

Тези пароли и DB credentials са публични учебни fixtures, никога не ги използвайте другаде. `/register` приема form parameters `username,password` и CSRF token; role винаги се задава от сървъра като USER. Паролите по подразбиране са BCrypt с генерирана salt. В `lab02` новите записи умишлено са `{noop}`.

### Работа с authenticated requests

```powershell
. ./scripts/lab-client.ps1
Invoke-RestMethod http://localhost:8080/api/me -WebSession $LabSession
Invoke-RestMethod http://localhost:8080/api/profile -Method Post `
  -WebSession $LabSession -Headers $LabHeaders -Body @{displayName='Alice Lab'}
```

Script-ът взема CSRF token преди login и нов token след login, когато Spring сменя session/CSRF контекста. При `lab07` CSRF е изключен и headers са празни. За Bob: `. ./scripts/lab-client.ps1 -UserName bob -Password 'Lab-bob-2026!'`. Не записвайте cookie jar, пароли и токени в Git или отчети.

### Режими и прогресия

`LAB_MODE=labXX` включва само съответния дефект; останалите controls са reference baseline. `lab01` няма exploit switch: студентът сравнява реалната конфигурация с unsafe пример в условието. `lab10` комбинира IDOR, SQL Injection и XSS. Режимът се чете при стартиране. След промяна на code изпълнете `docker compose up -d --build`; след промяна на env — `docker compose up -d --force-recreate app`.

| № | Упражнение | Основен code anchor | Тестова проверка |
|---|---|---|---|
| 01 | [Среда и Threat Modeling](/courses/bg/ueb-sigurnost/laboratorno-uprazhnenie-1/) | Compose, SecurityConfig | WebSecurityTest#lab01* |
| 02 | [Authentication](/courses/bg/ueb-sigurnost/laboratorno-uprazhnenie-2/) | Accounts, SecurityConfig | WebSecurityTest#lab02* |
| 03 | [Authorization и IDOR](/courses/bg/ueb-sigurnost/laboratorno-uprazhnenie-3/) | Documents.get | WebSecurityTest#lab03* |
| 04 | [Brute force защита](/courses/bg/ueb-sigurnost/laboratorno-uprazhnenie-4/) | LoginGuard, provider | LoginGuardTest; WebSecurityTest#lab04* |
| 05 | [SQL Injection](/courses/bg/ueb-sigurnost/laboratorno-uprazhnenie-5/) | Documents.search | WebSecurityTest#lab05*; PostgresIT |
| 06 | [XSS](/courses/bg/ueb-sigurnost/laboratorno-uprazhnenie-6/) | Web.render | WebSecurityTest#lab06* |
| 07 | [CSRF и browser security](/courses/bg/ueb-sigurnost/laboratorno-uprazhnenie-7/) | session chain | WebSecurityTest#lab07*; CookieIT |
| 08 | [Криптография](/courses/bg/ueb-sigurnost/laboratorno-uprazhnenie-8/) | Vault, Web.sensitive | VaultTest; WebSecurityTest#lab08* |
| 09 | [JWT security](/courses/bg/ueb-sigurnost/laboratorno-uprazhnenie-9/) | Tokens, tokenChain | JwtSecurityTest |
| 10 | [Интегрирана защита](/courses/bg/ueb-sigurnost/laboratorno-uprazhnenie-10/) | целият проект | пълната suite; AuditTest |

Guided controls имат reference implementations в кода; самостоятелните задачи разширяват приложението и нямат готови студентски решения. Преподавателят предоставя `labXX.md`, `resources/`, общия проект и архитектурата; `instructor-notes.md` са отделни преподавателски материали и не трябва да се публикуват в студентския сайт.

## Автоматизирани тестове

От `vulnerable-app`:

```powershell
mvn test
mvn verify -Psecurity-tests
# Очакван RED преди поправката:
mvn test '-Dlab.mode=lab03' '-Dtest=WebSecurityTest#lab03*'
# Същата команда след fix трябва да стане GREEN; режимът остава lab03.
```

`mvn test` изпълнява JUnit/MockMvc тестове и unit tests без Docker. `mvn verify -Psecurity-tests` добавя истински PostgreSQL с Testcontainers и CookieIT с embedded Tomcat. Docker е задължителен за PostgresIT; тестът не се пропуска мълчаливо при липса на engine. Failsafe задава Docker API 1.44 за съвместимост с Docker 29; настройката може да се промени с `-Ddocker.api.version=...` при друг engine. Reports: `target/surefire-reports` и `target/failsafe-reports`. MockMvc не изпълнява JavaScript и не доказва browser cookie enforcement. Browser проверките са описани отделно; тестовете за XSS проверяват encoded response и CSP, не симулират browser engine.

Всеки fix трябва да има тест, който се проваля на уязвимия code path и преминава на поправения, и положителен тест за нормална функционалност. HTTP 200 сам по себе си не е достатъчен: проверявайте identity, owner, съдържание и persistence. Не използвайте реални secrets, wordlists или destructive payloads.

От корена на курса: `./scripts/check-structure.ps1` проверява комплектността; `./scripts/check-network.ps1` проверява Compose network invariants; `./scripts/smoke.ps1` изпълнява реални HTTP flows през proxy в secure mode. Smoke test добавя synthetic comment и sensitive field. Преподавателският `./scripts/check-vulnerable-modes.ps1` доказва, че оригиналните уязвими режими причиняват assertion failures; след студентски поправки този diagnostic runner закономерно вече не минава.

За configurable limiter задайте например `$env:MAX_ATTEMPTS='3'`, `$env:LOCK_DURATION='PT30S'`, `$env:RESET_AFTER_SUCCESS='false'` преди `docker compose up -d --force-recreate app`. Премахнете тези env overrides преди следващото упражнение, за да възстановите documented defaults.

## Reset

За lockouts, sessions и JWT signing keys: `docker compose restart app`. Това не чисти DB и не сменя persistent AES ключа. JWT токените се обезсилват при restart, защото учебният RSA key е в паметта. Изчистете browser cookies, отново влезте и вземете нов CSRF token.

За пълен reset на **само този учебен project** от тази папка:

```powershell
docker compose down -v
$env:LAB_MODE='lab02'  # или следващото упражнение
docker compose up -d --build
```

`down -v` изтрива учебните записи и seed-ва наново трите users/документи. Проверете `docker compose config` и project name `applied-web-security` преди командата. Не използвайте `docker system prune`. При смяна от lab02 към secure е нужен пълен reset, защото съществуващите `{noop}` hashes не се пренаписват автоматично. При lab08 reset изчиства plaintext данните преди encrypted режим; студентът отделно проектира migration. `.secrets/field-key.txt` остава: загубата му прави старите encrypted стойности нечетими.

Без Docker за бърза работа: `mvn spring-boot:run` стартира loopback-only app с временна H2 база и временен AES key; restart губи данните. Това не заменя проверката с PostgreSQL/Nginx.

## Ethics / responsible use и ограничения

Работете само с localhost, предоставените containers и писмено определената изолирана учебна мрежа. Симулациите са ограничени по брой и не извличат чужди данни. Ако намерите проблем извън лабораторията, спрете експеримента и уведомете отговорното лице по утвърдения канал; не проверявайте чужда система „за сравнение“.

HTTP режимът е умишлен за лесно локално стартиране: session cookie има HttpOnly и SameSite=Lax, но **Secure=false**. При TLS termination трябва `COOKIE_SECURE=true`; CookieIT проверява emitted flags, а browser проверката изисква реален HTTPS. Учебният in-memory limiter е за един process и има ограничение на броя entries; не е production/distributed rate limiter. Курсът не представя default режима като завършен production продукт.
