---
title: "Упражнение 9 — Контролно 2 — Тестване, CI/CD и MLOps"
sidebar:
  order: 9
  label: "Упражнение 9"
---

# Упражнение 9 — Контролно 2 — Тестване, CI/CD и MLOps

## Обхват на контролното

Контролното обхваща материала до [упражнение 8](/courses/bg/software-engineering-ai/laboratorno-uprazhnenie-8/): тестове, CI проверки, оценъчни отчети, версии и rollback. На страницата „Задачи“ са предоставени учебни отчети и два кратки Python файла. Използвайте Python 3.11+ със стандартната библиотека. Работите върху проверката и избора на кандидат; отчетите са фиксирани тестови входове, а не доказателства за реално обучени модели. Обучение, Docker и външен CI прогон са извън обхвата.

Като AI инженер в Scrum екипа проверявате част от условията в DoD преди решение за активиране на кандидат. Успешният локален gate е доказателство за тази проверка; завършената продуктова история изисква и интеграционните проверки на екипа.

## Примерен проблем за подготовка

Промяната на разстоянията и регистъра скрива повторение между обучение и оценка. Ще реализираме минимална проверка за точно нормализирано съвпадение.

### Решение

Запишете `overlap_demo.py`:

```python
def normalize(text):
    return " ".join(text.lower().split())

def check_overlap(training, evaluation):
    common = {normalize(t) for t in training} & {normalize(t) for t in evaluation}
    if common:
        raise ValueError("Train/evaluation overlap")

check_overlap(["Fix login error"], ["Add task calendar"])
try:
    check_overlap(["Fix login error"], ["  FIX  login ERROR "])
except ValueError:
    print("PASS: leakage fixture rejected")
else:
    raise AssertionError("Overlap was accepted")
```

### Проверка на резултата

При `python overlap_demo.py` очаквайте `PASS: leakage fixture rejected`. Ако нормализацията се премахне, вторият случай не се открива. Това не открива семантични преформулировки; отделен преглед на данните остава необходим. Проверката се изпълнява преди fit и преди пакетиране, а не след избор на модел.
