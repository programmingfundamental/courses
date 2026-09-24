---
title: "Лабораторно упражнение 6 — Тестване на софтуер и AI компоненти"
sidebar:
  order: 6
  label: "Упражнение 6"
---

# Лабораторно упражнение 6 — Тестване на софтуер и AI компоненти

**Аудитория:** IV курс, бакалавър „Изкуствен интелект“. **Време:** 110 минути.
Работи се само с предоставения CPU проект и synthetic dataset, без платени услуги.

## 2. Инженерен сценарий

API тестът проверява само дали отговорът е 200. Повреден artifact, missing feature и променен feature order остават незабелязани. В друг test качеството варира, защото dataset split се сменя при всяко изпълнение.

## 3. Учебни цели

След упражнението студентът:

- проектира test pyramid за AI pipeline;
- реализира unit/integration/contract tests;
- тества data/schema/artifact failure paths;
- измерва model quality с фиксиран protocol;
- анализира deterministic и statistical guarantees;
- аргументира ограниченията на drift smoke test;

## 4. Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. Използвайте резултатите от предходните 5 упражнения като engineering input. Не преговаряме елементарни Python конструкции.

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

**Фокус в това упражнение:** Data contract → preprocessing → artifact → service → HTTP. Вижте [общата архитектура](/courses/bg/software-engineering/materiali/architecture/system-overview/). Отбележете данните, зависимостите и owner на всяка граница.

## 7. Теоретична подготовка

Unit test изолира малка отговорност, integration test проверява collaborators/IO, end-to-end test преминава реалните deployment boundaries. Contract test фиксира observable interface. Mocking е полезен за failure injection, но не доказва истинска serialization/serving compatibility.

Детерминистичният contract може да има exact assertions: missing feature →422, missing model→503. Statistical quality test има versioned held-out dataset, threshold и support; фиксираният seed не прави качеството универсална гаранция. Test pyramid държи много бързи проверки и малко скъпи end-to-end runs. Data drift е промяна на входното разпределение; model/concept drift засяга връзката между входове и цел. Mean-shift smoke сигнал е евтин индикатор, не доказателство за спад на F1; нужни са labels и наблюдение във времето.

Следвайте [източниците и version scope](/courses/bg/software-engineering/materiali/architecture/references/). Теорията трябва да обяснява engineering избора, не да замества evidence.

## 8. Лош / проблемен пример

```python
def test_api():
    assert response.status_code == 200  # няма schema, version, range, failure checks
```

Работещият starter и неговият TODO contract са в [starter/README.md](/courses/bg/software-engineering/materiali/lab06-testing/starter/readme/). Примерът е за анализ: първо запишете observable behavior и failure risks, после refactor-вайте. Не броим просто преименуване на файлове за архитектурна промяна.
