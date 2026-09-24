---
title: "Лабораторно упражнение 2 — Изисквания и спецификация на AI-базирани системи"
sidebar:
  order: 2
  label: "Упражнение 2"
---

# Лабораторно упражнение 2 — Изисквания и спецификация на AI-базирани системи

**Аудитория:** IV курс, бакалавър „Изкуствен интелект“. **Време:** 110 минути.
Работи се само с предоставения CPU проект и synthetic dataset, без платени услуги.

## 2. Инженерен сценарий

Възложителят казва: „Системата трябва да разпознава заявки добре и бързо.“ Разработчиците могат да докажат висок F1 на различен dataset или ниска latency при един request, но нито едно не определя приемането.

## 3. Учебни цели

След упражнението студентът:

- анализира двусмислени requirements;
- проектира functional и non-functional спецификация;
- измерва quality и latency с явен protocol;
- аргументира precision/recall trade-off;
- реализира traceability към acceptance tests;
- тества schema и data quality constraints;

## 4. Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. Използвайте резултатите от предходните 1 упражнения като engineering input. Не преговаряме елементарни Python конструкции.

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

**Фокус в това упражнение:** Stakeholder intent → SRS → measurable acceptance contracts. Вижте [общата архитектура](/courses/bg/software-engineering/materiali/architecture/system-overview/). Отбележете данните, зависимостите и owner на всяка граница.

## 7. Теоретична подготовка

Functional requirement описва действие/резултат; non-functional requirement задава качество или ограничение. AI-specific requirements включват model metrics върху versioned evaluation data, slice support и допустимо поведение при uncertainty. Accuracy е дял верни predictions; precision измерва надеждността на positive results, recall — намерените positives. F1 съчетава precision/recall, но не отчита самостоятелно различна бизнес цена на грешките.

SLO е цел за service indicator за определен прозорец; SLA е договор с последствия. p95 latency без hardware, workload, concurrency, batch size, warmup и sample count е непълно requirement. Availability трябва да определя denominator, maintenance windows и откази. Explainability/fairness се превръщат в проверими constraints само в ясно описан контекст; synthetic cohort metrics не доказват fairness за реални хора.

Следвайте [източниците и version scope](/courses/bg/software-engineering/materiali/architecture/references/). Теорията трябва да обяснява engineering избора, не да замества evidence.

## 8. Лош / проблемен пример

```json
{"requirement": "Моделът е точен и API е бърз", "test": "работи"}
```

Работещият starter и неговият TODO contract са в [starter/README.md](/courses/bg/software-engineering/materiali/lab02-requirements/starter/readme/). Примерът е за анализ: първо запишете observable behavior и failure risks, после refactor-вайте. Не броим просто преименуване на файлове за архитектурна промяна.
