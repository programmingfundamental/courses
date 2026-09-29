# Упражнение 1 — Среда и моделиране на заплахи в Task Manager — решения и насоки


## Решение на примерния проблем

Клиентът трябва да достига app:9000, а app достига db:5432 по Docker DNS. Премахването на db.ports не прекъсва тази връзка. В предоставения compose.yml вече има само app port 127.0.0.1:9000 и internal backend.

От task-manager проверката е:

```powershell
function Assert-Policy($c) {
    if (@($c.services.db.ports).Where({$null -ne $_}).Count) { throw 'DB port is published' }
    $ports = @($c.services.app.ports)
    if ($ports.Count -ne 1 -or $ports[0].host_ip -ne '127.0.0.1' -or $ports[0].target -ne 9000) { throw 'Unexpected app publication' }
    if ($c.networks.backend.internal -ne $true) { throw 'Backend is not internal' }
}
$json = docker compose config --format json
if ($LASTEXITCODE -ne 0) { throw 'Compose failed' }
Assert-Policy ($json | ConvertFrom-Json)
$bad = $json | ConvertFrom-Json
$bad.services.db | Add-Member -Force NoteProperty ports @(@{target=5432; published='5432'; host_ip='0.0.0.0'})
$rejected=$false
try { Assert-Policy $bad } catch { $rejected=$true }
if (-not $rejected) { throw 'Invalid configuration accepted' }
```

## Решение на самостоятелна задача 1

DFD: клиент → /auth/register → RegisterRequest → AuthService → PasswordEncoder/UserRepository → users. role се задава чрез Role.USER; входният DTO няма role.

| Заплаха | Риск | Код и решение | Тест |
|---|---|---|---|
| Клиент подава ADMIN | 2×3 | AuthService.register задава USER | JSON с role=ADMIN; users.findByUsername връща USER |
| Масови регистрации натоварват BCrypt/DB | 3×2 | Отделен лимит за регистрация | При надхвърлен праг няма INSERT |
| Изтичане на backup | 2×3 | BCrypt хеш, отделни DB права | Паролата не е plaintext; encoder.matches работи |
| Дублирано име разкрива профил/детайли | 2×2 | Контролирана грешка без SQL/stack trace | Повторна регистрация, един ред в DB, ограничен error body |
| Невалидни/големи полета | 3×2 | @Size и UTF-8 граница преди BCrypt | Празни/големи входове → 400 без INSERT |
| CSRF при сесийно удостоверяване | 2×2 | Token преди промяна; прилага се в упражнение 7 | Без token → 403, броят users е непроменен |

Примерен нов тест в копие на TaskManagerBaselineTest: POST /auth/register с `{"username":"newuser","password":"Password-2026!","role":"ADMIN"}` → 200; прочетеният User има Role.USER. Втори POST с празно username → 400 и users.count() не нараства. Използвайте @Transactional за отделяне на данните. Оценките са аргументирани предположения, а не измерени честоти.

## Решение на самостоятелна задача 2

В отделни копия на JSON заменяме app.host_ip с ::, добавяме db.ports или втори app port. Assert-Policy трябва да откаже всяко копие. Вътрешната мрежа регулира свързаността; собствеността на задача изисква отделна service проверка и се добавя в упражнение 3.


## Въпроси за анализ

1. Кой компонент се достига през публикуван DB порт?
2. Кои полета при регистрация контролира клиентът?
3. Къде мрежовата изолация спира да е достатъчна?

## Checklist

- [ ] Примерният проблем има работеща реализация в Task Manager.
- [ ] Самостоятелните задачи имат код/анализ и проверими резултати.
- [ ] Тестовете включват разрешен и отказан сценарий.
- [ ] Отказаната операция не променя DB.
- [ ] Изпълнените H2/PostgreSQL и браузърни проверки са разграничени.
- [ ] Отчетът не съдържа пароли, raw tokens или поверителни бележки.
