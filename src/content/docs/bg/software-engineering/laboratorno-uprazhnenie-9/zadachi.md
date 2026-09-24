---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## 9. Водена практическа задача

Командите за Python/pytest са от `ai-platform` при активирана среда. Процесът е **проблем → теория → анализ на лошо решение → практическа задача → самостоятелна задача → тестове → инженерна дискусия**.

### Стъпка 1

Използвайте incident.json и разделете known facts от hypotheses. Преди да обвините модела, проверете, че batch size е променен от1 на16; това прави latency comparison нееквивалентен.

### Стъпка 2

Прочетете middleware event schema: correlation_id, operation, status, duration_ms, model_version. Добавете test за липса на payload/credentials и bounded cardinality.

### Стъпка 3

Изпълнете локалния benchmark срещу Docker API с10 warmup и100 measured requests. Запишете hardware, concurrency1,batch 1,версия и клиентска измервателна точка; сравнете с server metric definition.

### Стъпка 4

Симулирайте missing artifact или unready release. Проверете live200/ready503/predict503 и че error_count се увеличава. Опишете retry/timeout policy без безкраен retry на deterministic validation failure.

### Стъпка 5

Създайте diagnostics runbook: signals → hypotheses → targeted measurements → mitigation → verification. Добавете drift smoke върху shifted copy и ясно разграничете signal от доказан quality regression.

Време: сценарий/теория15, анализ10, guided работа30, checkpoint5, самостоятелна работа25, tests15, дискусия10 минути — общо110. При 90 минути преподавателят подготвя environment и baseline evidence предварително.

## 10. Checkpoint

Model version е наблюдаема, logs не съдържат sensitive content, latency е измерена с protocol и failure не се маскира като prediction.

Покажете working increment и кратък before/after diff. Ако има failure, класифицирайте го като environment, contract, quality или implementation проблем. Не преминавайте нататък само заради един green happy-path test.

## 11. Самостоятелна задача

**Problem statement:** latency се е увеличила 5 пъти след deployment.

**Requirements:** observability plan с минимум 5 конкуриращи се хипотези, нужни metrics/spans/logs, controlled experiments, rollback/degradation trigger и owner.

**Constraints:** synthetic local workload, максимум1000 requests, без load върху чужди services; не променяйте няколко фактора едновременно.

**Acceptance criteria:** поне едно реално измерване и един failure-injection test, before/after comparison с еднакъв protocol; runbook описва time budget и критерий за успех, без да твърди причинност само от корелация.

Предайте собствена реализация/спецификация, rationale и evidence. Пълно решение не е включено тук; готовият общ проект е reference baseline за сравнение на contracts, а starter TODO задачите изискват ваш diff и допълнителни проверки.

## 12. Automated tests

Начални runnable проверки:

```bash
pytest tests/test_observability.py tests/test_api.py -q
python scripts/benchmark.py --count 100
```

Новите tests трябва да проверяват observable contract, negative/edge behavior и разрешения нормален flow. За документните задачи автоматизирайте структурните invariants, а смисъла проверете с peer review. За statistical/performance проверки запишете dataset/workload/seed/version/sample count; не твърдете универсална гаранция от малка synthetic извадка.

Добавете test/evidence traceability: **requirement ID → test name → command → actual result → limitation**. Поне една собствена проверка трябва да открива deliberate bad fixture или regression. След restore повторете suite; не променяйте assertions, за да прикриете failure.

## 13. Edge cases

- p95 от празен sample set — трябва null, не подвеждаща нула.
- Metrics се reset-ват при restart.
- Няколко workers имат отделни in-memory counters.
- Retry storm при претоварен service.

Изберете поне един за нов автоматизиран test; за останалите опишете expected behavior и owner.

## 14. Въпроси за анализ

1. Защо live200 не означава ready?
2. Защо model_version е metric dimension?
3. Какъв е рискът от user-ID metric label?
4. Защо timeout не гарантира cancellation?
5. Как се различава drift от performance incident?
6. Какво означава safe degradation?

## 15. Очакван резултат

Завършен engineering increment по **Наблюдаемост, надеждност и управление на грешки**, checkpoint evidence, самостоятелната задача според acceptance criteria и нови automated checks. Предайте decision/trade-off analysis, а не само screenshot или model score. Данните, моделът и test environment трябва да са идентифицируеми.

## 16. Checklist

- [ ] Анализирах проблемния starter и записах failure scenario.
- [ ] Избрах архитектурно/процесно решение с trade-offs.
- [ ] Реализирах guided increment и checkpoint.
- [ ] Самостоятелната задача е различна и покрива acceptance criteria.
- [ ] Tests покриват negative/edge и positive behavior.
- [ ] Evidence включва data/model/code/environment identity.
- [ ] Не включих реални secrets/PII или платени external dependencies.
- [ ] Описах limitations, technical debt и следваща стъпка.
