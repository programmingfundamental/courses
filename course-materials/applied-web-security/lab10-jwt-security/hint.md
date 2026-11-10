# Упражнение 10 — JWT, отделен Bearer API и refresh rotation — решения и насоки


## Решение на примерния проблем

JwtService използва Clock за издаване и parser.clock(() -> Date.from(clock.instant())). Издаването добавя .issuer("task-manager").audience().add("task-manager-api").and() към съществуващия builder. Parser verifyWith(getKey()) проверява подписа; след parseSignedClaims проверяваме header.getAlgorithm().equals("HS256"), sub!=null&&!sub.isBlank(), exp!=null&&now.before(exp), iss="task-manager" и audience съдържа task-manager-api. При която и да е грешка връщаме 401 без payload в лога. Алгоритъмът и ключът не се избират от входен URL/kid.

JwtAuthFilter получава валидирания subject, зарежда UserDetails и проверява isEnabled/isAccountNonLocked/isAccountNonExpired/isCredentialsNonExpired преди context. Unknown user/invalid signature/claim се улавят и sendError(401), без продължаване на chain или session fallback. Filter bean се изключва от автоматичната регистрация:

```java
@Bean
org.springframework.boot.web.servlet.FilterRegistrationBean<JwtAuthFilter> jwtRegistration(JwtAuthFilter filter) {
    var registration = new org.springframework.boot.web.servlet.FilterRegistrationBean<>(filter);
    registration.setEnabled(false);
    return registration;
}
```

В @Order(1) chain поставяме securityMatcher("/token-api/**"), STATELESS, NullSecurityContextRepository, csrf.disable(), httpBasic.disable(), formLogin.disable(), requestMatchers("/token-api/admin/**").hasRole("ADMIN") и anyRequest.authenticated(); добавяме JWT filter пред UsernamePasswordAuthenticationFilter. Session chain е @Order(2), пази CSRF и няма JWT filter. TokenTaskController използва същия TaskService и TaskPolicy. За ръчна проверка на 403 може да добавим GET /token-api/admin/status, връщащ фиксиран status само за ADMIN.

## Решение на самостоятелна задача 1

Преподавателят подготвя локално примерни tokens с учебния signing key, отделно от production issue метода. Не променяме само payload за missing-exp, защото това проверява подписа. Студентите копират token в Postman Bearer Token: correct=200; чужд ключ/payload mutation/wrong issuer/wrong audience/missing exp/empty or missing sub=401; aud=[other,task-manager-api]=200; disabled user=401; валиден USER към admin status=403. Реална login сесия без Bearer към /token-api/tasks=401. Неподготвените claim случаи се отбелязват като неизпълнени.

Използваме token с кратък срок и изпращаме Postman заявка преди и след изтичането: 200, после 401. Точната граница изисква контролиран Clock в упражнение 11. При смяна на signing key старият token е 401; рестарт със същия ключ не го обезсилва. Възстановяваме конфигурацията след проверката.

## Решение на самостоятелна задача 2

Изменяме RefreshToken: tokenDigest unique вместо raw token; пазим username, createdAt, expiryDate, revoked. Миграцията отменя старите raw записи и изисква нов login. Генерираме 32 случайни байта със SecureRandom, Base64url без padding; SHA-256 digest се пази в DB. Raw стойността се връща чрез отделен IssuedRefresh(recordEntity,rawValue), не чрез getToken на entity.

Repository метод findByTokenDigest има @Lock(PESSIMISTIC_WRITE); RefreshTokenService.rotate е @Transactional: digest вход → заключен ред → !revoked и now<expiryDate → активен user → revoked=true → издаване/запис на нов digest → връщане на raw нов token и access token. AuthService.refresh делегира цялата операция на тази транзакция. Повторна употреба е 401. Revoke-all маркира редовете revoked в транзакция; редът на заключванията е постоянен. Logout също инвалидира сесията. На PostgreSQL две едновременни refresh заявки в отделни транзакции имат един успех и един 401, с точно един активен наследник.

