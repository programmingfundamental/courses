---
title: "Lab 9"
sidebar:
  order: 9
---

# Laboratory Exercise 9

## Developing a Client Against an Existing API with the Assistance of AI

### Objective

The objective of this exercise is to examine the use of existing software artifacts as context when developing a new component with the assistance of an AI assistant.

By developing a client for an existing REST API, the exercise will examine how real project artifacts can provide grounding for the generated solution in the actual state of the system. It will be verified whether the existing API contract is preserved when developing the new component, without introducing unsupported endpoints, data structures, or behavior.

The practical task is to develop a minimal React client for the existing Spring Boot application that uses the available REST API to retrieve and create service requests.

### React Client for a REST API

React allows a user interface to be built using components. In functional components, *useState* can be used for state management, while *useEffect* can be used to perform actions such as initially fetching data.

A React client can communicate with a REST API using the standard fetch API. When sending a request, the HTTP method, URL, request body, and required headers must be considered, while the received response must be handled according to its status and content.

When using TypeScript, types can be defined for the data sent to and received from the REST API. These types must correspond to the actual contract of the backend application.

The frontend and backend applications can be developed and run independently. When they run on different ports, communication between them must be configured using an appropriate mechanism, such as a development server proxy or CORS configuration.


### Software Artifacts as Context and Grounding

When working on an existing system, context for the AI assistant can be provided through actual software artifacts rather than by repeatedly describing the existing implementation in text.

Such artifacts may include:

•	controller classes; 

•	request and response DTO classes; 

•	enum types; 

•	validation rules; 

•	error representation models; 

•	configuration files; 

•	tests or other artifacts that describe the contract being used. 

**Grounding** means that the generated solution is based on provided, verifiable information about the specific system rather than only on the model's general knowledge and assumptions.

When developing a client for an existing REST API, backend artifacts can provide information about the actual endpoints, HTTP methods, request and response data structures, permitted values, and error format.

Not all available artifacts are required for every task. Only the context necessary for the specific change should be selected. The presence of an artifact in the context also does not guarantee that the AI will interpret it correctly, so the generated solution must be verified against the source on which it is supposed to be based.

### Preserving the Existing API Contract

When a new component uses an existing interface, the generated solution must comply with its contract.

For a REST API, the contract includes, for example:

•	HTTP methods and URLs; 

•	request and response data structures; 

•	field names and types; 

•	permitted values; 

•	HTTP status codes; 

•	the structure of error responses. 

When the task is to develop a client for an existing API, a discrepancy between the generated client and the backend contract should not automatically result in a change to the backend application. The cause of the discrepancy should first be identified.

The frontend application may contain logic related to the user interface and the management of its state, but business rules remain the responsibility of the backend application.

### Verification of the Generated Solution

The generated frontend code should be verified both against the backend artifacts and through actual communication with the existing application.

It should be determined whether the client:

•	uses the existing REST operations and their corresponding data structures; 

•	uses permitted values from the backend model; 

•	handles responses and errors according to the actual API contract; 

•	does not unnecessarily duplicate backend business logic; 

•	does not introduce unsupported functionality, dependencies, or architectural complexity. 

Successfully starting the React application does not prove that the integration is correct. Actual operations against the backend application must be performed.
