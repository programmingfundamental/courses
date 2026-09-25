# Работна карта — lab06

Само предоставената локална учебна среда. Основен code anchor: **Stored comment / reflected query → HTML rendering → browser**.

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
| 1 | ordinary HTML characters → encoded response | |
| 2 | script-like stored input → literal text, без raw script tag | |
| 3 | normal Bulgarian text → правилно показан | |
| 4 | CSP присъства след fix | |
| 5 | browser marker не се изпълнява; отделно от MockMvc assertions | |

## Команди

От `vulnerable-app`:

```powershell
mvn test '-Dlab.mode=lab06' '-Dtest=WebSecurityTest#lab06*'
```

От корена на курса:

```powershell
$env:LAB_MODE='lab06'
docker compose up -d --build
curl.exe -i http://localhost:8080/health
```

За authenticated requests: `. ./scripts/lab-client.ps1`; използвайте `$LabSession` и `$LabHeaders`. Паролите, cookies и tokens не се включват в evidence. При lab02 е нужен пълен DB reset; при lab08 не смесвайте legacy plaintext и encrypted records. Подробните стъпки и independent acceptance criteria са в [lab06.md](../lab06.md).
