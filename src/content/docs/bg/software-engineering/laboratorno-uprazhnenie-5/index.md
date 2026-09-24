---
title: "Лабораторно упражнение 5 — Design Patterns и принципи за качествен код"
sidebar:
  order: 5
  label: "Упражнение 5"
---

# Лабораторно упражнение 5 — Design Patterns и принципи за качествен код

**Аудитория:** IV курс, бакалавър „Изкуствен интелект“. **Време:** 110 минути.
Работи се само с предоставения CPU проект и synthetic dataset, без платени услуги.

## 2. Инженерен сценарий

При всеки нов модел екипът редактира if/elif блокове в training, evaluation и serving. Част от branches използват различна preprocessing логика. Новият вариант работи в notebook, но чупи общия contract.

## 3. Учебни цели

След упражнението студентът:

- анализира code smells и duplication;
- refactor-ва selection логика със Strategy/Factory;
- проектира стабилен estimator contract;
- реализира extension без промяна на orchestration;
- тества behavior вместо implementation details;
- аргументира SOLID спрямо KISS/YAGNI;

## 4. Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. Използвайте резултатите от предходните 4 упражнения като engineering input. Не преговаряме елементарни Python конструкции.

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

**Фокус в това упражнение:** Training orchestration → estimator Strategy/Factory → sklearn Pipeline. Вижте [общата архитектура](/courses/bg/software-engineering/materiali/architecture/system-overview/). Отбележете данните, зависимостите и owner на всяка граница.

## 7. Теоретична подготовка

SOLID е набор от design heuristics: single responsibility, open/closed, substitutability, interface segregation и dependency inversion. DRY цели един източник на знание, не забранява всяка повторена линия. KISS пази простотата, YAGNI отлага speculative features.

Strategy капсулира взаимозаменяемо поведение; Factory избира/създава implementation; Adapter уеднаквява чужд interface; Repository отделя storage; Pipeline организира последователни transformations. Pattern е полезен, ако създава stable extension point с реална нужда. За три малки варианта registry от callables е достатъчен; hierarchy от абстрактни класове може да увеличи complexity без стойност. Единният sklearn estimator contract позволява orchestration да не знае дали моделът е linear/tree/forest.

Следвайте [източниците и version scope](/courses/bg/software-engineering/materiali/architecture/references/). Теорията трябва да обяснява engineering избора, не да замества evidence.

## 8. Лош / проблемен пример

```python
if model_type == "linear":
    model = LogisticRegression()
elif model_type == "tree":
    model = DecisionTreeClassifier()
elif model_type == "forest":
    model = RandomForestClassifier()
```

Работещият starter и неговият TODO contract са в [starter/README.md](/courses/bg/software-engineering/materiali/lab05-design-patterns/starter/readme/). Примерът е за анализ: първо запишете observable behavior и failure risks, после refactor-вайте. Не броим просто преименуване на файлове за архитектурна промяна.
