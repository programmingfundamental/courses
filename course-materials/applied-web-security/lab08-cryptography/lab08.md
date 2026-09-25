# 1. Упражнение 8 — Криптография и защита на чувствителни данни

**Продължителност:** 110 минути. **Аудитория:** IV курс, бакалавър „Киберсигурност“.

> Всички offensive действия в курса се изпълняват единствено срещу предоставената локална лабораторна среда.

## 2. Security сценарий

Backup на учебния портал съдържа личен identifier в plaintext. Authorization върху endpoint не помага на offline reader на този backup. Трябва да защитите полето, без да създавате собствен crypto алгоритъм.

## 3. Учебни цели

След упражнението студентът:

- разграничава hashing, encryption и encoding;
- анализира key/nonce lifecycle;
- защитава synthetic поле с authenticated encryption;
- диагностицира tampering и wrong key;
- тества persistence без plaintext и log leakage;
- аргументира избор на механизъм за различни secrets;

## 4. Предварителни знания

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [README](../README.md). Изпълнени предходните 7 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

## 5. Необходими инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](resources/README.md) съдържа работна карта и очаквани наблюдения.

## 6. Архитектурен контекст

```text
Browser
   |
Reverse Proxy (Nginx)
   |
Spring Security
   |
Controller
   |
Service
   |
Database (PostgreSQL)
```

**Фокус:** Web.sensitive → Vault → encrypted field в PostgreSQL. Съпоставете с [DFD и endpoints](../architecture/system-overview.md); отбележете кой input е недоверен и къде се взема security решението.

## 7. Необходима теория

Hashing е еднопосочен; encryption е обратим при наличие на key; Base64 е само encoding. Symmetric encryption използва общ secret key, asymmetric cryptography — public/private pair. Passwords се проверяват чрез password hashing; те не трябва да се възстановяват чрез decryption.

AES-GCM е authenticated encryption: защитава confidentiality и integrity. Използваме стандартния Java Cipher, 256-bit key, случаен 96-bit nonce/IV и 128-bit authentication tag. IV/nonce не е salt; nonce не трябва да се повтаря със същия key. SecureRandom генерира key/nonce. AAD (additional authenticated data) свързва encrypted field с owner, без да го криптира. Encryption at rest не защитава от компрометиран app, който има key. Key management включва отделно съхранение, access policy, backup, rotation и versioning.

Технически източници и version scope: [references](../architecture/references.md).

## 8. Уязвим пример

```java
// Web.sensitive в lab08:
db.update("UPDATE app_users SET sensitive=? WHERE username=?", value, user.getName());
```

Примерът е умишлен учебен дефект. За възпроизвеждане използвайте `LAB_MODE=lab08`; не пренасяйте този switch в production.

## 9. Водена практическа задача

Преди работа следвайте [startup/reset](../README.md). Командите за Maven се изпълняват от `vulnerable-app`, а Compose командите — от корена на курса.

### Стъпка 1

Стартирайте lab08 с чисти данни. POST /api/sensitive със synthetic value `SYNTHETIC-123` и валиден CSRF token. Прочетете през /api/sensitive → същата стойност.

### Стъпка 2

Чрез локален DB query установете, че полето съдържа plaintext. Не използвайте истински ЕГН, API keys или пароли. Изпълнете WebSecurityTest#lab08_sensitiveFieldNotPlaintext → red.

### Стъпка 3

Проследете Vault.encrypt/decrypt: Cipher AES/GCM/NoPadding, random nonce, AAD owner, envelope. Приложете този flow и в уязвимия code path. Key идва от Docker secret file; не го добавяйте в application.properties.

### Стъпка 4

Направете roundtrip и сравнете две encryption операции върху еднакъв plaintext: ciphertext трябва да е различен. Променете един byte в локален тест → generic authentication failure, без partial plaintext.

### Стъпка 5

Пуснете VaultTest и AuditTest. Рестартирайте Compose app и проверете decryption със същия persistent key. Обсъдете migration на стар plaintext и защо reset е позволен само за учебните fixtures.

