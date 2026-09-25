# Упражнение 4 — Microservices Architecture и Service-to-Service Communication

## 1. Инженерен сценарий
Activity Service трябва да потвърди owner, който вече се поддържа от отделен User Service. Локален method call се превръща в remote dependency. Когато User Service е бавен, create заявката блокира mobile UI или грешно връща „потребителят не съществува“.

## 2. Учебни цели
- Аргументира service boundary и ownership.
- Реализира Quarkus REST Client с timeouts.
- Разграничава missing user от unavailable dependency.
- Проследява request ID през remote call.
- Проектира трета услуга с ограничен contract.
- Анализира distributed coupling и partial effects.

## 3. Предварителни знания
Lab 3 DTO/status contract, Java CDI, basic synchronous HTTP. Една Activity операция остава в Activity Service.

## 4. Необходими инструменти
Четирите готови Quarkus modules, Maven/IDE, Docker Compose, curl и REST client test doubles. Android използва същата Activity functionality.

## 5. Архитектурен контекст
```text
Mobile request → [Activity Service → REST Client → User Service]
                      |
                     H2
Самостоятелно: Notification Service → избран owner API
```
User Service притежава profile; Activity Service — activity lifecycle. Модулите са независими processes и не четат чужди tables. Shared build configuration не отменя service boundaries.

## 6. Кратка теория
Разделянето има цена: serialization, network latency, independent failure, contract compatibility и deployment. Cohesion и ownership са аргументи; броят repositories не е цел. Synchronous call дава по-проста консистентна проверка, но добавя temporal coupling.

REST Client interface дефинира downstream URL чрез configKey, не hardcoded container IP. Connect/read timeouts са задължителни. Downstream 404 може да значи unknown user; timeout/503 не доказват липса. Не записвайте Activity преди необходимата user validation, освен ако explicit workflow не допуска pending validation. [Quarkus REST Client](https://quarkus.io/guides/rest-client/) описва registration, response mapping и deadlines.

Correlation ID се предава към фиксирани internal services. Няма arbitrary URL от client input и няма shared database join между owners. Read replica/cache може да намали coupling, но въвежда stale data и invalidation; това се обсъжда, без да се строи нова платформа в часа.

## 7. Мини експеримент
Успешен POST минава през User lookup. Спрете User container и повторете. Сравнете unknown user 404 с connection refused. Запишете кое би било невъзможно при обикновен in-process method call.

## 8. Водена практическа задача — 35 минути, включително checkpoint
### Стъпка 1
Използвайте готовия UsersResource /users/u1 и UsersClient. Инжектирайте @RestClient в Activity service layer; преди create извикайте user API. Пренесете X-Request-ID. В controller не се появява Docker hostname.

### Стъпка 2
Задайте connect 500 ms/read 1200 ms чрез config. Map-нете missing owner към client/domain error, а unavailable dependency към 503 с common body. Затваряйте raw Response, когато client contract връща Response; не swallow-вайте exceptions като false.

### Стъпка 3
Проверете, че неуспешна owner validation не оставя Activity record. HTTP transaction не обхваща две services; не обещавайте атомарна промяна на User+Activity. Не добавяйте retry на create в този lab.

### Стъпка 4
Променете User URL чрез environment и стартирайте отново. Mobile request остава един Activity contract. Добавете latency counters за owner lookup и overall request.

Работете в общия platform проект. От директорията starter изпълнете `node ../../platform/tools/lab.mjs 04 build`. Build проверява scaffold-а и вашите промени; наличието на 501 LAB_TODO означава незавършена учебна функционалност, а не успешен checkpoint.

## 9. Checkpoint
- Activity Service извиква реален User process.
- Unknown и unavailable user имат различни outcomes.
- Timeout прекратява чакането и не оставя нежелан insert.

## 10. Самостоятелна задача — 20 минути в часа
**Problem statement:** Реализирайте първата функционалност на Notification Service като трета самостоятелна boundary.

**Functional requirements:** Изберете една capability, например notification eligibility за activity owner. Опишете собствените данни и remote dependency; реализирайте един read-only service-to-service call и малък response DTO.

**Technical constraints:** Използвайте готовия module и client skeleton подход. Без shared DB, distributed transaction или втори mobile base URL. Scope е един endpoint и един call, не цяла notification система.

**Acceptance criteria:** Endpoint дава валиден резултат при reachable dependency и explicit 503/partial status при outage. Има timeout, ownership diagram и тест с dependency failure. Решението не дублира цялата Activity business logic.

Решението и кратката аргументация се проверяват в края на същото занятие. Това не е домашна работа.

## 11. Failure scenarios / Edge cases
| Сценарий | Очакване / наблюдение |
|---|---|
| User 404 | Unknown owner, без Activity insert. |
| User timeout/connection refused | Dependency unavailable, не user missing. |
| Невалиден downstream JSON | Contract failure с correlation ID. |
| Трета услуга недостъпна | Описана critical/optional policy, без silent success. |

## 12. Тестване
Happy create с u1; negative unknown ID; mock downstream 503, timeout и malformed DTO. Integration спира само users container от текущия Compose project. Сравнете Activity count преди/след failure. Възстановете service и проверете recovery без mobile URL промяна.

## 13. Наблюдение и измерване
Downstream и end-to-end duration, remote call count, errors by dependency и propagated requestId. Показвайте service identity, без tokens/PII. При serial chain latency може да се натрупва; не сравнявайте runs с различен dataset.

Запишете кратка таблица в `results/lab04/observations.md`: configuration, workload, expected/actual, sample count и ограничения. За latency използвайте p50/p95/max, когато имате достатъчно измервания, а не само средна стойност.

## 14. Въпроси за анализ
1. Коя граница оправдава отделен service?
2. Какви проблеми липсваха в един process?
3. Защо timeout не е 404?
4. Кой притежава Activity status?
5. Кога cache би помогнал и какво рискува?
6. Защо mobile не трябва да избира internal service URL?

## 15. Очакван резултат
Две свързани backend services и студентски трети bounded endpoint с ownership и failure policy.

## 16. Критерии за приемане
- [ ] User/Activity ownership е отделен.
- [ ] REST Client URLs са конфигурация.
- [ ] Всеки remote call има deadline.
- [ ] Failure не създава unwanted Activity.
- [ ] Третата услуга има собствен contract.
- [ ] Android не получава internal topology.
