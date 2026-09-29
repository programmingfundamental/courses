# Упражнение 11 — Интегрирана оценка и регресионни тестове на Task Manager — решения и насоки


## Решение на примерния проблем

Матрица на завършеното приложение:

| Област | Отрицателна проверка | Положителна |
|---|---|---|
| Identity | Грешен login дори при съществуваща сесия=401 | Правилен login=200, нов session ID |
| Ownership | Чужда задача=404, без промяна | Owner/admin работи |
| Search | SQL-подобен вход не връща чужди IDs | Апостроф/кирилица работят |
| HTML | Няма raw tag/опасна URL scheme | Текстът е четим |
| CSRF | Missing/other-session token=403 | Актуален token работи |
| Private note | Wrong owner/AAD/tag отказва | Owner/admin roundtrip |
| JWT/refresh | Invalid Bearer=401; used refresh=401 | Валиден token/нов refresh работят |
| Logs | Няма password/raw tokens/note | Event и correlation са налични |

Изпълняваме mvn test, отделната тестова PostgreSQL среда и mvn -Ppostgres-tests test. При mutation проверката променяме само owner условието и не променяме fixtures/status очаквания. Build failure не е валиден резултат от тази проверка.

## Решение на самостоятелна задача 1

Примерен отчет за началното lab11 и последващата реализация:

1. Регистрационният DTO има само @NotBlank: потвърдена липса на размерни ограничения в starter; root cause е липса на byte/length validation; защита от упражнение 3; тест с 40 кирилски символа → 400 без INSERT. След поправката отбелязваме „отстранено“, не „текущ дефект“.
2. Role.USER в AuthService е защитен случай: JSON role=ADMIN не създава admin; evidence е редът в DB и regression тест. Рискът от mass assignment остава предмет на бъдещи DTO промени.
3. Task в starter няма owner: липсва изолация между потребителите. След упражнение 4 bob PATCH чужд ID → 404 и всички полета остават непроменени; root cause е липсваща object policy, поправката е в service.

AuditFilter е OncePerRequestFilter с @Order пред security chain. Генерира UUID, задава X-Correlation-ID и във finally записва status и event type. Типът се избира по точен метод и фиксиран шаблон, например POST /auth/register → REGISTRATION_RESULT, PATCH /tasks/{число}/update → TASK_UPDATE_RESULT, останалото → HTTP_RESULT. Не логваме произволен URI, query, principal input, headers или body. Подаден от клиента X-Correlation-ID не заменя генерирания.

AuditTest използва OutputCaptureExtension и реални requests със sentinel PASSWORD-MARKER, TOKEN-MARKER и NOTE-MARKER. assertThat(output.getAll()).doesNotContain трите стойности; contains event type и върнатия server correlation. При 403/404 няма DB update и event отразява отказа. Output capture се прави без включено MockMvc print на request body.

Нов TaskUpdateIsolationTest: създаваме users/tasks, bob PATCH чужд task с валиден csrf → 404, после през repository сравняваме summary/description/deadline/owner с предишните стойности. Alice PATCH същия task → 200 с нови стойности. Така тестът доказва едновременно отказа и нормалната функционалност.

## Решение на самостоятелна задача 2

- Преди изпълнение записваме commit, spring.profiles.active, DB URL без credentials и API base URL. Проверка отхвърля production/различна тестова база за create-drop профила.
- PostgreSQL тестовете се изпълняват с профила postgres-tests срещу compose.test.yml; проверяваме reports за Tests>0, Failures=0, Errors=0, Skipped=0 и JDBC URL за PostgreSQL. H2 успех не замества този резултат.
- Sentinel log тест се изпълнява с действителните logging настройки; raw request logging остава изключено.
- HTTPS proxy е допълнителна среда: повторете CSRF/session потока и проверете cookie flags и реално изпращане в браузъра. Ако не е изпълнено, отбележете го като непроверено, не като успешно.


## Въпроси за анализ

1. Кое доказателство различава finding от hypothesis?
2. Защо тест само за status не доказва липса на DB промяна?
3. Как се разбира, че тестовете са минали срещу правилната база?

## Checklist

- [ ] Примерният проблем има работеща реализация в Task Manager.
- [ ] Самостоятелните задачи имат код/анализ и проверими резултати.
- [ ] Тестовете включват разрешен и отказан сценарий.
- [ ] Отказаната операция не променя DB.
- [ ] Изпълнените H2/PostgreSQL и браузърни проверки са разграничени.
- [ ] Отчетът не съдържа пароли, raw tokens или поверителни бележки.
