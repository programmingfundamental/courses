---
title: "Упражнение 8 — Криптография и защита на чувствителни данни"
sidebar:
  order: 8
  label: "Упражнение 8"
---

# Упражнение 8 — Криптография и защита на чувствителни данни

## 1. Теория

### 1.1. Избор на криптографски механизъм

1. **Hashing** е еднопосочно преобразуване; **encryption/decryption** криптира и възстановява данните с ключ; **encoding** сменя представянето.
   - Пример: паролата се проверява с BCrypt; поле, което трябва да бъде прочетено обратно, се криптира; Base64 само представя байтовете като текст.
2. **AEAD** е криптиране с проверка за цялост и допълнителни данни. **AES-GCM** е такъв механизъм; **nonce/IV** е уникалната за ключа стойност при операция; **tag** удостоверява целостта.
   - Пример: Java Cipher с AES/GCM/NoPadding използва 96-битов nonce и 128-битов tag; повторение на nonce със същия ключ нарушава гаранциите.
3. **AAD** се удостоверява, без да се криптира; **envelope** е форматът, който съхранява версия, nonce, ciphertext и tag.
   - Пример: owner=alice като AAD води до отказ, ако същият запис се декриптира за bob. **Roundtrip** е encrypt → decrypt с възстановяване на първоначалния текст.
4. **Entropy** описва непредсказуемостта; **key rotation** сменя ключ; **revocation** отнема валидност; **key versioning** различава поколения ключове.
   - Пример: SecureRandom генерира ключ, а key ID избира стария ключ за четене и новия за следващ запис.
5. **Decision record** описва решение и основанията му; **outbound use** означава изпращане на тайна към друга услуга.
   - Пример: API secret само за проверка може да се пази като хеш; за последващо изпращане трябва да може да се възстанови от защитено хранилище. **Migration** преобразува съществуващите записи към новия формат.

### 1.2. Ключове и защита на полета

1. Hashing е еднопосочен; encryption е обратим при наличие на key; Base64 е само encoding. Symmetric encryption използва общ secret key, asymmetric cryptography — public/private pair. Passwords се проверяват чрез password hashing; те не трябва да се възстановяват чрез decryption.

2. AES-GCM е authenticated encryption: защитава confidentiality и integrity. Използваме стандартния Java Cipher, 256-bit key, случаен 96-bit nonce/IV и 128-bit authentication tag. IV/nonce не е salt; nonce не трябва да се повтаря със същия key. SecureRandom генерира key/nonce. AAD (additional authenticated data) свързва encrypted field с owner, без да го криптира. Encryption at rest не защитава от компрометиран app, който има key. Key management включва отделно съхранение, access policy, backup, rotation и versioning.

### 1.3. Автоматизирани проверки: понятия и пример

1. **Security regression test** е автоматизиран тест, който проверява правило за сигурност и открива повторната поява на поправен проблем.
   - **Negative test** проверява отказана операция; **positive control** проверява нормална разрешена операция. **Security invariant** е правило, което трябва винаги да е изпълнено.
   - Пример: без вход GET /api/me трябва да върне 401, а след успешен вход трябва да върне името на текущия потребител.
2. **Assertion** сравнява очаквано и получено; **red → green** означава провалена проверка преди поправка и успешна проверка след нея.
   - Грешка при компилиране или недостъпна база е проблем на средата, а не доказателство, че проверката е открила нарушено правило.
3. **Unit test** проверява отделна единица; **integration test** проверява взаимодействието на компоненти; **test suite** е набор от тестове.
   - **JUnit** изпълнява Java тестовете; **MockMvc** подава HTTP заявки през Spring без браузър; **Testcontainers** стартира зависимости като PostgreSQL в Docker.
   - Пример: MockMvc проверява HTTP отговор, но изпълнението на JavaScript и поведението на cookies се проверяват в браузър. **Fixture** е наборът входни данни или конфигурация на теста.
4. **Test matrix** е таблица от случаи и очаквания; **edge case** е граничен случай; **test report** е отчетът от изпълнението.
   - Пример: липсващ вход → 401, собствен ресурс → успех, чужд ресурс → отказ. Проверявайте и съдържанието и състоянието в базата.

В съществуващия тестов клас WebSecurityTest полето mvc е MockMvc. Следният фрагмент подава заявка без сесия и проверява отказа:

```java
mvc.perform(get("/api/me"))
   .andExpect(status().isUnauthorized());
```

От vulnerable-app командата `mvn test '-Dtest=WebSecurityTest#lab01*'` изпълнява методите с префикс lab01. След промяна повторете същия тест, без да променяте очакването, и изпълнете положителния случай. Maven запазва отчета в target/surefire-reports. Профилът `mvn verify -Psecurity-tests` добавя интеграционните проверки; **profile** е именуван набор от настройки.

