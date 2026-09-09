---
layout: default
title: Лабораторно упражнение 9
parent: Учебна практика 3
has_children: true
nav_order: 9
---

# Лабораторно упражнение 9

## Добавяне на React клиент към съществуващо REST приложение

### Цел

Целта на упражнението е да се използва ИИ асистент за добавяне на фронтенд клиент към съществуващо Spring Boot REST приложение.

В рамките на упражнението ще бъдат разгледани:
- анализ на съществуващ REST API преди добавяне на клиент;
- генериране на React клиент с помощта на ИИ асистент;
- комуникация между React клиент и Spring Boot REST API;
- използване на fetch за изпращане на HTTP заявки;
- управление на състояние чрез React hooks;
- визуализиране на данни, получени от бекенд приложение;
- изпращане на данни от HTML форма към REST API;
- обработка и визуализиране на грешки, върнати от бекенд приложението;
- анализ и проверка на генерирания от ИИ код.

Фокусът на упражнението е върху интеграцията между фронтенд и бекенд и анализа на генерирания код, а не върху подробното изучаване на React или CSS.

### Подготовка

Към този момент приложението предоставя REST API за управление на заявки за услуги, оферти и изпълнение на услуги.

Фронтендът ще бъде добавен като отделно приложение в директория

```
frontend/
```
в основната директория на съществуващия Maven проект:

```
project-root/
├── frontend/
├── src/
│   ├── main/
│   └── test/
└── pom.xml
```
Бекенд и фронтенд приложенията се стартират независимо:

```
Spring Boot     → localhost:8080
React / Vite    → localhost:5173
```

За разработването на клиента ще се използват:
- React;
- TypeScript;
- Vite;
- стандартният fetch API.

Не се използват допълнителни библиотеки за управление на състояние, маршрутизиране или HTTP комуникация.

### Анализ на съществуващото приложение

Преди генерирането на фронтенд код ИИ асистентът трябва да анализира съществуващото REST API.

Първи промпт:
```
Analyze the existing Spring Boot application and its REST API in preparation for adding a simple React frontend client.

Identify:
- the existing REST endpoints that are relevant to the client;
- the request and response DTOs used by these endpoints;
- the user interactions that can be supported through the existing API;
- the data that should be displayed or entered for each interaction;
- any backend configuration that may be required for communication between a separately running React development server and the Spring Boot application.

Propose a minimal frontend structure suitable for this application.

Keep the proposed client simple. Do not introduce functionality that is not supported by the existing backend, and do not propose additional libraries, state-management frameworks, routing, or unnecessary architectural abstractions.

Do not generate or modify code yet.
```

Да се прегледа полученият анализ и да се провери дали ИИ асистентът правилно е идентифицирал REST endpoints, request и response DTO класовете, както и възможните действия на потребителя.

Да се обърне внимание на предложената функционалност. Наличието на дадена операция в REST API не означава автоматично, че тя трябва да бъде включена във фронтенд клиента. Обхватът на генерираното решение трябва да бъде определен от разработчика.

Необходима е проверка дали е идентифицирана необходимостта от конфигурация за комуникация между фронтенд и бекенд приложенията.

### Генериране на React клиента

Да се ограничи реализацията до операциите за извличане и създаване на заявки.

Промпт:

```
Based on the analysis, implement only the first minimal frontend slice.

Create a React + TypeScript + Vite frontend in a separate frontend directory in the project root.

For this step, support only:
- displaying service requests using GET /api/requests;
- creating a new service request using POST /api/requests.

Use the existing backend DTO structure and REST API.

Keep the implementation simple:
- use functional components and React hooks;
- use the native fetch API;
- do not add routing, external state-management libraries, UI frameworks, Axios, or other dependencies unless strictly necessary;
- do not duplicate backend business rules in the frontend;
- display backend validation/error messages to the user.

Add only the minimum backend configuration required for the React development client to communicate with the API.

Before modifying code, briefly state which files you plan to create or change. Then implement the changes and verify that both applications can run.
```

След приключване на генерирането, да се прегледат създавените и променените файлове. 

Да не се приема автоматично успешното компилиране или стартиране като доказателство, че генерираното решение е подходящо!

### Анализ на генерирания код

В App.tsx да се намерят TypeScript типовете, съотвестващи на request и response DTO класовете на бекенд приложението. Да се сравнят имената и типовете на полетата.

Да се обърне внимание на състоянието на компонента, например:

