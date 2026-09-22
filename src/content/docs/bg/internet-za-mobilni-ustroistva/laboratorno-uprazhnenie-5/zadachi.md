---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

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
