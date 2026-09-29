---
title: "Упражнение 10 — JWT, отделен Bearer API и refresh rotation"
sidebar:
  order: 10
  label: "Упражнение 10"
---

# Упражнение 10 — JWT, отделен Bearer API и refresh rotation

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



### 1.2. Ръчни проверки с Postman

1. **Postman** изпраща HTTP заявки към работещото приложение. Изберете метод, URL, headers и JSON body, натиснете Send и сравнете статуса и съдържанието с очакваното.
2. **Положителна проверка** доказва разрешена операция, а **отрицателна проверка** — отказ. След отказ проверете с нова GET заявка или в DB, че данните са непроменени.
3. **Матрица на проверките** описва потребител, начални данни, заявка, очакван и действителен резултат. Използвайте реални login сесии и ID от отговорите. При смяна на потребителя изчистете cookies и влезте отново.
4. **Доказателство** е записан резултат от изпълнена проверка: статус, обезличен отговор и състояние преди/след. Запазете заявките в Postman колекция без пароли, cookies и tokens. За HTML, JavaScript и cookie поведение използвайте и браузър.

До упражнение 10 изпълнявайте заявките поотделно с Send и попълвайте резултатите ръчно. Автоматизираните тестове се въвеждат в упражнение 11.

## 2. Подготовка

### Предварителни знания

Java, Spring Boot, HTTP, JPA и Task Manager от lab11. Продължете собственото решение от упражнение 9; запазете Postman заявките, протокола от ръчните проверки и данните с определени собственици.

### Начален проект и надграждане

След упражнение 9. Запазваме JJWT и HS256 от lab11; добавяме валидирани claims, Clock и отделна stateless верига /token-api/**. Refresh tokens се завъртат еднократно.

Използвайте Task Manager и подготовката. Всички Maven/Compose команди се изпълняват от task-manager. Работните Java класове са в src/main/java/bg/tu_varna/sit/task_manager.

**Файлове за работа:** JwtService, JwtAuthFilter, RefreshTokenService/Repository, SecurityConfig, нов TokenTaskController. Архитектурната карта показва кои маршрути съществуват в началото и кои се добавят последователно.

Използвайте Docker Compose, Postman и браузър; за локална компилация — JDK 17+ и Maven 3.9+ или Maven Wrapper. Преди промяна запишете поведението с Postman; след промяната изпълнете `docker compose up -d --build --wait` и повторете ръчните проверки. Новите класове/маршрути, описани като надграждане, се реализират в това упражнение.


## 3. Примерен проблем

Затегнете JwtService и отделете Bearer достъпа до задачите от сесийната верига.

### Стъпки за решаване


1. Добавете Clock bean към JwtService. Издавайте issuer=task-manager, audience=task-manager-api, sub=username, exp=now+TTL и HS256. Секретът идва от конфигурацията, не от request.
2. След cryptographic verification проверете algorithm=HS256, задължителни exp/sub/iss/aud и now<exp. Parse-вайте token веднъж за заявка. Invalid Bearer връща 401, включително ако клиентът има валидна сесия.
3. JwtAuthFilter зарежда user, отказва disabled/unknown user, създава context с актуалните authorities. Изключете автоматичната servlet регистрация на filter bean, така че да работи само в избраната security chain.
4. Добавете @Order(1) SecurityFilterChain за /token-api/**: STATELESS, NullSecurityContextRepository, CSRF disabled, без Basic/formLogin. @Order(2) запазва session/CSRF за останалото и не добавя JWT filter.
5. TokenTaskController GET /token-api/tasks делегира на TaskService.getAll. USER вижда собствени задачи; ADMIN всички. Проверете с Postman и истински token от login отговора.
