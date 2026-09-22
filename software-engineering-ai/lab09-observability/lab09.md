# 1. Упражнение 9 — Наблюдаемост, надеждност и управление на грешки

**Аудитория:** IV курс, бакалавър „Изкуствен интелект“. **Време:** 110 минути.
Работи се само с предоставения CPU проект и synthetic dataset, без платени услуги.

## 2. Инженерен сценарий

След deployment p95 latency е пет пъти по-висока. Екипът вижда само „server started“ в console. Не е ясно дали причината е model compute, batch size, опашка, dependency или промяна в входните данни.

## 3. Учебни цели

След упражнението студентът:

- проектира observability plan;
- реализира structured logging и bounded metrics;
- измерва latency с explicit workload;
- тества readiness/liveness и error handling;
- аргументира timeout/retry/degradation policy;
- анализира data/model drift без прибързани изводи;

## 4. Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. Използвайте резултатите от предходните 8 упражнения като engineering input. Не преговаряме елементарни Python конструкции.

## 5. Инструменти

Python 3.12, virtual environment, Jupyter Notebook по избор за notebook UI, pytest/coverage, Git, Docker, FastAPI/Pydantic и стандартните Python logging/JSON инструменти. Използвайте pinned environment от [README](../README.md). Training е върху 400 synthetic rows на CPU. Tracking/data versioning са local journal + Git/SHA manifest; не е необходим cloud account.

## 6. Архитектурен контекст

```text
Dataset + manifest
       |
Validation / Features
       |
Offline Training Pipeline
       |
Experiment metadata + immutable Model Registry
       |
Inference Service -> FastAPI -> Client
       |
Logs / Metrics -> CI/CD and maintenance feedback
```

**Фокус в това упражнение:** Request → structured event + metrics → readiness → diagnostic decision. Вижте [общата архитектура](../architecture/system-overview.md). Отбележете данните, зависимостите и owner на всяка граница.

## 7. Теоретична подготовка

Observability извежда вътрешното състояние от logs, metrics и traces. Logs са събития с контекст; metrics са агрегирани измервания; distributed tracing свързва spans на една операция през services. Prometheus/OpenTelemetry са стандартни ecosystems; този малък app предоставя JSON metrics, не се представя като Prometheus exporter.

Liveness показва, че process работи; readiness — че може да обслужва смислено. Request count/error count/latency и model version са базовите сигнали. Избягвайте unbounded labels като user ID/token. Retry е подходящ само за transient failure и ограничен budget; може да увеличи overload. Timeout без cancellation не спира непременно CPU работа. Graceful degradation трябва да е explicit, например 503 вместо fabricated prediction. Data drift не означава автоматично model drift; quality monitoring изисква delayed labels и evaluation protocol.

Следвайте [източниците и version scope](../architecture/references.md). Теорията трябва да обяснява engineering избора, не да замества evidence.

## 8. Лош / проблемен пример

```python
print("request:", body)  # privacy risk, без version/correlation
except Exception:
    return {"label": 0}   # скрива failure като нормална prediction
```

Работещият starter и неговият TODO contract са в [starter/README.md](starter/README.md). Примерът е за анализ: първо запишете observable behavior и failure risks, после refactor-вайте. Не броим просто преименуване на файлове за архитектурна промяна.

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
