# Упражнение 8 — Криптиране на поверителна бележка към задача — решения и насоки


## Решение на примерния проблем

FieldCipher чете Base64 файла и проверява точно 32 bytes. Ключът е SecretKeySpec(bytes,"AES"). Ядрото на encrypt е:

```java
byte[] nonce = new byte[12];
new java.security.SecureRandom().nextBytes(nonce);
var cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding");
cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key, new javax.crypto.spec.GCMParameterSpec(128,nonce));
cipher.updateAAD(aad.getBytes(java.nio.charset.StandardCharsets.UTF_8));
byte[] sealed = cipher.doFinal(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
byte[] packed = java.nio.ByteBuffer.allocate(12+sealed.length).put(nonce).put(sealed).array();
return "v1:"+java.util.Base64.getEncoder().encodeToString(packed);
```

decrypt проверява prefix, максимален размер, Base64 и минимум 28 bytes, взема първите 12 за nonce и останалото за doFinal с DECRYPT_MODE и същия AAD. GeneralSecurityException/невалиден envelope се превръщат в обща грешка без данни или ключ; никога не се връща частичен текст.

TaskSecretService използва aad=task.getId()+":"+task.getOwner().getId(). Policy се проверява преди decrypt и save. При PUT проверяваме 1–500 символа, записваме ciphertext и връщаме 204; GET връща plaintext само от отделния endpoint. Ключът е в secret файл, монтиран read-only в Compose; FIELD_KEY_FILE сочи файла. Това се добавя в упражнението, не е задължително за първоначалното стартиране.

## Решение на самостоятелна задача 1

FieldCipher получава Map<String,SecretKey> keyRing и currentKeyId от конфигурация. keyId се избира само от map, не се превръща в произволен file path. При encrypt записваме v1:keyId:base64; decrypt разделя на точно три части и отказва непозната версия/ключ. Migration service чете старата бележка и owner в транзакция със заключване, decrypt със стария ключ и encrypt с текущия. Ако keyId вече е current, не променя записа. Мигрираме партиди и броим оставащите стари ID преди премахване на ключ.

Тест: keyA запис → сменяме current на B при запазен A → old read работи → migrate → prefix v1:B → A може да се премахне след миграцията → GET пак работи. Повторна migration не губи данни; unknown key/отрязан tag отказва.

## Решение на самостоятелна задача 2

| Secret | Възстановяване | Съхранение/смяна | Тест |
|---|---|---|---|
| Password | Не | BCrypt със salt; password reset | matches true/false, различни hashes |
| JWT signing key | Да | Отделен secret файл; контролиран key set | Стар подпис след премахване на key=401 |
| Refresh token | Само проверка | Digest вместо raw token; rotation/revocation в упражнение 9 | Използван стар token се отказва |
| Private note | Да | AES-GCM, AAD, key ring | Roundtrip и wrong-owner отказ |
| DB credential | Да | Environment/secret file; смяна в DB и app | Новият работи, старият се отказва |

Автоматизирани тестове: две FieldCipher инстанции с един файл декриптират взаимно, различен key отказва; AAD за друг task/owner отказва; „Поверителна бележка“ се възстановява точно. Втори тест с PasswordEncoder проверява matches и различни hashes. Backup без ключ не възстановява бележките; ключът има отделен backup и контрол на достъпа.


## Въпроси за анализ

1. Защо бележката не се хешира като парола?
2. Защо AAD използва owner на задачата, а не четящия admin?
3. Кога може да се премахне стар ключ?

## Checklist

- [ ] Примерният проблем има работеща реализация в Task Manager.
- [ ] Самостоятелните задачи имат код/анализ и проверими резултати.
- [ ] Тестовете включват разрешен и отказан сценарий.
- [ ] Отказаната операция не променя DB.
- [ ] Изпълнените H2/PostgreSQL и браузърни проверки са разграничени.
- [ ] Отчетът не съдържа пароли, raw tokens или поверителни бележки.
