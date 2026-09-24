---
title: "Лабораторно упражнение 1 — Софтуерен жизнен цикъл и инженерни процеси"
sidebar:
  order: 1
  label: "Упражнение 1"
---

# Лабораторно упражнение 1 — Софтуерен жизнен цикъл и инженерни процеси

**Аудитория:** IV курс, бакалавър „Изкуствен интелект“. **Време:** 110 минути.
Работи се само с предоставения CPU проект и synthetic dataset, без платени услуги.

## 2. Инженерен сценарий

Експериментът има висок training score, но само авторът може да го стартира. Dataset path е в клетка, няма tests и никой не знае дали последният модел отговаря на последната версия на кода. Екипът трябва да организира прехода към поддържана система.

## 3. Учебни цели

След упражнението студентът:

- анализира разликата research code/production software;
- идентифицира technical debt и рискови assumptions;
- проектира iterative lifecycle за AI проекта;
- аргументира milestones и приоритети;
- реализира проверим definition of done;
- автоматизира clean-run проверка на експеримента;

## 4. Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. ML алгоритмите са даденост; не се изисква избор или tuning на нов classifier. Не преговаряме елементарни Python конструкции.

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

**Фокус в това упражнение:** Notebook → engineering backlog → reproducible pipeline. Вижте [общата архитектура](/courses/bg/software-engineering/materiali/architecture/system-overview/). Отбележете данните, зависимостите и owner на всяка граница.

## 7. Теоретична подготовка

SDLC (software development lifecycle) свързва requirements, design, implementation, verification, deployment, operation и retirement. AI lifecycle добавя data acquisition/validation, experimentation, model evaluation и наблюдение на статистическо качество. Итеративното развитие означава малки проверими increments; Agile не премахва документацията и acceptance criteria.

Research code оптимизира скоростта на проверка на хипотеза. Production code има users, contracts, versioned artifacts, error behavior и operational owner. Reproducibility изисква data identity, code/config/dependency identity, seed, split и инструкции; seed сам по себе си не е достатъчен. Technical debt е бъдеща цена от днешно решение; записвайте consequence, trigger, owner и repayment test. Definition of done (DoD) е обща проверка за завършен increment, а acceptance criteria са конкретни за отделното requirement.

Следвайте [източниците и version scope](/courses/bg/software-engineering/materiali/architecture/references/). Теорията трябва да обяснява engineering избора, не да замества evidence.

## 8. Лош / проблемен пример

```python
data = pd.read_csv('../../ai-platform/data/requests-v1.csv')
x = StandardScaler().fit_transform(data[FEATURES])
model.fit(x, data.label)
print(model.score(x, data.label))  # training score != acceptance quality
```

Работещият starter и неговият TODO contract са в [starter/README.md](/courses/bg/software-engineering/materiali/lab01-lifecycle-processes/starter/readme/). Примерът е за анализ: първо запишете observable behavior и failure risks, после refactor-вайте. Не броим просто преименуване на файлове за архитектурна промяна.
