---
layout: default
title: Лабораторно упражнение 5
parent: Учебна практика 3
has_children: true
nav_order: 5
---

# Лабораторно упражнение 5

## Разширяване на бизнес логиката

### Цел

Да се разшири процесът по обработка на заявките чрез въвеждане на оферта, ценова разбивка и нови времеви и lifecycle правила.

Да се анализира кои нови понятия изискват отделни домейн типове и кои архитектурни абстракции все още биха били преждевременни.

### Начално състояние

Да се използва крайната работеща версия от ЛУ 4. Приложението вече поддържа:
-	преход SUBMITTED към PROCESSING;
-	преход PROCESSING към REJECTED;
-	409 при недопустими операции, свързани с жизнения цикъл;
-	капсулиран ServiceRequest;
-	transactional операции за модификация.

### Нов бизнес контекст

След започване на обработването служителят може да създаде оферта за заявката.

Офертата:
-	принадлежи на точно една заявка;
-	може да съществува най-много една оферта за дадена заявка;
-	се създава само при заявка със статус PROCESSING;
-	съдържа описание на предложената работа;
-	съдържа момент на създаване и срок на валидност;
-	съдържа приблизителна продължителност;
-	съдържа ценова разбивка;
-	няма собствен статус;
-	след създаването й заявката преминава в състояние OFFERED.

Валидност:
-	нормална заявка – 3 работни дни след създаване;
-	спешна заявка – 30 минути след създаване;
-	оферта за спешна заявка трябва да бъде създадена до 1 час след подаването.

Цената се формира от:
-	базова цена според тип на услугата;
-	такса според начина на изпълнение;
-	надбавка според приоритета;
-	допълнителна такса извън работно време;
-	отстъпка за лоялен клиент.

В настоящата версия допълнителната такса извън работно време и отстъпката за лоялен клиент са със стойност 0. Параметрите subtotal, loyaltyDiscount и finalPrice се изчисляват от системата и не се подават отвън.

*Първи анализ без реализация*

Да се подаде следния промпт:

```
The business process now continues with creating an offer for given request. 
Business requirements:
• An offer can only be created for a request in status PROCESSING
• Each request can have at most one offer
• An offer contains the total proposed price and a validity period
• The total price depends on service type, execution mode, and whether the request is urgent
• The pricing rules are expected to evolve in future iterations
• Customer acceptance, scheduling, execution and expiration will be implemented later.
Analyze the current project together with these new requirements.
Propose:
• The required domain model changes
• Whether Offer should be a separate entity, value object or something else
• The relationship between Offer and ServiceRequest
• The minimal fields required for Offer in the current iteration
• Where pricing logic should be located
• Whether the current architecture is still enough or whether any new abstractions are justified
• Which functionality belongs in these changes and which should remain out of scope.
Do not generate or modify code.

```

След това да се оцени:
-	оправдано ли е офертата да е отделно entity;
-	нужна ли е двупосочна връзка;
-	оправдан ли е отделен компонент за ценообразуване още сега;
-	кои бъдещи концепции ИИ асистента се опитва да въведе преждевременно.

*Уточняване на модела*

Промпт: 

```
Refine the proposal based on the following decisions:
• Offer is a separate entity,
• Use a unidirectional relationship from Offer to ServiceRequest,
• ServiceRequest must not contain Offer field,
• The servce-request foreign key in Offer must be unique,
• Offer contains only id, serviceRequest, totalPrice, createdAt, and validUntil,
• The application uses a single currency so do not add currency or Money
• Creating the offer changes the request status to OFFERED
• An offer can be created only for request in PROCESSING state
• Offer creation and the request status change must be performed in one transaction
• Use a separate OfferService and do not add generic CRUD operations.

For this modification, do not introduce OfferPricingPolicy or OfferPriceCalculator, a rule engine, or another pricing abstraction.

First pricing version should be implemented directly and clearly, using the current fixed rules: base price according to ServiceType; fixed amount according to ExecutionMode; fixed surcharge when Priority is URGENT.

The purpose of this iteration is to obtain a simple working implementation whose growing conditional logic can be analyzed in the next iteration.
Considering this prompt, propose:
• the exact responsibilities of Offer and OfferService
• the required DTOs and endpoints
• where the fixed prices should be stored
• the transaction flow
• the required domain method on ServiceRequest
• the expected 400, 404 and 409 cases.
Do not generate or modify code.

```

