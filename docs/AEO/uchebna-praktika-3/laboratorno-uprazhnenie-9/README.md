---
layout: default
title: Laboratory exercise 9
parent: Training Practice 3
has_children: true
nav_order: 9
---

# Laboratory exercise 9

## Adding a React Client to an Existing REST Application

### Objective

The objective of this exercise is to use an AI assistant to add a frontend client to an existing Spring Boot REST application.

The exercise covers:

- analyzing the existing REST API before adding a client;
- generating a React client with the help of an AI assistant;
- communication between the React client and the Spring Boot REST API;
- using fetch to send HTTP requests;
- managing state with React hooks;
- displaying data received from the backend application;
- submitting data from an HTML form to the REST API;
- handling and displaying errors returned by the backend application;
- analyzing and verifying AI-generated code.

The focus of the exercise is on frontend-backend integration and analysis of the generated code rather than on an in-depth study of React or CSS.

### Preparation

At this stage, the application provides a REST API for managing service requests, offers, and service executions.

The frontend will be added as a separate application in the
```
frontend/
```
directory at the root of the existing Maven project:

```
project-root/
├── frontend/
├── src/
│   ├── main/
│   └── test/
└── pom.xml
```

The backend and frontend applications are started independently:

```
Spring Boot     → localhost:8080
React / Vite    → localhost:5173
```

The client will be developed using:

- React;
- TypeScript;
- Vite;
- the standard fetch API.

No additional libraries for state management, routing, or HTTP communication will be used.

### Analysis of the Existing Application

Before generating frontend code, the AI assistant should analyze the existing REST API.

First prompt:

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

Review the resulting analysis and verify whether the AI assistant has correctly identified the REST endpoints, the request and response DTO classes, and the possible user interactions.

Pay particular attention to the proposed functionality. The existence of an operation in the REST API does not automatically mean that it should be included in the frontend client. The scope of the generated solution should be determined by the developer.

Verify whether the need for configuration enabling communication between the frontend and backend applications has been identified.

### Generating the React Client

Limit the implementation to retrieving and creating service requests.

Prompt:

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

After generation is complete, review all created and modified files.

Do not automatically treat successful compilation or startup as proof that the generated solution is appropriate.

### Analysis of the Generated Code

In App.tsx, locate the TypeScript types corresponding to the request and response DTO classes in the backend application. Compare the field names and types.

Pay attention to the component state, for example:

```TypeScript
const [requests, setRequests] = useState<ServiceRequest[]>([]);
const [loading, setLoading] = useState(true);
const [submitting, setSubmitting] = useState(false);
const [error, setError] = useState<string>('');
const [form, setForm] =
    useState<CreateServiceRequest>(initialForm);
```

Determine the purpose of each state value.

Locate the initial loading of the requests:

```TypeScript
useEffect(() => {
    void loadRequests();
}, []);
```
and trace the sequence:
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
displaying the received data
```

Locate the code that sends the POST request and determine:

- how the HTTP method is specified;
- how the content type is specified;
- how the JavaScript object is converted to JSON;
- how the resulting HTTP response is handled.

Pay attention to how the form fields are connected to state:

```TypeScript
value={form.customerName}
onChange={(event) =>
    updateField('customerName', event.target.value)}
```

How does changing a value in the form cause the component state to change?

Special attention should be given to the conditional rendering of the address and urgencyReason fields. Determine whether this constitutes implementation of business rules or adaptation of the user interface to the current form state.

### Running and Verification

Start the Spring Boot application first. Then, from the frontend directory, start the client:

```
npm run dev
```
The frontend application is available at:

```
http://localhost:5173
```

The behavior of the application should be verified with an empty database.

This can be done by creating a valid request through the form. If the client works correctly, the new request should appear in the list without manually reloading the page.

This behavior is produced by code similar to:

```TypeScript
await apiCreateRequest(form);
setForm(initialForm);
await loadRequests();
```

### Testing an Invalid Request

Testing an Invalid Request
```
Service type:    REPAIR
Execution mode:  REMOTE
Priority:        URGENT
```
Fill in the remaining visible fields and submit the request.

Analyze the resulting error message.

Trace the sequence:
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
displaying the error
```

Determine:

- where the business validation is performed;
- how the error reaches the frontend application;
- why validation of business rules should not be left solely to the frontend client.

### Code review

Review the generated solution.

Pay attention to:

- consistency between the TypeScript types and the Java DTO classes;
- use of only the existing REST endpoints;
- HTTP error handling;
- unnecessary dependencies or architectural complexity;
- possible duplication of business logic;
- configuration for frontend-backend communication;
- behavior of conditional fields when the selected values change.

Review the CORS and Vite proxy configuration. If the AI assistant has added both solutions, determine whether both are necessary or whether this represents redundant configuration.

If problems are identified, formulate a follow-up prompt that requests only the necessary corrections, without regenerating the entire solution or changing unrelated code.

### Lab Result

The final solution should include:

- a React + TypeScript application created with Vite in a separate frontend directory;
- a form for creating a new service request;
- submission of form data to POST /api/requests;
- retrieval of existing requests through GET /api/requests;
- display of the retrieved requests;
- automatic updating of the list after a new request is successfully created;
- conditional rendering of form fields according to the selected values;
- handling and display of errors returned by the backend application;
- working communication between the React client and the Spring Boot REST API;
- reviewed and analyzed AI-generated code, with issues identified during code review corrected where necessary.

The backend and frontend applications must be able to run independently, with the React client successfully communicating with the existing REST API.

```Markdown
### Additional Information

For more information about React and the basic concepts used in this exercise, the official documentation can be consulted:

React – Learn: https://react.dev/learn
```


