---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---
### Разширен бизнес контекст

Клиентът подава заявка за услуга, като посочва:

•	име; 

•	електронна поща; 

•	описание; 

•	вид на услугата; 

•	начин на изпълнение; 

•	приоритет. 

При услуга на място се посочва адрес, а при спешна заявка – обосновка за спешност.

Системата записва момента на подаване и задава начален статус SUBMITTED. Клиентът не задава системно управляваните стойности.

Служител на фирмата може да започне обработването на заявка със статус SUBMITTED. При започване на обработката статусът се променя на PROCESSING.

На по-късен етап фирмата може да изготви оферта, а след нейното приемане да бъде планирано изпълнението на услугата. 
Тези части от процеса са извън обхвата на настоящото упражнение.

Преди работа с ИИ да се определи:

•	коя информация представлява функционален и домейн контекст; 

•	кои части от съществуващия проект са релевантни за текущата задача; 

•	коя известна информация за бъдещото развитие на системата не е необходима за текущия анализ. 

### Entity, value object и enum

Entity е домейн обект със собствена идентичност, която позволява той да бъде проследяван независимо от промяната на неговите свойства.

Value object е обект без собствена идентичност и самостоятелен жизнен цикъл, който представя стойност или съвкупност от свързани стойности и принадлежи към друг обект от домейн модела.

Enum е подходящ при затворено множество от предварително определени допустими стойности.

### Анализ на бизнес изискванията

Преди подаване на заявката да се определи кои съществуващи файлове са необходими за анализа на текущия домейн модел.
Да се предоставят:

•	ServiceRequest; 

•	съществуващите request и response DTOs за заявката; 

•	ServiceType; 

•	ExecutionMode; 

•	Priority; 

•	RequestStatus. 

Ако имената на генерираните в предходното упражнение DTOs са различни, да се използват действителните файлове от проекта.

Да не се предоставят останалите файлове само защото са налични в проекта.

Да се подаде следният промпт:


```
### Task
Analyse the following business requirements in the context of the existing application.
Identify the domain concepts relevant to the current task and explain their responsibilities.
Distinguish between entities, value objects and enums.
Assess whether customer information should remain directly in ServiceRequest or should be represented by a separate domain concept.
Do not modify the project.

### Context
A service request contains:
- customer name and email;
- description;
- service type;
- execution mode;
- priority;
- address when execution is ON_SITE;
- urgency reason when priority is URGENT.
A new request has status SUBMITTED. System-managed values are not provided by the customer.
A request in SUBMITTED status can start processing. When processing starts, its status becomes PROCESSING.
Offer creation and service execution are future stages and are outside the scope of the current task.

### Constraints
Do not redesign unrelated parts of the application.
Do not analyse or implement the internal structure of future Offer or ServiceExecution concepts.
Do not generate or modify code.
First provide only the domain analysis.

### Expected output
For the concepts relevant to the current task:
- identify their responsibilities;
- classify them as entities, value objects or enums where appropriate;
- explain whether customer information requires a separate domain concept;
- justify the proposed domain model.
```

### Оценка на предложения домейн модел

Полученият резултат да се оцени по критериите за анализ на ИИ резултат, въведени в предходното упражнение.

За предложените домейн понятия допълнително да се прецени:

•	има ли обектът собствена идентичност и самостоятелен жизнен цикъл; 

•	представлява ли стойност, принадлежаща към друг домейн обект; 

•	представлява ли затворено множество от допустими стойности; 

•	необходимо ли е отделянето му в самостоятелен клас в текущия обхват; 

•	произтича ли предложението от бизнес изискванията или е допълнително решение на ИИ. 

Особено внимание да се обърне на клиентската информация. Да се прецени дали клиентът се управлява независимо от заявката и има собствена идентичност и жизнен цикъл, или името и електронната поща представляват информация, принадлежаща към конкретната заявка.

Да не се приема автоматично класификацията, предложена от ИИ. Решението трябва да бъде аргументирано спрямо бизнес изискванията, съществуващия проект и определения scope.

### Одобряване на промените

След анализа да се определи кои предложения са оправдани в текущия обхват.

За целите на следващата част от упражнението да се приеме следното проектно решение:

•	клиентската информация се представя чрез immutable value object CustomerInfo, съдържащ customerName и customerEmail; 

•	CustomerInfo принадлежи към ServiceRequest и не представлява самостоятелно entity; 

•	address, description и urgencyReason остават полета от тип String; 

•	външният REST API запазва полетата customerName и customerEmail; 

•	бъдещите Offer и ServiceExecution не се реализират. 

Да се сравни това решение с предложението на ИИ и да се установи кои негови предложения се приемат и кои се отхвърлят.

### Impact analysis и актуализиране на контекста

Преди промяна на кода да се извърши impact analysis.

Да се определи:

•	кои съществуващи класове ще бъдат засегнати от въвеждането на CustomerInfo; 

•	как промяната влияе върху persistence слоя; 

•	кои DTO mappings трябва да бъдат адаптирани; 

•	кои от вече предоставените файлове остават релевантни; 

•	кои допълнителни файлове са необходими за безопасната реализация на промяната. 

Да се добавят само установените като релевантни файлове.

Контекстът, необходим за анализ на домейн модела, не е задължително да бъде достатъчен за неговата реализация. Той се актуализира според конкретната задача и очакваното въздействие на промяната.


### Реализиране на одобрената промяна

След актуализиране на контекста да се подаде:

```
### Task
Implement the approved change in the existing project.

### Approved change
Introduce an immutable CustomerInfo value object containing customerName and customerEmail.
Persist CustomerInfo as part of ServiceRequest using JPA @Embeddable and @Embedded.
CustomerInfo is not a separate entity and must not have its own repository.
Update the affected DTO mappings while preserving the existing external API fields customerName and customerEmail.
Keep address, description and urgencyReason as String fields.

### Constraints
Preserve the existing REST endpoints and current application behavior.
Do not add Offer, ServiceExecution or other future functionality.
Do not redesign unrelated parts of the application.
Modify only the files affected by the approved change.
Keep affected mappings, imports and persistence annotations consistent.
```

Agent може да предложи действия и промени върху предоставените части от проекта. Те не се приемат автоматично.

### Преглед на предложените промени

Преди одобряване на предложените от Agent промени да се провери:

•	кои файлове ще бъдат създадени или променени; 

•	съответстват ли те на извършения impact analysis; 

•	ограничени ли са промените до определения scope; 

•	реализирано ли е точно одобреното проектно решение; 

•	добавени ли са непоискани класове, зависимости или функционалност; 

•	запазен ли е външният REST API; 

•	има ли промени в несвързани части от приложението. 

При несъответствие предложението не се приема само защото Agent го е генерирал. Да се установи причината и да се формулира конкретно уточнение или корекция.

След проверката да се одобрят само необходимите промени.

### Финална проверка

След прилагане на одобрените промени:

•	да се прегледат действително променените файлове, imports и package declarations; 

•	да се провери дали CustomerInfo е поставен на подходящо място в структурата на проекта; 

•	да се компилира и стартира приложението; 

•	чрез Postman да се създаде нова заявка и да се извлече създадената заявка; 

•	да се провери дали външният JSON продължава да съдържа customerName и customerEmail; 

•	да се провери дали стойностите се записват и извличат коректно след въвеждането на CustomerInfo. 

Полученият резултат се приема само след проверка на действителното поведение на приложението.

