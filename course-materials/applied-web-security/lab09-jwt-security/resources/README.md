# Работна карта — lab09

Само предоставената локална учебна среда. Основен code anchor: **Bearer token → JwtDecoder → claim validators → authorities**.

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
| 1 | valid signed token → 200 и правилен subject | |
| 2 | expired token → 401 | |
| 3 | modified payload / чужда signature → 401 | |
| 4 | wrong issuer / audience → 401 | |
| 5 | missing required scope → 403 | |
| 6 | malformed / missing token → 401 | |

## Команди

От `vulnerable-app`:

```powershell
mvn test '-Dlab.mode=lab09' '-Dtest=JwtSecurityTest'
```

От корена на курса:

```powershell
$env:LAB_MODE='lab09'
docker compose up -d --build
curl.exe -i http://localhost:8080/health
```

За authenticated requests: `. ./scripts/lab-client.ps1`; използвайте `$LabSession` и `$LabHeaders`. Паролите, cookies и tokens не се включват в evidence. При lab02 е нужен пълен DB reset; при lab08 не смесвайте legacy plaintext и encrypted records. Подробните стъпки и independent acceptance criteria са в [lab09.md](../lab09.md).
