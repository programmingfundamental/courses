# Упражнение 1 — Среда и моделиране на заплахи в Task Manager

## 1. Теория


### 1.1. Архитектура, заплахи и граници на доверие

1. **Asset** е защитаван ресурс: парола, задача, отчет или наличност. **Threat** е нежелано събитие, **vulnerability** — слабост, **actor** — участник, който може да я използва.
   - Пример: клиент опитва да зададе role=ADMIN при регистрация; активът са административните права.
2. **DFD** е схема на потоците. **Trust boundary** е граница, през която данните преминават между различни нива на доверие; **entry point** е входна точка.
   - Task Manager: клиент → Spring Security → AuthController/TaskController → service → repository → PostgreSQL. Няма Nginx в началната версия. Входът POST /auth/register пресича клиент/сървър и приложение/DB.
3. **STRIDE** групира подмяна на самоличност, промяна на данни, отричане на действие, изтичане на информация, отказ на услуга и повишаване на привилегии.
   - Оценяваме **likelihood** (вероятност) и **impact** (въздействие) от 1 до 3; risk=likelihood×impact. **Residual risk** остава след защитата.
4. Docker **ports** публикува порт на хоста; вътрешната DNS услуга db е достъпна за приложението и без публикуван DB порт. **Loopback** 127.0.0.1 ограничава достъпа до хоста; **wildcard** приема през всички съответни интерфейси.
   - `9000:9000` и `5432:5432` в оригиналния lab11 публикуват двата компонента. Копието за курса публикува само 127.0.0.1:9000; db остава във вътрешната мрежа.
5. **Authentication** установява потребителя; **authorization** проверява права; **allowlist** изброява разрешеното. **DTO** описва входни/изходни полета; **repository** извършва достъпа до DB.
   - AuthService задава Role.USER на сървъра. BCrypt съхранява еднопосочен хеш на паролата; следващото упражнение разглежда проверката му.



### 1.2. Проверки и доказателства

1. **Security regression test** е автоматизиран тест на правило за сигурност, който открива повторна поява на проблем. **Assertion** сравнява очаквано и получено; **negative test** проверява отказ, **positive test** — разрешена операция.
   - Пример: GET /tasks без удостоверяване → 401, със съществуваща сесия → 200. Тестът за отказ не заменя теста за нормална работа.
2. **JUnit** изпълнява тестовете; **MockMvc** подава HTTP заявки през Spring; **H2** е базата в памет за бързи проверки. **Integration test** проверява взаимодействието на компоненти; същите тестове се изпълняват и с PostgreSQL.
   - В TaskManagerBaselineTest полето mvc е MockMvc: `mvc.perform(get("/tasks")).andExpect(status().isUnauthorized());`. Статичните imports са в готовия клас.
3. **Fixture** са началните данни на теста; **test matrix** е списък от входове и очаквания; **edge case** е граничен случай. **Regression** означава връщане на вече отстранен проблем.
   - Пример: собствена задача, чужда задача и липсващо ID се проверяват отделно; след отказана промяна записът в DB остава същият.
4. **Root cause** е първопричината, **mitigation** — защитата, **evidence** — доказателството. **Code diff** показва промяната, **test report** — резултата. **Baseline** е началната версия за сравнение.
   - Запазете заявката, очакването и отчета. Провалена компилация не е доказателство, че тестът е открил нарушение. Не представяйте непроверена хипотеза като установен дефект.

`mvn test` изпълнява тестовете с H2 и записва target/surefire-reports. `mvn -Ppostgres-tests test` използва отделната PostgreSQL тестова база, стартирана по инструкциите по-долу. **Maven profile** е именуван набор от настройки. Новите класове с тестове завършват на Test. За браузърно поведение се използва и реален браузър; MockMvc не изпълнява JavaScript.

### 1.3. Средства за стартиране

1. **Docker image** е образ с приложението и нужната среда; **container** е негов работещ екземпляр. **Docker Compose** описва и стартира свързаните услуги в compose.yml.
   - `docker compose up -d --build --wait` изгражда образа, стартира услугите във фонов режим и изчаква проверките за готовност. **Healthcheck** проверява състоянието на услуга; тук PostgreSQL има такава проверка, а готовността на приложението проверяваме с HTTP заявка.
2. **Environment variable** е стойност от средата на процеса; файлът **.env** предоставя такива стойности на Compose. **Volume** съхранява данните отделно от контейнера.
   - init-environment.ps1 генерира DB_PASSWORD и JWT_SECRET в .env. `docker compose down` спира услугите, но запазва данните в тома task-db.
