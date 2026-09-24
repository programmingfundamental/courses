---
title: "Работна карта — lab02"
sidebar:
  order: 100
---

# Работна карта — lab02

Само предоставената локална учебна среда. Основен code anchor: **SecurityFilterChain → AuthenticationProvider → Accounts → SecurityContext**.

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
| 1 | valid credentials → 204 и /api/me със същата session → Alice | |
| 2 | wrong password и unknown user → 401 generic | |
| 3 | anonymous protected endpoint → 401 | |
| 4 | logout → 204 и session invalidation | |
| 5 | stored password → BCrypt prefix и успешен matches | |

## Команди

От `vulnerable-app`:

```powershell
mvn test '-Dlab.mode=lab02' '-Dtest=WebSecurityTest#lab02*'
```

От корена на курса:

```powershell
$env:LAB_MODE='lab02'
docker compose up -d --build
curl.exe -i http://localhost:8080/health
```

За authenticated requests: `. ./scripts/lab-client.ps1`; използвайте `$LabSession` и `$LabHeaders`. Паролите, cookies и tokens не се включват в evidence. При lab02 е нужен пълен DB reset; при lab08 не смесвайте legacy plaintext и encrypted records. Подробните стъпки и independent acceptance criteria са в [lab02.md](/courses/bg/ueb-sigurnost/laboratorno-uprazhnenie-2/).