```TypeScript
const [requests, setRequests] = useState<ServiceRequest[]>([]);
const [loading, setLoading] = useState(true);
const [submitting, setSubmitting] = useState(false);
const [error, setError] = useState<string>('');
const [form, setForm] =
    useState<CreateServiceRequest>(initialForm);
```

Да се определи предназначението на всяка стойност.

Да се намери първоначалното зареждане на заявките:

```TypeScript
useEffect(() => {
    void loadRequests();
}, []);
```
и да се проследи последователността
```
useEffect()
    ↓
loadRequests()
    ↓
fetch("/api/requests")
    ↓
GET /api/requests
    ↓
setRequests(...)
    ↓
визуализиране на получените данни
```

Да се открие кода за изпращане на POST заявката и да се определи:
- как се задава HTTP методът;
- как се указва типът на съдържанието;
- как JavaScript обектът се преобразува в JSON;
- как се обработва полученият HTTP отговор.

Да се обърне внимание на начина, по който полетата на формата са свързани със state:

```TypeScript
value={form.customerName}
onChange={(event) =>
    updateField('customerName', event.target.value)}
```

Как промяната на стойност във формата води до промяна на състоянието?

Необходимо е специално внимание да се отдели на условното визуализиране на полетата address и urgencyReason. Трябва да се определи дали това представлява реализация на бизнес правилата или адаптиране на потребителския интерфейс към текущото състояния на формата.

### Стартиране и проверка

Първо се стартира Spring Boot приложението. След това от директорията frontend се стартира клиента:

```
npm run dev
```
Фронтенд приложението е достъпно на

```
http://localhost:5173
```

Поведението на приложението трябва да бъде проверено при празна база данни. 

Това може да стане чрез създаване на валидна заявка чрез формата. При правилно функциониране на клиента, новата заявка се появява в списъка без ръчно презареждане на страницата.

Подобно поведение се дължи на следния код фрагмент:

```TypeScript
await apiCreateRequest(form);
setForm(initialForm);
await loadRequests();
```

### Проверка на невалидна заявка

Тестването с невалидна заявка може да стане чрез комбинацията
```
Service type:    REPAIR
Execution mode:  REMOTE
Priority:        URGENT
```
и попълването на останалите видими полета, последвано от изпращането на заявката.

Да се анализира полученото съобщение за грешка.

Да се проследи последователността
```
React form
    ↓
POST /api/requests
    ↓
backend validation
    ↓
HTTP 400 + ApiError
    ↓
React
    ↓
визуализиране на грешката
```

Да се определи:
- къде се изпълнява бизнес валидацията;
- как грешката достига до фронтенд приложението;
- защо проверката на бизнес правилата не трябва да бъде оставена единствено на фронтенд клиента.

### Code review

Да се направи преглед на генерираното решение.

Да се обърне внимание на:
- съотвествието между TypeScript типовете и Java DTO класовете;
- използването само на съществуващи REST endpoints;
- обработката на HTTP грешки;
- наличието на ненужни зависимости или архитектурни усложнения;
- евентуално дублиране на бизнес логика;
- конфигурацията за комуникация между фронтенд и бекенд;
- поведението на условните полета при промяна на избраните стойности.

Да се провери конфигурацията за CORS и Vite proxy. Ако ИИ асистентът е добавил и двете решения, да се определи дали това е необходимо или представлява излишна конфигурация.

При наличие на установени проблеми да се формулира последващ промпт, който изисква само необходимите корекции, без повторно генериране на цялото решение и без промени в несвързан код.

### Резултат

Крайното решение трябва да включва:
- React + TypeScript приложение, създадено с Vite в отделна директория frontend;
- форма за създаване на нова заявка за услуга;
- изпращане на данните от формата към POST /api/requests;
- извличане на съществуващите заявки чрез GET /api/requests;
- визуализиране на получените заявки;
- автоматично обновяване на списъка след успешно създаване на нова заявка;
- условно визуализиране на полета във формата според избраните стойности;
- обработка и визуализиране на грешки, върнати от backend приложението;
- работеща комуникация между React клиента и Spring Boot REST API;
- проверен и анализиран генериран от ИИ код, като установените при code review проблеми са коригирани при необходимост.

Бекенд и фронтенд приложенията трябва да могат да бъдат стартирани независимо, като React клиентът успешно комуникира със съществуващото REST API.

```Markdown
### Допълнителна информация

За повече информация относно React и използваните в упражнението основни концепции може да използвате официалната документация:

React – Learn: https://react.dev/learn
```
