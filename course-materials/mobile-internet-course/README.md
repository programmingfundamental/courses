# Интернет за мобилни устройства

Комплект от **10 лабораторни упражнения по 90 минути** за магистри по софтуерно инженерство. Общият проект **Mobile Activity Platform** изследва mobile connectivity, договори, distributed failures, offline synchronization и наблюдение на система от край до край. Всяко занятие включва **20 минути самостоятелна работа в часа**, с проверим резултат.

## Аудитория и предварителна подготовка

Предпоставки: уверено Java/Kotlin програмиране, HTTP, ООП, concurrency, начално Android развитие, Git и IDE. Не се преподава основен синтаксис. Преди първото занятие преподавателят проверява build и downloads на лабораторните машини; това време не влиза в 90-те минути.

## Резултати от обучението

- Разграничава network capability от достижимост на конкретен API.
- Проектира cancellable mobile requests с explicit failure model и retry budget.
- Реализира валидирани, ограничени по размер API contracts.
- Аргументира service boundaries и различните отговорности на Gateway и BFF.
- Измерва mobile round trips, bytes и latency distributions.
- Осигурява offline writes, eventual consistency и идемпотентни effects.
- Обработва duplicate/stale events и reconnect без exactly-once допускания.
- Диагностицира latency и failures чрез trace, logs и metrics при правилни security boundaries.

## Програма

| № | Материал | Самостоятелно инженерно решение |
|---|---|---|
| 1 | [Mobile Connectivity](lab01-connectivity/lab01.md) | reducer и bounded transition history |
| 2 | [Resilient API Client](lab02-mobile-api-client/lab02.md) | retry classification и budget |
| 3 | [Quarkus Backend API](lab03-quarkus-api/lab03.md) | pagination/filter contract |
| 4 | [Microservices](lab04-microservices/lab04.md) | трета service boundary |
| 5 | [API Gateway](lab05-api-gateway/lab05.md) | единен error contract |
| 6 | [Mobile BFF](lab06-bff/lab06.md) | screen DTO и partial failure |
| 7 | [Resilience](lab07-resilience/lab07.md) | policy за нов failure scenario |
| 8 | [Offline Synchronization](lab08-offline-sync/lab08.md) | lost-response idempotency |
| 9 | [Realtime Events](lab09-realtime-events/lab09.md) | reconnect, duplicate/stale handling |
| 10 | [Security и Observability](lab10-security-observability/lab10.md) | диагноза по trace/logs/metrics |

```text
Mobile Connectivity → Resilient API Client → Quarkus Backend
 → Microservices → API Gateway → Mobile BFF → Resilience
 → Offline Synchronization → Realtime Events → Security + Observability
```

## Архитектура и реални директории

```text
Android (Kotlin/Compose/Room/WorkManager/OkHttp)
   |
Envoy API Gateway — routing, request ID, timing, trace
   |
Quarkus Mobile BFF — screen DTO, aggregation, partial result
   +---- User Service
   +---- Activity Service ---- H2 durable store
   +---- Notification Service
                ^
Activity events → Kafka-compatible Redpanda → Notification → BFF → WebSocket

Gateway + Quarkus spans → OTel Collector → Jaeger
```

[Архитектурният договор](architecture/system-overview.md) описва ownership, API paths, timeout budgets и ограниченията. `platform/android/` е един развиващ се Android проект. `platform/backend/` е Maven reactor с четири независими Quarkus приложения. `platform/infra/` съдържа Envoy и Collector configuration. Всеки `labXX-*/starter/` има runnable launcher, manifest с файловете за редакция и инструкции към **същия** общ проект. Не се създава нов Android/Quarkus проект всяка седмица.

Starter се компилира и стартира, но учебните endpoints връщат `501 LAB_TODO`, докато не бъдат реализирани. Fixture API е отделен и работи предварително. Предходните студентски решения се запазват; стартовата среда не е скрито пълно решение на десетте задачи.

## Software и hardware

JDK 21, Maven 3.9+, Node.js 22+, Docker с Linux containers и Compose v2, Android Studio, SDK 36/platform-tools и Android emulator API 26+. Използвайте Git и curl; PowerShell потребителите могат да извикват `curl.exe`. Конфигурацията фиксира Quarkus 3.37.4; Android AGP 8.13.2, Gradle wrapper 8.13, Kotlin/Compose compiler 2.3.10, Compose BOM 2025.12.00, Room 2.8.4, WorkManager 2.10.5 и OkHttp 5.1.0. Точните dependencies са в POM/Gradle файловете, без динамични версии.

Препоръчителна машина: 16 GB RAM, около 10 GB свободно място за images/caches. Emulator е достатъчен за целия курс. Физически Android телефон със cellular/VPN е полезен за Lab 1–2; при липса на такъв се използва replay на NetworkSnapshot и emulator network controls. Battery/radio изводи не се обобщават от emulator. Не са необходими sensors или BLE.

## Първоначално стартиране

