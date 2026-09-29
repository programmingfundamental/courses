---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---

### Selecting Functionality to Test

Use the service selected for unit testing in Laboratory Exercise 7 and determine through which REST operations its functionality is accessible from the web layer.

Select one REST operation related to business behavior that has already been tested.

For the selected operation, determine:

•	the controller and endpoint; 

•	the HTTP method; 

•	the request and response DTOs used; 

•	the applicable Bean Validation rules; 

•	the expected HTTP status codes; 

•	possible errors from the service layer and how they are represented as HTTP responses. 

### Determining the Required Context According to the Test Level

For the selected functionality, compare the context required for its unit testing in Laboratory Exercise 7 with the context required to verify the related REST behavior.

Determine:

•	which information from the previous context remains relevant; 

•	which information is no longer necessary for the controller test; 

•	what new context is required to verify the web layer;

•	which dependencies should be real, and which should be replaced with mock objects; 

•	which verifications belong to the controller test and should not duplicate business logic that has already been tested. 

Based on the analysis, independently select the project context for working with the AI assistant. Attach only the files relevant to the specific behavior and the selected test level.

If missing context is identified, determine what information is required and add only the corresponding files.

### Verification of the Test Configuration

Check whether the current project test configuration supports the tools required for controller testing – @WebMvcTest, MockMvc, Mockito, and JSON processing.

The required libraries may already be provided by the existing Spring Boot test dependencies. If a dependency is actually missing, make only the necessary change and reload the Maven project.

Proposed changes to pom.xml should not be accepted automatically without checking the current configuration.

### Generation and Verification of Controller Tests

Independently organize the work with the AI assistant to identify appropriate controller test scenarios and generate the necessary tests. Use the project context selected for the task and supplement it during the work if necessary.

Before applying the generated tests, check whether:

•	they verify the actual HTTP method and URL; 

•	the request data correspond to the actual DTOs; 

•	the expected HTTP status codes and response body correspond to the API contract; 

•	Bean Validation and the error response are verified in the applicable scenarios; 

•	service dependencies are appropriately isolated;

•	business logic that belongs to the unit tests is not duplicated; 

•	@SpringBootTest is not used unnecessarily; 

•	the AI has not added nonexistent API behavior. 

Add the approved tests to the project and execute them.

If a discrepancy is identified, apply the root-cause analysis process and determine whether the cause lies in the generated test, the provided context, the expected HTTP behavior, the test configuration, or the implementation of the web layer.

Correct the identified cause without automatically regenerating the entire test or changing the implementation solely to make the test pass.

### Extending the Set of Controller Tests

After verifying the initially selected REST operation, implement controller tests for several additional scenarios from the existing REST API that cover different aspects of web-layer behavior, for example:

•	successful execution of an operation; 

•	invalid input data; 

•	mapping an error from the service layer to the corresponding HTTP response. 

For each scenario, independently determine the behavior to be verified, the required project context, and the dependencies that should be isolated.

Independently organize the work with the AI assistant, generate the necessary tests, and review and execute them.

If a problem is identified, analyze its cause and correct the specific discrepancy. If additional project context is required for diagnosis, add only the necessary files.

### Final Verification

After completing the work, verify that:

•	for each test, it is clear what behavior it verifies and why that behavior belongs at the controller level; 

•	the project context used is relevant to the specific task; 

•	the HTTP method, URL, request data, status codes, and response body correspond to the actual API contract;

•	service dependencies are appropriately isolated, and the controller tests do not duplicate unit testing of the business logic; 

•	the generated tests have been reviewed and executed, and when a discrepancy was identified, its cause was verified before making a correction.
