# Упражнение 1 — Лабораторна среда и Threat Modeling — насоки

## Решение на примерния проблем

Публикуваният DB порт е отделна входна точка, която заобикаля Nginx и приложните проверки. Решението е:

1. DFD: браузър → Nginx → Spring Security → Web → Documents/Accounts → PostgreSQL. Отбелязваме границите клиент/сървър, proxy/приложение, security filters/приложна логика и приложение/база.
2. Премахваме `ports` от db и app. Те участват само в backend мрежата с `internal: true`. Nginx участва в backend и frontend и публикува само `127.0.0.1:8080:80` и `127.0.0.1:8081:81`.
3. lab_owner инициализира схемата; lab_runtime няма CREATE/DDL права. SecurityConfig запазва публичен allowlist, authentication за API, ADMIN за /admin/** и denyAll за останалото.
4. `docker compose config --format json` показва конфигурацията; `WebSecurityTest#lab01*` проверява /health → 200, /api/me без вход → 401 и /unlisted като alice → 403.

Примерна автоматизирана проверка от директорията на Compose проекта:

```powershell
function Assert-NetworkPolicy($config) {
    foreach ($name in @('db','app')) {
        if (@($config.services.$name.ports).Where({ $null -ne $_ }).Count -gt 0) {
            throw "Published backend port: $name"
        }
    }
    $ports = @($config.services.nginx.ports)
    if ($ports.Count -ne 2) { throw 'Expected two proxy ports' }
    foreach ($port in $ports) {
        if ($port.host_ip -ne '127.0.0.1') { throw 'Proxy is not loopback-only' }
    }
    if ($config.networks.backend.internal -ne $true) { throw 'Backend is not internal' }
}
$json = docker compose config --format json
if ($LASTEXITCODE -ne 0) { throw 'Compose configuration failed' }
Assert-NetworkPolicy ($json | ConvertFrom-Json)
$invalid = $json | ConvertFrom-Json
$invalid.services.db | Add-Member -Force NoteProperty ports @(
    [pscustomobject]@{target=5432; published='5432'; host_ip='0.0.0.0'}
)
$rejected = $false
try { Assert-NetworkPolicy $invalid } catch { $rejected = $true }
if (-not $rejected) { throw 'The negative configuration was accepted' }
```

## Решение на самостоятелна задача 1 — Регистрация

DFD: браузър → POST /register → session SecurityFilterChain → Web.register → Accounts.encode → app_users. Активи: пароли, самоличности, роли, наличност и записи. Параметрите са недоверени; role и password hash се определят от сървъра.

Оценките използват вероятност и въздействие 1–3 при предположение, че маршрутът е достъпен:

| Заплаха и участник | Код/граница | Риск | Защита и проверка |
|---|---|---|---|
| Клиент подава role=ADMIN | Web.register, клиент/сървър | 2 × 3 = 6 | INSERT задава USER; тест чете ролята от DB |
| Четец на backup получава пароли | Accounts.encode, приложение/DB | 2 × 3 = 6 | BCrypt; тест за prefix и matches; остава риск от отгатване на слаба парола |
| Клиент прави масови регистрации | POST /register | 3 × 2 = 6 | Лимит на честота и размер; отказ над праг без INSERT; остава разпределен abuse |
| Клиент извлича информация от грешка за дубликат | INSERT/error handling | 2 × 2 = 4 | Общ отговор без SQL/stack trace; тест с дублирано име; времеви разлики се проверяват отделно |
| Чужда страница изпраща регистрация | Session filter chain | 2 × 2 = 4 | CSRF token; липсващ token → 403 и без INSERT |
| Клиент променя SQL чрез параметър | Web.register | 2 × 3 = 6 | Binding и валидация на името; SQL вход се отказва или обработва като данни |

Изпълним тест в съществуващия WebSecurityTest, с неговите imports и полета:

```java
@Test void registrationCannotGrantAdmin() throws Exception {
    db.update("DELETE FROM app_users WHERE username=?", "studentnew");
    try {
        mvc.perform(post("/register").with(csrf())
            .param("username", "studentnew")
            .param("password", "A-long-password-2026!")
            .param("role", "ADMIN"))
            .andExpect(status().isCreated());
        assertThat(db.queryForObject("SELECT role FROM app_users WHERE username=?",
            String.class, "studentnew")).isEqualTo("USER");
    } finally {
        db.update("DELETE FROM app_users WHERE username=?", "studentnew");
    }
}
```

## Решение на самостоятелна задача 2 — Гранични случаи

- **IPv6 wildcard:** заменяме host_ip с `::` в копие на JSON; Assert-NetworkPolicy трябва да го отхвърли.
- **Спрян Nginx и публикуван app:** добавяме app.ports в копие на конфигурацията; проверката отказва независимо от състоянието на Nginx.
- **Role в регистрация/profile:** входният модел не позволява промяна на role. След регистрация и POST /api/profile с допълнителен role=ADMIN проверяваме, че ролята остава USER.

Инвариант: клиентски вход и мрежова конфигурация не създават алтернативен път за получаване на права.

## Въпроси за анализ

1. Може ли internal network да замени authorization?
2. Каква е разликата между asset и threat?
3. Къде е boundary при SQL?
4. Защо vulnerability без Internet exposure остава vulnerability?
5. Как доказвате mitigation?
6. Какво пропуска Top 10?

## Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
