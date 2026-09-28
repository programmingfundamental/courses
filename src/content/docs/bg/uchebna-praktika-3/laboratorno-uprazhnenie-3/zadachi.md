---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---
### Преглед на текущото решение

Преди работа по новите изисквания да се прегледа текущата реализация и да се установи:

•	къде се извършва валидацията на входните DTOs; 

•	какви Bean Validation ограничения вече съществуват; 

•	използва ли контролерът @Valid; 

•	има ли бизнес проверки в service слоя; 

•	как се обработват изключенията; 

•	какви HTTP status codes и response bodies се връщат при грешка. 

Bean Validation предоставя декларативен начин за задаване на ограничения върху входните данни чрез анотации като @NotBlank, @Email и @Size. В Spring Boot проверката на входен DTO обикновено може да бъде задействана чрез @Valid.
Да се определят и да се предоставят само частите на проекта, необходими за анализ на входните данни, обработката на заявките, бизнес проверките и съществуващата обработка на грешки.

### Изисквания за валидация и обработка на грешки

Да се разгледат следните изисквания:

•	customerEmail е задължителен и трябва да бъде валиден email адрес; 

•	description е задължително и има минимална и максимална дължина; 

•	при ON_SITE се изисква адрес; 

•	при URGENT се изисква обосновка за спешност; 

•	новата заявка започва със статус SUBMITTED; 

•	submittedAt се задава от системата; 

•	липсващ ресурс връща HTTP 404; 

•	невалидни входни данни връщат HTTP 400; 

•	неправилен JSON и невалидна enum стойност връщат HTTP 400; 

•	неочаквана грешка връща HTTP 500, без да излага вътрешното exception message. 

Да се подаде:

```
### Task
Analyse the following validation and error-handling requirements for the current ServiceRequest API.
For each requirement provide:
- classification;
- recommended enforcement location;
- reasoning.
Possible classifications include:
- Bean Validation;
- domain/business validation;
- API exception-mapping concern;
- system-managed value.
If a requirement could reasonably belong to more than one category, identify the ambiguity.
Do not modify the project.

### Context
Requirements:
- customerEmail is required and must be a valid email address;
- description is required and has minimum and maximum length;
- ON_SITE execution requires a non-blank address;
- URGENT priority requires a non-blank urgencyReason;
- a new request starts with SUBMITTED status;
- submittedAt is assigned by the system;
- a missing resource returns HTTP 404;
- invalid input data returns HTTP 400;
- malformed JSON and invalid enum values return HTTP 400;
- an unexpected error returns HTTP 500 without exposing
  the internal exception message.

### Constraints
Do not introduce requirements that are not present in the context.
Do not generate or modify code.
```

### Проверка на предложенията

Полученият резултат да се оцени по въведените в предходните упражнения критерии.

Допълнително да се провери:

•	разгледани ли са всички зададени изисквания; 

•	добавени ли са правила, които не присъстват в контекста; 

•	разграничени ли са ограниченията върху отделно поле от правилата, зависещи от комбинация от данни; 

•	подходящо ли е предложеното място за реализация на всяко правило; 

•	има ли предположения или unsupported claims. 

Да се сравнят например:

- Bean Validation на отделно поле, например:

```java
@NotBlank
@Email
@Size(max = ...)
private String customerEmail;
```
 
- правило, което зависи от комбинация от няколко полета, например:

```
executionMode == ON_SITE → address е задължителен
priority == URGENT → urgencyReason е задължителен
serviceType == INSTALLATION → executionMode трябва да е ON_SITE
```

Да се обсъди защо фактът, че дадено правило произтича от бизнес изискване, не определя автоматично техническия механизъм, чрез който то трябва да бъде реализирано.

### Експеримент с неточно изискване

Да се подаде допълнително изискване:

```
### Additional requirement
Invalid customer input should return HTTP 500 Internal Server Error.

### Task
Analyse this requirement in the context of the previously provided validation and error-handling requirements.
Identify whether it is consistent with the existing requirements and explain the consequences of implementing it.

### Constraint
Do not modify the project.
```

Да се провери дали ИИ асистентът:

•	разпознава противоречието със зададеното изискване невалидните входни данни да връщат HTTP 400; 

•	обяснява последствията от предложената промяна; 

•	приема последната инструкция без критична оценка; 

•	предлага промяна на кода, въпреки че е поискан само анализ. 

Противоречивото изискване не се включва в последващата реализация.


### Анализ на trade-offs