Ръчни проверки: wrong/expired/used digest=401; disabled user=401 без нов token; след expiry отказ; valid refresh=200 с различна raw стойност; DB не съдържа нито старата, нито новата raw стойност. След logout refresh е отказан, но access token остава валиден до exp при активен user. За незабавно access revocation е нужен отделен token version/denylist, който не е част от този договор.


## Въпроси за анализ

1. Как различавате parsing от проверен подпис?
2. Защо сесията не трябва да спасява невалиден Bearer?
3. Какво става при два едновременни refresh опита?

## Checklist

- [ ] Примерният проблем има работеща реализация в Task Manager.
- [ ] Самостоятелните задачи имат код/анализ и проверими резултати.
- [ ] Ръчните проверки включват разрешен и отказан сценарий.
- [ ] Отказаната операция не променя DB.
- [ ] Ръчните API проверки с Postman, DB наблюденията и браузърните проверки са разграничени.
- [ ] Отчетът не съдържа пароли, raw tokens или поверителни бележки.

## Тест за Microsoft Forms

**Упражнение 10 — JWT отделен Bearer API и еднократно подновяване.** Втората колона е ключът за Word файла. Номерацията в последната колона е от [учебния материал](lab10.md); „задача“ означава самостоятелна задача. Наблюденията в условията са учебни сценарии.

| Въпрос | Отговор | Обосновка | Къде е обяснено |
| --- | --- | --- | --- |
| 1 | D | Stateless веригата за token-api не използва сесията като резервен източник на удостоверяване. Невалидният Bearer води до 401. | §1.1, т. 3; §3, стъпки 2–5; задача 1. |
| 2 | B | В списък с аудитории трябва да присъства изискваният получател. Това не отменя проверката на останалите claims и текущите права в базата. | §1.1, т. 1 и 5; §3, стъпки 1–3; задача 1. |
| 3 | A | JwtAuthFilter използва текущите authorities. Активният потребител може да е удостоверен, но вече да не е упълномощен за административен ресурс. | §1.1, т. 5; §3, стъпка 3; задача 1. |
| 4 | C | Отмяната на refresh и сесията не променя вече подписания access token. Отделен механизъм е нужен за предсрочната му отмяна в описания модел. | §1.1, т. 4–5; задача 2, последно изречение. |
| 5 | A и D | В избраните образци невалидното твърдение е единствената причина за отказ. Грешен подпис, липсващ Bearer или грешен формат не изолират проверка на конкретен claim. | §1.1, т. 1–2; §3, стъпка 2; задача 1. |
| 6 | B и E | JWT филтърът трябва да работи само в избраната верига. Изключението за CSRF в stateless API не се пренася към сесийните маршрути. | §1.1, т. 3; §3, стъпки 3–4. |
| 7 | C и D | Съхраняването на digest не обезсилва автоматично стария запис. Последователната проба проверява единия случай, а атомарността при застъпване изисква отделна конкурентна проверка. | §1.1, т. 4; задача 2 — граница между последователната и конкурентната проверка. |
| 8 | A и C | Сравняването на конкретни ID проверява обхвата на върнатите задачи. Отделната проба с деактивиран потребител изолира проверката за актуален enabled статус. | §1.1, т. 5; §1.2, т. 3–4; §3, стъпки 3 и 5; задача 1. |
| 9 | 3 | При now=100 са валидни само exp=101 и 103. При now=102 е валиден само exp=103. Равенството now=exp не е успех. | §1.1, т. 2; §3, стъпки 1–2; задача 1. |
| 10 | 3 | Един успех издава R1. После R0 е невалиден, първата употреба на R1 успява, повторната се отказва, а употребата на R2 успява: общо 1 + 1 + 1. | §1.1, т. 4; задача 2 — атомарна употреба и подмяна. |

За импорт използвайте `web-security-lab10-ms-forms.docx` и изберете **Quiz**. Проверете по ключа въпроси 1–4, включете **Multiple answers** за 5–8 и задайте точните текстови отговори за 9–10. Сравнете текстовете, ако вариантите са разбъркани. В студентския преглед не трябва да се виждат редове `ANSWER:`. Файлът с ключа е предназначен за преподавателя.
