# Работна карта — lab07

Само предоставената локална учебна среда. Основен code anchor: **Browser cookie policy → CsrfFilter → state-changing Controller**.

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
| 1 | валиден token и authenticated session → 200 и update | |
| 2 | missing token → 403 и без update | |
| 3 | invalid token → 403 и без update | |
| 4 | token от друга session → 403 | |
| 5 | real Set-Cookie → HttpOnly, SameSite=Lax; Secure=true в CookieIT | |

## Команди

От `vulnerable-app`:

```powershell
mvn test '-Dlab.mode=lab07' '-Dtest=WebSecurityTest#lab07*'
```

От корена на курса:

```powershell
$env:LAB_MODE='lab07'
docker compose up -d --build
curl.exe -i http://localhost:8080/health
```

За authenticated requests: `. ./scripts/lab-client.ps1`; използвайте `$LabSession` и `$LabHeaders`. Паролите, cookies и tokens не се включват в evidence. При lab02 е нужен пълен DB reset; при lab08 не смесвайте legacy plaintext и encrypted records. Подробните стъпки и independent acceptance criteria са в [lab07.md](../lab07.md).
