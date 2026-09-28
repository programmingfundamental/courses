---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---

### Preparing the Frontend Project

The frontend application should be created in a frontend/ directory in the root directory of the existing Maven project:

```
project-root/
├── frontend/
├── src/
│   ├── main/
│   └── test/
└── pom.xml
```

The backend and frontend applications will run independently:

Spring Boot    → localhost:8080

React / Vite   → localhost:5173


Use the following technologies to develop the client:

•	React;

•	TypeScript;

•	Vite;

•	the standard fetch API.

Do not add additional libraries for state management, routing, HTTP communication, or user interface development.

Ask the AI agent to create a minimal React project with TypeScript and Vite in the frontend/ directory. At this stage, do not implement communication with the backend application or client functionality.

Before applying the proposed actions, verify that they correspond to the assigned task and technological constraints. After execution, verify the actual state of the project and whether the frontend application starts successfully.


### Selecting Backend Artifacts for Client Development

The client must support two operations from the existing REST API:

•	retrieving existing service requests; 

•	creating a new service request. 

Before generating the client, identify the backend artifacts containing the information required for these operations.

Determine:

•	the REST endpoints and HTTP methods used; 

•	the request and response data; 

•	the types and permitted values; 

•	the applicable validation rules; 

•	how the API represents errors; 

•	the configuration required for communication between the frontend and backend applications. 

Independently select the relevant project context. Do not automatically provide the entire backend project.

With the assistance of the AI agent, analyze the task and propose a minimal structure for the React client. At this stage, do not generate code.

Verify the proposal against the backend artifacts and the task constraints. Determine whether it uses the actual API contract, whether it introduces nonexistent endpoints, fields, or behavior, and whether it proposes changing the backend API solely for the convenience of the client.

If information is missing, determine what additional context is required and add only the relevant artifacts.

### Generating the React Client

Based on the analysis, develop a minimal React client that:

•	retrieves existing service requests using the available GET operation; 

•	allows a new service request to be created using the available POST operation; 

•	displays the retrieved requests; 

•	provides a form for entering the required data; 

•	handles and displays errors returned by the backend application. 

Before generating the code, independently determine the necessary context from the existing backend and frontend artifacts. Backend artifacts define the API contract, while frontend artifacts define the current structure and implementation of the client.

Ask the AI agent to implement the client within the defined scope and technological constraints.

Before applying the proposed changes, check:

•	which files will be created or modified; 

•	whether the changes are necessary for the assigned task; 

•	whether only existing REST operations are used; 

•	whether unnecessary libraries, components, or architectural elements are proposed; 

•	whether unrelated changes or changes to the backend API are proposed. 

After approval, apply the necessary changes and verify the actual state of the project.

The presence of backend and frontend artifacts in the context should not be treated as a guarantee that the AI has used them correctly. The generated implementation must be verified against the artifacts themselves.

### Verifying Compliance with the API Contract

After generating the React client, verify whether it uses the existing REST API correctly.

Compare the generated frontend code with the backend artifacts and check:

•	whether the HTTP methods and URLs correspond to the actual REST operations; 

•	whether the TypeScript types and the data being sent correspond to the request and response DTOs; 

•	whether the actual permitted values are used; 

•	whether HTTP responses and errors are handled according to the API contract; 

•	whether nonexistent fields, endpoints, or behavior have been added; 

•	whether backend business logic has been duplicated in the frontend client; 

•	whether the changes remain within the defined scope. 

If a discrepancy is identified, apply root-cause analysis and determine the cause before making a change. The cause may lie in the provided context, the AI's interpretation of it, the generated implementation, or the configuration of communication between the applications.

If the context is insufficient, add only the necessary information and make the minimal correction required for the identified problem.

Do not change the existing backend API solely to make the generated frontend code work.

### Verification Through Execution

After making the necessary corrections, start the Spring Boot application and the React client.

Through actual execution, verify:

- loading the existing service requests;

- creating a valid new service request;

- behavior with invalid input data;

- display of the response or error returned by the backend application.

Successfully starting both applications is not sufficient evidence of correct integration. The observed behavior must be compared with the existing API contract.
If a discrepancy is identified, perform root-cause analysis before changing the frontend or backend implementation.

### Final Verification

After completing the work, verify that:

•	the React client uses the actual API contract, and the data correspond to the backend artifacts; 

•	the frontend application does not duplicate backend business logic or introduce unsupported behavior; 

•	the project context used is relevant to the specific task, and the AI's decisions have been verified against the provided artifacts; 

•	the applied changes remain within the defined scope and do not introduce unnecessary dependencies or architectural complexity; 

•	when problems were identified, their cause was analyzed before making a correction; 

•	communication between the frontend and backend applications has been verified through actual execution. 