3. **Maven Wrapper** е включен в проекта скрипт, който изтегля и стартира необходимия Maven. **JDK** предоставя Java компилатора и средата за локалните тестове.
   - В PowerShell `./mvnw.cmd test` изпълнява тестовете без отделна инсталация на Maven; все пак е нужен JDK.
4. **HTTP session** свързва поредица заявки с влезлия потребител чрез cookie. **JSON** е текстовият формат на изпратените и получените данни.
   - `ConvertTo-Json` превръща PowerShell обект в JSON; `Invoke-RestMethod` изпраща заявката. `-SessionVariable taskSession` запазва получените cookies, а `-WebSession $taskSession` ги подава в следващата заявка.


## 2. Подготовка

### Предварителни знания

Java, Spring Boot, HTTP, JPA и Task Manager от lab11. Започнете от предоставеното копие на lab11.

### Начален проект и надграждане

Начално копие от lab11. Не добавяме нов функционален модул; описваме архитектурата и проверяваме конфигурацията.

[**Изтеглете началния работещ проект Task Manager (ZIP)**](/courses/downloads/task-manager-starter.zip)

Архивът съдържа папка task-manager с кода от lab11, конфигурацията за стартиране, Maven Wrapper и началните тестове. Включени са също setup.md и архитектурната карта. Не е необходимо да създавате проект от нулата или да изтегляте цялото хранилище на курса. Използвайте това копие за първото упражнение и продължавайте със своите промени във всички следващи упражнения.

Всички Maven/Compose команди по-долу се изпълняват от task-manager. Работните Java класове са в src/main/java/bg/tu_varna/sit/task_manager; тестовете — в съответния src/test/java package.

**Файлове за работа:** compose.yml, SecurityConfig, AuthController, AuthService, RegisterRequest, UserRepository. [Архитектурната карта](../architecture/system-overview.md) показва кои маршрути съществуват в началото и кои се добавят последователно.

### Изтегляне и разархивиране

1. Инсталирайте и стартирайте Docker Desktop с Linux containers и Docker Compose. За локалните тестове инсталирайте JDK 17+; проверете с `java -version`. Maven 3.9+ е по избор — проектът включва Wrapper.
2. Свалете ZIP файла чрез връзката по-горе. В PowerShell отворете папката, в която сте го запазили, и изпълнете:

   ```powershell
   Expand-Archive -LiteralPath ./task-manager-starter.zip -DestinationPath ./web-security
   Set-Location ./web-security/task-manager
   ```

3. Отворете тази папка в IDE като Maven проект чрез pom.xml. При следващите упражнения отваряйте същата работна папка. Не разархивирайте началното копие върху своите промени.

### Първо стартиране

Изпълнете от папката task-manager:

```powershell
./init-environment.ps1
docker compose config --quiet
docker compose up -d --build --wait
docker compose logs --tail 30 app
curl.exe -i http://localhost:9000/tasks
```

Първото изграждане изтегля Docker образи и Maven зависимости и изисква интернет. Скриптът създава .env с генерирани стойности за DB_PASSWORD и JWT_SECRET; при следващо изпълнение запазва съществуващия файл. Запазете .env и за следващите упражнения.

След стартиране на Spring заявката GET /tasks без вход трябва да върне **HTTP 401** — приложението работи и изисква удостоверяване. `--wait` изчаква готовността на базата, но приложението може да се нуждае от още няколко секунди; повторете заявката. При проблем прочетете `docker compose logs --tail 100 app db`.

Ако порт 9000 е зает, задайте `$env:TASK_MANAGER_PORT='19000'` преди командата за стартиране и използвайте http://localhost:19000 във всички заявки. За Bash използвайте `sh ./init-environment.sh`, същите Compose команди и `curl -i http://localhost:9000/tasks`.

### Начални потребители и първа задача

Празната база няма потребители. Изпълнете следните PowerShell команди веднъж, за да създадете alice, bob и admin. Регистрацията задава USER; следващата SQL команда дава ADMIN на профила admin:

```powershell
$base = 'http://localhost:9000'
foreach ($name in @('alice', 'bob', 'admin')) {
    $body = @{username=$name;password="$name-password-2026!"} | ConvertTo-Json
    Invoke-RestMethod "$base/auth/register" -Method Post -ContentType 'application/json' -Body $body
}
docker compose exec -T db psql -U task_user -d tasksdb -c "UPDATE users SET role='ADMIN' WHERE username='admin';"
```

