# Упражнение 6 — Тестване на софтуер, данни и AI модели

## Теория

### Работа в Scrum: проверка на данните и модела

Целта на следващия прираст е потребителят да получава предложения от измерен модел зад съществуващия договор. AI инженерът добавя в Sprint Backlog проверка на данните, обучение, сравнение с baseline и интеграционни проверки. При Daily Scrum съобщава конкретен риск, например липсващ оценъчен клас, и договаря действие. За Review представя работещ сценарий и действителни метрики; за Retrospective избира как да предотврати повторно изтичане на данни. ML-01 е условие за приемане на обучената версия; ако не е изпълнено, запазвате предишния допустим прираст и записвате нужната работа.

**Data leakage** е използване на информация от оценката при обучение или избор на признаци. **Training-serving skew** е разлика между обработката при обучение и предсказване. Един сериализиран pipeline за текста и модела намалява втория риск. Висок резултат върху почти повторени задачи е слаб аргумент за обобщаване към нови потребители.

### 1. Нива и цели на тестовете

1. **Unit тест.** Проверява малка отговорност без външна среда.
   - **Пример:** DONE → IN_PROGRESS е забранен преход; стратегията по правила дава еднакъв резултат за един и същ текст.
2. **Integration тест.** Проверява действителна граница между компоненти.
   - **Пример:** записване и прочитане на confirmedCategory през JPA; HTTP адаптерът разчита реален JSON от локален тестов сървър.
3. **E2E тест.** Проверява завършен сценарий през публичния вход.
   - **Пример:** вход → създаване → предложение → потвърждение → прочитане. Само успешен unit тест не доказва този поток.
4. **Fixture, изолация и регресия.** Fixture задава началното състояние; изолацията предотвратява зависимост между тестове; регресионният тест пази вече договорено поведение.

### 2. Проверки на AI компоненти

1. **Договор и качество.** Валиден JSON доказва формата, но не правилността на категорията. Тестов заместител е подходящ за отказ на HTTP услуга, но не може да замени реална оценка на модела.
2. **Обучаваща и оценъчна извадка.** Моделът и преобразуването на текста се обучават само върху обучаващите данни. Оценъчните примери се пазят отделно; дублирани или почти еднакви задачи в двете части водят до изтичане на информация.
3. **Precision, recall и F1.** Precision отчита верните положителни сред предсказаните; recall — сред действителните. F1 е хармоничното им средно. Macro-F1 дава равна тежест на класовете, а support показва броя примери.
   - **Пример:** добър общ резултат може да прикрива нулев recall за DOCUMENTATION. В отчета включваме всички договорени класове.
4. **Mutation и red → green.** Умишлено нарушаваме правило, наблюдаваме провал, възстановяваме кода и получаваме успех. Така проверяваме дали тестът открива реален дефект.

### 3. Първи обучен модел

Категоризаторът по правила от упражнение 4 е функционална основа. Сега добавяме малък обучен текстов модел зад същия договор. Подгответе синтетични `data/train.csv` и `data/evaluation.csv` с колони `id,summary,description,category`. Използвайте поне 15 обучаващи и 5 оценъчни примера за всеки от BUG, FEATURE и DOCUMENTATION. Това е учебен минимум, не доказателство за реално качество.

Създайте папка `ai-service` до Java проекта. Нужни са Python 3.11+, виртуална среда и пакетите pandas, scikit-learn, joblib и Flask. Запазете разрешените версии в `requirements.lock.txt` чрез `python -m pip freeze`; друг екип инсталира точно тях с `python -m pip install -r requirements.lock.txt`. Не включвайте виртуалната среда в Git.

Примерно ядро на `ai-service/train.py`, изпълнявано от `ai-service`:

```python
from pathlib import Path
import json
import joblib
import pandas as pd
from sklearn.pipeline import make_pipeline
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report

labels = ["BUG", "FEATURE", "DOCUMENTATION"]
train = pd.read_csv("data/train.csv")
evaluation = pd.read_csv("data/evaluation.csv")
# Преди fit добавете проверките на данните от стъпка 2.
model = make_pipeline(TfidfVectorizer(), LogisticRegression(max_iter=1000, random_state=42))
model.fit(train.summary + " " + train.description, train.category)
predicted = model.predict(evaluation.summary + " " + evaluation.description)
report = classification_report(evaluation.category, predicted, labels=labels,
                               output_dict=True, zero_division=0)
Path("artifacts").mkdir(exist_ok=True)
joblib.dump({"model": model, "version": "v1"}, "artifacts/model.joblib")
Path("reports").mkdir(exist_ok=True)
Path("reports/evaluation.json").write_text(json.dumps(report, indent=2), encoding="utf-8")
```

