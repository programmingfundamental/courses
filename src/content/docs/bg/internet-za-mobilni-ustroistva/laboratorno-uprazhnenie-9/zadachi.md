---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## 7. Мини експеримент
Покажете два еднакви events и след това version2 след version3. Наивният handler увеличава count два пъти и връща стар status. Прекъснете socket, променете status през HTTP и reconnect-нете: липсващото събитие не се възстановява магически.

## 8. Водена практическа задача — 35 минути, включително checkpoint
### Стъпка 1
Добавете status command endpoint към Activity Service с validated transition и version increment. Сериализирайте ActivityEvent след durable commit и извикайте готовия EventPublisher. Използвайте activityId като broker key; failure на publish се наблюдава отделно от commit.

### Стъпка 2
Стартирайте compose.events.yaml с --profile events. Готовият Notification consumer relay-ва към internal BFF endpoint. Покажете log timeline command→commit→publish→consume→socket. За този bounded lab има една BFF instance и synthetic feed; не обещавайте durable fanout.

### Стъпка 3
Добавете Gateway /api/realtime route към BFF /realtime с WebSocket upgrade и HTTP route timeout=0. Android RealtimeSource е даден transport scaffold. Свържете lifecycle-owned collection и DTO decoding; ready/pong control messages не са ActivityEvent.

### Стъпка 4
Запишете accepted status в Room и наблюдавайте през UI Flow. Добавете heartbeat на transport и resource counters. Водената част показва happy delivery; reconnect/version policy остава independent.

Работете в общия platform проект. От директорията starter изпълнете `node ../../platform/tools/lab.mjs 09 build`. Build проверява scaffold-а и вашите промени; наличието на 501 LAB_TODO означава незавършена учебна функционалност, а не успешен checkpoint.

## 9. Checkpoint
- HTTP status change достига mobile без polling.
- Broker/Notification/BFF участват в timeline.
- STOP затваря socket, bounded channel overflow е видим.

## 10. Самостоятелна задача — 20 минути в часа
**Problem statement:** Добавете reconnect, duplicate detection и stale-event handling, без state rollback.

**Functional requirements:** Определете event identity/version policy; reconnect максимум 3 attempts с backoff/jitter и snapshot reconciliation след gap. STOP отменя retries. При reconnect duplicate/stale messages не променят current Activity към по-стара версия.

**Technical constraints:** Ползвайте готовия socket adapter и EventPolicy interface; state/version е durable в Room. Не разчитайте на timestamp ordering или unbounded set от event IDs. Изберете как snapshot и concurrent event се reconcile-ват.

**Acceptance criteria:** Trace versions1,3,3,2 завършва във version3 без duplicate effect. Disconnect→HTTP update→reconnect/snapshot възстановява latest state. Стар callback не поврежда новата session; 10 STOP cycles оставят socket/retry counts0.

Решението и кратката аргументация се проверяват в края на същото занятие. Това не е домашна работа.

## 11. Failure scenarios / Edge cases
| Сценарий | Очакване / наблюдение |
|---|---|
| Duplicate broker/relay event | Няма повторен business effect. |
| Stale version след нова | Няма rollback. |
| Socket loss при status change | Reconcile snapshot след recovery. |
| Publish failure след DB commit | Gap е видим; outbox limitation е описана. |
| Overflow/slow consumer | Bounded failure и resync, без silent lossless claim. |

## 12. Тестване
Happy status update през цялата pipeline; fixtures със duplicate, stale и gap; pure EventPolicy tests; socket fake за late callback/heartbeat timeout. Lifecycle test START/STOP10 пъти. Проверете DB version след restart. Snapshot race test подава по-нов event преди по-стар snapshot response.

## 13. Наблюдение и измерване
Commit→receive latency с уточнена clock synchronization uncertainty; на една машина използвайте instrumentation spans. Events sent/received/accepted/duplicate/stale, reconnect attempts, socket active time и polling-equivalent request count. Не използвайте различни несинхронизирани wall clocks като точен end-to-end stopwatch.

Запишете кратка таблица в `results/lab09/observations.md`: configuration, workload, expected/actual, sample count и ограничения. За latency използвайте p50/p95/max, когато имате достатъчно измервания, а не само средна стойност.

## 14. Въпроси за анализ
1. Защо event не е command?
2. Как duplicate възниква дори при надежден broker?
3. Защо version е per Activity?
4. Какво губи WebSocket при disconnect?
5. Кога push е по-подходящ от постоянен socket?
6. Къде е DB→broker atomicity gap?

## 15. Очакван резултат
Realtime transport през broker и WebSocket със студентски bounded recovery и version-aware state reconciliation.

## 16. Критерии за приемане
- [ ] Status command и event са различни.
- [ ] Pipeline включва broker/Notification/BFF.
- [ ] Socket е foreground-owned и bounded.
- [ ] Duplicate/stale handling е durable.
- [ ] Reconnect има budget и snapshot recovery.
- [ ] Outbox/fanout limitations са изрично описани.
