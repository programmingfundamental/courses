---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## 7. Мини експеримент
Извикайте protected test path без token, с expired fixture и с viewer role. После изпратете create request с member token през Gateway→BFF→Activity→User. Намерете trace и проследете общото време; премахнете token от видими screenshots/logs.

## 8. Водена практическа задача — 35 минути, включително checkpoint
### Стъпка 1
Генерирайте локални keys с token.mjs --init и използвайте member/viewer/--expired fixture. Public key се монтира в services; private key остава само в ignored local runtime. Проверете issuer=https://mobile-lab.invalid и audience=mobile-api в configuration.

### Стъпка 2
Приложете authenticated/role policy към BFF business endpoints и съответните service paths. Create изисква member; User/Activity read има explicit access rule. Проверете ownership. Пренесете Authorization само през trusted REST clients и не го логвайте.

### Стъпка 3
Затворете fixture/direct service/WebSocket routes на публичния Gateway за този security exercise. Android държи краткия demo token само в memory и изпраща Bearer header; expiry води до NeedsLogin, без endless retry. Production login и secure token storage са извън обхвата на това занятие; starter не съдържа готов OIDC login adapter.

### Стъпка 4
Стартирайте --profile observability. Изпратете create request през цялата chain и сравнете Gateway span, BFF, Activity и User. Свържете requestId към logs и traceId. Проверете metrics counts/error rate; не излагайте /q/metrics през mobile ingress.

Работете в общия platform проект. От директорията starter изпълнете `node ../../platform/tools/lab.mjs 10 build`. Build проверява scaffold-а и вашите промени; наличието на 501 LAB_TODO означава незавършена учебна функционалност, а не успешен checkpoint.

## 9. Checkpoint
- Missing/expired/member/viewer случаи имат правилни 401/403/успех outcomes.
- Body userId не позволява чужд owner.
- Един request има проследима Gateway→BFF→Activity→User chain.

## 10. Самостоятелна задача — 20 минути в часа
**Problem statement:** Диагностицирайте предварително подготвен slow/failure Home scenario чрез trace, logs и metrics.

**Functional requirements:** Определете dominant component и дали забавянето е service execution, queue/network wait или retry amplification. Посочете поне един trace, свързан log и metric/count наблюдение. Дайте кратка хипотеза и проверка.

**Technical constraints:** Няма задача да „оправите всичко“ за 20 минути. Не променяйте произволно timeouts преди измерване. Не публикувайте credentials, tokens или лични данни в report.

**Acceptance criteria:** Диагнозата назовава component/call и concrete evidence; разделя symptom от cause; сравнява baseline и injected scenario; предлага bounded следваща промяна с trade-off. Липса на trace се описва като instrumentation gap, а не се измисля result.

Решението и кратката аргументация се проверяват в края на същото занятие. Това не е домашна работа.

## 11. Failure scenarios / Edge cases
| Сценарий | Очакване / наблюдение |
|---|---|
| Missing/expired/invalid signature token | 401, без downstream business effect. |
| Viewer role / чужд owner | 403 или explicit ownership policy. |
| Slow User dependency | Trace показва къде се натрупва latency/timeout. |
| Collector недостъпен | Business API остава работоспособен; telemetry gap е видим. |
| Token в logs/URL | Недопустимо; test/report се коригира. |

## 12. Тестване
Security integration matrix: no token, expired, wrong audience, tampered signature, viewer, member и cross-user body. Token tool поддържа --expired, --wrong-audience и --other-user. Проверете директен internal service access със същите authorization expectations. Trace test съпоставя parent/child и request IDs. Failure scenario използва compose.slow-user.yaml. В report не включвайте token output.

## 13. Наблюдение и измерване
Съберете 20 baseline и 20 injected requests с counts/status/p50/p95/max, един representative trace и sanitized logs. Разграничете server spans от mobile total duration и capture overhead. Проверете retry attempts, а не само request rate; metric labels не съдържат userId/requestId.

Запишете кратка таблица в `results/lab10/observations.md`: configuration, workload, expected/actual, sample count и ограничения. За latency използвайте p50/p95/max, когато имате достатъчно измервания, а не само средна стойност.

## 14. Въпроси за анализ
1. Защо JWT decoding не е validation?
2. Как role се различава от ownership?
3. Защо native app няма безопасен embedded secret?
4. Кога token propagation е неподходящо?
5. Защо сборът от parallel spans е подвеждащ?
6. Как трите вида telemetry се допълват?

## 15. Очакван резултат
Защитен REST path и evidence-based diagnosis report за distributed mobile system; explicit ограничения на laboratory issuer и realtime security.

## 16. Критерии за приемане
- [ ] JWT signature/issuer/audience/expiry се проверяват.
- [ ] 401/403 и ownership са тествани.
- [ ] Tokens не се логват.
- [ ] Gateway не публикува internal/fixture paths.
- [ ] Trace chain и metrics са достъпни.
- [ ] Самостоятелният report посочва причина, доказателства и trade-off.
- [ ] Няма измислени latency/energy резултати.
