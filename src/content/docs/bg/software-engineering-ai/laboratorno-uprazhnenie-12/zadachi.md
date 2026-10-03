---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## Самостоятелни задачи

### Начални условия и договор

**Казус:** предложение за категория изпраща текст преди проверката на достъпа, променя потвърдената категория и не обработва отказ на AI услугата. Поправете предоставената функция `suggest`. Потребителят е вече удостоверено име или `None`; задачата винаги съществува, входните полета и `request_id` са валидни. Не е необходимо да реализирате вход или съхранение на данни.

| Случай | Очакван резултат от `suggest` |
| --- | --- |
| `user is None` | `(401, None)`, нула AI извиквания и без ново AI събитие |
| Чужд потребител | `(404, None)`, нула AI извиквания и без ново AI събитие |
| Собственик, успешен predictor | `(200, отговорът на predictor)`, едно извикване и едно събитие с `reason="ok"` |
| Собственик, `TimeoutError` | `(200, UNAVAILABLE)`, едно извикване и едно събитие с `reason="timeout"` |
| Собственик, `ConnectionError` | `(200, UNAVAILABLE)`, едно извикване и едно събитие с `reason="connection"` |

`UNAVAILABLE` е `{"category": None, "source": "UNAVAILABLE", "modelVersion": None}`. Всеки сценарий запазва всички полета на задачата. Всяко ново събитие съдържа точно `requestId`, `source` и `reason`; `source` съвпада с върнатия отговор. Не записвайте текст на задача или текст на exception в събитията. Успешният predictor връща валиден отговор; непознати exceptions не трябва да се прикриват като договорен отказ.

Запишете началния код в `assessment3.py`. Той съдържа умишлени дефекти:

```python
TASK = {
    "owner": "Alice", "summary": "Fix login error",
    "description": "Show a clear message", "confirmedCategory": "DOCUMENTATION",
}

def make_predictor(mode, calls):
    def predict(summary, description):
        calls.append((summary, description))
        if mode == "timeout":
            raise TimeoutError("controlled timeout")
        if mode == "connection":
            raise ConnectionError("controlled connection failure")
        return {"category": "BUG", "source": "MODEL", "modelVersion": "v1"}
    return predict

def suggest(task, user, predict, request_id, events):
    response = predict(task["summary"], task["description"])
    if user is None:
        return 401, None
    if task["owner"] != user:
        return 404, None
    task["confirmedCategory"] = response["category"]
    events.append({"requestId": request_id, "text": task["description"]})
    return 200, response

if __name__ == "__main__":
    calls, events = [], []
    result = suggest(dict(TASK), "Bob", make_predictor("ok", calls), "req-1", events)
    print(result, "calls=", len(calls))
```

Първоначално `python assessment3.py` връща `(404, None)`, но показва `calls=1`. Това е дефект: отказаният потребител вече е предизвикал изпращане на текста към категоризатора. `make_predictor` е тестов заместител; exceptions симулират отказ, без реално мрежово изчакване.

### Задача 1. Достъп преди AI извикване

Поправете реда на операциите и премахнете промяната на потвърдената категория. В `test_assessment3.py` добавете `unittest` проверки за анонимен посетител, Bob и Alice с успешен predictor. Проверявайте статуса, броя извиквания и равенството на цялата задача преди и след операцията. Покажете поне един провал на началната реализация и успешен резултат след поправката.

### Задача 2. Отказ и безопасни събития

Допълнете `suggest` за двата договорени отказа и формата на събитията. Добавете тестове за Alice при timeout и connection failure, както и проверка на събитието при успех. За всеки отказ проверете целия отговор, едното извикване, непроменената задача и точното съдържание на събитието. За отказан достъп проверете липсата на ново AI събитие. Изпълнете `python -m unittest -v test_assessment3.py`.

### Задача 3. Поддръжка и действие от ретро

В `answer.md` посочете причината за един от дефектите и съответния регресионен тест. Формулирайте един backlog запис с принос на AI инженера и проверим критерий за приемане. Запишете по една бележка за „Започни“, „Спри“ и „Продължи“ въз основа на казуса и изберете едно действие за следващия спринт с отговорник и проверка на ефекта при следващото ретро. Накрая посочете една необходима проверка в реалната HTTP система, която този локален модел не доказва.

Предайте `assessment3.py`, `test_assessment3.py` и `answer.md` с командите и действителните резултати. Допълнителни UML диаграми, миграции и цялостно преработване на Task Manager не се изискват.
