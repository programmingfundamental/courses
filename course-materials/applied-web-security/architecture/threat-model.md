# Модел на заплахите за Task Manager

Активи: задачи, отчети, потребителски роли, паролни хешове, session/JWT/refresh credentials, поверителни бележки и наличност. Участници: anonymous клиент, USER, ADMIN, оператор на базата и клиент с откраднат token.

Граници: клиент/HTTP API, Authentication/обектна policy, service/repository, приложение/DB, DB/хранилище за ключове, HTML текст/интерпретация в браузъра. Входове са маршрути, JSON/form полета, path IDs, cookies и Authorization.

| Заплаха | Проверка/защита | Упражнение |
|---|---|---|
| Публикувана база | Compose policy | 1 |
| Непроверен повторен login | authenticate за всяка заявка | 2 |
| Чужда задача по ID | TaskPolicy и owner | 3 |
| Много опити | Clock/праг/срок | 4 |
| SQL/JPQL синтаксис от input | Binding, owner predicate | 5 |
| HTML от task fields | Output encoding, URL policy, CSP | 6 |
| Заявка с автоматичен session cookie | CSRF token/rotation | 7 |
| Backup с private note | AES-GCM, AAD, отделен key | 8 |
| JWT/refresh reuse | Claims, stateless isolation, rotation | 9 |
| Изтичане в logs/непроверен обхват | Audit allowlist, regression suite | 10 |

Вероятност и въздействие се оценяват 1–3 с явни предпоставки. Risk е произведението им. За всяко наблюдение посочете evidence, status (потвърдено/защитено/хипотеза), root cause, mitigation, ръчна проверка и остатъчен риск. Скрит DB порт не заменя owner policy; криптирана DB не защитава от компрометирано приложение с ключа.
