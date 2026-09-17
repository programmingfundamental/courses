---
layout: default
title: Лабораторно упражнение 1
parent: Учебна практика 3
has_children: true
nav_order: 1
---

# Лабораторно упражнение 1

## Spring Boot REST приложение и първи експерименти с контекста

### Цел: 

В упражнението ще бъде създадено началното състояние на REST приложение за управление на заявки за услуги.
Ще бъдат сравнени резултатите от работа с AI асистент при различна степен на конкретност на зададения контекст. Полученият код трябва да бъде прегледан, стартиран и оценен, преди да бъде използван като основа за следващите упражнения.

### Кратко въведение в Spring Boot REST приложенията

Spring Boot улеснява създаването на Java приложения, базирани на Spring, като предоставя готова конфигурация и управление на необходимите зависимости.

Типично REST приложение може да бъде организирано в няколко слоя:
-	Controller – приема HTTP заявки и връща HTTP отговори;
-	Service – съдържа логиката по изпълнение на операциите;
-	Repository – осигурява достъп до съхраняваните данни;
-	Entity – представя обектите, които се съхраняват в базата данни;
-	Data Transfer Object (DTO) – използва се за пренасяне на данни към и от REST API.

Обработването на една заявка може да бъде представено така:

HTTP Request
     ↓
Controller
     ↓
Service
     ↓
Repository
     ↓
Database

*Dependency Injection*

Не е необходимо обектите в Spring приложението сами да създават зависимостите, от които се нуждаят. Те могат да ги получават чрез така наречения dependency injection.

Например контролер може да получи необходимия service чрез своя конструктор:

```java
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
}
```

*REST endpoints*

REST приложението предоставя endpoints, достъпни чрез HTTP заявки. Най-често използваните HTTP методи са:

| Метод  | Предназначение                |
|--------|-------------------------------|
| GET    | извличане на данни            |
| POST   | създаване на нов ресурс       |
| PUT    | промяна на съществуващ ресурс |
| DELETE | изтриване на ресурс           |

Данните в заявката и отговора обикновено се представят в JSON формат.

*Структура на Maven проект*

Основните директории и файлове в Maven проект са:

<img
  width="300"
  alt="mvn project structure"
  src="https://github.com/user-attachments/assets/965b6dfe-6296-4b8b-8dfa-657ccf04b9bf"
/>


*pom.xml*

Файлът pom.xml описва проекта и неговите зависимости, а src/main/resources съдържа конфигурационни файлове като application.properties.

### Задача

Да се разработи REST приложение за управление на заявки за услуги. 
Клиентите трябва да могат да подават заявки за услуги, а служителите на офиса да могат да преглеждат постъпилите заявки.
Приложението ще бъде разширявано с допълнителна функционалност в следващите упражнения.

### Експерименти

*Експеримент 1: минимален контекст*

Да се създаде празен Java проект. Да се подаде на ИИ асистента следния промпт:

```text
Create REST app that handles client requests.
```

Да се разгледа генерираното решение, без предварително да се променя.
Да се определи:
-	какви технологии е избрал ИИ асистентът;
-	какви класове е създал;
-	как е разпределил отговорностите между тях;
-	какви свойства на заявката е предположил;
-	какви операции е реализирал;
-	какви решения е взел, въпреки че те не са зададени в промпта.

Да се сравни структурата на получения проект със структурата на Spring Boot REST приложение, разгледана в началото на упражнението.
Кои части от полученото решение са следствие от поставената задача и кои са предположения на ИИ асистента?

*Експеримент 2: добавяне на технологичен контекст*

Да се започне отново от празен проект. Да се подаде следния промпт:

```text
Create SpringBoot REST app that handles customer requests. It should use Maven, Spring Web, Lombok, H2, validation; the app should be called ServiceRequestManagementSystem.
```

Да се анализира получения проект.
Да се провери:
-	създаден ли е Maven проект;
-	добавени ли са необходимите зависимости;
-	как са организирани пакетите;
-	има ли разделение между controller, service и repository;
-	използвани ли са DTOs;
-	какъв модел на клиентска заявка е създаден;
-	какви REST endpoints са реализирани;
-	какви функционални решени е взел ИИ асистентът самостоятелно.
Да се сравни резултата с този от първия експеримент.


Проверка на генерираното решение: да се компилира и стартира приложението. Да не се приема твърдението на ИИ асистента, че приложението се компилира или работи успешно, като доказателство за това.
При възникване на грешка:
-	да се анализира съобщението за грешка;
-	да се определи вероятната причина;
-	да се провери конфигурация и използваните версии;
-	при необходимост да се използва ИИ асистента за анализ на проблема;
-	да се провери самостоятелно предложеното решение.