Командите са от `mobile-internet-course/platform/`:

```powershell
node tools/token.mjs --init
mvn -B -f backend/pom.xml verify
docker compose up -d --build
curl.exe http://localhost:8080/health
curl.exe "http://localhost:8080/api/fixture/activities?mode=ok"
```

Изчакайте Quarkus услугите да приключат startup, преди да проверите fixture. `/health` потвърждава само работещ Gateway; успешният fixture request потвърждава и връзката му с BFF.

Android: отворете `platform/android/` в Android Studio, задайте локалния SDK и стартирайте app. От същата Android директория:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
```

На Linux/macOS използвайте `sh ./gradlew`. За emulator base URL е `http://10.0.2.2:8080/api/`. За физическо устройство използвайте `adb reverse tcp:8080 tcp:8080` и `http://127.0.0.1:8080/api/`; host listener остава на loopback. Cleartext е разрешен само в debug build за loopback/emulator hosts. Production клиентът използва HTTPS с нормална certificate verification; няма trust-all TLS код.

За Lab 9 добавете broker и events profile:

```powershell
docker compose -f compose.yaml -f compose.events.yaml --profile events up -d --build
```

За tracing добавете `--profile observability` към същата команда. Jaeger е на `http://localhost:16686`. Отсъстващ Collector в ранните labs не означава дефект на API; при ненужно export логване задайте `QUARKUS_OTEL_SDK_DISABLED=true` за локален run. За спиране на цялата среда използвайте `docker compose -f compose.yaml -f compose.events.yaml --profile events --profile observability down`; без `-v`, за да останат данните.

## Ports

| Компонент | Port | Достъп |
|---|---:|---|
| Gateway | 8080 | host loopback; единен mobile entry point |
| User / Activity / Notification / BFF | 8081 / 8082 / 8083 / 8084 | само Compose network; същите port номера при IDE run |
| Redpanda Kafka | 9092 | само Compose network |
| Collector OTLP gRPC | 4317 | само Compose network |
| Jaeger UI | 16686 | host loopback |
| Lost-response proxy | 18080 | допълнителен loopback test tool за Lab 8 |

Имената `users`, `activities`, `notifications`, `bff`, `broker`, `collector` са Docker DNS имена, не mobile URLs. Не публикувайте internal ports, за да заобикаляте Gateway. За backend development стартирайте избран service с `mvn quarkus:dev` от неговата директория и тествайте с host curl; това е development access, не mobile architecture.

При IDE/host run задайте `LAB_PUBLIC_KEY` на `file:` плюс абсолютния път до вашия `platform/.runtime/keys/publicKey.pem`. Например от `platform/` в PowerShell: `$env:LAB_PUBLIC_KEY = 'file:' + (Resolve-Path '.runtime/keys/publicKey.pem').Path`. Предайте същата променлива в IDE run configuration. Compose вече задава правилния container path. Така JWT проверката използва локално генерирания ключ, а не примерния public key от resources.

## Security fixture и ограничения

`node tools/token.mjs member` създава кратък **лабораторен** RS256 access token; `viewer`, `--expired`, `--wrong-audience` и `--other-user` дават negative cases. Private key се генерира локално в ignored `.runtime/keys/`; в service containers се монтира само public key. Fixture issuer не е OIDC provider или production login. Lab 10 обяснява Authorization Code + PKCE за публичен mobile client и валидира signature/issuer/audience/expiry/roles на API boundaries. Не записвайте tokens в reports, logs или Git.

За предварително подготвения бавен downstream сценарий в Lab 10 добавете `-f compose.slow-user.yaml` след `-f compose.events.yaml` към командата за Compose. User Service забавя всяка трета заявка с 900 ms, под базовия downstream read timeout. Сравнете trace преди и след забавянето. Премахването на overlay от командата и ново `up -d` възстановява обичайното поведение.

## Работен процес

Всеки starter се build-ва от собствената си директория с `node ../../platform/tools/lab.mjs XX build`, където XX е номерът. Launcher компилира нужните shared modules; промените са в общия `platform/`, не в копие. `TODO Lxx-G` е водена задача, `Lxx-I` — самостоятелна. TODO връща explicit unsupported state/501, никога фиктивен success. Запазвайте tags `lab01`…`lab10` и `results/labXX/` с tests, measurements, decisions и known limitations.

Основната времева рамка е 10 min проблем, 15 min теория/demo, 35 min водена работа с checkpoint, 20 min самостоятелна задача, 10 min failures/анализ. Преподавателят подготвя fixtures и transport scaffolds предварително. Отчетите са кратки таблици в часа; няма скрито домашно за завършване на основните acceptance criteria.

`labXX.md` са canonical sources за сайта. `node scripts/sync-mobile-internet-labs.mjs` от корена на repository обновява десетте студентски страници. Instructor notes остават извън site content; който има достъп до Git repository, може да ги прочете.

Изпълнените build, runtime и consistency проверки са описани в [VERIFICATION.md](VERIFICATION.md).
