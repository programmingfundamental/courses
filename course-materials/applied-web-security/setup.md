# Подготовка на Task Manager

## Получаване на проекта

Използвайте папката [task-manager](task-manager/README.md) от този курс. Тя е фиксирано работно копие на познатия lab11 с описани технически корекции и готови начални тестове. За всяко следващо упражнение продължете своите промени; запазвайте отделен commit за завършените стъпки. Не е нужен втори портал или превключване на режим.

## Изисквания и първо стартиране

Docker Desktop с Linux containers и Docker Compose; свободен порт 9000. За локални Java тестове: JDK 17+ и Maven 3.9+ или включеният Maven Wrapper. PowerShell команди от course-materials/applied-web-security/task-manager:

```powershell
./init-environment.ps1
docker compose config --quiet
docker compose up -d --build --wait
docker compose logs --tail 30 app
curl.exe -i http://localhost:9000/tasks
```

Изчакайте Spring да стартира; GET /tasks без вход трябва да върне 401. На първото изграждане се изтеглят Maven зависимости и Docker образи. Ако порт 9000 е зает, задайте $env:TASK_MANAGER_PORT='19000' преди Compose командата и променете base URL в клиентските команди; вътрешният app порт остава 9000.

За Bash: `sh ./init-environment.sh`, после същите Compose команди. .env съдържа генерирани DB_PASSWORD и JWT_SECRET и е игнориран от Git. Init script не го презаписва. Промяна само на POSTGRES_PASSWORD не сменя паролата в вече създаден DB volume; съгласувайте промяната в DB и app.

## Начални потребители и JSON заявки

В празната база няма alice/bob/admin. Регистрацията винаги задава USER. Изпълнете веднъж за всеки потребител; при повторение използвайте съществуващия профил:

```powershell
$base = 'http://localhost:9000'
foreach ($name in @('alice','bob','admin')) {
    $body = @{username=$name;password="$name-password-2026!"} | ConvertTo-Json
    Invoke-RestMethod "$base/auth/register" -Method Post -ContentType 'application/json' -Body $body
}
# Само начална подготовка на административния профил:
docker compose exec -T db psql -U task_user -d tasksdb -c "UPDATE users SET role='ADMIN' WHERE username='admin';"
$body = @{username='alice';password='alice-password-2026!'} | ConvertTo-Json
$auth = Invoke-RestMethod "$base/auth/login" -Method Post -ContentType 'application/json' -Body $body -SessionVariable taskSession
Invoke-RestMethod "$base/tasks" -WebSession $taskSession
$task = @{summary='First task for Alice';description='Description for the first task';deadline='2099-12-31T12:00:00'} | ConvertTo-Json
$created = Invoke-RestMethod "$base/tasks" -Method Post -ContentType 'application/json' -Body $task -WebSession $taskSession
Invoke-RestMethod "$base/tasks/$($created.id)" -WebSession $taskSession
```

Вземайте ID от отговора, вместо да предполагате, че alice притежава конкретно число. Началният Task няма owner; той се добавя в упражнение 3. GET /reports/** изисква ADMIN. При лабораторните тестове потребителите се създават във fixtures и не се използва тази работна база.

## След упражнение 7 — CSRF клиент

След добавяне на GET /auth/csrf и token rotation заменете входа с:

```powershell
$csrf = Invoke-RestMethod "$base/auth/csrf" -SessionVariable taskSession
$headers = @{}; $headers[$csrf.headerName] = $csrf.token
$body = @{username='alice';password='alice-password-2026!'} | ConvertTo-Json
$auth = Invoke-RestMethod "$base/auth/login" -Method Post -ContentType 'application/json' -Body $body -WebSession $taskSession -Headers $headers
$csrf = Invoke-RestMethod "$base/auth/csrf" -WebSession $taskSession
$headers = @{}; $headers[$csrf.headerName] = $csrf.token
# Всички session POST/PATCH/PUT/DELETE използват -WebSession $taskSession -Headers $headers.
```

За нова регистрация след упражнение 7 първо вземете /auth/csrf и подайте същата сесия/headers с POST /auth/register. След logout изхвърлете session и tokens.

## След упражнение 9 — Bearer API

```powershell
$bearerHeaders = @{Authorization="Bearer $($auth.accessToken)"}
Invoke-RestMethod "$base/token-api/tasks" -Headers $bearerHeaders
```

/token-api/** съществува след упражнение 9 и използва отделна stateless chain. /tasks и /ui/** остават сесийни и CSRF-защитени. Refresh rotation връща нов accessToken и refreshToken; заменете старите стойности в клиента. Началният lab11 използва обща верига; не прилагайте договора на упражнение 9 към непроменения starter.

## Тестове и отчети

```powershell
mvn test
docker compose -f compose.test.yml up -d --wait
mvn -Ppostgres-tests test
docker compose -f compose.test.yml down
```

Без инсталиран Maven използвайте ./mvnw.cmd (PowerShell) или sh ./mvnw (Bash). H2 тестовете не изискват Docker. PostgreSQL профилът ползва tasks_test на 127.0.0.1:55432 с create-drop; не го насочвайте към базата с работни данни. TEST_DB_URL/USER/PASSWORD могат да изберат друга отделна тестова база. Report: target/surefire-reports. Профилът не пропуска тихо недостъпна база.

Новите тестове от упражненията се добавят в src/test/java/bg/tu_varna/sit/task_manager с имена *Test. След промяна на кода: docker compose up -d --build. Test code с with(user(...)) не доказва password login или JWT signature; за тях използвайте истински HTTP login/Bearer token.

## Спиране и данни

`docker compose down` спира проекта и запазва named volume. `docker compose down -v` изтрива единствено данните на този Compose проект; използвайте го само когато искате празна работна база. Преди това проверете project name web-security-task-manager и запазете нужните данни. Кодът, .env и файловете с ключове не се възстановяват чрез Docker reset.

Ключът за private note се добавя в упражнение 8 и трябва да се пази между рестартиранията. Загубата му прави старите бележки нечетими. JWT_SECRET в .env също се запазва; рестарт със същия ключ не отменя сам по себе си access tokens.
