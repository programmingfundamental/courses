---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---
### Нов бизнес контекст

Към съществуващото приложение се добавят следните изисквания:

•	служител може да започне обработването само на заявка със статус SUBMITTED; 

•	при започване на обработването статусът се променя на PROCESSING; 

•	само заявка със статус PROCESSING може да получи оферта или да бъде отхвърлена; 

•	при отхвърляне трябва да бъде посочена причина; 

•	при отхвърляне статусът се променя на REJECTED; 

•	отхвърлена заявка не може да бъде възстановена; 

•	недопустима операция спрямо текущия статус води до HTTP 409 Conflict; 

•	при създаване на оферта заявката преминава от PROCESSING в OFFERED; 

•	ако заявката не бъде обработена в определения срок, тя може да премине в EXPIRED. 

Част от изискванията описват бъдещи стъпки от жизнения цикъл. Не е необходимо всички идентифицирани изисквания да бъдат реализирани в една итерация.

### Анализ на жизнения цикъл

Преди подаване на промпта да се определи какъв контекст от текущия проект е необходим за анализ на:

•	текущото състояние на ServiceRequest; 

•	статусите и lifecycle правилата; 

•	service операциите; 

•	REST API; 

•	съществуващата обработка на грешки. 

Да се предоставят само релевантните файлове. В зависимост от текущата реализация това могат да бъдат ServiceRequest, RequestStatus, съответните service и controller класове и класовете, участващи в обработката на грешки.

Да не се добавя автоматично целият проект.

От тази стъпка до приключване на упражнението се използва една и съща Agent сесия, така че следващите промптове да могат да използват натрупания conversational context.

Да се подаде:

```
### Task
Analyse the following new business requirements in the context of the existing application.
Identify:
- the lifecycle transitions implied by the requirements;
- the domain, service and REST operations required;
- the appropriate distribution of responsibilities;
- the required error handling.
Distinguish between functionality needed for the next implementation step and functionality that can be deferred.

### Context
- only a SUBMITTED request can start processing;
- starting processing changes the status to PROCESSING;
- only a PROCESSING request can receive an offer or be rejected;
- rejection requires a reason;
- rejection changes the status to REJECTED;
- a REJECTED request cannot be restored;
- an operation not allowed for the current status returns HTTP 409 Conflict;
- creating an offer changes PROCESSING to OFFERED;
- a request that is not processed within the applicable deadline may become EXPIRED.

### Constraints
Do not generate or modify code.
Do not redesign unrelated parts of the application.
Do not assume that all identified lifecycle functionality must be implemented in the same iteration.
If the requirements or provided project context are insufficient for a concrete decision, identify the missing information instead of making an unsupported assumption.
```

Ако Agent предложи създаване или промяна на файлове въпреки ограничението Do not generate or modify code, действията не се одобряват и поведението се отчита при оценката на резултата.

### Оценка на първоначалното предложение

Полученият анализ да се оцени по въведените в предходните упражнения критерии.

Допълнително да се провери:

•	идентифицирани ли са правилно lifecycle преходите; 

•	разграничена ли е недопустима lifecycle операция от невалидни входни данни и липсващ ресурс; 

•	правилно ли са разпределени отговорностите между domain object, service и controller; 

•	предлага ли ИИ реализация на Offer или EXPIRED още в непосредствената итерация; 

•	предлага ли други класове, endpoints или операции, които не са необходими; 

•	разпознава ли липсваща информация вместо да прави unsupported assumptions; 

•	какво предлага да бъде реализирано сега и какво да бъде отложено. 

Предложението на ИИ не определя автоматично обхвата на реализацията. Разработчикът определя кои от идентифицираните промени са необходими за текущата итерация.

### Итеративно уточняване на scope

За текущата итерация се взема решение да не се реализират Offer, deadline calculation и преминаване към EXPIRED.
Ще бъдат реализирани само:

•	SUBMITTED → PROCESSING; 

•	PROCESSING → REJECTED; 

•	задължителна причина за отхвърляне; 

•	HTTP 409 Conflict при недопустима lifecycle операция. 

Това представлява scope refinement – първоначално анализираният проблем се ограничава до функционалността, необходима за текущата итерация.

В същата Agent сесия да се подаде следният follow-up prompt:

```
## Task
Refine the proposed solution for the current implementation step based on the previous analysis.
Do not generate or modify code yet.

## Scope
For this iteration, implement only the following lifecycle operations:
- start processing only from SUBMITTED;
- reject a request only from PROCESSING;
- require a rejection reason;
- return 409 Conflict for invalid lifecycle operations.

Do not introduce:
- Offer;
- deadline calculation;
- expiration handling.

## Design Requirements
- Lifecycle transition rules should be enforced by ServiceRequest domain behavior.
- The service should load the request, invoke the corresponding domain operation, and persist the change transactionally.

## Expected Output
Revise the proposed solution for this reduced scope and describe:
- domain operations;
- service operations;
- REST endpoints;
- exceptions and their mapping to HTTP responses;
- transaction boundaries.

Keep the proposal limited to the current implementation step.
```

