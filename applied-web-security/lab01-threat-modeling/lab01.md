# 1. Упражнение 1 — Лабораторна среда и Threat Modeling

**Продължителност:** 110 минути. **Аудитория:** IV курс, бакалавър „Киберсигурност“.

> Всички offensive действия в курса се изпълняват единствено срещу предоставената локална лабораторна среда.

## 2. Security сценарий

Екипът добавя учебен портал и твърди, че „щом е зад Nginx, е защитен“. Преди да оцените конкретен exploit, трябва да установите кой може да достигне всеки компонент и какви данни преминават през него.

## 3. Учебни цели

След упражнението студентът:

- анализира data flows от browser до DB;
- идентифицира assets и attack surface;
- разграничава threat, vulnerability, exploit, risk и mitigation;
- диагностицира излишно публикуван port;
- аргументира пет threats чрез STRIDE;
- тества deny-by-default и network exposure;

## 4. Предварителни знания

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [README](../README.md). Не се изисква предварително познаване на конкретен exploit. Елементарните Java конструкции не се преговарят.

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

**Фокус:** Compose, Nginx, endpoint inventory и trust boundaries. Съпоставете с [DFD и endpoints](../architecture/system-overview.md); отбележете кой input е недоверен и къде се взема security решението.

## 7. Необходима теория

Asset е ценност: документи, самоличност, ключ, availability. Threat е нежелано събитие, vulnerability — причиняваща слабост, exploit — конкретно използване, risk — likelihood × impact, mitigation — намаляващ риска control. Threat actor има capability и достъп; не приемайте, че anonymous и authenticated user имат еднаква позиция.

Trust boundary се преминава при промяна на доверие, например client → server и app → DB. Reverse proxy не валидира ownership. STRIDE групира Spoofing, Tampering, Repudiation, Information disclosure, Denial of service и Elevation of privilege. OWASP Top 10 е ориентир за пропуски, а не доказателство за сигурност. В курса използваме likelihood/impact 1–3 с изрично описани предпоставки.

Технически източници и version scope: [references](../architecture/references.md).

## 8. Уязвим пример

```yaml
# Unsafe пример за сравнение, НЕ го активирайте:
services:
  db:
    ports: ["5432:5432"]  # host interfaces, заобикаля app policy
```

Примерът е умишлен учебен дефект. Сравнявайте го като текстов fixture; не отваряйте DB port.

## 9. Водена практическа задача

Преди работа следвайте [startup/reset](../README.md). Командите за Maven се изпълняват от `vulnerable-app`, а Compose командите — от корена на курса.

### Стъпка 1

Стартирайте baseline по README. Запишете `docker compose ps` и `docker compose config`; очакват се само loopback ports 8080/8081. Не копирайте secret file или environment dumps в отчета.

### Стъпка 2

Изпълнете `curl.exe -i http://localhost:8080/health` и `/api/me`. Очаквания: 200 и 401. Влезте като Alice и повторете `/api/me` → нейното username. Съпоставете резултатите с endpoint inventory.

### Стъпка 3

Прочетете `SecurityConfig.java`, `infra/nginx.conf` и Compose. Начертайте DFD с TB1–TB4; за всеки поток отбележете protocol, identity и sensitive data. Обяснете защо `expose` и `ports` не означават едно и също.

### Стъпка 4

Сравнете unsafe YAML с реалната конфигурация, без да публикувате DB. Опишете заплахата с actor, entry point, asset и impact. Подредете поне пет threats по риск; добавете evidence и предложен test.

### Стъпка 5

Изпълнете baseline test, добавете автоматизирана проверка, че `docker compose config --format json` няма публикувани DB/app ports и host_ip на proxy ports е 127.0.0.1. Проверете и отрицателен конфигурационен fixture в памет, без стартиране на unsafe topology.

Разпределение: scenario/theory 15 мин, наблюдение 15, root cause 10, guided fix 25, tests 15, самостоятелна работа 25, защита 5. Общо 110 минути.

## 10. Анализ на root cause

Нарушеното assumption е „вътрешният компонент е недостъпен по дефиниция“. Публикуван host port създава отделен entry point извън приложната authentication/authorization. Наличието на Nginx не променя правата върху директна DB връзка.

Предайте причинна верига: **недоверен вход → нарушено assumption → липсващ/грешен control → наблюдавано въздействие**. Оценете likelihood/impact по скалата в [threat model](../architecture/threat-model.md); аргументирайте residual risk след fix.

## 11. Реализация на защита

Публикувайте само Nginx на loopback, премахнете host ports на DB/app, използвайте internal network и отделен runtime DB role. За HTTP routes приложете explicit allowlist и deny-by-default. Докажете controls поотделно: network isolation не замества identity или object policy.

Reference branch в общия проект служи за guided comparison. За предаване променете уязвимия code path и запазете същия lab mode. Самостоятелната задача по-долу изисква собствено разширение и няма готово решение в този файл.

## 12. Security regression test

Изпълнете:

```powershell
mvn test '-Dtest=WebSecurityTest#lab01*'
```

Началната baseline проверка трябва да е green; вашият unsafe config fixture трябва да бъде отхвърлен.

Примерен test fragment (пълният runnable class и imports са в `vulnerable-app/src/test/java/bg/tuvarna/lab`):

```java
mvc.perform(get("/api/me")).andExpect(status().isUnauthorized());
mvc.perform(get("/unlisted").with(user("alice")))
   .andExpect(status().isForbidden());
```

Задължителна матрица:

- public health → 200;
- anonymous /api/me → 401;
- authenticated unknown route → 403;
- Compose DB/app ports → няма;
- unsafe config fixture → проверката трябва да го отхвърли;

Добавете поне един нов автоматизиран test за edge case или самостоятелната задача. Повторете `mvn test` след fix и накрая `mvn verify -Psecurity-tests`. Проверявайте content/state/identity, когато са приложими, а не само HTTP status.

## 13. Самостоятелна задача

**Problem statement:** оценете модула registration преди бъдещо публично използване.

**Requirements:** DFD, assets, entry points, trust boundaries, минимум 5 конкретни threats, likelihood/impact и mitigation/test за всяка.

**Constraints:** анализирайте само предоставения код; не добавяйте реален mail provider и не изпращайте заявки навън.

**Acceptance criteria:** всяка threat има actor/precondition и връзка към code/config; поне една е abuse/availability, една е privilege escalation и една е information disclosure. Добавете един автоматизиран security test по избраната threat; не предавайте готовия register от общата архитектура.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

## 14. Edge cases

- Публикуване на IPv6 wildcard вместо IPv4 loopback.
- Nginx е спрян, но директният app port остава публикуван.
- User създава profile с role параметър, който server не трябва да приема.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.

## 15. Въпроси за анализ

1. Може ли internal network да замени authorization?
2. Каква е разликата между asset и threat?
3. Къде е boundary при SQL?
4. Защо vulnerability без Internet exposure остава vulnerability?
5. Как доказвате mitigation?
6. Какво пропуска Top 10?

## 16. Очакван резултат

Работещ guided fix за **Лабораторна среда и Threat Modeling**, контролиран before/after evidence, root-cause анализ, test matrix и реализирана самостоятелна задача според acceptance criteria. Нормалният разрешен flow остава работещ. Отчетът разграничава доказаното с автоматизиран test, провереното ръчно и оставащите ограничения.

## 17. Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
