---
layout: default
title: Лабораторно упражнение 10
parent: Учебна практика 3
has_children: true
nav_order: 10
---

# Лабораторно упражнение 10

## Интеграция между приложения и интеграционно тестване

### Цел

Целта на упражнението е да се разшири разработеното приложение чрез отделен *Notification Service*, който комуникира с основното приложение чрез HTTP.

При създаване на оферта основното приложение трябва да изпраща информация към новия Notification Service. След реализиране на комуникацията трябва да бъде създаден интеграционен тест, който проверява взаимодействието между двете приложения.

При изпълнение на задачата ИИ асистентът се използва за анализ на съществуващия код, предлагане на решение, генериране на необходимите компоненти и интеграционния тест. Предложените решения трябва да бъдат преглеждани и оценявани преди да бъдат приети.

### Кратка теория

#### Интеграционно тестване

Интеграционните тестове проверяват дали отделни компоненти или приложения работят правилно заедно.

При unit тестовете обикновено се изолира един клас и неговите зависимости могат да бъдат заменени с mock обекти. При интеграционния тест участват реални компоненти и се проверява взаимодействието между тях.

| Unit тест                         | Интеграционен тест                                |
|-----------------------------------|---------------------------------------------------|
| Проверява отделен компонент       | Проверява взаимодействие между компоненти         |
| Зависимостите често се mock-ват   | Използват се реални зависимости, когато това е част от проверяваната интеграция |
| Изпълнява се бързо и изолирано    | Обикновено изисква повече инфраструктура          |
| Открива грешки в конкретна логика | Открива проблеми при взаимодействието             |


В Spring Boot интеграционният тест може да стартира реален application context, web server, база данни и други необходими компоненти.

Когато две приложения комуникират чрез HTTP, интеграционният тест може да проверява:
- дали заявката действително се изпраща към другото приложение;
- дали endpoint-ът и HTTP методът са правилни;
- дали изпратените данни съответстват на очаквания контракт;
- дали второто приложение може да приеме заявката;
- какво се случва, когато бизнес операцията в първото приложение е неуспешна.

Важна част от интеграционния тест е определянето на границата на интеграцията. Например HTTP сървърът може да бъде заменен със stub, но тогава тестът проверява само изходящата комуникация на първото приложение. Ако целта е да се провери реалното взаимодействие между две приложения, и двете трябва да участват в теста.

### Практическа задача

Към разработеното приложение трябва да бъде добавено отделно Spring Boot приложение Notification Service.

Notification Service трябва да получава известие при успешно създаване на оферта. За целите на упражнението не е необходимо реално изпращане на e-mail или използване на външна услуга. Полученото известие може да бъде обработено и записано в application log.

Преди генериране на код да се анализира с помощта на AI асистента къде notification функционалността естествено се вписва в съществуващия бизнес процес.

Промпт:

```
Analyze the existing Spring Boot application and identify where notification functionality would naturally fit into the current business workflows.

The goal is to introduce a separate Notification Service that will be responsible for receiving notification requests from the main application.

Identify:

which existing business events should trigger a notification;
what information the main application would need to send to the Notification Service;
which parts of the current application would need to be changed;
what responsibilities should remain in the main application and what should belong to the Notification Service;
suitable options for communication between the two applications;
what should be verified with an integration test.

Keep the proposal minimal and consistent with the current project. Do not introduce messaging infrastructure, service discovery, authentication, or other additional infrastructure unless it is clearly necessary.

Do not generate or modify code yet.
```

Да се прегледа получения анализ. Да се обърне внимание дали ИИ асистентът предлага функционалност или инфраструктура, която не е необходима за поставената задача.

За текущото упражнение е необходимо ограничаването на функционалността до един бизнес сценарий, например успешно създаване на оферта.

#### Реализиране на Notification Service

След анализа функционалността се ограничава до един бизнес сценарий – изпращане на известие при успешно създаване на оферта.

Notification Service трябва да бъде реализиран като отделно Spring Boot приложение. Комуникацията между основното приложение и Notification Service се осъществява чрез синхронна HTTP заявка.

За целите на упражнението не се реализира реално изпращане на e-mail. Получената информация се обработва от Notification Service и съобщението се записва в application log.

Промпт:

```
Based on the analysis, implement a minimal separate Notification Service and integrate it with the existing application.

For this exercise, support only one notification scenario: when an offer is successfully created for a service request.

Requirements:

create the Notification Service as a separate Spring Boot application;
use synchronous HTTP communication from the main application to the Notification Service;
keep the notification contract minimal;
the main application should send the notification only after successful offer creation;
the Notification Service should receive the request and compose/log the notification message;
do not add messaging infrastructure, service discovery, authentication, persistence, retry mechanisms, or other additional infrastructure;
do not modify unrelated production code.

Before modifying code, describe the files and components you plan to add or change.

Do not implement the integration test yet.
```

Преди генерирането на кода трябва да се прегледа предложеният план. Следва да се провери дали предлаганите промени са ограничени до необходимите компоненти и дали не се променя несвързана бизнес логика.

След генерирането на кода се преглеждат:

- мястото, от което се изпраща известието;
- HTTP клиентът и конфигурацията на адреса на Notification Service;
- структурата на изпращаните данни;
- endpoint-ът на Notification Service;
- разделението на отговорностите между двете приложения.

Особено внимание трябва да се обърне на момента на изпращане на известието. Известие не трябва да бъде изпращано, ако създаването на офертата не завърши успешно.

#### Проверка на комуникацията

Двете приложения се стартират едновременно на различни портове. Чрез Postman се изпълнява бизнес последователността, необходима за създаване на оферта:

създаване на заявка
        ↓
преминаване към PROCESSING
        ↓
създаване на оферта
        ↓
HTTP заявка към Notification Service

Проверява се дали при успешно създадена оферта Notification Service получава заявката и записва съответното съобщение в application log.

#### Създаване на интеграционен тест

След като комуникацията между двете приложения бъде проверена ръчно, трябва да се анализира какъв интеграционен тест е подходящ за реализираното взаимодействие.

На този етап не трябва предварително да се приема, че всички компоненти задължително трябва да бъдат реални или че трябва да бъдат заменени със mock/stub обекти. Първо трябва да се определи границата на интеграционния тест.

Промпт:

```
Analyze the implemented offer-created notification integration and propose the integration test cases needed to verify the communication between the main application and the Notification Service.

Focus on the integration boundary, not on duplicating unit tests of OfferService or NotificationService.

Identify:

- what components should be real in the integration test;
- what, if anything, should be mocked;
- how the HTTP communication should be exercised;
- what should be asserted in the successful case;
- what should be verified when offer creation fails or the transaction does not complete successfully.

Keep the test scope minimal and suitable for the current project.

Do not generate or modify code yet.
```

Полученото предложение трябва да бъде анализирано преди генериране на теста. Трябва да се установи какво в действителност ще доказва предложеният тест.

За настоящата задача интеграционният тест трябва да проверява реалната HTTP комуникация между двете Spring Boot приложения.

*Задача*

При необходимост първоначалното предложение на AI асистента да се уточни така, че в интеграционния тест да участват реалните web слоеве на двете приложения и реалната HTTP комуникация между тях. Да не се добавя сложна външна инфраструктура единствено за целите на теста.

Два задължителни сценария:
- успешно създаване на оферта → Notification Service получава точно едно известие с очакваните данни;
- неуспешно създаване на оферта → Notification Service не получава известие.
