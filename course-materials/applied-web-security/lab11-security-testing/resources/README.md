# Работна карта — Task Manager, упражнение 11

Използвайте резултата от упражнения 2–10. Началното копие от lab11 не съдържа тези завършени надграждания; тук се проверява натрупаната студентска реализация.

| Поле | Резултат |
|---|---|
| Версия/commit | |
| HTTP метод и маршрут | |
| Потребител и роля | |
| Начални данни/owner | |
| Очакван/получен статус | |
| DB преди/след | |
| Проверявано правило | |
| Test class и report | |
| Оставащ риск | |

Файлове: Всички надграждания; нов AuditFilter и AuditTest; H2/PostgreSQL тестови профили.

След подготовката по-долу изпълнете от task-manager: mvn test; след стартиране на compose.test.yml — mvn -Ppostgres-tests test.


## Добавяне на тестовата среда за първи път

Пакетът task-manager-testing.zip съдържа тази инструкция, src/test и compose.test.yml. Разархивирайте го отделно и копирайте src/test и compose.test.yml в своя task-manager от упражнение 10. Не заменяйте приложния код и pom.xml с начална версия.

Добавете в съществуващия раздел dependencies на pom.xml:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

Добавете в properties: `<spring.profiles.active>test</spring.profiles.active>`. Като пряк елемент на project добавете профила (или го включете в съществуващия profiles):

```xml
<profiles>
    <profile>
        <id>postgres-tests</id>
        <properties><spring.profiles.active>postgres-test</spring.profiles.active></properties>
    </profile>
</profiles>
```

В build/plugins добавете:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <systemPropertyVariables>
            <spring.profiles.active>${spring.profiles.active}</spring.profiles.active>
        </systemPropertyVariables>
    </configuration>
</plugin>
```

Адаптирайте TaskManagerBaselineTest към owner, CSRF и refresh digest реализацията си, както е описано в упражнение 11. Добавете необходимата конфигурация за FieldCipher и останалите нови компоненти към двата профила, като използвате само отделни учебни ключове. Новите класове завършват на Test.

От task-manager с JDK 17+ изпълнете последователно:

```powershell
./mvnw.cmd test
docker compose -f compose.test.yml up -d --wait
./mvnw.cmd -Ppostgres-tests test
docker compose -f compose.test.yml down
```

За Bash заменете ./mvnw.cmd с sh ./mvnw; при инсталиран Maven може да използвате mvn. H2 не изисква Docker. PostgreSQL профилът използва tasks_test на 127.0.0.1:55432 с create-drop; не го насочвайте към работните данни. TEST_DB_URL/USER/PASSWORD избират само отделна тестова база. Отчетите са в target/surefire-reports. Запишете действителния брой тестове, failures/errors/skipped за всяко изпълнение. Старите пет примера не доказват всички добавени защити.
