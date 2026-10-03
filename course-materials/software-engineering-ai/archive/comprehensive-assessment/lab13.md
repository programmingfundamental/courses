# Обобщаващо контролно — AI инженерът в Scrum екипа

## Обхват на контролното

Проследете пълния жизнен цикъл на ограничена промяна: анализ, FR/NFR, Sprint Backlog, UML, реализация, проверки, внедряване и поддръжка. Използвайте достигнатата версия на Task Manager и покажете собствения принос като AI инженер и договорите с останалия екип.

## Примерен проблем за подготовка

Потребителят иска да отвори отново задача и да поиска ново предложение, без да губи потвърдената категория. Отказът на AI не трябва да отменя отварянето. Следващият изпълним модел показва разделянето на двете действия.

### Решение

Запишете `reopen_demo.py`:

```python
def reopen_and_suggest(task, user, predict):
    if user is None:
        return 401, None
    if task is None or task["owner"] != user:
        return 404, None
    if task["status"] != "DONE":
        return 409, None
    task["status"] = "OPEN"
    try:
        proposal = predict(task["summary"], task["description"])
    except TimeoutError:
        proposal = {"category": None, "source": "UNAVAILABLE", "modelVersion": None}
    return 200, proposal

calls = []
def unavailable(summary, description):
    calls.append((summary, description))
    raise TimeoutError()

task = {"owner": "Alice", "status": "DONE", "confirmedCategory": "BUG",
        "summary": "Fix login validation", "description": "Show a clear error"}
assert reopen_and_suggest(task, "Bob", unavailable)[0] == 404
assert task["status"] == "DONE" and not calls
status, proposal = reopen_and_suggest(task, "Alice", unavailable)
assert status == 200 and proposal["source"] == "UNAVAILABLE"
assert task["status"] == "OPEN" and task["confirmedCategory"] == "BUG"
assert len(calls) == 1
print("PASS: reopen independent of AI failure")
```

### Проверка на резултата

При `python reopen_demo.py` очаквайте `PASS: reopen independent of AI failure`. В реалната система запишете статуса в отделна завършена транзакция, преди да поискате предложение; не задържайте DB транзакция през мрежовото чакане. Потвърждаването на нова категория остава отделна операция. Този модел проверява правилата, а не заменя API, миграционните и CI проверките.

## Самостоятелни задачи

### Задача 1. Анализ, изисквания и проект

Промяна: за задачи с общ текст под 40 знака след премахване на начални/крайни интервали категоризаторът трябва да се въздържа. Договорете точното броене на `summary + " " + description`. За такъв вход върнете `category=OTHER, source=RULES, modelVersion=length-policy-v1` без извикване на модела; останалите заявки запазват съществуващия договор. Потвърдената категория не се променя. Формулирайте FR/NFR, Sprint Goal, backlog с зависимости и поне две UML диаграми. Предайте анализ на въздействието върху данни, метрики, API и наблюдение; OTHER остава въздържане, не нов обучен клас.

### Задача 2. Реализация, оценка и внедряване

Реализирайте политиката с проверки за входове под, точно на и над границата, собственик/чужд достъп и недостъпен модел. Измерете дела на въздържане и качеството върху същата фиксирана извадка, без да изключвате трудните примери от основния отчет. Предайте код, тестове, действителен CI статус, идентичност на модела и план за връщане на предишната политика.

### Задача 3. Review, експлоатация и поддръжка

Демонстрирайте използваемия прираст, непроменена потвърдена категория и работещо ръчно потвърждение при отказ. Обяснете по доказателства дали промяната е полезна и какъв следващ backlog запис произтича от резултата. Предайте протокол за Review, едно действие за Retrospective, model card ограничения и проследимост изискване → диаграма → код → тест → резултат. Разграничете завършена и непроверена работа по DoD.