Това е минимален пример, който се допълва с валидация и тестове. Строгото публикуване на неизменяеми версии се добавя в тема 8. Зареждайте само артефакти, създадени от собствения доверен pipeline; joblib не е безопасен формат за произволни външни файлове.


### Прилагане в Task Manager


След рефакториране предложението неволно започва да записва категорията. Едновременно с това новият модел връща валиден JSON, но греши за DOCUMENTATION. Трябва да разграничим дефект в сценария от дефект в качеството.

#### Стъпка 1. Подготовка на Java тестовете

Продължете версията от [тема 4](../week04-design/lab04.md). Началният архив няма тестови зависимости. Добавете `spring-boot-starter-test`, `spring-security-test` и H2 с scope=test; версиите се управляват от Spring Boot parent. Създайте `src/test/resources/application-test.properties` с отделна H2 база и `spring.jpa.hibernate.ddl-auto=create-drop`; активирайте профил test. За PostgreSQL специфично поведение използвайте отделна тестова PostgreSQL база, никога работната.

Unit тестовете не стартират целия Spring контекст. При integration тест задайте тестов JWT secret с необходимата за приложението дължина; не копирайте работния `.env`. Изпълнете `./mvnw.cmd test` в PowerShell или `./mvnw test` в Bash от Java проекта.

#### Стъпка 2. Данни и модел

Проверете липсващи текстове, уникални ID, допустими категории, присъствие на трите класа във всяка извадка и липса на еднакви нормализирани текстове между извадките. Обучете модела с примера и запазете отчета, реалните версии и ограниченията. Ако ML-01 не е изпълнено, отчетете провала; не подменяйте метриката и не обучавайте върху оценъчната извадка.

#### Стъпка 3. Сервиране и адаптер

Реализирайте `ai-service/app.py` с Flask: зарежда артефакта еднократно при старт, предоставя `/health` и `POST /suggest` по договора от тема 3, валидира полетата и връща действителната версия. Във функцията за HTTP обработка няма `fit`. При липсващ/повреден артефакт readiness е неуспешна и предсказване не се допуска.

За първа локална проверка стартирайте Python услугата на `127.0.0.1:8000` и Java приложението локално с URL от конфигурацията. Ако Java е в Docker Desktop, адресът до услуга на хоста е `host.docker.internal`, а не localhost на контейнера; съгласувайте bind адреса и достъпа само за учебната среда. В тема 7 двете услуги ще бъдат в Compose мрежа. Реализирайте HttpCategorySuggester, проверявайте категорията и непразната версия и преобразувайте транспортен/договорен отказ в UNAVAILABLE. Задайте краен timeout още при първата реализация; в тема 10 го измерваме и обосноваваме.

#### Стъпка 4. Два независими вида доказателства

Java тест създава задача, извиква предложението и проверява, че confirmedCategory остава null. Python проверка оценява действителния модел върху evaluation.csv. Отчетите имат различни цели; успешният HTTP тест не прикрива нисък F1.

#### Стъпка 5. Контролиран дефект

В работен клон временно добавете записване на предложената категория. Съответният тест трябва да се провали. Възстановете правилното поведение, изпълнете пак и запазете red → green резултатите. Актуализирайте матрицата FR → UML сценарий → тест.


## Примерен проблем

Валиден отговор не доказва полезен модел. Ще обучим малък текстов pipeline, ще го сравним с правилата върху отделни примери и ще запазим реален отчет. Корпусът е нарочно малък, за да видим целия изпълним процес.

### Решение

Запишете `ai-service/example_train.py`:

