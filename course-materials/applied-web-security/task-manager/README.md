# Task Manager — начална версия за Уеб сигурност

Това е копие на tasks-manager/lab11, върху което последователно се изпълняват десетте упражнения. Запазени са package bg.tu_varna.sit.task_manager, моделите Task/Report/User, JPA repositories, REST адресите /tasks, /reports и /auth, BCrypt, сесийният login и JJWT HS256.

## Стартиране

От тази директория: `./init-environment.ps1` (PowerShell) или `sh ./init-environment.sh` (Bash), после `docker compose up -d --build --wait`. Приложението слуша на http://localhost:9000; базата не публикува порт. Изчакването --wait удостоверява DB health; проверете допълнително GET /tasks → 401, за да установите, че Spring е готов.

Подробните JSON заявки и подготовката на потребители са в [setup.md](../setup.md). Не се изисква локален Maven за Docker build; за локални тестове са нужни JDK 17+ и Maven 3.9+ или включеният wrapper.

## Проверки

- `mvn test` — H2, пет начални интеграционни теста.
- `docker compose -f compose.test.yml up -d --wait`, после `mvn -Ppostgres-tests test` — същите тестове с отделен PostgreSQL на порт 55432.
- `docker compose -f compose.test.yml down` — спира само тестовата среда.
- Без инсталиран Maven: заменете mvn с ./mvnw.cmd в PowerShell или sh ./mvnw в Bash.

## Какво се добавя в упражненията

Началната версия няма owner на Task, search/lookup, HTML изглед, CSRF endpoint, rate limiter, private note и отделен token-api. Те се добавят в описания ред в [архитектурната карта](../architecture/system-overview.md). Студентът продължава своето решение от предходното упражнение. Няма LAB_MODE и не се връща автоматично към първоначалното копие.

Първоначалният TaskManagerBaselineTest е изпълним и не изисква готови решения. След добавянето на owner тестовите users се записват и в UserRepository преди създаване на задачи; след включване на CSRF реалният клиент взема token преди и след login. Документираните нови тестови класове се създават в съответните упражнения.

## Произход и промени

Вижте [SOURCE.md](SOURCE.md). Оригиналната директория tasks-manager/lab11 не е променяна. Тук са коригирани техническите пречки пред изпълнението, добавени са конфигурация, Docker build и начални тестове. Общите security решения остават работа по упражненията.
