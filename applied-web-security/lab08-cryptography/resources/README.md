# Работна карта — lab08

Само предоставената локална учебна среда. Основен code anchor: **Web.sensitive → Vault → encrypted field в PostgreSQL**.

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
| 1 | encrypt/decrypt → original value | |
| 2 | еднакъв input два пъти → различни envelopes | |
| 3 | wrong key/owner → generic error | |
| 4 | corrupted ciphertext/tag → rejected | |
| 5 | null/empty/malformed envelope → rejected | |
| 6 | logs не съдържат synthetic field или credentials | |

## Команди

От `vulnerable-app`:

```powershell
mvn test '-Dlab.mode=lab08' '-Dtest=WebSecurityTest#lab08*'
```

От корена на курса:

```powershell
$env:LAB_MODE='lab08'
docker compose up -d --build
curl.exe -i http://localhost:8080/health
```

За authenticated requests: `. ./scripts/lab-client.ps1`; използвайте `$LabSession` и `$LabHeaders`. Паролите, cookies и tokens не се включват в evidence. При lab02 е нужен пълен DB reset; при lab08 не смесвайте legacy plaintext и encrypted records. Подробните стъпки и independent acceptance criteria са в [lab08.md](../lab08.md).
