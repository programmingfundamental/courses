# Упражнение 8 — Криптография и защита на чувствителни данни — насоки

## Решение на примерния проблем

В двата метода Web.sensitive премахваме избора според mode. След валидацията при POST винаги криптираме; след прочитане на raw при GET винаги декриптираме:

```java
// POST, след check(value,200) и проверката за непразна стойност:
db.update("UPDATE app_users SET sensitive=? WHERE username=?",
    vault.encrypt(value, user.getName()), user.getName());
// GET, след прочитане на raw за текущия потребител:
return Map.of("value", raw == null ? "" : vault.decrypt(raw, user.getName()));
```

Vault използва AES-GCM, 32-байтов ключ, 12-байтов nonce, 128-битов tag и owner като AAD. Ключът е в secret файл, DB съдържа v1: envelope. Старите plaintext записи се мигрират чрез еднократно encrypt с правилния owner; не се подават директно на decrypt.

Проверки: POST/GET възстановява текста; DB не съдържа plaintext; две криптирания дават различни envelopes; променен tag и грешен owner отказват; рестарт със същия secret файл запазва четимостта. Изпълняваме `WebSecurityTest#lab08*`, VaultTest и AuditTest.

## Решение на самостоятелна задача 1 — Криптографски решения

| Данни/употреба | Възстановяване | Механизъм и източник | Съхранение | Смяна/отмяна и тестова спецификация |
|---|---|---|---|---|
| Парола | Не | BCrypt чрез PasswordEncoder, случайна salt | Само хеш в app_users | Смяна на парола; correct matches=true, wrong=false, различни hashes за една парола |
| API secret само за проверка | Не | 32 случайни байта от SecureRandom, SHA-256 digest | Digest и token ID | Нов token и отмяна на стария; сравнение на digest и липса на raw token в DB/log |
| API secret за изпращане | Да | AES-GCM/secret store; ключ извън DB | Envelope с service/owner като AAD | Версионирани ключове; roundtrip и wrong-AAD отказ |
| Личен идентификатор | Да, при разрешено четене | AES-GCM, ключ от SecureRandom/secret файл | Envelope отделно от ключа | Миграция при rotation; тест на права, roundtrip и липса в логовете |
| Session identifier | Сървърът разпознава сесията | Генераторът на servlet контейнера | Cookie и server session store | Нов ID при login, invalidation при logout/expiry; старият ID не дава достъп |
| Database credential | Да, за връзка към DB | Генерирана случайна парола и secret store/file | Извън repo, image и DB dump | Координирана смяна; нов credential работи, старият се отказва |

SHA-256 тук е за случаен високоентропиен API token, не за избрана от човек парола. Secret, който трябва да бъде изпратен към услуга, трябва да може да се възстанови. Криптирането защитава хранилището, но не и от приложение, което вече е компрометирано и има ключа.

Изпълним JUnit клас за два механизма в `src/test/java/bg/tuvarna/lab/CryptoDecisionTest.java`:

```java
package bg.tuvarna.lab;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import static org.assertj.core.api.Assertions.*;

class CryptoDecisionTest {
    @Test void passwordIsVerifiedWithoutRecovery() {
        var encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String password = "Long-test-password-2026!";
        String a = encoder.encode(password), b = encoder.encode(password);
        assertThat(a).startsWith("{bcrypt}").isNotEqualTo(b).doesNotContain(password);
        assertThat(encoder.matches(password, a)).isTrue();
        assertThat(encoder.matches("incorrect", a)).isFalse();
    }
    @Test void recoverableFieldRequiresCorrectOwner() throws Exception {
        var vault = new Vault("");
        String a = vault.encrypt("PERSON-123", "alice");
        String b = vault.encrypt("PERSON-123", "alice");
        assertThat(a).startsWith("v1:").isNotEqualTo(b).doesNotContain("PERSON-123");
        assertThat(vault.decrypt(a, "alice")).isEqualTo("PERSON-123");
        assertThatThrownBy(() -> vault.decrypt(a, "bob"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
```

Команда: `mvn test '-Dtest=CryptoDecisionTest'`. За всички механизми добавяме проверка за липса на сурови secrets в логовете. Backup без ключ не позволява възстановяване; backup на ключа се пази отделно с ограничен достъп. За rotation пазим старите ключове за четене до края на миграцията, а новите записи използват новия ключ.

## Решение на самостоятелна задача 2 — Гранични случаи

- **Повторен nonce:** SecureRandom генерира нов nonce; при голям обем задаваме лимит за употреба и rotation на ключа. Тестът за различни envelopes открива постоянен nonce, но не доказва математическа уникалност.
- **Друг owner:** decrypt(envelope,"bob") отказва за AAD=alice и не връща частичен текст.
- **Нов ключ след рестарт:** две Vault инстанции с един secret файл декриптират взаимно; различен ключ отказва. За rotation са нужни key ID и запазен стар ключ.
- **Версия/tag/Unicode:** v2: се отказва; отрязан tag се отказва; кирилицата се възстановява точно чрез UTF-8. Грешките не логват plaintext, ключ или envelope.

## Въпроси за анализ

1. Защо Base64 не е encryption?
2. Защо password не се encrypt-ва?
3. Защо GCM tag е нужен?
4. Какво пази AAD owner?
5. Какъв риск остава при app compromise?
6. Как се прави rotation без загуба?

## Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
