# Насоки за преподавателя — контролно 2, 90 минути

## Текущ формат и подготовка

Предоставените модули се изпълняват с Python 3.11+ без външни зависимости. Проверете интерпретатора и копирането на двата начални файла преди отчитане на времето. Фиксираните отчети са fixtures за gate, не резултат от реално обучение. Контролното проверява тестове, блокиране на следваща стъпка и решение за версия. Не изисквайте Docker, ново обучение, UML, истинско пакетиране или изчакване на GitHub Actions.

## Времеви план — общо 90 минути

| Дейност | Минути |
| --- | ---: |
| Прочитане и начален прогон | 5 |
| Задача 1 — gate и тестове | 35 |
| Задача 2 — блокиращ pipeline | 20 |
| Задача 3 — избор и rollback | 20 |
| Проверка и предаване | 10 |

## Вътрешна рубрика — 100 точки

| Задача | Критерии | Точки |
| --- | --- | ---: |
| 1 | Целият договор на gate — 20; независими тестове — 15; действителен red → green резултат — 10 | 45 |
| 2 | Проверка на exit code и спиране — 15; успешен и отказан локален прогон с доказателства — 15 | 30 |
| 3 | Правилни състояния и основания — 15; разлика между метрика, пълен отчет и валиден артефакт — 5; ограничение — 5 | 25 |

Не приемайте print(REJECT) с exit code 0 за блокиране. Липсата на външен CI прогон не намалява оценката — той не е част от задачата. Изисквайте ясно означение на локалния модел. Готовият report е даден вход, не заслуга за обучение.

## Решение на задача 1

Заменете функцията `accept` в началния модул със следната; `LABELS` вече е дефиниран там:

```python
# solution: accept
def accept(report):
    if "macro_f1" not in report or not 0.70 <= report["macro_f1"] <= 1:
        return False
    classes = report.get("classes", {})
    for label in LABELS:
        metrics = classes.get(label, {})
        if "support" not in metrics or metrics["support"] <= 0:
            return False
        if "f1" not in metrics or not 0 <= metrics["f1"] <= 1:
            return False
    return True
```

В задачата числовите типове и структурата на наличните речници са договорени. Не изисквайте защита срещу всички възможни невалидни JSON типове. Липсващи задължителни полета обаче трябва да се отхвърлят.

```python
# file: test_assessment2.py
from copy import deepcopy
import unittest
from assessment2 import accept, REPORTS

class GateTests(unittest.TestCase):
    def test_valid(self):
        self.assertTrue(accept(deepcopy(REPORTS["v2"])))

    def test_low_macro(self):
        report = deepcopy(REPORTS["v2"])
        report["macro_f1"] = 0.69
        self.assertFalse(accept(report))

    def test_missing_class(self):
        self.assertFalse(accept(deepcopy(REPORTS["v3"])))

    def test_zero_support(self):
        report = deepcopy(REPORTS["v2"])
        report["classes"]["DOCUMENTATION"]["support"] = 0
        self.assertFalse(accept(report))

    def test_bad_f1(self):
        report = deepcopy(REPORTS["v2"])
        report["classes"]["BUG"]["f1"] = 1.1
        self.assertFalse(accept(report))

    def test_missing_fields(self):
        report = deepcopy(REPORTS["v2"])
        del report["macro_f1"]
        self.assertFalse(accept(report))
        report = deepcopy(REPORTS["v2"])
        del report["classes"]["BUG"]["f1"]
        self.assertFalse(accept(report))

    def test_boundary(self):
        report = deepcopy(REPORTS["v2"])
        report["macro_f1"] = 0.70
        self.assertTrue(accept(report))

if __name__ == "__main__":
    unittest.main()
```

## Решение на задача 2

```python
# file: pipeline2_solution.py
from pathlib import Path
import subprocess
import sys

gate = Path(__file__).with_name("assessment2.py")
result = subprocess.run([sys.executable, str(gate), sys.argv[1]])
if result.returncode != 0:
    raise SystemExit(result.returncode)
print("PACKAGE", flush=True)
```

Копирайте решението като `pipeline2.py`. След поправката: v2 → ACCEPT, PACKAGE, exit 0; v3 → REJECT, без PACKAGE, exit 1. Вариант със `check=True` също е допустим. Проверете спирането при отказан gate, не само при Python грешка в тестовете.

## Решение на задача 3

| Стъпка | Решение | Активна версия след стъпката |
| --- | --- | --- |
| Активиране на v2 | Допуска се: пълен допустим отчет и валиден артефакт | v2 |
| Активиране на v3 | Отказ: липсва DOCUMENTATION | v2 |
| Rollback с повреден v1 | Отказ: невалиден checksum | v2 |
| Rollback с оригинален проверен v1 | Допуска се | v1 |

Приемливи ограничения: gate не доказва липса на data leakage, представителност на оценъчната извадка или еднакво качество за всички езици. Не допускайте промяна на активната версия при неуспешна проверка. Няма твърдение за реален deployment.

<details>
<summary>Архивни насоки преди формата за 90 минути — не са текущата рубрика</summary>

### Предишна организация

## Организация

Преподавателят задава конкретен дефект или кандидат за модел и време за работа.

Общо: 100 точки.

## Вътрешна рубрика

### Задача 1. Тестове и дефект — 35 точки

Оценяване: подходящи тестове 15 т., реална корекция 10 т., доказателства 10 т.

### Задача 2. CI/CD — 30 точки

Оценяване: зависимости и отказ 15 т., изолация 10 т., възпроизводимост 5 т.

### Задача 3. Модел и rollback — 35 точки

Оценяване: реални метрики и ограничения 15 т., произход 10 т., доказан rollback 10 т.


## Актуален AI и Scrum фокус

Публичната страница съдържа конкретното студентско задание и отделен решен пример за подготовка. Запазената рубрика се прилага към анализа, собствения AI принос, съгласуваните договори и действителните доказателства. Не изисквайте възпроизвеждане на текста от примера като самостоятелно решение. Scrum протоколите описват състояние и адаптация; броят на проведените срещи не е доказателство за завършен прираст.

</details>
