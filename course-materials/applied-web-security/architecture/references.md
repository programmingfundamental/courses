# Технически източници

- Началният код и версиите: [Task Manager](../task-manager/README.md), [pom.xml](../task-manager/pom.xml), [произход](../task-manager/SOURCE.md).
- Spring Security: https://docs.spring.io/spring-security/reference/ — authentication, authorization, sessions, CSRF и тестове.
- Spring Data JPA: https://docs.spring.io/spring-data/jpa/reference/ — @Query, binding, transactions и locking.
- JJWT: https://github.com/jwtk/jjwt — API за подпис и проверка; проектът използва 0.12.5.
- Java Cipher: https://docs.oracle.com/en/java/javase/17/docs/api/java.base/javax/crypto/Cipher.html — AES-GCM и AAD.
- OWASP Cheat Sheet Series: https://cheatsheetseries.owasp.org/ — XSS, CSRF, password storage, logging и threat modeling.

При използване на документация съпоставете API с фиксираните версии в pom.xml; версията на библиотеката сама по себе си не доказва наличие или липса на уязвимост.
