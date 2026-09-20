# Mobile Activity Platform — архитектурен договор

## Собственост и граници

| Компонент | Притежава | Не притежава |
|---|---|---|
| Android | локален UI state, pending operations, последно наблюдавани versions | service topology, authoritative server status |
| Gateway / Envoy | public routing, request ID, transport timeouts, access timing, ingress spans | business aggregation, Activity validation |
| Mobile BFF | screen-specific projections и orchestration | чужди таблици, authoritative User/Activity records |
| User Service | user identity/profile demo record | Activity storage |
| Activity Service | Activity lifecycle/version, request ledger | mobile UI layout |
| Notification Service | notification policy/read model | промяна на Activity source of truth |
| Broker | transport/retention на events | точно еднократно business изпълнение |

Service boundaries се обсъждат преди разделяне: latency, consistency и deployment complexity са цена. В лабораторията услугите са в различни processes и имат отделни ports. Общ Maven parent споделя versions, не database tables. H2 file е устойчив локален storage за една Activity instance; не е multi-replica production database.

## Contracts и еволюция

Activity: `id`, `userId`, `title`, `status`, `version`, `createdAt`. Create command няма server `id/version/status`. Title е trimmed 1..120; userId е до 40 chars. Status е CREATED/ACTIVE/DONE. Version расте монотонно за един Activity. Timestamps са UTC ISO 8601 на wire; вътрешният store използва epoch ms. DTO mapping е explicit.

Основни endpoints: User `/users/{id}`; Activity `/activities`, `/activities/{id}`, student `/activities/search`; BFF `/mobile/home`, `/mobile/activities`; WebSocket `/realtime`. Gateway публикува `/api/...` и премахва само `/api` prefix. `/internal/events`, `/q/*`, broker и databases не са public mobile routes. Fixture `/api/fixture/activities` се използва само за Lab 2/7 и се премахва от security exercise ingress.

Common error body: `code`, `message`, `requestId`; не излагайте stack traces/SQL/credentials. Validation е 400 или последователно дефинирано 422; not found 404, identity conflict 409, unauthorized 401, forbidden 403, downstream unavailable 502/503, deadline 504. Успешният create е 201 с Location. BFF не преобразува чуждо 500 в mobile 200 с празен успешен списък.

Lab 3 pagination е bounded: size 1..50, stable secondary ID sort, validated filter allowlist. Offset е достатъчен за дадения fixed dataset; при concurrent writes студентът описва drift и кога cursor е нужен. API добавя optional fields съвместимо; unknown enum values имат explicit client fallback.

## Network и time budgets

Connectivity VALIDATED е OS оценка за Internet, не доказателство за health на нашия API. Transport може да съдържа VPN и underlying network; metered не означава offline. Callback state служи за presentation/scheduling hints, а реалната заявка има собствен result.

Първоначален mobile callTimeout 5 s; Gateway HTTP route 4 s; BFF budget 2.5 s; downstream connect 0.5 s/read 1.2 s. Lab 7 преизчислява attempts+backoff в този budget. Не умножавайте retries в Android, Gateway, BFF и service едновременно. Gateway няма automatic retries в starter. Idempotent GET може да има ограничен retry; POST има uncertain outcome, докато Lab 8 не въведе durable idempotency.

За WebSocket HTTP request timeout=0 е отделен route избор, а не безкраен application lifetime: client visibility, heartbeat deadline, reconnect budget и cleanup остават задължителни. Не изпълнявайте постоянен socket във WorkManager.

## Offline и events

Room е mobile source of truth. Създаването на local Activity и PendingOp е една transaction. Pending→Sending→Synced са explicit states; permanent validation и identity errors се различават от retryable failure. След crash Sending се reconcile-ва по lease/attempt policy. Sync работи на batches до 20 и unique work; осигурете wake-up при enqueue по време на завършващ Worker, а не само KEEP без проверка на опашката.

Lab 8 определя idempotency key scope `(principal, operationId)`, canonical payload hash и retention. Ledger и Activity insert трябва да са атомарни; ключът се запазва при network retry, process restart и изгубен response. Различен payload със същия ключ е conflict. Authentication principal заменя demo userId в Lab 10.

Event envelope: `eventId`, `activityId`, `userId`, `version`, `status`, `occurredAt`. Command заявява промяна; event описва commit-нат факт. Event version се сравнява за конкретен aggregate; wall time не е глобален total order. Broker key е activityId. Reconnect изисква snapshot reconciliation — WebSocket няма сам по себе си durable replay. Duplicate/stale events са нормални.

Starter relay е transport scaffold за synthetic data и една BFF instance. Не гарантира durable notification delivery, atomic DB+Kafka commit или cross-instance fanout. Lab 9 показва тези gaps и обсъжда outbox/consumer inbox, без да изисква production event platform за 90 минути. Lab 10 затваря публичния учебен WebSocket route. Повторното му активиране изисква per-user stream isolation и authentication; извън изолираната учебна мрежа internal relay изисква service identity.

## Security и telemetry

OAuth2 описва authorization; OIDC добавя identity; JWT е token format. Mobile public client няма embedded client secret; production login е system browser Authorization Code + PKCE. Лабораторният token tool дава само подписани test credentials и не е login service. Services проверяват signature, issuer, audience, expiry и roles; resource ownership се проверява отделно от role. Downstream token propagation е само към фиксирани trusted URLs и подходяща audience; production може да използва token exchange/service identity.

Gateway е единствен вход, но routing не е authentication. Lab 10 затваря exercise routes и налага BFF/service authorization. TLS exception е debug-only loopback. Token/PII не влизат в request ID, logs, metric labels или URL query. Корелационният ID е opaque и ограничен; traceparent е tracing context, не identity.

Envoy създава ingress span; Quarkus OTel instrumentation продължава контекста през REST Client. Collector приема OTLP и изпраща към Jaeger. Metrics `/q/metrics` се четат само административно вътре в environment. Няма public wildcard management route. Report сравнява p50/p95/max с брой samples, request/attempt counts и bytes; не сумира паралелни span durations като end-to-end latency.
