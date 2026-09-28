# Упражнение 4 — Brute-Force атаки и защита на Authentication — насоки

## Решение на примерния проблем

В SecurityConfig AuthenticationProvider свързваме LoginGuard независимо от LAB_MODE. Тялото на authenticate става:

```java
String name = input.getName();
if (guard.blocked(name)) throw new LockedException("Invalid credentials");
try {
    Authentication result = delegate.authenticate(input);
    guard.success(name);
    return result;
} catch (AuthenticationException e) {
    guard.failure(name);
    throw e;
}
```

Blocked проверката е преди try/catch: вече блокиран опит не добавя failure и не удължава срока. При стандартните настройки пет неуспеха достигат прага; следващият опит с правилна парола получава 401. На 60-ата секунда след последния отчетен failure входът е разрешен. `WebSecurityTest#lab04*` проверява реалния HTTP/provider път; LoginGuardTest проверява състоянията.

## Решение на самостоятелна задача 1 — Две конфигурации

Настройките вече са свързани чрез application.properties и Compose. Добавяме два интеграционни тестови класа с различни Spring контексти:

| Конфигурация | max-attempts | lock-duration | reset-after-success |
|---|---:|---|---|
| A | 3 | PT30S | false |
| B | 5 | PT60S | true |

За A използваме `@SpringBootTest(properties={"lab.mode=lab04", "lab.max-attempts=3", "lab.lock-duration=PT30S", "lab.reset-after-success=false"})` и @AutoConfigureMockMvc. За B задаваме съответните стойности в отделен клас. И двата импортират тестова конфигурация:

```java
@org.springframework.boot.test.context.TestConfiguration
class ClockConfig {
    @org.springframework.context.annotation.Bean
    @org.springframework.context.annotation.Primary
    MutableClock mutableClock() { return new MutableClock(); }
}

final class MutableClock extends java.time.Clock {
    private final java.util.concurrent.atomic.AtomicReference<java.time.Instant> now =
        new java.util.concurrent.atomic.AtomicReference<>(java.time.Instant.parse("2026-01-01T00:00:00Z"));
    void advance(java.time.Duration duration) { now.updateAndGet(t -> t.plus(duration)); }
    public java.time.Instant instant() { return now.get(); }
    public java.time.ZoneId getZone() { return java.time.ZoneOffset.UTC; }
    public java.time.Clock withZone(java.time.ZoneId zone) {
        MutableClock source = this;
        return new java.time.Clock() {
            public java.time.Instant instant() { return source.instant(); }
            public java.time.ZoneId getZone() { return zone; }
            public java.time.Clock withZone(java.time.ZoneId z) { return source.withZone(z); }
        };
    }
}
```

Добавяме @Import(ClockConfig.class) и инжектираме MutableClock, MockMvc и LoginGuard. Преди всеки тест извикваме guard.clear(). POST /login използва валиден CSRF и не преизползва authenticated session.

Последователност A:

1. Един грешен login → 401; правилен login → 204. Броячът остава 1.
2. Още два грешни login → 401; броячът става 3. Правилен login → 401.
3. clock.advance(Duration.ofSeconds(29)); правилен login → 401.
4. clock.advance(Duration.ofSeconds(1)); правилен login → 204. Блокираният опит не е удължил срока.
5. Bob с правилна парола получава 204 независимо от състоянието на alice.

Последователност B: четири грешки → правилен login 204 и нулиране → още четири грешки → правилен login 204. В отделен тест пет грешки блокират следващия login; на t+60s той е разрешен.

Невалидна конфигурация се проверява чрез ApplicationContextRunner с LoginGuard и Clock bean: max=0, PT0S, PT-1S и невалиден текст за duration трябва да дадат `assertThat(context).hasFailed()`. Това доказва отказ при startup, а не само изключение от ръчно извикан конструктор.

**NAT/cluster:** съчетаваме account лимит с по-широк праг по доверен proxy адрес и общ капацитет. Много клиенти зад NAT не бива да получават общия нисък праг за един account. При няколко инстанции броячите изискват общо атомарно хранилище. В задачата описваме интерфейса за него без външен Redis. Кратък lockout и общ лимит намаляват lockout DoS, но не го премахват.

## Решение на самостоятелна задача 2 — Гранични случаи

- **Два едновременни failures:** след max−2 грешки стартираме две guard.failure(name) с ExecutorService и бариера; след завършване blocked(name) е true. ConcurrentHashMap.compute осигурява атомарна промяна за ключа.
- **Споделен IP:** alice и bob имат независими account броячи; блокирана alice не блокира bob.
- **Неограничени имена:** текущата map граница е приблизителна. За строг капацитет синхронизираме добавянето и освобождаването на ключове и отказваме нови опити при запълване. Тест над капацитета проверява размера и отказа, а не допуска заобикаляне чрез ново име.
- **Рестарт:** нов LoginGuard губи старите броячи. Тест с две инстанции доказва това; изискване за устойчивост при рестарт налага постоянно общо хранилище.

## Въпроси за анализ

1. Защо account lockout създава DoS риск?
2. Защо IP-only не е достатъчно?
3. Защо не използваме sleep в тест?
4. Как се проверява wiring?
5. Какво се логва?
6. Какъв е cluster проблемът?

## Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
