---
title: "Упражнение 10 — Експлоатация на AI компонент — наблюдаемост и надеждност"
sidebar:
  order: 10
  label: "Упражнение 10"
---

# Упражнение 10 — Експлоатация на AI компонент — наблюдаемост и надеждност

## Теория

### Работа в Scrum: експлоатация и обратна връзка

Целта на прираста е Task Manager да остава използваем при отказ на категоризатора. AI инженерът различава проблем в качеството на предсказване от инфраструктурен отказ, добавя измерване и предлага проверима промяна в backlog. За Review демонстрира timeout и възстановяване; за Retrospective анализира дали сигналът е открит навреме и кой тест липсва.

**Data drift** е промяна в разпределението на входовете, например повече български текстове от очакваното. Това е сигнал за анализ, не автоматично доказателство за спад на качеството. **Concept drift** означава промяна във връзката вход–етикет. За действителна оценка са нужни проверени етикети и достатъчен брой примери. Отделяйте оперативните метрики, като timeout, от моделните, като recall; потребителското потвърждение не е автоматично надежден обучаващ етикет.

### 1. Наблюдаемост

1. **Логове.** Записват конкретни събития с време, ниво, request ID и причина. Не трябва да съдържат пароли, сесии или пълни текстове на задачите.
   - **Пример:** `event=category_unavailable reason=timeout modelVersion=v2 requestId=...` позволява проследяване без description.
2. **Метрики.** Обобщават поведение: брой заявки, грешки, fallback и време за отговор. Етикети като taskId създават неограничен брой серии и не са добър избор.
3. **Проследяване на заявка.** Един request ID свързва Spring и Python логовете. Приема се само ограничен и валидиран външен ID или се създава нов.
4. **Liveness и readiness.** Liveness показва дали процесът е жив; readiness — дали може да обслужва договорените заявки. Зареден несъвместим модел не означава готова AI услуга.

### 2. Времеви бюджети и отказ

**Timeout** ограничава чакането. **Retry** повтаря операция при определени откази, но може да увеличи натоварването и да удвои странични ефекти. **Circuit breaker** временно спира опитите към системно отказваща зависимост. **Fallback** е договорено резервно поведение, а не скрит успешен AI резултат.

За учебния договор общият бюджет е 1500 ms. Той включва свързване, четене, сериализация и локална обработка; не задаваме два отделни timeout по 1500 ms и не твърдим общо 1500 ms. Записваме допустимото измервателно отклонение и проверяваме действителното време.

**Пример:** при отказ връщаме category=null, source=UNAVAILABLE, modelVersion=null. Създаването и ръчното категоризиране на задача работят независимо. Не връщаме случайна категория като MODEL.

### 3. Надеждност и измерване

SLO е цел за измерим показател през определен период. p95 описва опашката на разпределението, а не най-лошия случай. Кратък локален тест не доказва месечна достъпност.

При сравнение записваме хардуер, версии, размер на входа, warmup, брой заявки и concurrency. Примерен протокол: 10 загряващи и 100 измервани заявки при concurrency=1, после отделен прогон при 5. Отчитаме успешни отговори и fallback отделно.


### Прилагане в Task Manager


След публикуване на v2 AI услугата понякога отговаря за 8 секунди. Task Manager трябва да остане използваем, а екипът да различи забавяне от невалиден отговор или проблем с модела.

#### Стъпка 1. Измерване на изходното поведение

Продължете версията от [тема 8](/courses/bg/software-engineering-ai/laboratorno-uprazhnenie-8/). Използвайте локален контролиран HTTP заместител, който забавя отговора, връща 503 или невалиден JSON. Измерете поведението на адаптера от тема 6. Тези заместители проверяват надеждността, а не точността на модела.

#### Стъпка 2. Логове и метрики

Добавете request ID и структурирани събития за success, timeout, invalid_response и unavailable. Записвайте версия само когато е действително известна. Добавете броячи за опити и fallback и измерител за продължителност. Ограничете достъпа до диагностичните пътища; не публикувайте настройки и secrets.

