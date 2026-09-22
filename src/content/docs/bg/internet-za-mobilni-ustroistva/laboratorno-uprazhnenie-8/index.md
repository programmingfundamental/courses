---
title: "Упражнение 8 — Offline-First и Synchronization"
sidebar:
  order: 8
  label: Упражнение 8
---

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
