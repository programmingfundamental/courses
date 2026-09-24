---
title: "Лабораторно упражнение 8 — MLOps и управление на модели и данни"
sidebar:
  order: 8
  label: "Упражнение 8"
---

# Лабораторно упражнение 8 — MLOps и управление на модели и данни

**Аудитория:** IV курс, бакалавър „Изкуствен интелект“. **Време:** 110 минути.
Работи се само с предоставения CPU проект и synthetic dataset, без платени услуги.

## 2. Инженерен сценарий

В директория има model-final.joblib и model-final2.joblib. Вторият има по-висока accuracy, но е обучен върху различен split; dataset origin липсва. Екипът не може да избере release или да възстанови предишното поведение.

## 3. Учебни цели

След упражнението студентът:

- анализира model/data lineage;
- реализира experiment tracking с пълни metadata;
- автоматизира data/model version checks;
- сравнява release кандидати по инженерни критерии;
- тества immutable versions и rollback;
- аргументира champion/challenger promotion;

## 4. Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. Използвайте резултатите от предходните 7 упражнения като engineering input. Не преговаряме елементарни Python конструкции.

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

**Фокус в това упражнение:** Dataset manifest → experiment journal → immutable registry → promotion/rollback. Вижте [общата архитектура](/courses/bg/software-engineering/materiali/architecture/system-overview/). Отбележете данните, зависимостите и owner на всяка граница.

## 7. Теоретична подготовка

MLOps свързва ML lifecycle с автоматизация и operations. Experiment tracking пази parameters, metrics, artifacts и контекста на run; model registry управлява version identities и promotion state. Lineage проследява model → dataset → source/config/dependencies → evaluation. Име на файл и timestamp не са достатъчни за възпроизводимост.

В курса local immutable experiment journal е еквивалент на основните tracking функции: всеки version folder съдържа model и metadata с params/metrics/data hash/git commit/source hash/lock hash. Git+CSV manifest е малък data-versioning workflow, еквивалентен за този dataset на DVC pointer/content workflow. Не е разпределен MLflow/DVC service. Champion е избраната версия; challenger е кандидатът. Promotion сменя registry pointer, deployment сменя running immutable bundle. Rollback трябва да запази стария artifact и runtime compatibility; само alias update не променя вече работещия процес.

Следвайте [източниците и version scope](/courses/bg/software-engineering/materiali/architecture/references/). Теорията трябва да обяснява engineering избора, не да замества evidence.

## 8. Лош / проблемен пример

```text
models/model-final.joblib
models/model-final-new.joblib
# няма dataset identity, split, params, metrics или source version
```

Работещият starter и неговият TODO contract са в [starter/README.md](/courses/bg/software-engineering/materiali/lab08-mlops/starter/readme/). Примерът е за анализ: първо запишете observable behavior и failure risks, после refactor-вайте. Не броим просто преименуване на файлове за архитектурна промяна.
