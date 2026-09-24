---
title: "Лабораторно упражнение 8 — Криптография и защита на чувствителни данни"
sidebar:
  order: 8
  label: "Упражнение 8"
---

# Лабораторно упражнение 8 — Криптография и защита на чувствителни данни

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

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [README](/courses/bg/ueb-sigurnost/podgotovka/). Изпълнени предходните 7 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

## 5. Необходими инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](/courses/bg/ueb-sigurnost/materiali/lab08-cryptography/resources/readme/) съдържа работна карта и очаквани наблюдения.

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

**Фокус:** Web.sensitive → Vault → encrypted field в PostgreSQL. Съпоставете с [DFD и endpoints](/courses/bg/ueb-sigurnost/materiali/architecture/system-overview/); отбележете кой input е недоверен и къде се взема security решението.

## 7. Необходима теория

Hashing е еднопосочен; encryption е обратим при наличие на key; Base64 е само encoding. Symmetric encryption използва общ secret key, asymmetric cryptography — public/private pair. Passwords се проверяват чрез password hashing; те не трябва да се възстановяват чрез decryption.

AES-GCM е authenticated encryption: защитава confidentiality и integrity. Използваме стандартния Java Cipher, 256-bit key, случаен 96-bit nonce/IV и 128-bit authentication tag. IV/nonce не е salt; nonce не трябва да се повтаря със същия key. SecureRandom генерира key/nonce. AAD (additional authenticated data) свързва encrypted field с owner, без да го криптира. Encryption at rest не защитава от компрометиран app, който има key. Key management включва отделно съхранение, access policy, backup, rotation и versioning.

Технически източници и version scope: [references](/courses/bg/ueb-sigurnost/materiali/architecture/references/).

## 8. Уязвим пример

```java
// Web.sensitive в lab08:
db.update("UPDATE app_users SET sensitive=? WHERE username=?", value, user.getName());
```

Примерът е умишлен учебен дефект. За възпроизвеждане използвайте `LAB_MODE=lab08`; не пренасяйте този switch в production.
