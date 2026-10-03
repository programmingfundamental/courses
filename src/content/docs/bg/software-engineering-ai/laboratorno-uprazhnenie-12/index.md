---
title: "Упражнение 12 — Контролно 3 — Надеждност, сигурност и поддръжка"
sidebar:
  order: 12
  label: "Упражнение 12"
---

# Упражнение 12 — Контролно 3 — Надеждност, сигурност и поддръжка

## Обхват на контролното

Контролното обхваща материала до [упражнение 11](/courses/bg/software-engineering-ai/laboratorno-uprazhnenie-11/): контрол на достъпа, отказ на AI компонента, наблюдаемост и поддръжка. На страницата „Задачи“ е предоставен кратък Python модел на операцията за предложение. Използвайте Python 3.11+ със стандартната библиотека. Работите върху една функция с контролирани зависимости; стартиране на HTTP услуга, миграция на база данни и измерване на реален мрежов timeout са извън обхвата.

## Примерен проблем за подготовка

При отказ AI адаптерът хвърля грешка, която може да провали целия потребителски сценарий. Ще проверим, че договорено UNAVAILABLE запазва потвърдената категория.

### Решение

Запишете `fallback_demo.py`:

```python
def suggest(task, predict):
    try:
        return predict(task["summary"], task["description"])
    except TimeoutError:
        return {"category": None, "source": "UNAVAILABLE", "modelVersion": None}

def delayed(summary, description):
    raise TimeoutError("controlled failure")

task = {"summary": "Fix login validation", "description": "Show a clear error",
        "confirmedCategory": "DOCUMENTATION"}
before = dict(task)
assert suggest(task, delayed)["source"] == "UNAVAILABLE"
assert task == before
print("PASS: fallback preserves confirmed category")
```

### Проверка на резултата

При `python fallback_demo.py` очаквайте `PASS: fallback preserves confirmed category`. Това е проверка на резервното поведение с контролиран отказ; не измерва мрежов timeout. За общия времеви бюджет използвайте HTTP протокола от упражнение 10, а за чужд достъп — реда на проверките от упражнение 11.
