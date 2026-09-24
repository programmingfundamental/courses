---
title: "Лабораторно упражнение 9 — Наблюдаемост, надеждност и управление на грешки"
sidebar:
  order: 9
  label: "Упражнение 9"
---

# Лабораторно упражнение 9 — Наблюдаемост, надеждност и управление на грешки

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

Python 3.12, virtual environment, Jupyter Notebook по избор за notebook UI, pytest/coverage, Git, Docker, FastAPI/Pydantic и стандартните Python logging/JSON инструменти. Използвайте pinned environment от [README](/courses/bg/software-engineering/podgotovka/). Training е върху 400 synthetic rows на CPU. Tracking/data versioning са local journal + Git/SHA manifest; не е необходим cloud account.

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

**Фокус в това упражнение:** Request → structured event + metrics → readiness → diagnostic decision. Вижте [общата архитектура](/courses/bg/software-engineering/materiali/architecture/system-overview/). Отбележете данните, зависимостите и owner на всяка граница.

## 7. Теоретична подготовка

Observability извежда вътрешното състояние от logs, metrics и traces. Logs са събития с контекст; metrics са агрегирани измервания; distributed tracing свързва spans на една операция през services. Prometheus/OpenTelemetry са стандартни ecosystems; този малък app предоставя JSON metrics, не се представя като Prometheus exporter.

Liveness показва, че process работи; readiness — че може да обслужва смислено. Request count/error count/latency и model version са базовите сигнали. Избягвайте unbounded labels като user ID/token. Retry е подходящ само за transient failure и ограничен budget; може да увеличи overload. Timeout без cancellation не спира непременно CPU работа. Graceful degradation трябва да е explicit, например 503 вместо fabricated prediction. Data drift не означава автоматично model drift; quality monitoring изисква delayed labels и evaluation protocol.

Следвайте [източниците и version scope](/courses/bg/software-engineering/materiali/architecture/references/). Теорията трябва да обяснява engineering избора, не да замества evidence.

## 8. Лош / проблемен пример

```python
print("request:", body)  # privacy risk, без version/correlation
except Exception:
    return {"label": 0}   # скрива failure като нормална prediction
```

Работещият starter и неговият TODO contract са в [starter/README.md](/courses/bg/software-engineering/materiali/lab09-observability/starter/readme/). Примерът е за анализ: първо запишете observable behavior и failure risks, после refactor-вайте. Не броим просто преименуване на файлове за архитектурна промяна.
