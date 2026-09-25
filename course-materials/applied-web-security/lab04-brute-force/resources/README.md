# Работна карта — lab04

Само предоставената локална учебна среда. Основен code anchor: **AuthenticationProvider → LoginGuard → clock/counters**.

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
| 1 | под прага → валиден login е приет | |
| 2 | прагът е достигнат → следващ валиден login е отказан | |
| 3 | blocked request → generic 401 и без изместване на срока | |
| 4 | точно на expiry → разрешен нов опит | |
| 5 | success reset true/false → съответните counters | |
| 6 | concurrent failures → няма изгубени increments | |

## Команди

От `vulnerable-app`:

```powershell
mvn test '-Dlab.mode=lab04' '-Dtest=WebSecurityTest#lab04*'
```

От корена на курса:

```powershell
$env:LAB_MODE='lab04'
docker compose up -d --build
curl.exe -i http://localhost:8080/health
```

За authenticated requests: `. ./scripts/lab-client.ps1`; използвайте `$LabSession` и `$LabHeaders`. Паролите, cookies и tokens не се включват в evidence. При lab02 е нужен пълен DB reset; при lab08 не смесвайте legacy plaintext и encrypted records. Подробните стъпки и independent acceptance criteria са в [lab04.md](../lab04.md).