Условните правила могат да бъдат реализирани по различни начини. Изборът между алтернативни решения трябва да отчита техните trade-offs – предимствата, недостатъците и последствията от всеки подход в контекста на конкретния проект.

За правилата:

•	ON_SITE изисква непразен address; 

•	URGENT изисква непразен urgencyReason; 

да се сравнят:

•	проверка в service слоя; 

•	class-level custom Bean Validation constraint върху request DTO. 

Да се подаде:

```
### Task
Compare two approaches for implementing the conditional request rules in the current ServiceRequest API:
1. service-level business validation;
2. a class-level custom Bean Validation constraint on the request DTO.
The rules are:
- ON_SITE requires a non-blank address;
- URGENT requires a non-blank urgencyReason.
Compare the approaches in terms of:
- responsibility;
- reuse;
- error reporting;
- implementation complexity;
- maintainability.

### Constraints
Do not select an approach.
Do not modify the project.
```

Полученото сравнение да се оцени критично. Да се провери дали посочените предимства и недостатъци произтичат от действителните характеристики на подходите и текущия проект, а не от неподкрепени предположения на ИИ.

На базата на анализа да се избере подход за текущия проект и изборът да се аргументира.

Избраното решение не се разглежда като универсално правило за реализация на условна валидация.

### Актуализиране на контекста

След избора на подход да се извърши impact analysis и да се определи кои части от проекта ще бъдат засегнати от реализацията.

Да се прецени:

•	кои от вече предоставените файлове остават релевантни; 

•	кои допълнителни файлове са необходими за избрания подход; 

•	кои части от проекта трябва да останат непроменени. 

Да се добави само необходимият допълнителен контекст.

### Реализиране на одобреното решение

Да се формулира промпт за реализация, като в него се включи избраният подход за условните правила.

Може да се използва следната структура:

```
### Task
Apply the approved validation and error-handling changes to the existing ServiceRequest API.

### Required changes
Add the required field-level Bean Validation constraints:
- customerEmail — required and valid email;
- description — required and with minimum and maximum length.
Implement the approved approach for these conditional rules:
- ON_SITE requires a non-blank address;
- URGENT requires a non-blank urgencyReason.
Preserve the existing behavior in which:
- status is initialized to SUBMITTED;
- submittedAt is assigned by the system.
Ensure that:
- a missing resource returns HTTP 404;
- validation errors return HTTP 400;
- malformed JSON and invalid enum values return HTTP 400;
- unexpected errors return HTTP 500 without exposing the internal exception message.

### Constraints
Keep the existing endpoints unchanged.
Preserve the external request and response structure.
Do not modify the persistence model unless required by the approved validation approach.
Do not add unrelated functionality.
Do not refactor unrelated code.
Modify only the files required for the approved changes.
```

Преди одобряване на действията и промените на Agent да се провери:

•	реализирани ли са всички одобрени промени; 

•	използван ли е избраният подход за условните правила; 

•	спазени ли са зададените constraints; 

•	запазен ли е съществуващият API; 

•	има ли непоискани промени или функционалност; 

•	съответства ли описанието на Agent на действително предложените промени. 

При отклонение да се определи дали причината е неспазено ограничение, неясна инструкция, липсващ релевантен контекст или неправилно решение на ИИ.

Да се формулира follow-up prompt само за необходимата корекция, вместо да се генерира повторно цялото решение.

### Проверка на API поведението

След одобряване на промените приложението да се компилира и стартира.

Чрез Postman да се проверят поне следните сценарии:

•	валидна заявка; 

•	невалиден email адрес; 

•	ON_SITE заявка без адрес; 

•	URGENT заявка без причина за спешност; 

•	невалидна enum стойност или неправилен JSON; 

•	заявка към несъществуващ id. 

За всеки сценарий да се сравнят очакваният и действителният HTTP status и да се прегледа response body.

Да се провери дали error responses съдържат достатъчно информация за възникналия проблем, без да излагат ненужна вътрешна техническа информация.

### Анализ при несъответствие

При несъответствие между очаквания и действителния резултат да се извърши root-cause analysis, преди да се поиска от ИИ асистента корекция.

Действителният резултат, response body, exception и наличната диагностична информация се използват за формулиране на хипотеза за причината. Предложена от ИИ причина не се приема автоматично, а се проверява спрямо действителния код, конфигурацията и наблюдаваното поведение.

След установяване на действителната причина да се извърши само необходимата корекция и съответният сценарий да се изпълни повторно. Целта е да бъде отстранена причината за проблема, а не само наблюдаваният симптом.