#### Стъпка 3. Управление на отказа

Настройте connect/read timeout в общия бюджет и без автоматичен retry по подразбиране. В отделен вариант проверете един ограничен retry само за операцията за предложение; тя е без запис. Повторно изпращане на създаване на задача изисква отделен идемпотентен договор и не се добавя автоматично.

#### Стъпка 4. Проверка на основните операции

Спрете само category услугата в учебния Compose проект. Създайте задача, променете статуса и потвърдете категория ръчно. Всички операции трябва да запазят договора си. Заявката за предложение трябва да върне UNAVAILABLE в измерения бюджет и да остави confirmedCategory непроменена.

#### Стъпка 5. Възстановяване

Стартирайте category услугата и проверете readiness и действителната версия. Потвърдете, че предложенията отново работят без рестарт на цялата система. Актуализирайте Sequence и Timing моделите от упражнение 3 с реалните настройки; отделете изискваното от измереното време.


## Примерен проблем

AI услугата връща бавен или повреден отговор. Ще проверим границата чрез локален HTTP заместител и ще върнем UNAVAILABLE без измислена категория или версия.

### Решение

Запишете `timeout_demo.py`:

```python
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from threading import Thread
from urllib.request import Request, urlopen
from urllib.error import URLError
import json
import socket
import time

UNAVAILABLE = {"category": None, "source": "UNAVAILABLE", "modelVersion": None}

class Stub(BaseHTTPRequestHandler):
    def do_POST(self):
        if self.path == "/slow":
            time.sleep(0.2)
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.end_headers()
        body = b'{"category":"BUG","modelVersion":"fixture-v1"}'
        try:
            self.wfile.write(b"invalid json" if self.path == "/invalid" else body)
        except (BrokenPipeError, ConnectionResetError):
            pass
    def log_message(self, *args):
        pass

def suggest(url):
    request = Request(url, data=json.dumps({"summary": "Fix login validation",
                      "description": "Show a clear error for invalid input"}).encode(),
                      headers={"Content-Type": "application/json"})
    started = time.monotonic()
    try:
        with urlopen(request, timeout=0.05) as response:
            value = json.load(response)
        if value["category"] not in {"BUG", "FEATURE", "DOCUMENTATION", "OTHER"}:
            raise ValueError("Unknown category")
        if not isinstance(value["modelVersion"], str) or not value["modelVersion"].strip():
            raise ValueError("Missing version")
        result = {**value, "source": "MODEL"}
    except (URLError, socket.timeout, ValueError, KeyError, TypeError):
        result = dict(UNAVAILABLE)
    return result, (time.monotonic() - started) * 1000

server = ThreadingHTTPServer(("127.0.0.1", 0), Stub)
Thread(target=server.serve_forever, kwargs={"poll_interval": 0.01}, daemon=True).start()
try:
    base = "http://127.0.0.1:" + str(server.server_port)
    for path in ("/ok", "/slow", "/invalid"):
        result, milliseconds = suggest(base + path)
        expected = "MODEL" if path == "/ok" else "UNAVAILABLE"
        assert result["source"] == expected, result
        print(path, result["source"], round(milliseconds, 1), "ms")
finally:
    server.shutdown()
    server.server_close()
```

Примерът проверява транспортния и договорния отказ, не качеството на модела. `urlopen(timeout=...)` ограничава блокиращи операции, не гарантира общ краен срок за произволен сървър, който подава отговора на части. За NFR-03 в Task Manager измерете целия request и използвайте клиент с общ deadline, ограничен размер на отговора и контрол на повторенията. Краткото локално измерване не доказва бюджет 1500 ms при реално натоварване.

### Проверка на резултата

Изпълнете `python timeout_demo.py`. Очаквайте `/ok MODEL`, `/slow UNAVAILABLE` и `/invalid UNAVAILABLE` с действително измерени времена. Портът се избира автоматично. При силно натоварена среда дори `/ok` може да надхвърли краткия тестов timeout; отчетете провала и измерете причината.