Влезте като alice, запазете сесията и създайте задача:

```powershell
$body = @{username='alice';password='alice-password-2026!'} | ConvertTo-Json
$auth = Invoke-RestMethod "$base/auth/login" -Method Post -ContentType 'application/json' -Body $body -SessionVariable taskSession
Invoke-RestMethod "$base/tasks" -WebSession $taskSession
$task = @{summary='First task for Alice';description='Description for the first task';deadline='2099-12-31T12:00:00'} | ConvertTo-Json
$created = Invoke-RestMethod "$base/tasks" -Method Post -ContentType 'application/json' -Body $task -WebSession $taskSession
Invoke-RestMethod "$base/tasks/$($created.id)" -WebSession $taskSession
```

Очаквайте успешен вход, списък от задачи и създадената задача с ID от отговора. При повторно стартиране влизайте със съществуващите профили, вместо да ги регистрирате отново. Началният Task още няма собственик; тази връзка се добавя в упражнение 3.

### Изпълнение на началните тестове

Преди промяна изпълнете петте готови теста с H2 — те не изискват стартиран Docker:

```powershell
./mvnw.cmd test
```

След това изпълнете същите тестове с отделна PostgreSQL база:

```powershell
docker compose -f compose.test.yml up -d --wait
./mvnw.cmd -Ppostgres-tests test
docker compose -f compose.test.yml down
```

Очаквайте `BUILD SUCCESS` и 5 успешни теста. Отчетите са в target/surefire-reports. Тестовата PostgreSQL база е tasks_test на 127.0.0.1:55432; данните ѝ се пресъздават за тестовете. Тя е отделна от работната база на приложението.

При инсталиран Maven можете да замените `./mvnw.cmd` с `mvn`; за Bash използвайте `sh ./mvnw`. След промени повторете тестовете и обновете работещото приложение с `docker compose up -d --build --wait`.

### Спиране и продължаване

`docker compose down` спира приложението и базата, като запазва работните данни. За продължаване изпълнете `docker compose up -d --wait`. Използвайте `docker compose down -v` само ако искате да изтриете работната база и да започнете с празни данни; тогава създайте потребителите отново.


## 3. Примерен проблем

Оценете как публикуването на PostgreSQL променя достъпа до Task Manager и докажете конфигурацията на предоставеното копие.

### Стъпки за решаване


1. Стартирайте Task Manager по инструкциите в раздел „Подготовка“ по-горе и изпълнете `docker compose ps`. Сравнете compose.yml с началния фрагмент: app ports=[9000:9000], db ports=[5432:5432].
2. Начертайте DFD и означете три граници: клиент/HTTP API, security context/приложна логика, приложение/DB. Посочете кои стойности идват от клиента.
3. Изпълнете GET http://localhost:9000/tasks без вход → 401. Регистрирайте alice с JSON, влезте и повторете със сесията → 200 и списък от задачи.
4. Прочетете `docker compose config --format json`: db няма ports, app има един публикуван порт с host_ip=127.0.0.1, backend е internal. Връзката app → db:5432 продължава да работи.
5. Изпълнете готовия `TaskManagerBaselineTest`. Добавете `NetworkPolicyTest` или PowerShell проверка за конфигурацията и отрицателно копие на JSON с публикуван DB порт. Няма нужда от промяна на реалната мрежа за този отрицателен тест.

Очакваният резултат е схема, сравнение на достъпа и проверка, която приема текущата конфигурация и отхвърля публикуван DB порт.


## Самостоятелни задачи


### Задача 1 — Оценка на регистрацията

Анализирайте POST /auth/register през AuthController → AuthService.register → UserRepository. Предайте DFD, активи, входове, граници и поне пет заплахи с участник, предпоставка, риск 1–9, предложена защита и тест. Включете поне една заплаха за наличност, една за повишаване на права и една за изтичане на информация. Добавете нов тест, който доказва, че параметър role=ADMIN не създава администратор. Проверете ролята в базата, не само HTTP статуса. Съществуващият baseline тест е ориентир; разширете го и с проверка на повторна регистрация или липсващо задължително поле.

### Задача 2 — Гранични конфигурации

Разширете мрежовата проверка с host_ip=::, публикуван DB порт и допълнителен app порт. Изберете поне един случай за автоматизирана отрицателна проверка. Обяснете защо вътрешната мрежа не заменя проверката на права върху задача.
