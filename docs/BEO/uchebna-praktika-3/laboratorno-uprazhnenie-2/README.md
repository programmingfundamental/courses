---
layout: default
title: Лабораторно упражнение 2
parent: Учебна практика 3
has_children: true
nav_order: 2
---

# Лабораторно упражнение 2

## Анализ и развитие на домейн модела

### Цел

Да се анализира началният модел на приложението, създаден в предходното упражнение, и да се уточнят основните понятия от предметната област, техните отговорности и отношения.

ИИ първо се използва за анализ и моделиране, без генериране или промяна на кода. След оценка на предложенията се реализират само одобрените промени.


### Начално състояние

Да се използва крайната работеща версия на проекта от ЛУ 1.

Проектът съдържа: ServiceRequest; основните изброими типове; DTO; repository; service; основни REST endpoints.

### Разширен бизнес контекст

Клиентът подава заявка за услуга, като посочва:
-	име;
-	електронна поща;
-	описание;
-	вид на услугата;
-	начин на изпълнение;
-	приоритет.

При услуга на място се посочва адрес, а при спешна заявка – обосновка за спешност.

Системата записва момента на подаване и задава начален статус.

Служител на фирмата може да започне обработването на подадена заявка.

На по-късен етап от обработването фирмата може да изготви оферта, а след нейното приемане да бъде планирано изпълнение на услугата. Тези части от процеса не се реализират в настоящото упражнение.


### Анализ на бизнес изискванията

Да се подаде следния промпт:

```
Analyze the following business requirements together with the existing Spring Boot project.
Requirements:
•	request has client name, email, description, service type, execution type, address if the execution is on site, urgency reason if the request is urgent; 
•	request is in SUBMITTED status on creation; 
•	the customer cannot define initial status, price, deadline to receive offer and internal data; 
•	description should have minimal and maximal length; 
•	when execution is remote there is no need of address; 
•	INSTALLATION and REPAIR could be executed ON SITE; 
•	DIAGNOSTICS and CONSULTATION could be ON SITE/REMOTE; 
•	Priority URGENT expects text processing. 
Request processing requirements:
•	only request with SUBMITTED status could be processed; 
•	when processing starts, the request status switches to PROCESSING; 
•	the company should send offer or cancel the request up to 3 working days from the submitting moment; 
•	working days are defined as Monday to Friday; holidays are not considered; 
•	urgent requests could be submitted from Monday to Friday till 8PM; 
•	urgent request processing starts immediately; 
•	if there is no offer up to the deadline and such offer is not rejected, the status of the request switches to EXPIRED; 
•	only request with status PROCESSING could get an offer and be rejected; 
•	the cancellation requires a reason; 
•	if request is rejected, its status switches to REJECTED; 
•	rejected request could not be restored; the customer should place a new request. 
Identify the necessary domain concepts and explain their responsibilities. Distinguish between entities, value objects, and enums. Assess whether customer information requires a separate entity.
The later offer and service-execution stages may be identified as future domain concepts, but they are outside of the implementation scope at the moment.
Do not generate or modify code.

```

*Оценка на предложения домейн модел*

Да се разгледат предложените понятия. За всяко от тях да се определи:
-	има ли собствена идентичност;
-	има ли самостоятелен жизнен цикъл;
-	представлява ли стойност, принадлежаща на друг обект;
-	представлява ли затворено множество от допустими стойности;
-	необходимо ли е да бъде отделен клас в настоящия размер на проекта.
  
Особено внимание да се обърне на:
-	ServiceRequest;
-	клиентската информация;
-	адреса;
-	описанието;
-	причината за спешност;
-	ServiceType, ExecutionMode, Priority, RequestStatus.

*Анализ на клиентската информация*

Да се прецени дали клиентът трябва да бъде отделно entity. Да се разгледат следните съображения:
-	има ли клиентът собствен идентификатор;
-	има ли самостоятелен жизнен цикъл;
-	управлява ли се клиентът независимо от заявката;
-	необходимо ли е няколко заявки да сочат към един и същи клиентски обект;
-	възможно ли е заявката да пази snapshot на името и имейла при подаването.

*Одобряване на промените*

След анализа да се определят само промените, които са оправдани в настоящия етап на проекта.
Ако се приеме решение клиентската информация да бъде value object, може да се използва следния промпт:

```
Based on the analysis, apply only the following approved changes to the current project:
• Introduce an immutable CustomerInfo value object containing customerName and customerEmail. Persist it as part of ServiceRequest using JPA @Embeddable and @Embedded. CustomerInfo is not a separate entity and must not have its own repository.
• Keep address, description, and urgencyReason as String fields. Do not introduce separate value-object classes for them.
• Update the request and response DTO mappings to work with CustomerInfo while preserving the existing external API field names customerName and customerEmail.
• Preserve the existing REST endpoints and application behavior.
Do not add Offer, ServiceExecution, pricing, deadline calculation, new status transitions, domain-policy services, or any other future functionality.
Update all affected mappings, imports, constructors, and persistence annotations. Then build and run the application and report the actual result.

```

*Проверка на организацията на домейн типовете*

След промяната да се провери:
-	в кой пакет е поставен CustomerInfo;
-	съответства ли името на пакета на ролята на класа;
-	смесени ли са entities, value objects и enums без ясна причина;
-	създадени ли са твърде много технически пакети;
-	има ли празни пакети, останали след преместванията.

Ако организацията на пакетите не изглежда семантично последователна, да се поиска от ИИ асистента да аргументира направения избор, без да променя кода.

*Уточняване на пакетната организация*

Ако е необходимо, да се поиска препоръка за проста и семантично последователна структура, без създаване на пакет за всеки технически детайл.

Може да се използва следния промпт:

```
The goal is not to introduce a dedicated package for every technical concept but the structure to remain simple and semantically consistent. Which package organization would you recommend for this project if I want to distinguish between entities from other domain types without unnecessary technical packages?
Do not modify the code.

```

Да се оцени полученото предложение и да се избере структура, която:
-	ясно различава entity от останалите домейн типове;
-	не създава ненужна дълбочина;
-	остава подходяща за текущия размер на проекта.


*Финална проверка*

След реализиране на одобрените промени:
-	да се прегледат всички променени файлове;
-	да се проверят imports и package declarations;
-	да се провери дали няма останали празни пакети;
-	да се компилира проектът;
-	да се стартира приложението.

Работата на REST API да се провери чрез Postman.

Да се изпълнят заявки към реализираните endpoints за:
-	създаване на нова заявка;
-	извличане на заявка по идентификатор;
-	извличане на заявки по електронна поща на клиент;
-	извличане на заявките от служител на офиса.

При създаване на заявка да се провери:
-	кои полета се изпращат от клиента;
-	кои стойности се задават от системата;
-	как изглежда полученият JSON response;
-	запазени ли са имената customerName и customerEmail във външния API след въвеждането на CustomerInfo.

### Резултат от упражнението

В края на упражнението трябва да бъде получен:
-	аргументиран концептуален модел на текущата предметна област;
-	разграничение между entity, value object и enum;
-	решение кои понятия трябва да бъдат реализирани сега и кои да останат за бъдещ етап;
-	работещ ServiceRequest модел с клиентска информация, организирана според избраното домейн решение;
-	запазен външен REST API;
-	семантично последователна пакетна структура.


