---
title: "Лабораторно упражнение 3 — Софтуерна архитектура и архитектурни стилове"
sidebar:
  order: 3
  label: "Упражнение 3"
---

# Лабораторно упражнение 3 — Софтуерна архитектура и архитектурни стилове

**Аудитория:** IV курс, бакалавър „Изкуствен интелект“. **Време:** 110 минути.
Работи се само с предоставения CPU проект и synthetic dataset, без платени услуги.

## 2. Инженерен сценарий

Монолитният Python script обучава модел при import и после стартира API. Един restart на serving води до ново обучение; промяна в dataset променя production поведението без release.

## 3. Учебни цели

След упражнението студентът:

- анализира runtime coupling;
- проектира layered и pipeline архитектура;
- разделя batch training и online inference чрез архитектурен contract;
- аргументира embedded/service trade-offs;
- тества import и artifact boundaries;
- измерва operational последствия от design решение;

## 4. Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. Използвайте резултатите от предходните 2 упражнения като engineering input. Не преговаряме елементарни Python конструкции.

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

**Фокус в това упражнение:** Data → offline training → immutable bundle → serving boundary. Вижте [общата архитектура](/courses/bg/software-engineering/materiali/architecture/system-overview/). Отбележете данните, зависимостите и owner на всяка граница.

## 7. Теоретична подготовка

Architecture описва отговорности, dependencies, deployment units и качествени компромиси. Layered architecture разделя API/application/domain/infrastructure; pipeline architecture описва последователни transformation stages. Client–server отделя потребител от услуга, а service-oriented design поставя network boundaries между capabilities. Microservices са operational избор, не синоним на модулност.

Coupling е зависимост между компоненти; cohesion — доколко една отговорност е събрана на едно място. Batch inference обработва набори извън интерактивен request, online inference има latency/availability contract. Embedded model има по-малка network сложност; separate inference service позволява независимо scaling/version lifecycle, но добавя timeout, serialization, auth и observability нужди. Model artifact е versioned boundary между training и serving.

Следвайте [източниците и version scope](/courses/bg/software-engineering/materiali/architecture/references/). Теорията трябва да обяснява engineering избора, не да замества evidence.

## 8. Лош / проблемен пример

```python
# module import:
data = load_data()
model = train(data)
app = FastAPI()
# всеки serving restart зависи от training data и training cost
```

Работещият starter и неговият TODO contract са в [starter/README.md](/courses/bg/software-engineering/materiali/lab03-architecture/starter/readme/). Примерът е за анализ: първо запишете observable behavior и failure risks, после refactor-вайте. Не броим просто преименуване на файлове за архитектурна промяна.