### 1.4. Работа с материалите и резултатите

- **Code diff** показва промените в кода; **evidence** е доказателство като резултат от заявка или тест.
  - Пример: предайте разликата в метода и отчета от теста, който проверява промяната.
- **Acceptance criteria** са проверимите условия за приемане; **constraints** са ограниченията на решението.
  - Пример: отказана промяна не трябва да обновява запис в базата.
- **Baseline** е началното състояние за сравнение; **LAB_MODE** избира конфигурация при стартиране.
  - Пример: след промяна на кода повторете теста със същата конфигурация, за да сравните поведението.

- **Root cause** е първопричината; **control** е защитна мярка; **policy** е правило за достъп или поведение.
  - Пример: липсваща проверка на owner е първопричина; сравняването му с текущия потребител прилага правилото за собственост.
- **Audit** е журнал на действията; **correlation ID** свързва заявката със записите за нея.
  - Пример: запис с тип LOGIN_FAILURE и идентификатор на заявката позволява проследяване на отказан вход без записване на паролата.
- **State** е състоянието на системата; **persistence** е запазването на данни; **migration** преобразува вече записани данни.
  - Пример: след отказана промяна записът в базата остава непроменен; смяна на формата на пароли изисква и обработка на старите записи.

Технически източници и version scope: [references](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/references.md).

## 2. Подготовка

### Предварителни знания

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [подготовка на средата](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Изпълнени предходните 7 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

### Инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/lab08-cryptography/resources/README.md) съдържа работна карта и очаквани наблюдения.

### Архитектурен контекст

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

**Фокус:** Web.sensitive → Vault → encrypted field в PostgreSQL. Съпоставете с [DFD и endpoints](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/system-overview.md); отбележете кой input е недоверен и къде се взема security решението.

## 3. Примерен проблем

Backup на портала съдържа личен identifier в plaintext. Authorization върху endpoint не помага на offline reader на този backup. Трябва да защитите полето, без да създавате собствен crypto алгоритъм.

### Начален код

```java
// Web.sensitive в lab08:
db.update("UPDATE app_users SET sensitive=? WHERE username=?", value, user.getName());
```

### Стъпки за решаване

Преди работа следвайте [стартиране и възстановяване](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Командите за Maven се изпълняват от `vulnerable-app`, а Compose командите — от корена на курса.

#### Стъпка 1

Стартирайте lab08 с чисти данни. POST /api/sensitive със synthetic value `SYNTHETIC-123` и валиден CSRF token. Прочетете през /api/sensitive → същата стойност.

#### Стъпка 2

Чрез локален DB query установете, че полето съдържа plaintext. Не използвайте истински ЕГН, API keys или пароли. Изпълнете WebSecurityTest#lab08_sensitiveFieldNotPlaintext → red.

#### Стъпка 3

Проследете Vault.encrypt/decrypt: Cipher AES/GCM/NoPadding, random nonce, AAD owner, envelope. Приложете този flow и в уязвимия code path. Key идва от Docker secret file; не го добавяйте в application.properties.

#### Стъпка 4

Направете roundtrip и сравнете две encryption операции върху еднакъв plaintext: ciphertext трябва да е различен. Променете един byte в локален тест → generic authentication failure, без partial plaintext.

#### Стъпка 5

Пуснете VaultTest и AuditTest. Рестартирайте Compose app и проверете decryption със същия persistent key. Опишете преобразуването на съществуващите некриптирани записи при запазване на данните.

### Анализ на причината

Storage access се смята за еквивалентен на permission за четене на чувствителните полета. Ако key и ciphertext са в един dump, encryption добавя малка защита; затова key boundary е отделен. Непроверен authentication tag или nonce reuse нарушават гаранциите на AEAD.

Причинната верига се описва като: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/threat-model.md); аргументирайте residual risk след fix.

### Реализация на защита

Използвайте стандартен AES-GCM чрез Vault, random nonce за всяка операция и owner като AAD. Envelope има version `v1`, nonce, ciphertext и tag; това не е custom crypto algorithm. Не логвайте crypto exceptions с input/key material. Persistent key е нужен при persistent DB; временно генериран key е допустим само с временната H2 база. За rotation проектирайте key ID и decrypt-old/encrypt-new стратегия.

### Проверка с регресионен тест

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

Матрица на примерните проверки:

- encrypt/decrypt → original value;
- еднакъв input два пъти → различни envelopes;
- wrong key/owner → generic error;
- corrupted ciphertext/tag → rejected;
- null/empty/malformed envelope → rejected;
- logs не съдържат synthetic field или credentials;

Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.
