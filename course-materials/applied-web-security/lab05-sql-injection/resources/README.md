# Работна карта — lab05

Само предоставената локална учебна среда. Основен code anchor: **Documents.search → JDBC query → DB parser**.

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
| 1 | normal notes → един result | |
| 2 | O'Reilly → валиден result | |
| 3 | structured malicious input → нула results | |
| 4 | empty input → само собствени docs | |
| 5 | oversized/NUL input → 400 | |
| 6 | wildcards → allowed semantics, без чужди owners | |

## Команди

От `vulnerable-app`:

```powershell
mvn test '-Dlab.mode=lab05' '-Dtest=WebSecurityTest#lab05*'
```

От корена на курса:

```powershell
$env:LAB_MODE='lab05'
docker compose up -d --build
curl.exe -i http://localhost:8080/health
```

За authenticated requests: `. ./scripts/lab-client.ps1`; използвайте `$LabSession` и `$LabHeaders`. Паролите, cookies и tokens не се включват в evidence. При lab02 е нужен пълен DB reset; при lab08 не смесвайте legacy plaintext и encrypted records. Подробните стъпки и independent acceptance criteria са в [lab05.md](../lab05.md).
