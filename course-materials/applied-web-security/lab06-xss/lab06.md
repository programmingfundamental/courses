# Упражнение 6 — HTML изглед на задачите и XSS защита

## 1. Теория


### 1.1. Данни в браузъра

1. **XSS** е изпълнение на недоверено съдържание в origin на приложението. **Stored XSS** използва записан вход; **reflected XSS** отразява заявка; **DOM XSS** възниква при обработка от JavaScript.
   - summary/description се пазят в Task и се показват в /ui/tasks. Рискът е при HTML render, а не при JPA записването.
2. **Origin** е протокол, хост и порт; **sink** е място, което интерпретира вход; **DOM** е дървото на страницата.
   - innerHTML интерпретира тагове; textContent показва текст. Сървърното конкатениране на raw summary в <h2> също е HTML sink.
3. **Output encoding** зависи от контекста. **Sanitization** допуска ограничен HTML; тук полетата са обикновен текст.
   - HtmlUtils.htmlEscape превръща < в &lt; за HTML текст/quoted attribute. Пазим оригинала в DB, кодираме при извеждане.
4. **URL scheme** е частта преди :, **attribute breakout** затваря кавичката, **double encoding** кодира повторно вече кодиран текст.
   - href изисква и allowlist http/https, и кодиране на атрибут. HTML escaping не прави javascript: безопасен.
5. **CSP** задава разрешени източници/действия; **defense in depth** съчетава независими защити.
   - CSP без inline scripts ограничава изпълнението, но тестът пак проверява, че summary е encoded. **Marker** е видим индикатор за изпълнение, например заглавието на страницата.



### 1.2. Проверки и доказателства

1. **Security regression test** е автоматизиран тест на правило за сигурност, който открива повторна поява на проблем. **Assertion** сравнява очаквано и получено; **negative test** проверява отказ, **positive test** — разрешена операция.
   - Пример: GET /tasks без удостоверяване → 401, със съществуваща сесия → 200. Тестът за отказ не заменя теста за нормална работа.
2. **JUnit** изпълнява тестовете; **MockMvc** подава HTTP заявки през Spring; **H2** е базата в памет за бързи проверки. **Integration test** проверява взаимодействието на компоненти; същите тестове се изпълняват и с PostgreSQL.
   - В TaskManagerBaselineTest полето mvc е MockMvc: `mvc.perform(get("/tasks")).andExpect(status().isUnauthorized());`. Статичните imports са в готовия клас.
3. **Fixture** са началните данни на теста; **test matrix** е списък от входове и очаквания; **edge case** е граничен случай. **Regression** означава връщане на вече отстранен проблем.
   - Пример: собствена задача, чужда задача и липсващо ID се проверяват отделно; след отказана промяна записът в DB остава същият.
4. **Root cause** е първопричината, **mitigation** — защитата, **evidence** — доказателството. **Code diff** показва промяната, **test report** — резултата. **Baseline** е началната версия за сравнение.
   - Запазете заявката, очакването и отчета. Провалена компилация не е доказателство, че тестът е открил нарушение. Не представяйте непроверена хипотеза като установен дефект.

`mvn test` изпълнява тестовете с H2 и записва target/surefire-reports. `mvn -Ppostgres-tests test` използва отделната PostgreSQL тестова база, стартирана по setup.md. **Maven profile** е именуван набор от настройки. Новите класове с тестове завършват на Test. За браузърно поведение се използва и реален браузър; MockMvc не изпълнява JavaScript.


## 2. Подготовка

### Предварителни знания

Java, Spring Boot, HTTP, JPA и Task Manager от lab11. Продължете собственото решение от упражнение 5; запазете тестовете и данните с определени собственици.

### Начален проект и надграждане

След упражнение 5. Добавя се HTML изглед /ui/tasks към същото приложение. JSON REST отговор сам по себе си не изпълнява HTML; XSS се анализира при новия изходен контекст.

Използвайте [Task Manager](../task-manager/README.md) и [подготовката](../setup.md). Всички Maven/Compose команди се изпълняват от task-manager. Работните Java класове са в src/main/java/bg/tu_varna/sit/task_manager; тестовете — в съответния src/test/java package.

**Файлове за работа:** нов TaskPageController, TaskServiceImp, SecurityConfig; по избор Task.referenceUrl. [Архитектурната карта](../architecture/system-overview.md) показва кои маршрути съществуват в началото и кои се добавят последователно.

JDK 17+, Maven 3.9+ или Maven Wrapper, Docker Compose и браузър са достатъчни. Преди промяна изпълнете mvn test; след промяната повторете съответните тестове и PostgreSQL профила. Новите класове/маршрути, описани като надграждане, се реализират в това упражнение.


## 3. Примерен проблем

Добавете HTML страница /ui/tasks, която показва разрешените summary и description като текст.

### Стъпки за решаване


1. Създайте TaskPageController с GET /ui/tasks, produces=text/html, който използва TaskService.getAll от упражнение 3. Не правете repository.findAll в контролера.
2. Поставете summary/description в <h2>/<p> след HtmlUtils.htmlEscape. Не кодирайте стойностите при запис в DB.
3. SecurityConfig изисква удостоверяване за /ui/**. Добавете CSP default-src none; form-action self; frame-ancestors none; base-uri none.
4. Създайте Task със summary/description, съдържащи <b> и script marker; отворете /ui/tasks като owner и като друг user. Другият user не вижда задачата.
5. TaskHtmlTest проверява encoded HTML, липса на raw script и нормална кирилица. В браузър marker не се изпълнява. Не правете извод за JavaScript само от MockMvc.


## Самостоятелни задачи


### Задача 1 — Външна референция към задача

Добавете незадължително referenceUrl към Task, TaskRequestDto и TaskResponseDto, до 2048 символа. Празно поле премахва връзката; приемат се само абсолютни http/https URL без userinfo; относителни, javascript: и data: се отказват с 400. В /ui/tasks изведете quoted href с encoding. Запазете owner policy при промяна. Сървърът не изтегля URL.

### Задача 2 — Контексти и повторно кодиране

Проверете текст &lt;, кавички в URL, опасна scheme при правилно escaped attribute и CSP, която блокира script при липсващо encoding. Добавете поне два автоматизирани теста и една браузърна проверка. Невалиден URL не трябва да променя останалите полета на задачата.
