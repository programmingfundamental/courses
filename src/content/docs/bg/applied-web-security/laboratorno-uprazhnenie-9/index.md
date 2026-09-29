---
title: "Упражнение 9 — JWT, отделен Bearer API и refresh rotation"
sidebar:
  order: 9
  label: "Упражнение 9"
---

# Упражнение 9 — JWT, отделен Bearer API и refresh rotation

## 1. Теория


### 1.1. Подпис, claims и отмяна

1. **JWT** има header, payload и signature; **Base64url** е кодиране; **claim** е твърдение. **HS256** подписва с общ таен ключ, **issuer/audience/subject/expiration** определят издател/получател/потребител/срок.
   - JwtService вече проверява подпис чрез verifyWith и издава exp. Добавяме задължителни iss, aud, sub, exp и точен алгоритъм; не заменяме проверката с parsing.
2. **Clock skew** допуска разлика между часовници; **replay** използва token повторно.
   - Приетият договор е now<exp с нулев skew; кратък срок ограничава, но не забранява replay.
3. **Stateless chain** не използва HttpSession; **Bearer** се подава изрично от клиента. **SecurityContextRepository** определя къде се пази context.
   - /token-api/tasks приема само Bearer и няма session fallback; /tasks и /ui остават session API с CSRF.
4. **Refresh rotation** обезсилва стар refresh token при издаване на нов; **revocation** го отменя; **digest** пази стойност за сравнение вместо raw token.
   - RefreshTokenService в lab11 връща същия token. При rotation две едновременни употреби трябва да дадат точно един успех.
5. **Key rotation** сменя signing key; **roles** в JWT са различни от актуални права в DB.
   - Тук JwtAuthFilter зарежда UserDetails от DB, проверява enabled и използва текущите authorities. Logout отменя refresh/session; вече издаден access остава валиден до exp, освен ако добавим server-side revocation.



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

Java, Spring Boot, HTTP, JPA и Task Manager от lab11. Продължете собственото решение от упражнение 8; запазете тестовете и данните с определени собственици.

### Начален проект и надграждане

След упражнение 8. Запазваме JJWT и HS256 от lab11; добавяме валидирани claims, Clock и отделна stateless верига /token-api/**. Refresh tokens се завъртат еднократно.

Използвайте [Task Manager](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/task-manager/README.md) и [подготовката](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Всички Maven/Compose команди се изпълняват от task-manager. Работните Java класове са в src/main/java/bg/tu_varna/sit/task_manager; тестовете — в съответния src/test/java package.

**Файлове за работа:** JwtService, JwtAuthFilter, RefreshTokenService/Repository, SecurityConfig, нов TokenTaskController. [Архитектурната карта](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/system-overview.md) показва кои маршрути съществуват в началото и кои се добавят последователно.

JDK 17+, Maven 3.9+ или Maven Wrapper, Docker Compose и браузър са достатъчни. Преди промяна изпълнете mvn test; след промяната повторете съответните тестове и PostgreSQL профила. Новите класове/маршрути, описани като надграждане, се реализират в това упражнение.


## 3. Примерен проблем

Затегнете JwtService и отделете Bearer достъпа до задачите от сесийната верига.

### Стъпки за решаване


1. Добавете Clock bean към JwtService. Издавайте issuer=task-manager, audience=task-manager-api, sub=username, exp=now+TTL и HS256. Секретът идва от конфигурацията, не от request.
2. След cryptographic verification проверете algorithm=HS256, задължителни exp/sub/iss/aud и now<exp. Parse-вайте token веднъж за заявка. Invalid Bearer връща 401, включително ако клиентът има валидна сесия.
3. JwtAuthFilter зарежда user, отказва disabled/unknown user, създава context с актуалните authorities. Изключете автоматичната servlet регистрация на filter bean, така че да работи само в избраната security chain.
4. Добавете @Order(1) SecurityFilterChain за /token-api/**: STATELESS, NullSecurityContextRepository, CSRF disabled, без Basic/formLogin. @Order(2) запазва session/CSRF за останалото и не добавя JWT filter.
5. TokenTaskController GET /token-api/tasks делегира на TaskService.getAll. USER вижда собствени задачи; ADMIN всички. JwtContractTest използва истински подписан token, не само mocked jwt()/user().