*Експеримент 3: функционален, технологичен и архитектурен контекст*

Да се започне отново от празен проект. Този път на ИИ ще бъдат зададени както използваните технологии, така и началната структура и функционалност на приложението. 

Да се подаде следния промпт:

```text
Create SpringBoot REST app that handles customer requests. It should use Maven, Spring Web, Lombok, H2, validation; the app should be called ServiceRequestManagementSystem. Necessary packages: controller, dto, exception, entity, repository, service.
Two controllers: RequestController and OfficeRequestController. Endpoints:
POST /api/requests
GET /api/requests/{id}
GET /api/requests?customerEmail={email}
GET /api/office/requests
GET /api/office/requests/{id}
ServiceRequest to be the base entity. Properties: id, customerName, customerEmail, description, serviceType, executionMode, priority, address, urgencyReason, status, submittedAt. serviceType, executionMode, priority and status are enums.
ServiceType: INSTALLATION, REPAIR, DIAGNOSTICS, CONSULTATION.
ExecutionMode: REMOTE, ON_SITE.
Priority: NORMAL, URGENT.
RequestStatus: SUBMITTED, PROCESSING, OFFERED, ACCEPTED, SCHEDULED, IN_PROGRESS, COMPLETED, REJECTED, EXPIRED, CANCELLED.
New request is created with status SUBMITTED, submittedAt is set by the system. Customer doesn't send id, status and submittedAt.

```

Да се анализира генерираното решение и то да бъде сравнено с резултатите от предходните два експеримента.
Да се обърне внимание на:
-	структурата на пакетите;
-	създадените класове;
-	отговорностите на отделните слоеве;
-	зависимостите между тях;
-	използването на DTO;
-	реализираните endpoints;
-	представянето на изброимите типове;
-	мястото, на което се извършва преобразуването между entity и DTO;
-	решенията, които ИИ асистентът все още е взел самостоятелно.

Да се компилира и стартира създаденото приложение.
Как се промени генерираното решение, когато технологичните, функционалните и архитектурните решения бяха зададени предварително?


*Експеримент 4: архитектурен преглед с ИИ асистент*

След като приложението работи, да се използва ИИ асистента не за генериране на нов код, а за оценяване на съществуващото решение.

Промпт: 

```text
Review the architecture of the currently generated Spring Boot application. Analyze its package organization, layering, separation of responsibilities, dependencies between components, and maintainability. Identify possible improvements and justify each recommendation. Distinguish between improvements that are justified at the current size of the project and improvements that would be premature at this stage. Do not modify the code and do not add new functionality.
```

Да се разгледа всяка получена препоръка. За всяка от тях да се прецени:
-	какъв проблем според ИИ асистента се решава;
-	съществува ли действително този проблем в текущия проект;
-	необходима ли е промяната на структурата или само добавя допълнителна сложност;
-	приема ли се или се отхвърля дадената препоръка и защо.
Да не се променя код само защото ИИ асистентът е препоръчал промяна.

Да се избере поне една препоръка, която очаква повече аргументи или ще се отхвърли.
Да се продължи разговора с ИИ като се посочи за коя препоръка става въпрос; да се предоставят допълнителния контекст, който е важен; да се поиска аргументация без промяна на кода.
След получения отговор да се вземе окончателно решение дали дадената препоръка трябва да бъде приложена.
Целта не е ИИ асистента да бъде убеден да приеме посоченото решение, а да се използва дискусията с цел оценка на различните възможности.


*Експеримент 5: прилагане на избраните промени*

След определяне на кои архитектурни промени ще бъдат приети, да се поиска от ИИ асистента да приложи само тях.
Да се използва следния промпт като шаблон:

```text
Please apply the following approved architectural changes to the current project:
[описват се избраните промени]
Update all package declarations, imports, constructor dependencies, and references affected by these changes. 
Do not change the existing REST endpoints, DTOs, entity properties, validation rules, persistence behavior, exception handling, or application functionality.
After the refactoring, run tests/build and report the actual results.

```

След промяната:
-	да се прегледат действително променените файлове;
-	да се провери дали ИИ асистентът е направил само поисканите промени;
-	да се компилира и стартира приложението;
-	да се провери дали съществуващата функционалност е запазена.

### Резултат от упражнението

В края на занятието трябва да бъде налично работещо Spring Boot REST приложение, което съдържа базовата структура и началния модел на заявка за услуга. Полученият проект ще бъде използван и разширяван в следващите упражнения.
