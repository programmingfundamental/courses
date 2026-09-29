# Подготовка на Task Manager

## Получаване на проекта

Използвайте папката [task-manager](task-manager/README.md) от този курс. Тя е фиксирано работно копие на познатия lab11 с описани технически корекции и подготовка за ръчни проверки с Postman. За всяко следващо упражнение продължете своите промени; запазвайте отделен commit за завършените стъпки. Не е нужен втори портал или превключване на режим.

## Изисквания и първо стартиране

Docker Desktop с Linux containers и Docker Compose; свободен порт 9000. За локална компилация: JDK 17+ и Maven 3.9+ или включеният Maven Wrapper. Използвайте Postman за HTTP заявките. PowerShell команди от course-materials/applied-web-security/task-manager:

```powershell
./init-environment.ps1
docker compose config --quiet
docker compose up -d --build --wait
docker compose logs --tail 30 app
```

Изчакайте Spring да стартира; в Postman GET http://localhost:9000/tasks без вход трябва да върне 401. На първото изграждане се изтеглят Maven зависимости и Docker образи. Ако порт 9000 е зает, задайте $env:TASK_MANAGER_PORT='19000' преди Compose командата и променете base URL в клиентските команди; вътрешният app порт остава 9000.

За Bash: `sh ./init-environment.sh`, после същите Compose команди. .env съдържа генерирани DB_PASSWORD и JWT_SECRET и е игнориран от Git. Init script не го презаписва. Промяна само на POSTGRES_PASSWORD не сменя паролата в вече създаден DB volume; съгласувайте промяната в DB и app.

## Начални потребители и първа задача в Postman

1. Създайте колекция Task Manager и environment с `baseUrl=http://localhost:9000`. За заявките изберете No Auth; входът използва cookie сесия.
2. Изпратете `POST {{baseUrl}}/auth/register` с Body → raw → JSON:

   ```json
   {"username":"alice","password":"alice-password-2026!"}
   ```

   Повторете ръчно за bob и admin с отделни пароли. При съществуващи профили преминете към вход. Регистрацията винаги задава USER. Само за началната подготовка на admin изпълнете от task-manager:

   ```powershell
   docker compose exec -T db psql -U task_user -d tasksdb -c "UPDATE users SET role='ADMIN' WHERE username='admin';"
   ```

3. Изпратете `POST {{baseUrl}}/auth/login` със същия JSON за alice. Очаквайте 200 и JSESSIONID в cookies. Следващата `GET {{baseUrl}}/tasks` трябва да върне 200. Не добавяйте Authorization header за сесийните проверки.
4. Изпратете `POST {{baseUrl}}/tasks` с JSON:

   ```json
   {"summary":"First task for Alice","description":"Description for the first task","deadline":"2099-12-31T12:00:00"}
   ```

5. Очаквайте 201. Копирайте ръчно id от отговора в environment променлива `taskId`. Изпратете `GET {{baseUrl}}/tasks/{{taskId}}` и сравнете полетата с изпратените. Началният Task няма owner; той се добавя в упражнение 4.
6. За проверка като bob изчистете cookies за localhost и влезте като bob. За анонимна проверка изчистете cookies и оставете No Auth. Отделни environments сами по себе си не изолират cookie jar.

## Протокол от ръчните проверки

| Потребител и начално състояние | Заявка | Очакван резултат | Действителен резултат | Данни преди/след |
| --- | --- | --- | --- | --- |
| Без сесия | GET /tasks | 401 | Попълнете | Без промяна |
| Alice след вход | GET /tasks | 200, списък | Попълнете | Без промяна |
| Alice след вход | POST /tasks с валиден JSON | 201, нов ID | Попълнете | Нов запис |
| Alice след създаване | GET /tasks/{id} | 200, същите полета | Попълнете | Без промяна |

Изпращайте всяка заявка с Send. Не добавяйте Postman scripts или Collection Runner. Запазвайте обезличени резултати и описания на заявките; премахвайте credentials, cookies и tokens от споделяните файлове. След промени обновете приложението с `docker compose up -d --build --wait` и повторете проверките.

## След упражнение 8 — CSRF в Postman

След реализиране на GET /auth/csrf изпратете тази заявка с No Auth и запазете cookie сесията. Копирайте ръчно token и headerName от JSON отговора. За POST /auth/register и POST /auth/login добавете header с точно това име и token като стойност. След успешен login отново изпратете GET /auth/csrf и заменете token с новия. Всички session POST/PATCH/PUT/DELETE подават този header и същата cookie сесия.

За отрицателна проверка премахнете header или подайте грешна стойност; очаквайте 403 и непроменени данни. След logout изчистете cookies и tokens. Изпращането на cookies от реален браузър се проверява отделно.

## След упражнение 10 — Bearer API в Postman

Копирайте ръчно accessToken от login/refresh отговора в локална environment променлива. За GET {{baseUrl}}/token-api/tasks изберете Authorization → Bearer Token и въведете {{accessToken}}. Изчистете session cookies, за да проверите отделния Bearer поток. /token-api/** се добавя в упражнение 10; /tasks и /ui/** остават сесийни и CSRF-защитени.

За POST /auth/refresh първо вземете нов GET /auth/csrf след изчистването на cookies. Изпратете JSON {"refreshToken":"..."} и получения CSRF header със същата cookie сесия. След rotation копирайте новите accessToken и refreshToken ръчно. Повторете заявката със стария refresh token и запишете отказа. Не споделяйте локалните token стойности.

## Преминаване към упражнение 11

Запазете кода, Postman колекцията и попълнените протоколи от упражнения 2–10. В упражнение 11 тези сценарии се превръщат в автоматизирани тестове; подготовката на JUnit, H2 и отделната PostgreSQL база е в ресурсите към него.

## Спиране и данни

`docker compose down` спира проекта и запазва named volume. `docker compose down -v` изтрива единствено данните на този Compose проект; използвайте го само когато искате празна работна база. Преди това проверете project name web-security-task-manager и запазете нужните данни. Кодът, .env и файловете с ключове не се възстановяват чрез Docker reset.

Ключът за private note се добавя в упражнение 9 и трябва да се пази между рестартиранията. Загубата му прави старите бележки нечетими. JWT_SECRET в .env също се запазва; рестарт със същия ключ не отменя сам по себе си access tokens.
