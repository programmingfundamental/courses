# Упражнение 8 — CSRF защита на сесиите и формите в Task Manager

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



### 1.2. Ръчни проверки с Postman

1. **Postman** изпраща HTTP заявки към работещото приложение. Изберете метод, URL, headers и JSON body, натиснете Send и сравнете статуса и съдържанието с очакваното.
2. **Положителна проверка** доказва разрешена операция, а **отрицателна проверка** — отказ. След отказ проверете с нова GET заявка или в DB, че данните са непроменени.
3. **Матрица на проверките** описва потребител, начални данни, заявка, очакван и действителен резултат. Използвайте реални login сесии и ID от отговорите. При смяна на потребителя изчистете cookies и влезте отново.
4. **Доказателство** е записан резултат от изпълнена проверка: статус, обезличен отговор и състояние преди/след. Запазете заявките в Postman колекция без пароли, cookies и tokens. За HTML, JavaScript и cookie поведение използвайте и браузър.

До упражнение 10 изпълнявайте заявките поотделно с Send и попълвайте резултатите ръчно. Автоматизираните тестове се въвеждат в упражнение 11.

## 2. Подготовка

### Предварителни знания

Java, Spring Boot, HTTP, JPA и Task Manager от lab11. Продължете собственото решение от упражнение 7; запазете Postman заявките, протокола от ръчните проверки и данните с определени собственици.

### Начален проект и надграждане

След упражнение 7. Включва се CSRF за съществуващата сесийна верига и се добавя HTML форма. До упражнение 10 POST/PATCH/DELETE с Bearer също изискват CSRF в тази обща верига.

Използвайте [Task Manager](../task-manager/README.md) и [подготовката](../setup.md). Всички Maven/Compose команди се изпълняват от task-manager. Работните Java класове са в src/main/java/bg/tu_varna/sit/task_manager.

**Файлове за работа:** SecurityConfig, AuthController/AuthService, нов GET /auth/csrf, TaskPageController. [Архитектурната карта](../architecture/system-overview.md) показва кои маршрути съществуват в началото и кои се добавят последователно.

Използвайте Docker Compose, Postman и браузър; за локална компилация — JDK 17+ и Maven 3.9+ или Maven Wrapper. Преди промяна запишете поведението с Postman; след промяната изпълнете `docker compose up -d --build --wait` и повторете ръчните проверки. Новите класове/маршрути, описани като надграждане, се реализират в това упражнение.


## 3. Примерен проблем

Включете CSRF при съществуващия session login и докажете, че отказана промяна на Task не записва данни.

### Стъпки за решаване


1. Премахнете csrf.disable() от текущата SecurityConfig и използвайте HttpSessionCsrfTokenRepository. Добавете публичен GET /auth/csrf преди общите правила.
2. Преди POST /auth/login клиентът взема token с анонимна сесия. След authenticate извикайте стратегия за смяна на session ID и изчистване на CSRF token, после запазете SecurityContext.
3. Клиентът взема нов token след login и използва неговата headerName за JSON POST/PATCH/DELETE. В Postman копирайте token и headerName от отговора и подавайте съответния header при всяка променяща заявка със същата cookie сесия.
4. Като owner изпратете PATCH /tasks/{id}/update без token, с грешен token и с token от друга сесия: 403 и без DB промяна. Валидният token със същата сесия дава 200.
5. В Postman вземете реален GET /auth/csrf отговор, копирайте headerName/token и проверете GET /tasks без сесия=401 и logout с валиден token=200.


## Самостоятелни задачи


### Задача 1 — HTML форма за нова задача

Добавете GET /ui/tasks/new с полета summary, description, deadline и hidden CSRF. POST /ui/tasks приема form data и делегира към същия TaskService.create. Добавете setters към TaskRequestDto за @ModelAttribute binding или отделен form DTO със същата валидация. Не копирайте repository логика. Успех=201; липсващ/чужд token=403 без INSERT. Owner е текущият потребител.

### Задача 2 — Token и cookie граници

Проверете стар token след login, стар session cookie след logout, same-site/cross-origin form и Secure cookie през HTTPS. Изпълнете ръчни Postman заявки за token/session, а действителното изпращане на cookie проверете с браузър. Опишете защо 403 за POST без token не доказва правилна authentication policy.
