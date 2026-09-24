---
title: "Лабораторно упражнение 7 — Version Control, CI/CD и автоматизация"
sidebar:
  order: 7
  label: "Упражнение 7"
---

# Лабораторно упражнение 7 — Version Control, CI/CD и автоматизация

**Аудитория:** IV курс, бакалавър „Изкуствен интелект“. **Време:** 110 минути.
Работи се само с предоставения CPU проект и synthetic dataset, без платени услуги.

## 2. Инженерен сценарий

На лаптопа build-ът минава, но CI инсталира различни dependencies и моделът не се зарежда. Deployment използва tag latest и няма начин да се докаже кой artifact е бил serving преди регресията.

## 3. Учебни цели

След упражнението студентът:

- проектира Git/review workflow;
- автоматизира lint/tests/build quality gates;
- реализира reproducible dependency installation;
- тества immutable release и deployment readiness;
- аргументира CI спрямо CD и manual gates;
- измерва regression impact преди promotion;

## 4. Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. Използвайте резултатите от предходните 6 упражнения като engineering input. Не преговаряме елементарни Python конструкции.

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

**Фокус в това упражнение:** Commit → lint → tests → model gate → immutable image → local deployment. Вижте [общата архитектура](/courses/bg/software-engineering/materiali/architecture/system-overview/). Отбележете данните, зависимостите и owner на всяка граница.

## 7. Теоретична подготовка

CI интегрира малки промени с автоматични проверки; continuous delivery подготвя release с контролирана promotion/deployment стъпка, а continuous deployment публикува автоматично след gates. Pull request review оценява design, tests и риск; branch е изолирана линия работа, не заместител на review.

Semantic versioning следва compatibility на публичния API, докато model version идентифицира конкретен trained artifact; те не са едно и също. Lock file pin-ва resolved dependencies; reproducibility допълнително зависи от Python/OS и base image. Immutable release съдържа конкретен model bundle и image ID/digest. Tag latest е подвижен pointer. Coverage е quality signal, не доказателство за correctness. Deployment readiness gate проверява, че service може да обслужва request, не само че process съществува.

Следвайте [източниците и version scope](/courses/bg/software-engineering/materiali/architecture/references/). Теорията трябва да обяснява engineering избора, не да замества evidence.

## 8. Лош / проблемен пример

```yaml
- run: pip install scikit-learn fastapi
- run: echo 'tests passed'
# unpinned dependencies, без tests, image/version или failure gate
```

Работещият starter и неговият TODO contract са в [starter/README.md](/courses/bg/software-engineering/materiali/lab07-cicd/starter/readme/). Примерът е за анализ: първо запишете observable behavior и failure risks, после refactor-вайте. Не броим просто преименуване на файлове за архитектурна промяна.
