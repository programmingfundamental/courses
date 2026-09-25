# Работна карта — lab10

Само предоставената локална учебна среда. Основен code anchor: **Цялата архитектура → evidence → fixes → regression suite**.

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
| 1 | authentication и session lifecycle | |
| 2 | owner/admin/non-owner policy | |
| 3 | brute-force threshold/expiration | |
| 4 | SQL input остава data; XSS input остава text | |
| 5 | CSRF mutation отказ без state change | |
| 6 | crypto roundtrip/tamper/logging | |
| 7 | JWT signature/claims/scope | |
| 8 | config, headers, real PostgreSQL и cookie flags | |

## Команди

От `vulnerable-app`:

```powershell
mvn test '-Dlab.mode=lab10' '-Dtest=WebSecurityTest,JwtSecurityTest,LoginGuardTest,VaultTest,AuditTest'
```

От корена на курса:

```powershell
$env:LAB_MODE='lab10'
docker compose up -d --build
curl.exe -i http://localhost:8080/health
```

За authenticated requests: `. ./scripts/lab-client.ps1`; използвайте `$LabSession` и `$LabHeaders`. Паролите, cookies и tokens не се включват в evidence. При lab02 е нужен пълен DB reset; при lab08 не смесвайте legacy plaintext и encrypted records. Подробните стъпки и independent acceptance criteria са в [lab10.md](../lab10.md).
