# Consistency review и проверка на комплекта

Проверка: 22.09.2026. Обхватът е само създаденият локален курс; съществуващите курсове и сайтът не са променяни.

## Изпълнени проверки

| Проверка | Резултат |
|---|---|
| Точно 10 упражнения по зададените теми | PASS |
| 17 номерирани student sections във всяко | PASS, check-structure.ps1 |
| 16 instructor sections, оценяване 100 точки, въпроси с отговори | PASS |
| resources и local-only scope във всяко | PASS |
| 5–8 цели, поне 3 edge cases, 5–10 въпроса | PASS при съдържателния review |
| Guided и independent задачите са различни | PASS; отделни модули/операции/test contracts |
| Instructor answers са в отделни файлове | PASS; да не се публикуват със student handouts |
| Build и mvn test | PASS: 21 tests, 0 failures/errors |
| mvn verify -Psecurity-tests | PASS: 21 tests + 2 integration tests, 0 failures/errors/skips |
| PostgreSQL SQL semantics | PASS: PostgresIT с postgres:17.6-alpine |
| Реални servlet cookie flags | PASS: CookieIT, HttpOnly/Secure/SameSite=Lax |
| Original vulnerable modes lab02–lab10 | PASS: всички 9 причиняват очакван assertion failure, без test errors |
| Docker Compose config/build/start | PASS, Nginx → Spring Boot → PostgreSQL |
| Network exposure | PASS: само 2 loopback proxy ports, app/DB без host ports |
| HTTP smoke през proxy | PASS: login, identity, ownership, SQLi, encoding/CSP, CSRF/no-state-change, crypto, JWT, logout |
| DB sensitive field | PASS: записът има v1: envelope, не plaintext |
| Decryption след app restart | PASS със същия persistent Docker secret |
| Пълен Compose reset | PASS: 3 users, 3 documents, 0 comments, 0 sensitive fields |
| Локални Markdown връзки | PASS |

Използвана среда: Windows, JDK 21.0.3, Maven 3.9.10, Docker Desktop/Engine 29.6.2. Validation Compose project е `aws-course-validation`, на ports 18080/18081, за да не се засяга съществуваща услуга на 8080. Основните учебни defaults остават 8080/8081.

След проверката временните validation containers, networks и техният DB volume са премахнати. Не са променяни други Docker projects. Генерираният локален AES key остава в игнорираната `.secrets/` папка за следващо учебно стартиране.

## Корекции от review

- Разделени stateless Bearer и stateful session filter chains; JWT не изключва CSRF за browser flow.
- Поправени Java imports и конфликт на bean name; повторно изпълнени tests.
- Nginx има отделен frontend bridge за host publication; app/DB остават само на internal backend.
- Failsafe задава Docker API 1.44: първият PostgresIT run се провали поради Docker 29/client API mismatch; след корекцията точната документирана команда преминава.
- Lab02 изрично изисква DB reset при смяна на password-storage mode.
- Lab07 използва cross-origin, same-site localhost ports, за да не се представя SameSite като причина за невъзпроизводим demo.
- Разграничени HTTP cookie defaults, emitted Secure flag test и реална HTTPS browser проверка.
- Вторият SQL query и homepage XSS flow са unsafe independent starters, не завършени студентски решения.
- Configurable limiter и cookie env overrides са свързани с Compose.

## Граници на проверката

MockMvc/HTTP smoke проверяват encoded HTML и CSP, но не изпълняват JavaScript. Ръчните browser XSS/CSRF демонстрации са подробно описани за занятието; тук не се отчита автоматизиран browser execution test или HTTPS browser cookie test. CookieIT доказва реалния Set-Cookie header. Не е изпълняван ZAP scan или dependency vulnerability scan; lab10 включва методика за тях, а pinned versions не са обявени за production-safe.

Възпроизводимата среда има умишлени учебни ограничения: in-memory single-process limiter и JWT signing key, публични synthetic credentials, HTTP по подразбиране. Default secure означава reference controls за курса, не production certification. Независимите студентски разширения се оценяват по acceptance criteria след реализацията им; не са представени като вече изпълнен код.

## Възпроизвеждане на проверките

```powershell
# Корен на курса, със стартирана secure Compose среда:
./scripts/check-structure.ps1
./scripts/check-network.ps1
./scripts/smoke.ps1
./scripts/check-vulnerable-modes.ps1
cd vulnerable-app
mvn verify -Psecurity-tests
```

Diagnostic red-mode runner не е финалният green regression gate. След поправки студентът изпълнява съответните tests в същия lab mode и очаква success.