Този промпт не повтаря целия първоначален контекст. Той използва натрупания conversational context и уточнява само променения обхват.

### Оценка на уточненото предложение

Да се сравни новото предложение с първоначалния анализ и да се провери: 

•	премахнати ли са от текущия scope Offer, deadline calculation и expiration handling; 

•	запазени ли са изискванията, които остават валидни; 

•	ограничени ли са предложените промени до необходимите lifecycle операции; 

•	правилно ли са разпределени отговорностите; 

•	разграничени ли са HTTP 400, 404 и 409; 

•	продължава ли ИИ да използва решения или assumptions, които вече не са приложими; 

•	добавена ли е непоискана функционалност. 

Последователността

*първоначално предложение → оценка → follow-up prompt → уточнено предложение*

представлява итеративно уточняване на решението.

### Разграничаване на грешките

Преди реализацията да се определи очакваното поведение в следните ситуации:

•	празна причина за отхвърляне → HTTP 400 Bad Request; 

•	заявка с несъществуващ идентификатор → HTTP 404 Not Found; 

•	започване на обработване на заявка, която не е SUBMITTED → HTTP 409 Conflict; 

•	отхвърляне на заявка, която не е PROCESSING → HTTP 409 Conflict. 

Да се обоснове разликата между:

•	невалидни входни данни; 

•	липсващ ресурс; 

•	валидна заявка за операция, която не е допустима спрямо текущото състояние на ресурса. 

### Актуализиране на контекста

След одобряване на уточненото решение да се извърши impact analysis.

Да се определят:

•	файловете, които трябва да бъдат променени; 

•	допълнителният проектен контекст, необходим за реализацията; 

•	частите на приложението, които трябва да останат непроменени. 

Да се добавят само допълнителните файлове, необходими за реализацията.

Натрупаният conversational context се запазва, но това не отменя необходимостта от предоставяне на релевантна информация от действителното текущо състояние на проекта.


### Реализиране на текущата итерация

В същата Agent сесия да се подаде:

```
### Task
Implement the approved changes for the current lifecycle iteration.
Add support for:
- starting processing of a SUBMITTED request;
- rejecting a PROCESSING request;
- requiring a rejection reason;
- returning HTTP 409 Conflict for invalid lifecycle operations.
Use the responsibilities and REST operations approved in the previous analysis.

### Constraints
Keep lifecycle transition rules in ServiceRequest domain behavior.
The service should load the request, invoke the corresponding domain operation, and persist the change transactionally.
Preserve the existing behavior for HTTP 400 and HTTP 404.
Do not introduce Offer, deadline calculation, or expiration handling.
Do not add unrelated functionality.
Do not refactor unrelated code.
Modify only the files required for the approved changes.
```

Agent може да предложи необходимите действия и промени по проекта. Те се преглеждат преди одобряване.

Да се провери:

•	съответстват ли действително засегнатите файлове на извършения impact analysis; 

•	ограничени ли са промените до одобрения scope;

•	реализирани ли са точно договорените lifecycle операции; 

•	запазени ли са валидните решения от предходните итерации; 

•	добавена ли е непоискана функционалност; 

•	съответства ли обобщението на Agent на действително предложените промени. 

При проблем да не се генерира решението отначало. Да се формулира кратък follow-up prompt, който описва конкретното несъответствие и необходимата корекция.

### Оценка на домейн модела след реализацията

След реализиране на промените да се прегледа текущото състояние на ServiceRequest и да се оцени дали lifecycle правилата са защитени от заобикаляне.

Да се провери:

•	по какъв начин може да бъде променян status; 

•	може ли rejectionReason да бъде зададен извън операцията за отхвърляне; 

•	кой определя началния статус и submittedAt; 

•	могат ли lifecycle правилата да бъдат заобиколени чрез setters, constructors, builder или други публично достъпни операции; 

•	преминават ли промените в състоянието през дефинираните domain операции. 

Ако бъде установен конкретен проблем, в същата Agent сесия да се формулира follow-up prompt, който описва проблема и изисква минималната необходима корекция.

Ако проблем не бъде установен, не се извършва допълнителен refactoring само с цел да бъде направена още една итерация.

### Проверка на поведението

След приключване на промените приложението да се компилира и стартира.

Чрез REST API да се проверят:

•	започване на обработване на SUBMITTED заявка → PROCESSING; 

•	повторно започване на обработването → HTTP 409 Conflict; 

•	отхвърляне на PROCESSING заявка с валидна причина → REJECTED; 

•	отхвърляне с празна причина → HTTP 400 Bad Request; 

•	отхвърляне на заявка, която не е PROCESSING → HTTP 409 Conflict; 

•	операция върху несъществуваща заявка → HTTP 404 Not Found. 

Проверката представлява verification на действителното поведение спрямо одобрения scope на текущата итерация.
При несъответствие се прилага използваният в предходното упражнение процес:

*наблюдение → диагностична информация → хипотеза за причината → проверка на хипотезата → минимална корекция → повторна проверка*

Следващият follow-up prompt към ИИ се формулира след анализ на наличната диагностична информация. Предложена от ИИ причина не се приема автоматично за действителната причина.

