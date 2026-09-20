# Упражнение 8 — Offline-First и Synchronization

## 1. Инженерен сценарий
Потребител създава Activity във влак без Internet. При recovery сървърът записва POST, но отговорът се губи точно при Wi-Fi→cellular transition. Приложението остава Pending и повторният sync създава duplicate. Трябва да пазим local intent и да направим effect идемпотентен.

## 2. Учебни цели
- Реализира Room като local source of truth.
- Създава атомарна local operation queue.
- Проектира explicit sync states и recovery.
- Използва bounded WorkManager synchronization.
- Реализира server-supported idempotency.
- Проверява lost response и duplicate concurrent requests.

## 3. Предварителни знания
Lab 2 failure model, Lab 3–7 contracts/BFF. Room DAO/schema и WorkManager dependency са готови; database синтаксисът не е учебната цел.

## 4. Необходими инструменти
Android emulator, Room Inspector, WorkManager tests, Activity H2 ledger table, BFF forwarding и готов tools/drop-response-proxy.mjs.

## 5. Архитектурен контекст
```text
[Compose ← Room local source of truth]
             |
        PendingOp → WorkManager → Gateway → BFF → Activity DB + ledger
```
Mobile продължава да работи локално. BFF пренася command metadata; Activity Service е owner на durable deduplication и business insert.

## 6. Кратка теория
Offline-first започва със local commit, не с показване на network error. Local Activity и PendingOp се записват в една Room transaction. Worker изпраща finite batches; UI наблюдава database, а не transient Worker memory. CONNECTED constraint е scheduling hint, не API success guarantee. Unique work ограничава scheduling duplicates, но не доказва exactly-once. [Android offline-first](https://developer.android.com/topic/architecture/data-layer/offline-first) и [WorkManager](https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work) са основата.

Pending/Sending/Synced и permanent Failed/NeedsAction са explicit. След process death Sending трябва да има recovery/lease policy. Enqueue, когато Worker завършва, може да създаде lost wake-up при наивно KEEP; изберете chaining или recheck/re-enqueue strategy. Metered network може да е допустима за малки commands, а големи uploads да изискват unmetered; това е product policy.

Idempotency key обозначава logical operation, не HTTP attempt. Server пази scope, payload hash и резултат durable; duplicate със същия payload връща същия resource, различен payload със същия key е 409. Ledger и business write са една transaction. Няма distributed transaction между Room и server — reconciliation води до eventual consistency.

## 7. Мини експеримент
Local create при airplane mode е видим веднага. Стартирайте lost-response proxy: той чака успешен upstream POST, после затваря mobile connection без response. Проверете server count преди retry. Това е commit-without-ack, не server rollback.

## 8. Водена практическа задача — 35 минути, включително checkpoint
### Стъпка 1
Създайте application-scoped Room instance от LocalDatabase. Repository transaction insert-ва LocalActivity и PendingOp със stable UUID operationId и canonical payload. UI показва pending status и bounded history100.

### Стъпка 2
SyncWorker чете до 20 operations, изпраща sequentially с общ run budget и обновява local state. Success записва serverId; validation error става NeedsAction; transient transport failure запазва intent и връща retry. CancellationException се propagation-ва.

### Стъпка 3
Планирайте unique WorkManager sync с CONNECTED constraint и exponential backoff. Изберете wake-up стратегия за operation, добавена докато текущият worker завършва. Спрете drain след bounded batch и оставете следващо изпълнение.

### Стъпка 4
В BFF добавете POST /mobile/activities forwarding със същия Idempotency-Key и payload към Activity Service. Водената част показва missing-ack проблема, но server ledger deduplication остава самостоятелната задача. Прибягвайте до debug loopback proxy на 18080 само за fixture.

Работете в общия platform проект. От директорията starter изпълнете `node ../../platform/tools/lab.mjs 08 build`. Build проверява scaffold-а и вашите промени; наличието на 501 LAB_TODO означава незавършена учебна функционалност, а не успешен checkpoint.

## 9. Checkpoint
- Offline create и queue са атомарни и видими след restart.
- Worker изпраща bounded batch и различава transient/permanent failure.
- Lost response е възпроизводим, а command key остава същият.

## 10. Самостоятелна задача — 20 минути в часа
**Problem statement:** Предотвратете duplicate Activity след server commit и изгубен response.

**Functional requirements:** Използвайте готовия request_ledger и transaction helper; запазете key scope, canonical payload hash и replay result. Същият key/payload връща същия ID; различен payload е conflict.

**Technical constraints:** Без in-memory dedup map и без нов key при retry. Ledger+Activity insert са атомарни; concurrent duplicates се решават чрез unique constraint/transaction, не check-then-insert race. Demo principal u1 се заменя с authenticated principal в Lab 10.

**Acceptance criteria:** Lost-response retry дава един server record и Synced local item. Два паралелни еднакви POST също дават един record. Different payload със същия key е 409; restart на service не губи dedup. Опишете retention спрямо максималния offline retry период.

Решението и кратката аргументация се проверяват в края на същото занятие. Това не е домашна работа.

## 11. Failure scenarios / Edge cases
| Сценарий | Очакване / наблюдение |
|---|---|
| Server commit, response lost | Retry със същия key; един effect. |
| Process death в Sending | Intent не изчезва; explicit recovery. |
| Concurrent duplicate POST | Unique ledger/transaction, един resource. |
| 422 permanent error | NeedsAction, без endless retry. |
| Нова op при завършващ Worker | Няма изгубено събуждане на sync. |

## 12. Тестване
Room transaction test за create+enqueue rollback; Worker fake transport; happy offline→online; proxy lost response; concurrent server calls със същия key; hash conflict; H2 restart persistence. Fixture proxy се стартира с node tools/drop-response-proxy.mjs от platform; emulator използва http://10.0.2.2:18080/api/. След тест върнете основния URL.

## 13. Наблюдение и измерване
Queued/Pending/Sending/Synced counts, queue age, attempts, server created count и dedup replay count. Измерете delay connectivity recovery→sync, без да обещавате точен WorkManager старт. Отчетете metered policy и не логвайте command bodies.

Запишете кратка таблица в `results/lab08/observations.md`: configuration, workload, expected/actual, sample count и ограничения. За latency използвайте p50/p95/max, когато имате достатъчно измервания, а не само средна стойност.

## 14. Въпроси за анализ
1. Защо unique work не дава exactly-once effect?
2. Къде трябва да е idempotency source of truth?
3. Защо key не се сменя при timeout?
4. Какво се случва след crash в Sending?
5. Защо ledger и insert трябва да са атомарни?
6. Как retention на keys влияе на offline потребителя?

## 15. Очакван резултат
Offline create с durable local queue и студентски server idempotency contract, проверен при изгубен response.

## 16. Критерии за приемане
- [ ] Room е local source of truth.
- [ ] Local create+queue е transaction.
- [ ] Sync е bounded и cancellable.
- [ ] Sending/retry/NeedsAction имат policy.
- [ ] Lost-response/concurrent duplicate tests дават един effect.
- [ ] Retention и metered trade-offs са описани.