```python
from pathlib import Path
import json
import joblib
from sklearn.pipeline import make_pipeline
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report

LABELS = ["BUG", "FEATURE", "DOCUMENTATION"]
train = [
    ("fix login error", "BUG"), ("repair crash on save", "BUG"),
    ("resolve invalid token error", "BUG"),
    ("add calendar view", "FEATURE"), ("implement task export", "FEATURE"),
    ("add search filter", "FEATURE"),
    ("update readme guide", "DOCUMENTATION"), ("document API usage", "DOCUMENTATION"),
    ("write installation guide", "DOCUMENTATION"),
]
evaluation = [
    ("login crash", "BUG"), ("token error", "BUG"),
    ("calendar filter", "FEATURE"), ("export tasks", "FEATURE"),
    ("API guide", "DOCUMENTATION"), ("installation readme", "DOCUMENTATION"),
]

def validate(training, held_out):
    for rows in (training, held_out):
        if {label for _, label in rows} != set(LABELS):
            raise ValueError("Missing or unknown class")
        if any(not text.strip() for text, _ in rows):
            raise ValueError("Empty text")
    normalize = lambda text: " ".join(text.lower().split())
    if {normalize(t) for t, _ in training} & {normalize(t) for t, _ in held_out}:
        raise ValueError("Train/evaluation overlap")

def rules(text):
    text = text.lower()
    return next((label for word, label in [("error", "BUG"), ("add", "FEATURE"),
                                          ("readme", "DOCUMENTATION")] if word in text), "OTHER")

validate(train, evaluation)
model = make_pipeline(TfidfVectorizer(), LogisticRegression(max_iter=1000, random_state=42))
model.fit([t for t, _ in train], [label for _, label in train])
texts, expected = zip(*evaluation)
report = classification_report(expected, model.predict(texts), labels=LABELS,
                               output_dict=True, zero_division=0)
baseline = classification_report(expected, [rules(t) for t in texts], labels=LABELS,
                                 output_dict=True, zero_division=0)
Path("reports").mkdir(exist_ok=True)
Path("artifacts").mkdir(exist_ok=True)
Path("reports/evaluation.json").write_text(json.dumps(report, indent=2), encoding="utf-8")
joblib.dump({"model": model, "version": "example-v1"}, "artifacts/example-model.joblib")
print("model macro-F1:", round(report["macro avg"]["f1-score"], 3))
print("rules macro-F1:", round(baseline["macro avg"]["f1-score"], 3))
try:
    validate(train, [row for row in evaluation if row[1] != "DOCUMENTATION"])
except ValueError as error:
    print("PASS: invalid evaluation rejected:", error)
else:
    raise AssertionError("Missing class was accepted")
```

TfidfVectorizer и LogisticRegression се обучават в един pipeline само върху train. Проверката открива точни нормализирани повторения; семантични дубликати изискват допълнителен преглед. Малкият корпус е демонстрация, не достатъчна оценка за ML-01: в задачите използвайте собствени данни с договорения минимум и сравнете по същия протокол. OTHER от baseline е въздържане и се брои като пропуск за действителния клас. Справка за отчета: [scikit-learn classification metrics](https://scikit-learn.org/stable/modules/model_evaluation.html#classification-report).

### Проверка на резултата

В активирана Python 3.11+ среда инсталирайте `python -m pip install scikit-learn joblib`, запишете версиите с `python -m pip freeze > requirements.lock.txt` и от `ai-service` изпълнете `python example_train.py`. Получавате два изчислени macro-F1 резултата, `reports/evaluation.json`, артефакт и `PASS: invalid evaluation rejected: Missing or unknown class`. В отчета support трябва да е 2 за всеки клас. Не заменяйте резултатите с предварително записани числа.


## Самостоятелни задачи

### Задача 1. Данни и оценъчен протокол

Подгответе собствени синтетични `train.csv` и `evaluation.csv` с `id,summary,description,category`: поне 15 обучаващи и 5 оценъчни примера за всеки клас. Не копирайте миникорпуса от решението. Проверете полета, ID, класове и дубликати; опишете как избягвате близки преформулировки между извадките. Предайте данни, правила за етикетиране и изпълнима валидация с положителен и отрицателен тест.

### Задача 2. Модел и регресионни проверки

Обучете TF-IDF + LogisticRegression или обоснована алтернатива, оценете по ML-01 и сравнете с baseline върху същата извадка. Предайте macro-F1, precision/recall/F1 и support по клас, реален артефакт и lock файл. Реализирайте поне шест автоматични проверки: дефектни данни, липсващ оценъчен клас, непозната категория, липсваща версия, незареден модел и предложение без запис. Един тест трябва действително да отказва при неизпълнен праг.

### Задача 3. Сервиране и Scrum доказателства

Реализирайте `/suggest` и `/health` и свържете адаптера по упражнение 3. Проверете, че не се извиква fit по време на request и че отказът оставя потвърдената категория непроменена. Покажете red → green за конкретен дефект, обновете FR → UML → тест и приложете `.mmd`/SVG. Предайте работещ сценарий за Review, ограниченията на извадката и следващ backlog запис, основан на действителна грешка на модела.
