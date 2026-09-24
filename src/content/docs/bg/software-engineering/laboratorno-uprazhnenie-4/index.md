---
title: "Лабораторно упражнение 4 — Модулност, слоеве и разделяне на отговорностите"
sidebar:
  order: 4
  label: "Упражнение 4"
---

# Лабораторно упражнение 4 — Модулност, слоеве и разделяне на отговорностите

**Аудитория:** IV курс, бакалавър „Изкуствен интелект“. **Време:** 110 минути.
Работи се само с предоставения CPU проект и synthetic dataset, без платени услуги.

## 2. Инженерен сценарий

Една функция чете CSV, избира модел, обучава го и връща business result. За да тествате един prediction branch, трябва да имате filesystem, dataset и training runtime. Промяната на storage се разпространява до API.

## 3. Учебни цели

След упражнението студентът:

- анализира separation of concerns;
- refactor-ва tightly coupled компонент;
- проектира dependency direction и Protocol;
- реализира configuration извън domain logic;
- тества модули с малки fakes;
- аргументира границата domain/infrastructure;

## 4. Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. Използвайте резултатите от предходните 3 упражнения като engineering input. Не преговаряме елементарни Python конструкции.

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

**Фокус в това упражнение:** api → inference port → repository adapter; data/features отделно. Вижте [общата архитектура](/courses/bg/software-engineering/materiali/architecture/system-overview/). Отбележете данните, зависимостите и owner на всяка граница.

## 7. Теоретична подготовка

Модулността намалява броя причини за промяна на компонент и позволява независими tests. Domain logic описва бизнес правила; infrastructure се занимава с filesystem, network, serialization и framework lifecycle. Dependency inversion означава високото ниво да зависи от contract, а adapter да реализира този contract.

Python Protocol описва structural typing: подходящ object удовлетворява интерфейса без общ base class. Dependency injection подава collaborator отвън; не изисква DI framework. Configuration е validated input на приложението, не глобален набор от hardcoded paths. Добрата граница следва отговорност и change rate, а не произволно разпределяне на функции в много файлове. Модулите data/features/training/inference/api/config имат различни allowed dependencies.

Следвайте [източниците и version scope](/courses/bg/software-engineering/materiali/architecture/references/). Теорията трябва да обяснява engineering избора, не да замества evidence.

## 8. Лош / проблемен пример

```python
def predict_one():
    data = pd.read_csv(HARDCODED_PATH)
    model = DummyClassifier().fit(data[FEATURES], data.label)
    return model.predict(data[FEATURES].head(1))
```

Работещият starter и неговият TODO contract са в [starter/README.md](/courses/bg/software-engineering/materiali/lab04-modularity/starter/readme/). Примерът е за анализ: първо запишете observable behavior и failure risks, после refactor-вайте. Не броим просто преименуване на файлове за архитектурна промяна.
