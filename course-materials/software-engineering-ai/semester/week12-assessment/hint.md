# Насоки за преподавателя — контролно 3, 90 минути

## Текущ формат и подготовка

Проверете Python 3.11+ и предоставения начален модул преди началото. Използва се само стандартната библиотека. Умишлените дефекти са локални: AI извикване преди достъп, промяна на потвърдената категория, липсващ fallback и небезопасно събитие. Участникът поправя една функция и добавя тестове. Контролираният predictor не доказва реален HTTP timeout, удостоверяване, база данни или работеща ръчна операция в цялото приложение; тези ограничения се посочват в отговора.

## Времеви план — общо 90 минути

| Дейност | Минути |
| --- | ---: |
| Прочитане и начален прогон | 5 |
| Задача 1 — достъп и неизменени данни | 25 |
| Задача 2 — откази, събития и тестове | 35 |
| Задача 3 — поддръжка и ретро действие | 15 |
| Проверка и предаване | 10 |

## Вътрешна рубрика — 100 точки

| Задача | Критерии | Точки |
| --- | --- | ---: |
| 1 | Проверки преди AI — 15; непроменена задача — 10; тестове за трите участника и red → green — 10 | 35 |
| 2 | Двата договорени отказа — 15; точно и безопасно събитие — 15; доказателства от тестовете — 15 | 45 |
| 3 | Причина и регресионен тест — 5; backlog с критерий — 5; три ретро бележки и действие — 5; ограничение на модела — 5 | 20 |

Не приемайте общо `except Exception`, което прикрива програмен дефект. HTTP 404 след вече извършено AI извикване не покрива контрола на достъпа. Събитията се проверяват като точни речници, не само за наличие на requestId. Тестовите списъци `calls` са инструмент за проверка, а не производствени логове.

## Решение на задачи 1 и 2

Заменете само `suggest`; входните task, request_id и успешен AI отговор са валидни по условие:

```python
# solution: suggest
def suggest(task, user, predict, request_id, events):
    if user is None:
        return 401, None
    if task["owner"] != user:
        return 404, None
    try:
        response = predict(task["summary"], task["description"])
        reason = "ok"
    except TimeoutError:
        response = {"category": None, "source": "UNAVAILABLE", "modelVersion": None}
        reason = "timeout"
    except ConnectionError:
        response = {"category": None, "source": "UNAVAILABLE", "modelVersion": None}
        reason = "connection"
    events.append({"requestId": request_id, "source": response["source"], "reason": reason})
    return 200, response
```

```python
# file: test_assessment3.py
from copy import deepcopy
import unittest
from assessment3 import TASK, make_predictor, suggest

class SuggestionTests(unittest.TestCase):
    def test_denied_before_predictor(self):
        for user, status in [(None, 401), ("Bob", 404)]:
            for mode in ("ok", "timeout", "connection"):
                with self.subTest(user=user, mode=mode):
                    task, calls, events = deepcopy(TASK), [], []
                    result = suggest(task, user, make_predictor(mode, calls), "req-1", events)
                    self.assertEqual(result, (status, None))
                    self.assertEqual(calls, [])
                    self.assertEqual(events, [])
                    self.assertEqual(task, TASK)

    def test_owner_success_and_failures(self):
        for mode in ("ok", "timeout", "connection"):
            with self.subTest(mode=mode):
                task, calls, events = deepcopy(TASK), [], []
                result = suggest(task, "Alice", make_predictor(mode, calls), "req-2", events)
                expected = ({"category": "BUG", "source": "MODEL", "modelVersion": "v1"}
                            if mode == "ok" else
                            {"category": None, "source": "UNAVAILABLE", "modelVersion": None})
                self.assertEqual(result, (200, expected))
                self.assertEqual(len(calls), 1)
                self.assertEqual(task, TASK)
                self.assertEqual(events, [{"requestId": "req-2", "source": expected["source"], "reason": mode}])

    def test_unexpected_error_is_not_hidden(self):
        def broken(summary, description):
            raise RuntimeError("programming defect")
        task, events = deepcopy(TASK), []
        with self.assertRaises(RuntimeError):
            suggest(task, "Alice", broken, "req-3", events)
        self.assertEqual(task, TASK)
        self.assertEqual(events, [])

if __name__ == "__main__":
    unittest.main()
```

## Примерно решение на задача 3

Причина: достъпът е проверен след работа с поверения текст. Регресионен тест: Bob с predictor, който би хвърлил timeout, получава 404 с нула извиквания. Backlog: „Като AI инженер искам проверката за собственост да предхожда изпращането на текст, за да предотвратя обработване на чужди задачи“; критерий: Bob и анонимен посетител не задействат predictor и не променят задачата.

Ретро: започни проверка на страничните ефекти при отказ; спри да приемаш HTTP статуса за достатъчно доказателство; продължи използването на контролирани откази. Действие: AI и backend разработчикът добавят тест с брояч към прегледа на всеки нов път за AI извикване; AI инженерът координира до първата такава промяна в следващия спринт. На следващото ретро се проверяват промените и тестовете; липса на промяна означава липса на случай за оценка на ефекта. Реален HTTP timeout и крайният времеви бюджет остават за отделна интеграционна проверка.

<details>
<summary>Архивни насоки преди формата за 90 минути — не са текущата рубрика</summary>

### Предишна организация

## Организация

Не се въвежда нов материал.

Преподавателят задава вариант с отказ, проблем с достъпа или заявка за промяна.

Общо: 100 точки.

## Вътрешна рубрика

### Задача 1. Отказ и наблюдаемост — 35 точки

Оценяване: поведение 15 т., автоматична проверка 10 т., диагностични доказателства 10 т.

### Задача 2. Достъп и данни — 35 точки

Оценяване: коректна проверка 15 т., тестова матрица 15 т., миграция 5 т.

### Задача 3. Поддръжка и етика — 30 точки

Оценяване: съгласувана документация 10 т., обоснован дълг 10 т., етика и ограничения 10 т.


## Актуален AI и Scrum фокус

Публичната страница съдържа конкретното студентско задание и отделен решен пример за подготовка. Запазената рубрика се прилага към анализа, собствения AI принос, съгласуваните договори и действителните доказателства. Не изисквайте възпроизвеждане на текста от примера като самостоятелно решение. Scrum протоколите описват състояние и адаптация; броят на проведените срещи не е доказателство за завършен прираст.

</details>
