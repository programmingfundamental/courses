# Упражнение 5 — API Gateway и единна входна точка

## 1. Инженерен сценарий
Android build съдържа три service адреса. При backend deployment един от тях се променя и старите приложения спират да работят. Logs имат несвързани IDs, а mobile request към непознат path получава случайна HTML грешка.

## 2. Учебни цели
- Реализира routing през един public endpoint.
- Разграничава Gateway от business service/BFF.
- Проследява request ID и timing.
- Проверява path rewriting и query preservation.
- Проектира една cross-cutting error policy.
- Анализира authentication и rate-limit boundaries.

## 3. Предварителни знания
Lab 4 работещи services; HTTP path/header semantics. Envoy bootstrapping, clusters и Compose DNS са дадени.

## 4. Необходими инструменти
Docker Compose, Envoy, curl, Android client и gateway JSON logs. Не се пише нов proxy от socket primitives.

## 5. Архитектурен контекст
```text
Android → [Envoy Gateway]
              +→ User Service
              +→ Activity Service
              +→ BFF fixture
Internal service ports не са публикувани.
```
Gateway route table се редактира в infra/envoy.yaml. Mobile base URL остава един. Не се добавят joins или business DTO transformations в Gateway.

## 6. Кратка теория
Gateway е стабилен ingress с routing и cross-cutting policies: request metadata, timing, authentication enforcement, ограничаване на натоварване. Business aggregation за конкретен UI принадлежи на BFF. Request ID корелира операции, но не е authorization token и не е идентичен с trace ID.

Prefix matching изисква boundary: /api/users и /api/users/… са допустими, /api/users-evil не е User route. Query string и HTTP method трябва да се запазят. Не route-вайте произволен upstream host от input. Deadline на Gateway трябва да е съгласуван с downstream и mobile budget. [Envoy HTTP routing](https://www.envoyproxy.io/docs/envoy/latest/configuration/http/http_conn_man/route_matching) описва matching.

Starter генерира ingress request ID, презаписва недоверен външен ID и логва route/status/duration/bytes. Не логва Authorization или request bodies. Rate limit е admission policy; в Lab 5 го обсъждаме концептуално — той не замества domain quota. Routing само по себе си не е authentication; Lab 10 добавя проверките.

## 7. Мини експеримент
Изпратете request с измислен X-Request-ID към /health; покажете генерирания ID. Непознат /api/path връща default proxy error. Променете вътрешен service адрес в config и покажете, че mobile base URL не се променя.

## 8. Водена практическа задача — 35 минути, включително checkpoint
### Стъпка 1
Добавете routes за /api/users и /api/activities със exact root плюс bounded child path matching или path_separated_prefix. Rewrite премахва само /api. Използвайте готовите clusters users/activities и HTTP route timeout=4 s.

### Стъпка 2
Проверете GET detail и POST body през Gateway. Запазете query parameters и content type. Проверете /api/users-evil и /internal/events — не трябва да стигат до internal resource.

### Стъпка 3
Проследете X-Request-ID от response към Gateway и service logs/filter. Потвърдете, че header се предава и към REST Client в Lab 4. Basic request timing вече е scaffold; анализирайте какво включва.

### Стъпка 4
Настройте Android на един base URL. Спрете activities и наблюдавайте Gateway status/response flags. Не добавяйте автоматичен retry на POST в Envoy.

Работете в общия platform проект. От директорията starter изпълнете `node ../../platform/tools/lab.mjs 05 build`. Build проверява scaffold-а и вашите промени; наличието на 501 LAB_TODO означава незавършена учебна функционалност, а не успешен checkpoint.

## 9. Checkpoint
- Публичните User/Activity routes работят, включително query и POST.
- Internal management/relay endpoints са недостижими отвън.
- Request ID и timing могат да се свържат с един call.

## 10. Самостоятелна задача — 20 минути в часа
**Problem statement:** Проектирайте единен Gateway-level error response за неизвестен public route и недостъпен upstream.

**Functional requirements:** JSON code/message/requestId или header-linked request identity; различни codes за route missing и dependency unavailable. Запазете реалния HTTP status и content type.

**Technical constraints:** Използвайте local-reply configuration или малък bounded filter, без business lookup и без промяна на успешни body DTOs. Не включвайте internal hostname/stack/token в response.

**Acceptance criteria:** Unknown path е JSON 404; stopped upstream е JSON 503/504 според failure; ID може да се намери в logs. Успешният Activity response остава същият. Опишете защо тази логика е Gateway concern.

Решението и кратката аргументация се проверяват в края на същото занятие. Това не е домашна работа.

## 11. Failure scenarios / Edge cases
| Сценарий | Очакване / наблюдение |
|---|---|
| Неизвестен path | Стандартен JSON 404. |
| Stopped service / connect timeout | 503/504 и request identity. |
| /api/users-evil | Не match-ва User route. |
| Подаден външен request ID | Sanitized/generated ingress ID; без log injection. |

## 12. Тестване
Happy GET/POST през Gateway; compare status/body с direct development service call. Negative paths, query escaping, upstream stop/recovery. Използвайте curl -i за headers. Проверете, че /q/metrics и /internal/events не са public. Validate Envoy config преди restart чрез container envoy --mode validate -c /etc/envoy/envoy.yaml.

## 13. Наблюдение и измерване
Gateway duration, upstream duration когато е налична, response bytes/status и requestId. Сравнете overhead на еднакви requests с/без Gateway при local development; не правете универсален performance извод от localhost.

Запишете кратка таблица в `results/lab05/observations.md`: configuration, workload, expected/actual, sample count и ограничения. За latency използвайте p50/p95/max, когато имате достатъчно измервания, а не само средна стойност.

## 14. Въпроси за анализ
1. Защо business aggregation не принадлежи в Gateway?
2. Какво скрива единният public URL?
3. Защо prefix matching може да е опасен?
4. Как request ID се различава от trace ID?
5. Къде е authentication boundary преди Lab 10?
6. Как retry в Gateway може да удвои POST effect?

## 15. Очакван резултат
Единен ingress с проверени routes, metadata/timing и самостоятелна error policy.

## 16. Критерии за приемане
- [ ] Android използва един base URL.
- [ ] Path boundaries и query се пазят.
- [ ] Management/internal routes са затворени.
- [ ] Има request ID/timing evidence.
- [ ] Самостоятелната error policy има failure tests.
- [ ] Gateway не извършва business aggregation.