Да се анализират следните моменти:
-	отговорностите на офертата;
-	отговорностите на съответния service;
-	endpoints;
-	transaction boundary;
-	400, 404, 409;
-	защо markedOffered принадлежи на ServiceRequest.

*Уточняване на окончателния модел*

Следващият промпт вече трябва да въвежда пълния одобрен модел:
-	оферта;
-	PriceBreakdown;
-	изброими типове със свойства;
-	валидност;
-	изчисляване на цената;
-	без статус на оферта;
-	без използване на шаблони за проектиране;
-	без автоматизация на процесите по приемане/планиране/изтичане на оферта.

*Анализ на фиксирани домейн характеристики*

Да се разгледа предложението фиксираните характеристики да бъдат върху енумерациите:
-	в тип на услуга – базова цена, стандартна продължителност, поддържа ли отдалечено изпълнение;
-	в режим на изпълнение – добавка за изпълнение;
-	в приоритета – надбавка.

Да се оцени кога подобни стойности са разумни като свойства на енумерация и кога вече трябва да бъдат конфигурируеми данни.

В текущият проект този вариант е разумен, защото стойностите са фиксирани домейн характеристики, а не примерно администраторски настройки.

Експеримент: какво става при липса на конкретни стойности. Да се провери кои стойности са били избрани от ИИ асистента и дали са следствие от бизнес изискванията или са измислени.

*Реализация*

Да се подаде промпт, съдържащ одобрените за реализация промени. Промптът трябва:
-	да описва само одобрените промени;
-	да задава ограниченията на текущата итерация;
-	да запазва съществуващото API и организация на проекта, освен когато промяната го изисква;
-	да забранява добавянето на непоискани архитектурни абстракции;
-	да изисква компилиране и стартиране след промяната.

*Преглед на генерирания код*

Да се проверят:
-	правилна ли е JPA връзката;
-	има ли unique constraint;
-	дали PriceBreakdown наистина изчислява вътрешно получените стойности;
-	могат ли параметрите subtotal и finalPrice да бъдат подадени отвън;
-	правилна ли е проверката за валидност на офертата;
-	дали има излишен статус на офертата;
-	дали DTO обектите съдържат само необходимата за целта информация;
-	дали логиката по ценообразуването е останала директна и ясна;
-	дали са добавени абстракции извън scope;
-	качеството на именуването, особено на методите.


*Финална проверка*

Чрез REST заявки да се проверят:
-	валидно създаване на оферта;
-	второ извикване на метод offer за същата заявка – 409;
-	оферта за заявка в състояние SUBMITTED – 409;
-	оферта за несъществуваща заявка – 409;
-	празна стойност за описание на необходимата работа – 400;
-	спешна оферта след едночасовия срок – 409;
-	статусът става OFFERED;
-	отговорът съдържа PriceBreakdown;
-	съгласувани стойности на вътрешно изчислените полета.

### Резултат от упражнението

В края на ЛУ 5 трябва да има:
-	отделно Offer entity;
-	еднопосочна връзка към ServiceRequest;
-	максимум една оферта за заявка;
-	поле PriceBreakdown като @Embeddable value object;
-	фиксирани ценови характеристики вътре в изброимите типове;
-	първа директна реализация на ценообразуване;
-	правила за валидност на офертата;
-	преход от PROCESSING към OFFERED;
-	transactional създаване на оферта;
-	запазени 400, 404 и 409;
-	проверена функционалност без излишна архитектура за ценообразуване.


