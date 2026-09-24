---
title: "Лабораторно упражнение 10 — Сигурност, етика, технически дълг и поддръжка"
sidebar:
  order: 10
  label: "Упражнение 10"
---

# Лабораторно упражнение 10 — Сигурност, етика, технически дълг и поддръжка

**Аудитория:** IV курс, бакалавър „Изкуствен интелект“. **Време:** 110 минути.
Работи се само с предоставения CPU проект и synthetic dataset, без платени услуги.

## 2. Инженерен сценарий

Review открива secret в source, стар model artifact без version, недокументиран dataset, sensitive logs и deprecated dependency. Високият F1 не отговаря дали системата е безопасна, поддържаема или подходяща за употреба.

## 3. Учебни цели

След упражнението студентът:

- анализира trust boundaries и privacy risks;
- проектира risk/technical-debt register;
- реализира проверими security/ethics constraints;
- тества access control и log redaction;
- аргументира fairness/explainability ограничения;
- планира maintenance, deprecation и retirement;

## 4. Предварителни знания

Python, основи на ML/Jupyter, Git, REST API, Docker, scikit-learn/pandas/numpy, Linux и бази данни. Използвайте резултатите от предходните 9 упражнения като engineering input. Не преговаряме елементарни Python конструкции.

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

**Фокус в това упражнение:** API/model/data trust boundaries → risk/debt register → maintenance/retirement. Вижте [общата архитектура](/courses/bg/software-engineering/materiali/architecture/system-overview/). Отбележете данните, зависимостите и owner на всяка граница.

## 7. Теоретична подготовка

Secure coding включва input validation, server-side access control, bounded requests, trusted artifacts и secret handling. Pickle/joblib loading може да изпълни код; checksum не прави непознат artifact безопасен. API key е учебен access mechanism, не пълна identity/authorization система. Sensitive inputs не се логват; retention и deletion трябва да имат policy и owner.

Responsible AI изисква intended use, limitations, data provenance, fairness/explainability reasoning и human oversight според риска. Synthetic cohort A/B показва mechanics на slice analysis, не сертифицира fairness. Technical debt включва code/dependency debt, model debt (неясен lifecycle/validation) и data debt (липсващ provenance/quality ownership). Deprecation има срок, migration path и измерване на usage; retirement включва отказ на стар model, запазване/изтриване на artifacts според policy и комуникация с users.

Следвайте [източниците и version scope](/courses/bg/software-engineering/materiali/architecture/references/). Теорията трябва да обяснява engineering избора, не да замества evidence.

## 8. Лош / проблемен пример

```text
API_KEY = "SYNTHETIC-NOT-A-REAL-SECRET"
model-final-final.joblib; version = null
log: {"email": "student@example.invalid", "input": "..."}
# non-executable review fixture; няма реални credentials
```

Работещият starter и неговият TODO contract са в [starter/README.md](/courses/bg/software-engineering/materiali/lab10-security-ethics-maintenance/starter/readme/). Примерът е за анализ: първо запишете observable behavior и failure risks, после refactor-вайте. Не броим просто преименуване на файлове за архитектурна промяна.
