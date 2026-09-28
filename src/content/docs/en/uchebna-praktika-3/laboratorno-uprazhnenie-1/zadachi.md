---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---
### Task

Develop a REST application for managing service requests. Customers should be able to submit service requests, while office staff should be able to view the submitted requests. The application will be extended with additional functionality in the subsequent exercises.

The initial version of the application will be created through a series of experiments with the AI assistant in which the specificity of the task and the provided context will be varied. The resulting solutions will be compared, analyzed, and verified.

### Experiments

For the first three experiments, each experiment is started in a new Agent session. This ensures that the result of the current experiment does not use conversational context accumulated during the previous one.

For the first two experiments, use a new Java project with no application functionality implemented. For the third experiment, use a pre-created minimal Maven project.

Submit the prompts exactly as provided in the exercise instructions.

Due to the nondeterministic nature of generative models, different runs are not expected to produce identical solutions. The choices and assumptions made are compared rather than the exact content of the generated code.

### Experiment 1: Minimal Context

Create a new Java project with no application functionality implemented and start a new Agent session.

Submit the following prompt:

```
Create a REST application for managing service requests.
```

Observe the actions proposed by the Agent and the generated solution without correcting them in advance.

Analyze:

•	which technologies the AI assistant selected; 

•	what application structure it proposed; 

•	which classes, files, or other artifacts it created or proposed; 

•	what request model it assumed; 

•	which operations it implemented; 

•	which decisions follow directly from the assigned task; 

•	which decisions represent assumptions; 

•	whether there are any unsolicited decisions or functionality. 

Determine what missing information would limit the assumptions made.

### Experiment 2: Adding Technological Context

Start with a new Java project with no application functionality implemented and a new Agent session.

Submit:

```
Create a Spring Boot REST application for managing service requests.
Use Java 26, Maven, Spring Web, Spring Data JPA, Lombok, H2 and validation.
The application should be called ServiceRequestManagementSystem.
```

Analyze the request and the generated result according to the introduced criteria. Additionally, check:

•	whether a Maven project with the required dependencies has been created or proposed; 

•	how the packages are organized; 

•	whether there is a separation between controller, service, and repository; 

•	whether DTOs are used; 

•	what request model has been proposed; 

•	which REST endpoints have been implemented; 

•	which technological decisions are now determined by the provided context; 

•	which functional and architectural decisions the AI continues to make independently. 

Compare the result with the first experiment and determine which assumptions have been limited by adding the technological context and for which parts of the solution information is still missing.

At this stage, the generated project is used for analysis and comparison, and it is not necessary to accept the proposed changes or complete the application.

### Experiment 3: Functional, Technological, and Architectural Context

Create a new minimal Maven project with:

•	GroupId: com.example 

•	ArtifactId: ServiceRequestManagementSystem 

•	Java: 26 

Do not add Spring Boot or any other dependencies to the initial pom.xml.

Start a new Agent session. Explicitly add the pom.xml file to the task context.

Submit the following structured prompt:

```text
### Task
Convert the existing Maven project into a Spring Boot REST application for managing service requests.

### Context
Use Java 26, Spring Web, Spring Data JPA, Lombok, H2 and validation.
Use the following packages: controller, dto, entity, repository, service.

ServiceRequest is the main entity. Properties: id, customerEmail, description, serviceType, executionMode, priority, status, submittedAt.
ServiceType: INSTALLATION, REPAIR, CONSULTATION.
ExecutionMode: REMOTE, ON_SITE.
Priority: STANDARD, URGENT.
RequestStatus: SUBMITTED.
A new request is created with status SUBMITTED. submittedAt is set by the system. The customer does not provide id, status or submittedAt.

Required endpoints:
POST /api/requests
GET /api/requests
GET /api/requests/{id}

### Constraints
Do not add functionality that is not required above.

### Expected output
Create or modify only the files needed for this initial version of the application.
```

Before accepting the changes proposed by the Agent, evaluate the request and the result according to the introduced criteria.

Additionally, check:

•	whether the structure corresponds to the specified architectural context; 

•	whether the specified REST endpoints have been implemented; 

•	whether ServiceRequest corresponds to the specified model; 

•	whether the initial status and system-assigned values have been implemented correctly; 

•	whether the necessary dependencies and configuration have been proposed; 

•	whether unsolicited components or functionality have been added; 

•	whether there is any requirement that the Agent has not followed. 

Compare the proposed solution with the results of the first two experiments. Determine which assumptions have been limited by adding the functional and architectural context and which decisions the model continues to make independently.

After the analysis, review the proposed actions and changes. Accept only those that correspond to the assigned task.
If the solution is incomplete or incorrect, identify the specific problem and formulate a refinement request only for the necessary change instead of generating the entire application again.

### Verification and Troubleshooting

After creating the project, perform verification of the generated solution:

•	review the files that were actually created and modified; 

•	check their compliance with the specified requirements; 

•	compile and run the application; 

•	test the main REST endpoints; 

•	if an error occurs, analyze the actual error message, code, and configuration; 

•	if necessary, use the AI assistant to formulate possible causes; 

•	treat the cause proposed by the AI as a hypothesis that must be verified; 

•	identify the actual root cause before accepting a correction; 

•	apply only the necessary correction and verify it again. 

This sequence represents a debugging process in which the AI assistant can support the analysis, but identifying the actual cause and verifying the correction remain part of the developer's engineering work.

A claim by the AI assistant that the application compiles or runs successfully is not accepted as evidence.

### Comparison of Results

Compare the results of the three experiments and analyze:

•	how the generated solution changes as task specificity and the provided context increase; 

•	which assumptions are limited by the additional context; 

•	which decisions the model continues to make independently; 

•	whether unsolicited solutions appear; 

•	whether more detailed context necessarily leads to a complete and correct solution; 

•	the role of verification even when detailed functional, technological, and architectural requirements are provided. 

Based on the observations, formulate a conclusion about the relationship between task specificity, the provided context, model assumptions, and the need to verify the generated result.