Разпределение: scenario/theory 15 мин, наблюдение 15, root cause 10, guided fix 25, tests 15, самостоятелна работа 25, защита 5. Общо 110 минути.

## 10. Анализ на root cause

Storage access се смята за еквивалентен на permission за четене на чувствителните полета. Ако key и ciphertext са в един dump, encryption добавя малка защита; затова key boundary е отделен. Непроверен authentication tag или nonce reuse нарушават гаранциите на AEAD.

Предайте причинна верига: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](../architecture/threat-model.md); аргументирайте residual risk след fix.

## 11. Реализация на защита

Използвайте стандартен AES-GCM чрез Vault, random nonce за всяка операция и owner като AAD. Envelope има version `v1`, nonce, ciphertext и tag; това не е custom crypto algorithm. Не логвайте crypto exceptions с input/key material. Persistent key е нужен при persistent DB; временно генериран key е допустим само с временната H2 база. За rotation проектирайте key ID и decrypt-old/encrypt-new стратегия.

Reference branch в общия проект служи за guided comparison. За предаване променете уязвимия code path и запазете същия lab mode. Самостоятелната задача по-долу изисква собствено разширение и няма готово решение в този файл.

## 12. Security regression test

Изпълнете:

```powershell
mvn test '-Dlab.mode=lab08' '-Dtest=WebSecurityTest#lab08*'
```

Преди поправка очаквайте assertion failure, който показва дефекта. След поправката същата команда трябва да премине. Infrastructure error не е валиден red security test.

Примерен test fragment (пълният runnable class и imports са в `vulnerable-app/src/test/java/bg/tuvarna/lab`):

```java
Vault vault = new Vault(""); // временен ключ само за unit test
String a = vault.encrypt("synthetic", "alice");
assertThat(vault.decrypt(a, "alice")).isEqualTo("synthetic");
assertThatThrownBy(() -> vault.decrypt(a, "bob"))
   .isInstanceOf(IllegalArgumentException.class);
```

Задължителна матрица:

- encrypt/decrypt → original value;
- еднакъв input два пъти → различни envelopes;
- wrong key/owner → generic error;
- corrupted ciphertext/tag → rejected;
- null/empty/malformed envelope → rejected;
- logs не съдържат synthetic field или credentials;

Добавете поне един нов автоматизиран test за edge case или самостоятелната задача. Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.

## 13. Самостоятелна задача

**Problem statement:** подгответе crypto decision record за password, API secret, personal identifier, session identifier и database credential.

**Requirements:** за всеки посочете нужда от възстановяване, избран механизъм, entropy/key source, storage boundary и rotation/revocation. Добавете test specification за всеки, а за два механизма — изпълними автоматизирани tests.

**Constraints:** стандартни Java/Spring APIs; без custom algorithms, hardcoded production secrets или истински лични данни. API secret трябва да разгледа два случая: само verify и outbound use.

**Acceptance criteria:** решенията следват употребата на данните; посочени са backup/key loss и log risks. Не е достатъчна таблица, в която всичко се encrypt-ва.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

## 14. Edge cases

- Повторен nonce със същия key.
- Коректен envelope се копира към друг owner — AAD трябва да откаже.
- Restart със сменен key и запазена DB.
- Unknown envelope version, truncated tag, Unicode input.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.

## 15. Въпроси за анализ

1. Защо Base64 не е encryption?
2. Защо password не се encrypt-ва?
3. Защо GCM tag е нужен?
4. Какво пази AAD owner?
5. Какъв риск остава при app compromise?
6. Как се прави rotation без загуба?

## 16. Очакван резултат

Работещ guided fix за **Криптография и защита на чувствителни данни**, контролиран before/after evidence, root-cause анализ, test matrix и реализирана самостоятелна задача според acceptance criteria. Нормалният разрешен flow остава работещ. Отчетът разграничава доказаното с автоматизиран test, провереното ръчно и оставащите ограничения.

## 17. Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
