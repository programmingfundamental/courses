# Проверка на курса с Task Manager

## Историческа проверка на началното копие — 28.09.2026

- Източник: tasks-manager/lab11; измененията са описани в task-manager/SOURCE.md.
- `mvn test`: 5 теста, 0 failures, 0 errors, 0 skipped, H2.
- `mvn -Ppostgres-tests test`: същите 5 теста, 0 failures, 0 errors, 0 skipped, PostgreSQL 17.6 в отделен Compose проект.
- Проверяват се регистрация/BCrypt/USER, реален login/tokens/session, task/report поток със сбор PT31H и отказ за anonymous и logout с реално премахване на refresh записите.

- Docker build/start и реален HTTP поток: PASS — регистрация, login със сесия/tokens, създаване/прочит на задача, ADMIN отчет и сбор PT2H30M, logout и премахнати refresh записи. Проверено в отделен Compose проект на порт 19000; тестовите контейнери и volume са премахнати.
- Astro check: 0 errors/warnings; build: успешно; checks за синхронизация, задачи, вътрешни връзки и миграция: PASS.

## Граница на доказаното

Тестовете по-горе се отнасят до началното копие. Owner, search, HTML, CSRF, cipher и token-api се добавят от студента в упражненията; решенията в hint.md са преподавателски материали, а не твърдение за изпълнени проверки на завършените десет надграждания. Ръчни браузърни проверки и HTTPS cookie enforcement се отчитат отделно след реализацията.

## Промяна на учебната последователност — 29.09.2026

Упражнения 1–10 вече използват ръчни проверки и Postman. Примерните Java тестове, тестовите properties и compose.test.yml са преместени в lab11-security-testing/resources. Starter няма тестова конфигурация. Историческите резултати по-горе не са ново изпълнение на преместения пакет или на студентските надграждания.

Проверено след промяната: структура на 11 упражнения; синхронизация на страниците; валиден starter pom.xml; отделни ZIP пакети без тестове в starter; Astro check с 0 errors/warnings; успешно изграждане; 90965 вътрешни връзки и 372 страници със задачи. Build показва предупреждения за размер на bundle и липсващ content entry за 404. Java тестовете и ръчните Postman сценарии не са изпълнявани наново.

## Повторение

След добавяне на пакета и Maven настройките от упражнение 11, от task-manager: mvn test; docker compose -f compose.test.yml up -d --wait; mvn -Ppostgres-tests test; docker compose -f compose.test.yml down. Командите се изпълняват последователно. Reports са в target/surefire-reports. Структурата се проверява чрез ../scripts/check-structure.ps1 от директорията на проекта или scripts/check-structure.ps1 от корена на курса.
