# Работна карта — lab01

Само предоставената локална учебна среда. Основен code anchor: **Compose, Nginx, endpoint inventory и trust boundaries**.

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
| 1 | public health → 200 | |
| 2 | anonymous /api/me → 401 | |
| 3 | authenticated unknown route → 403 | |
| 4 | Compose DB/app ports → няма | |
| 5 | unsafe config fixture → проверката трябва да го отхвърли | |

## Команди

От `vulnerable-app`:

```powershell
mvn test '-Dtest=WebSecurityTest#lab01*'
```

От корена на курса:

```powershell
$env:LAB_MODE='lab01'
docker compose up -d --build
curl.exe -i http://localhost:8080/health
```

За authenticated requests: `. ./scripts/lab-client.ps1`; използвайте `$LabSession` и `$LabHeaders`. Паролите, cookies и tokens не се включват в evidence. При lab02 е нужен пълен DB reset; при lab08 не смесвайте legacy plaintext и encrypted records. Подробните стъпки и independent acceptance criteria са в [lab01.md](../lab01.md).
