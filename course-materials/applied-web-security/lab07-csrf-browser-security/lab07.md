# Упражнение 7 — CSRF защита на сесиите и формите в Task Manager

## 1. Теория


### 1.1. Браузърни credentials и намерение на потребителя

1. **CSRF** използва автоматично изпратени credentials за нежелана операция. Session cookie се изпраща от браузъра; наличието на JWT в приложението не премахва сесийния достъп.
   - AuthService.login създава HttpSession, а SecurityConfig е IF_REQUIRED и csrf.disable(). Следователно анализираме реално съществуващ сесиен път.
2. **CSRF token** е непредсказуема стойност, свързана със сесията; сървърът я сравнява преди промяна.
   - GET /auth/csrf връща token/headerName/parameterName. JSON клиентът изпраща заглавката; HTML формата — hidden parameter.
3. **Origin** включва схема, хост и порт; **site** не се различава само по порт. **SOP** ограничава четенето, **CORS** разрешава избрани script origins.
   - HTML form може да изпрати cross-origin POST, дори да няма право да прочете отговора. CORS не доказва намерението на user.
4. **HttpOnly** ограничава JavaScript достъп до cookie; **Secure** изисква защитен транспорт; **SameSite** ограничава cross-site изпращане.
   - Различни localhost портове са cross-origin, но same-site. Cookie flags са отделни проверки от token.
5. **Session fixation protection** сменя session ID; **token rotation** сменя CSRF token след удостоверяване.
   - При custom AuthService.login изрично извикваме SessionAuthenticationStrategy, вместо да предполагаме, че стандартният login filter го прави.



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

Java, Spring Boot, HTTP, JPA и Task Manager от lab11. Продължете собственото решение от упражнение 6; запазете тестовете и данните с определени собственици.

### Начален проект и надграждане

След упражнение 6. Включва се CSRF за съществуващата сесийна верига и се добавя HTML форма. До упражнение 9 POST/PATCH/DELETE с Bearer също изискват CSRF в тази обща верига.

Използвайте [Task Manager](../task-manager/README.md) и [подготовката](../setup.md). Всички Maven/Compose команди се изпълняват от task-manager. Работните Java класове са в src/main/java/bg/tu_varna/sit/task_manager; тестовете — в съответния src/test/java package.

**Файлове за работа:** SecurityConfig, AuthController/AuthService, нов GET /auth/csrf, TaskPageController. [Архитектурната карта](../architecture/system-overview.md) показва кои маршрути съществуват в началото и кои се добавят последователно.

JDK 17+, Maven 3.9+ или Maven Wrapper, Docker Compose и браузър са достатъчни. Преди промяна изпълнете mvn test; след промяната повторете съответните тестове и PostgreSQL профила. Новите класове/маршрути, описани като надграждане, се реализират в това упражнение.


## 3. Примерен проблем

Включете CSRF при съществуващия session login и докажете, че отказана промяна на Task не записва данни.

### Стъпки за решаване


1. Премахнете csrf.disable() от текущата SecurityConfig и използвайте HttpSessionCsrfTokenRepository. Добавете публичен GET /auth/csrf преди общите правила.
2. Преди POST /auth/login клиентът взема token с анонимна сесия. След authenticate извикайте стратегия за смяна на session ID и изчистване на CSRF token, после запазете SecurityContext.
3. Клиентът взема нов token след login и използва неговата headerName за JSON POST/PATCH/DELETE. Актуализирайте setup клиентските стъпки; всички тестови mutations вече използват with(csrf()).
4. Като owner изпратете PATCH /tasks/{id}/update без token, с грешен token и с token от друга сесия: 403 и без DB промяна. Валидният token със същата сесия дава 200.
5. Добавете CsrfSessionTest с поне един реален GET /auth/csrf response, не само csrf() helper. Проверете GET /tasks без сесия=401 и logout с token=200.


## Самостоятелни задачи


### Задача 1 — HTML форма за нова задача

Добавете GET /ui/tasks/new с полета summary, description, deadline и hidden CSRF. POST /ui/tasks приема form data и делегира към същия TaskService.create. Добавете setters към TaskRequestDto за @ModelAttribute binding или отделен form DTO със същата валидация. Не копирайте repository логика. Успех=201; липсващ/чужд token=403 без INSERT. Owner е текущият потребител.

### Задача 2 — Token и cookie граници

Проверете стар token след login, стар session cookie след logout, same-site/cross-origin form и Secure cookie през HTTPS. Добавете автоматизирана проверка за token/session, а действителното изпращане на cookie проверете с браузър. Опишете защо 403 за POST без token не доказва правилна authentication policy.
