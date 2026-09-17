---
layout: default
title: Лабораторно упражнение 4
parent: Учебна практика 3
has_children: true
nav_order: 4
---

# Лабораторно упражнение 4

## Обработка на заявката и жизнен цикъл

### Цел

Да се разшири приложението с първите операции от жизнения цикъл на заявката и да се разграничат:
-	невалидни входни данни;
-	липсващ ресурс;
-	недопустима бизнес операция спрямо текущото състояние.
Да се анализира къде трябва да бъдат разположени lifecycle правилата и как entity-то да бъде защитено от директна промяна на състоянието му.

### Начално състояние

Да се използва крайната работеща версия на проекта от ЛУ 3. В проекта вече са налични:
-	ServiceRequest;
-	DTO и Bean Validation;
-	REST endpoints за създаване и извличане;
-	service и repository слой;
-	единен отговор при грешка;
-	обработка на 400, 404 и 500.

### Нов бизнес контекст

Към съществуващото приложение да се добавят следните правила:
-	служител може да започне обработването само на заявка със статус SUBMITTED;
-	при започване на обработването статусът става PROCESSING;
-	заявка може да бъде отхвърлена само по време на обработването;
-	при отхвърляне трябва да бъде посочена причина;
-	при отхвърляне статусът на заявката става REJECTED;
-	недопустима операция спрямо текущия статус трябва да връща 409.

Фирмата трябва да предостави оферта до три работни дни след подаването на заявката. Самата оферта ще бъде реализирана на следващ етап.

### Анализ на жизнения цикъл

Да се подаде:

```
Analyze the following business requirements together with the current codebase.
Requirements:
•	Employee could start request processing if its status is SUBMITTED 
•	When processing starts, the status changes to PROCESSING 
•	Company have to prepare offer up to 3 business days after request submission 
•	Request could be rejected during processing with rejection reason cited 
•	Offer is created for processed and approved requests 
•	After offer is created, the request status changes to OFFERED 
•	Forbidden operation depending on request status returns 409 Conflict. 
Propose:
•	The required changes to the ServiceRequest lifecycle 
•	The necessary domain operations 
•	Whether Offer should be a separate entity, value object, or part of ServiceRequest 
•	The relationship between ServiceRequest and Offer 
•	The responsibilities of the entity, service, and controllers 
•	The REST operations required for the office workflow 
•	How invalid lifecycle operations should be represented. 
Distinguish current requirements from functionality that should remain outside the scope at this moment.
Do not generate or modify code.

```

След това да се оцени дали приложението:
-	въвежда бъдеща функционалност твърде рано;
-	поставя lifecycle проверки на подходящото място;
-	смесва входна валидация и бизнес конфликт;
-	предлага излишни CRUD операции за оферта.

*Ограничаване на scope*

След анализа да се вземе решение в настоящото упражнение да не се реализира офертата. Да се подаде:

```
Please reevaluate the current scope without introducing Offer yet.
The purpose of current modification is to implement the first request-processing operations and lifecycle rules:
•	start processing only from SUBMITTED 
•	reject only from PROCESSING 
•	store a rejection reason 
•	return 409 Conflict for invalid lifecycle operations 
•	keep lifecycle checks inside ServiceRequest domain methods 
•	let the service load, invoke domain behavior, and persist transactionally. 
The offer itself will be introduced in a later stage.
Propose the required domain methods, service methods, REST endpoints, exceptions, and transaction boundaries.
Do not generate or modify the code yet.

```

*Разграничение между 400, 404 и 409*

Да се разгледат следните състояния:
-	празна причина за отказ;
-	несъществуващ requestId;
-	опит за startProcessing()  върху заявка със статус, различен SUBMITTED;
-	опит за reject() върху заявка, която не е PROCESSING.

Да се определи подходящият HTTP статус.

*Реализиране на lifecycle операциите*

Да се подаде:

