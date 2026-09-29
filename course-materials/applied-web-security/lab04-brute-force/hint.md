# Упражнение 4 — Ограничаване на опитите за вход — решения и насоки


## Решение на примерния проблем

LoginAttemptService е @Service с Clock, max, duration и reset. Използваме synchronized методи за достъп до bounded Map<String,State>, където State съдържа failures, until. За даден username:

1. Преди операция премахваме state, когато now>=until.
2. blocked връща failures>=max. Ако map е пълна с активни записи и името е ново, отказваме временно, вместо да разрешим обход.
3. failure създава count=1 или увеличава count. До достигане на max обновява until=now+duration; при вече блокиран state не променя срока.
4. success премахва state само при reset=true. Зануляване на глобалната map е достъпно само в теста, не чрез HTTP.

В AuthService преди authenticate: ако attempts.blocked(username), хвърляме общ 401. В catch(AuthenticationException) извикваме attempts.failure(username) и връщаме същия 401. След успешен authenticate извикваме attempts.success(username). Промените по session/token са след този резултат. Не държим заключване на map по време на BCrypt.

## Решение на самостоятелна задача 1

Настройките се инжектират с @Value; max<1 или duration<=0 хвърля IllegalArgumentException в конструктора. Clock bean се заменя в тест с @TestConfiguration, @Bean @Primary MutableClock, който връща Instant и има advance(Duration). Един тестов клас е @SpringBootTest(properties={"security.login.max-attempts=3","security.login.lock-duration=PT30S","security.login.reset-after-success=false"}); втори използва 5/PT60S/true. POST заявките имат JSON username/password и отделни request sessions.

Конфигурация A: failure → success(200) → failure → failure → правилна парола(401). Advance 29s → 401; още 1s → 200. Конфигурация B: четири failures → success(200) → още четири failures → success(200). Отделен случай с пет failures блокира до t+60s. Bob остава 200. За startup използваме ApplicationContextRunner с Clock и LoginAttemptService и assertThat(context).hasFailed() при max=0, PT0S и отрицателен срок.

## Решение на самостоятелна задача 2

Две конкурентни failure операции след max−2 довеждат до blocked=true; synchronized предотвратява загуба. Тест с capacity+1 имена проверява, че размерът не расте и новият опит се отказва до освобождаване. При NAT account броячите остават отделни; общият IP лимит има по-висок праг. Рестарт губи map — проверяваме с нова инстанция; устойчивостта изисква общо хранилище. За истинско ограничение на паралелни BCrypt операции е необходим отделен ограничител на едновременните заявки; броячът сам не осигурява това.


## Въпроси за анализ

1. Кога започва срокът на блокировка?
2. Защо блокиран опит не увеличава failures?
3. Как NAT и cluster променят дизайна?

## Checklist

- [ ] Примерният проблем има работеща реализация в Task Manager.
- [ ] Самостоятелните задачи имат код/анализ и проверими резултати.
- [ ] Тестовете включват разрешен и отказан сценарий.
- [ ] Отказаната операция не променя DB.
- [ ] Изпълнените H2/PostgreSQL и браузърни проверки са разграничени.
- [ ] Отчетът не съдържа пароли, raw tokens или поверителни бележки.
