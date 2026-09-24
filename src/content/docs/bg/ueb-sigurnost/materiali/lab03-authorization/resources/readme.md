---
title: "Работна карта — lab03"
sidebar:
  order: 100
---

# Работна карта — lab03

Само предоставената локална учебна среда. Основен code anchor: **Controller → Documents.get → object ownership policy**.

## Работен запис

| Поле | Попълнете |
|---|---|
| Mode / commit | |
| Actor / entry point | |
| Asset / trust boundary | |
| Baseline allowed request | |
| Контролирана reproduction | |
| Expected / actual result | |
| Root cause / code location | |
| Likelihood × impact / аргумент | |
| Fix / residual risk | |
| Regression test / report path | |
| Positive functionality check | |

## Test matrix

| Case | Expected | Actual / evidence |
|---|---|---|
| 1 | anonymous → 401 | |
| 2 | owner → 200 с правилния object | |
| 3 | other user → 404, без чуждо съдържание | |
| 4 | ADMIN → 200 | |
| 5 | missing ID → 404; nonnumeric → 400; USER admin route → 403 | |

## Команди

От `vulnerable-app`:

```powershell
mvn test '-Dlab.mode=lab03' '-Dtest=WebSecurityTest#lab03*'
```

От корена на курса:

```powershell
$env:LAB_MODE='lab03'
docker compose up -d --build
curl.exe -i http://localhost:8080/health
```

За authenticated requests: `. ./scripts/lab-client.ps1`; използвайте `$LabSession` и `$LabHeaders`. Паролите, cookies и tokens не се включват в evidence. При lab02 е нужен пълен DB reset; при lab08 не смесвайте legacy plaintext и encrypted records. Подробните стъпки и independent acceptance criteria са в [lab03.md](/courses/bg/ueb-sigurnost/laboratorno-uprazhnenie-3/).
