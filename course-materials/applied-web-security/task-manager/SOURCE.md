# Произход на началната версия

Източник: локалният проект tasks-manager/lab11, прегледан на 28.09.2026. HEAD на хранилището при копиране: 97eb7d8c6a4275da447711864b93fc5c936e6351. Копието е от наличните работни файлове; този commit обозначава контекста, а не твърдение, че работното дърво е било чисто.

Копирани са src, pom.xml, Maven Wrapper и Git ignore/attributes. Не са копирани .idea, target, data и локални credentials. src/main/java остава в bg.tu_varna.sit.task_manager.

## Подготовка в копието

- RefreshTokenService.revokeAllUserTokens е транзакционен, така че logout да премахва записите и извън тестова транзакция.
- ReportServiceImp получава TaskRepository чрез constructor injection.
- Сумирането на workedTime се извършва като Duration; отговорът totalWorkedTime е ISO-8601 текст (например PT31H), вместо сумиране на SQL TIME/LocalTime.
- Explicit mapping свързва ReportRequestDto.workTime с Report.workedTime и обратно към ReportResponseDto.workTime. ReportResponseDto използва taskId, а TaskResponseDto не връща рекурсивно reports.
- Размерите на Task.summary/description и Report.content съответстват на входната валидация.
- Добавени са H2 и spring-security-test, application-test/application-postgres-test и TaskManagerBaselineTest.
- DB_PASSWORD и JWT_SECRET идват от средата. Init scripts създават .env без презаписване.
- Compose използва отделно project name, PostgreSQL 17.6, internal backend за db, допълнителна frontend мрежа за app и app port 127.0.0.1:9000. Тестовата база е отделна и временна на 55432.
- Dockerfile изгражда JAR с Maven/Java 17 и стартира с Java 17 JRE.

## Начално поведение и последователност

Сесии, JWT и Basic от lab11, csrf.disable(), сегашната регистрационна валидация, ранното връщане при authenticated login, Task без owner и преизползването на refresh token са описани в упражненията според действителния код. Началните тестове проверяват работоспособност, а не завършеност на всички security изисквания.