```
Apply only the approved request-processing lifecycle changes:
• Extend ServiceRequest with a nullable rejectionReason field, startProcessing() only for SUBMITTED, and reject(String reason) only from PROCESSING
• Keep lifecycle transition checks inside ServiceRequest; do not duplicate them in controllers or service methods
• Add service operations startProcessing(Long id) and reject(Long id, String reason)
• Add office endpoints POST /api/office/requests/{id}/start-processing and POST /api/office/requests/{id}/reject
• Introduce a request DTO for rejection with a required non-blank reason
• Introduce RequestLifecycleConflictException and map it to 409 Conflict
• Keep missing request results in 404, invalid rejection payload in 400, and invalid lifecycle transition in 409
• Make modifying service operations transactional. Rely on JPA dirty checking where appropriate rather than adding unnecessary explicit save calls.
Preserve all existing endpoints, DTOs, validation, package organization, and error response format.
Do not add any other changes.
After modifications, build and run the application and report the actual results.

```

*Преглед на капсулацията на ServiceRequest*

След добавянето на startProcessing() и reject() да се провери дали entity-то все още позволява правилата относно жизнения цикъл да бъдат заобиколени. Да се обърне внимание на:
-	анотация @Setter на ниво клас;
-	@Builder;
-	@AllArgsConstructor;
-	директна промяна на статус;
-	директна промяна на submittedAt;
-	директна промяна на rejectReason.

Следващ промпт:

```
Review the encapsulation of ServiceRequest.
The entity currently uses class-level @Setter, @Builder, and @AllArgsConstructors, while lifecycle rules are implemented through startProcessing() and reject(...). The create method in the service also assigns status and submittedAt through the builder.
Propose refactoring that:
•	prevents external modification of id 
•	prevents direct modification of status, submittedAt, and rejectionReason 
•	guarantees that every newly created request starts as SUBMITTED and receives submittedAt from the system 
•	preserves JPA compatibility 
•	keeps DTO mapping and persistence orchestration in the service 
•	avoids unnecessary setters and unrestricted constructors. 
Do not modify the code yet.

```

*Прилагане на модификация с цел капсулиране*

След одобрение:

```
Apply the approved encapsulation refactoring to ServiceRequest:
• Remove class-level @Setter, @Builder, and @AllArgsConstructor
• Keep @Getter
• Use a protected JPA no-arg constructor
• Add a controlled static factory method submit(...)
• The factory must always initialize status to SUBMITTED, submittedAt from value supplied by the service, and rejectionReason to null
• Do not expose public setters for id, status, submittedAt, or rejectionReason
• Keep startProcessing() and reject(String reason) as only lifecycle mutation methods
• Keep DTO mapping and persistence orchestration in the service
• Inject Clock into the service and use LocalDateTime.now(clock) when calling the factory
• Preserve everything else and do not add any other changes.
After refactoring, build and run the application and report the actual results.

```

Задължително да се прегледат променените файлове и imports.

*Финална проверка*

Да се проверят чрез REST заявки следните сценарии:
-	SUBMITTED → PROCESSING;
-	втори опит за стартиране на обработка – 409;
-	PROCESSING → REJECTED с валидна причина;
-	отхвърляне с празна причина – 400;
-	отхвърляне на заявка SUBMITTED – 409;
-	операция върху несъществуващ id – 404.

Да се провери дали:
-	статус не може да бъде зададен от клиент;
-	submittedAt не може да бъде зададен от клиент;
-	причината за отхвърляне не може да бъде променена директно;
-	lifecycle състояние се променя само чрез домейн методи.

### Резултат от упражнението

В края на ЛУ 4 трябва да има:
-	ServiceRequest с контролиран жизнен цикъл;
-	операции startProcessing() и reject(…);
-	причина за отхвърляне;
-	service методи и офис endpoints;
-	RequestLifecycleConflictException;
-	409 за недопустими операции;
-	transactional операции за модификация;
-	капсулиран entity обект без неоторизирани мутатори/builder;
-	контролирано създаване чрез фабричен метод;
-	запазен работещ REST API.
