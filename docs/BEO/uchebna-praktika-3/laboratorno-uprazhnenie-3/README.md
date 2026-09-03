---
layout: default
title: Лабораторно упражнение 3
parent: Учебна практика 3
has_children: true
nav_order: 3
---

# Лабораторно упражнение 3

## Валидация на входни данни и обработка на грешки

### Цел

Да се анализира и подобри договорът на REST API чрез подходящо разделяне на:
-	валидация на входните DTO;
-	условни бизнес правила;
-	persistence ограничения;
-	обработка и преобразуване на грешки към HTTP response.

ИИ се използва първо за анализ на съществуващата реализация и сравнение на възможни решения. Код се променя едва след избор на конкретен подход.

### Начално състояние

Да се използва крайната работеща версия от ЛУ 2. Приложението съдържа:
-	ServiceRequest;
-	CustomerInfo;
-	request/response DTO;
-	repository;
-	service;
-	REST controllers;
-	глобална обработка на грешки;
-	базова Bean Validation.

### Преглед на текущия REST API

Преди да се добавя нова функционалност, да се анализира съществуващото решение. Да се подаде:

```
Review the current implementation of the ServiceRequest REST API.
Analyze:
•	the responsibilities of the controllers, service, and repository; 
•	the request and response DTO; 
•	the entity-to-DTO and DTO-to-entity mapping; 
•	the HTTP methods and response status codes; 
•	whether persistence entities are exposed through the API; 
•	whether any layer is bypassed; 
•	whether the current mapping approach is appropriate for the size of the project. 
Identify justified improvements and distinguish them from premature abstractions. Do not modify the code and do not add new functionality.

```

След анализа да се провери:
-	има ли ясно разделение между слоевете;
-	използват ли се DTO или entity обектите се излагат директно през API;
-	къде се извършва преобразуването между DTO и entity;
-	оправдан ли е отделен mapper при текущия размер на проекта;
-	коректни ли са HTTP методите и status codes;
-	кои предложени промени са действително необходими.

### Валидационни и error-handling изисквания

Да се разгледат следните правила.

За входните данни:
-	името на клиента е задължително и има максимална дължина;
-	имейл адресът е задължителен, трябва да бъде валиден и има максимална дължина;
-	описанието е задължително и има минимална и максимална дължина;
-	при ON_SITE се изисква адрес;
-	при REMOTE адрес не се изисква;
-	при URGENT се изисква обосновка;
-	INSTALLATION и REPAIR заявки могат да се изпълняват само ON_SITE;
-	DIAGNOSTICS и CONSULTATION могат да бъдат ON_SITE или REMOTE;
-	нова заявка винаги започва със статус SUBMITTED;
-	submittedAt се задава от системата.

При грешки:
-	липсващ ресурс – 404;
-	невалидни входни данни – 400;
-	неправилен JSON, невалидна enum стойност или неправилен тип на path/query параметър – 400;
-	неочаквана грешка – 500, без излагане на вътрешното съобщения за грешка.

Промпт:

```
Analyze the following validation and error-handling requirements for the current ServiceRequest API.
Classify each rule as:
•	Bean Validation; 
•	domain/business validation; 
•	persistence constraint; 
•	API exception-mapping concern. 
Recommend where each rule should be enforced and explain whether the current GlobalExceptionHandler should be changed.
Do not modify the code yet.

```

*Проверка на предложенията*

Да се разгледа класификацията на ИИ и да се оцени дали всяко правило е поставено на подходящото място. Особено внимание да се обърне на разликата между:

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

Входна валидация ли е всяко правило, свързано с бизнес изискване?

*Експеримент с неточно изискване*

Да се промени временно едно от error-handling изискванията така, че очевидна клиентска грешка да бъде описана с код 500. Да се поиска от ИИ асистента анализ, без промяна на кода. Да се наблюдава дали асистентът:
-	ще оспори изискването;
-	ще поиска уточнение;
-	или ще го приеме и ще предложи реализация.

*Избор на подход за условните правила*

Промпт:

```
Now, compare the two approaches for the conditional request rules:
•	service-level business validation 
•	a class-level custom Bean Validation constraint on ServiceRequestCreateRequest. 
The rules are listed in a previous prompt. Recommend the more appropriate approach for the current project and justify the choice in terms of responsibility, reuse, error reporting, complexity, and maintainability.
Do not modify the code yet.

```

След това да се оцени предложението според:
-	отговорност;
-	възможност за повторна употреба;
-	начин за връщане на грешките;
-	сложност;
-	бъдещи входове към системата.

Избраното решение е подходящо за текущия контекст и не трябва да се разглежда като универсално правило за всички бизнес проверки да се реализират чрез DTO валидация.

*Реализиране само на одобрените промени*

Да се подаде:

```
Apply only the approved changes.
• Add the missing field-level Bean Validation constraints to ServiceRequestCreateRequest — customerName (required and maximum length), customerEmail (required, valid email, and maximum length), description (required, minimum length, and maximum length).
• Implement a class-level custom Bean Validation constraint on ServiceRequestCreateRequest that enforces ON_SITE requires a non-blank address, URGENT requires a non-blank urgencyReason, INSTALLATION and REPAIR are allowed only with ON_SITE, DIAGNOSTICS and CONSULTATION may be ON_SITE/REMOTE.
• Preserve the existing rule that status is set to SUBMITTED and submittedAt is assigned by the system.
• Update GlobalExceptionHandler so that: ResourceNotFoundException returns 404; Bean Validation errors return 400; malformed JSON and invalid enum values return 400; invalid path or query parameter types return 400; unexpected errors return 500 with a fixed safe message and do not expose the original error message.
• Keep the existing endpoints, DTO shape, manual mapping approach, persistence model, and package organization intact.
Do not add any other changes.
After the changes, build and run the application and report the actual result.

```

*Преглед на генерираните промени*

Да се проверят:
-	добавените Bean Validation анотации;
-	реализацията на class-level constraints;
-	мястото на constraint анотацията и валидиращия клас;
-	дали контролерът използва анотацията @Valid;
-	дали условната валидация не е дублирана ненужно на няколко места;
-	промените в GlobalExceptionHandler;
-	дали 500 handler връща вече безопасно съобщение;
-	дали не са направени промени извън изрично одобрения scope.

*Проверка на API поведението*

След стартиране на приложението да се проверят следните сценарии:
-	валидна ON_SITE заявка;
-	валидна REMOTE заявка;
-	ON_SITE без адрес;
-	URGENT без причина за спешност;
-	INSTALLATION с REMOTE;
-	невалиден имейл адрес;
-	твърде кратко/дълго описание;
-	невалидна enum стойност;
-	неправилен JSON;
-	заявка към несъществуващ id;
-	неправилен тип на path/query параметър.

Да се сравнят очакваните и действително върнатите HTTP status codes и съобщения за грешки.

### Резултат от упражнението

В края на ЛУ 3 трябва да има:
-	ясно разграничение между Bean Validation, cross-field validation, persistence constraints и API exception mapping;
-	валидиран ServiceRequestCreateRequest;
-	реализирани условни проверки;
-	специализирано преобразуване на клиентските грешки към 400;
-	404 за липсващи ресурси;
-	безопасен 500 отговор за неочаквани грешки;
-	запазени endpoints, DTO договор и persistence модел;
-	проверена работоспособност чрез REST заявки.

