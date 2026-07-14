---
layout: default
title: Postman
parent: Laboratory excercise 1
grand_parent: Internet Technologies
nav_order: 3
---



# Postman 

Postman is an application used for API testing. It is an HTTP client that tests HTTP requests using a graphical user interface through which we receive different types of responses that can subsequently be validated.

It can be used directly on the web at [go.postman.co/home](https://go.postman.co/home) or installed as a desktop application. [Download link](https://www.postman.com/downloads/).

# Sending a request with Postman

You can send requests in Postman to connect to the APIs you are working with. Your requests can retrieve, add, delete, and update data. Parameters, authorization details, and any basic data you require can be sent.

When you send a request, Postman displays the response received from the API server in a way that allows you to review it, visualize it, and, if necessary, troubleshoot the problem.

![](<../../../assets/image (6).png>)

## Creating requests

You can create a new request from the Postman home screen by using New > HTTP Request or by selecting + to open a new tab.

![](<../../../assets/image (31).png>)

Select Save to create your query. You can give your query a name and description, and select or create a collection to save it to.

Once your new tab is open, you can provide the details you need for your request.

![](<../../../assets/image (57).png>)

### Add request details

If you have a request that you want to execute, you need to know the URL, the method, and other optional details like parameters and authentication data.

To initially test sending a request in Postman, you can set the URL of the Postman Echo API sample endpoint to https://postman-echo.com/get and the GET method, then select Send.

**Set the request URL**

Every request you send in Postman requires a URL representing the API endpoint you are working with.

Every operation you can perform using an API is usually associated with an endpoint. Each endpoint in an API is accessible at a specific URL. This is what you enter into Postman to access the API.

Postman will automatically add http:// to the beginning of your URL if you don't specify a protocol.

Optionally, you can enter query parameters in the URL field, or you can enter them in the Params tab. If your query uses path parameters, you can enter them directly in the URL field.

**Selecting a request method**

By default, Postman will choose the GET method for a new request. You can use various other methods to send data to your APIs.

<img src="../../../assets/image (64).png" alt="" data-size="original">

**Sending parameters**

You can send path and query parameters using the URL field and the Params tab.

Query parameters are added to the end of the request URL, after ?, listed in key-value pairs and separated by & using the following syntax:

?id=1\&type=new

Path parameters form part of the request URL and are specified using containers preceded by :, as in the following example: /customer/:id

To send a query parameter, add it directly to the URL or open Params and enter the name and value. When you enter your query parameters in the URL or Params fields, those values ​​will update wherever they are used in Postman.

To send a path parameter, enter the parameter name in the URL field after a colon, for example :id. When you enter a path parameter, Postman will populate it in the Params tab, where you can also edit it.

<figure><img src="../../../assets/image (28).png" alt=""><figcaption></figcaption></figure>

**Sending parameters via the request body**

Sending parameters via the request body is necessary when you need to add or update structured data with PUT, POST, or PATCH requests. The Body section in Postman is provided for this purpose.

Select the data type you need for your request body – form data, URL-encoded, raw, binary, or GraphQL. By default, Postman will choose None — leave it selected if you don't need to send a body with your request.

* **Form data**

Website forms often send data to the API as `multipart/form-data`. You can replicate this in Postman using the `form-data` option. It allows sending key-value pairs and specifying the content type.

![](<../../../assets/image (63).png>)

* **URL-encoded**

URL-encoded data uses the same encoding as URL parameters. If your API requires url-encoded data, select x-www-form-urlencoded in the Body section. Enter your key-value pairs to send with the request and Postman will encode them before sending.

* **Raw data**

With this option you can send your data in raw text format. Use the raw checkbox and the type from the drop-down list to specify the format of your data (**Text**, **JavaScript**, **JSON**, **HTML** or **XML**) and Postman will enable syntax highlighting and take care of adding the appropriate headers to your request.

![](<../../../assets/image (14).png>)

* **Binary data**

You can use binary data to send information that you can't manually enter into the Postman editor with the body of your request, such as images, audio, and video files (you can also send text files).

* **GraphQL**

You can send GraphQL queries using the GraphQL option.

**Requests requiring authentication**

Some APIs require authentication credentials. Authentication involves verifying the identity of the client making the request, and authorization involves confirming that the client has permission to perform the operation on the endpoint. Configuring access credentials is done in the Authorization tab.

![](<../../../assets/image (33).png>)

**Configuring request headers**

Some APIs require that you send specific headers with your requests, usually to provide more metadata about the operation you are performing. You can set these up in the Headers tab. Enter any key-value pairs you need, and Postman will send them along with your request. As you enter text, Postman prompts you with common options that you can use to automatically complete your setup, such as Content-Type.

![](<../../../assets/image (69).png>)

Postman automatically adds certain headers to your requests based on your request selections and settings. These can be hidden or shown by selecting the Hidden/Hide auto-generated headers options.

**Using cookies**

Postman allows the use of cookies when sending a request. To do this, select **Cookies (located below the Send button).**

![](<../../../assets/image (22).png>)

# Getting a response

Postman's response viewer helps you visualize and verify the correctness of API responses. An API response consists of a response body, headers, and an HTTP status code.

### **Response body**

The Body tab provides several views of the response: Pretty, Raw, Preview, and Visualize.

#### **Pretty**

The Pretty view formats JSON or XML responses so that they are easier to view. Hyperlinks in the Pretty view are highlighted and selecting them can load a GET request in Postman with the URL of the link.

![](<../../../assets/image (29).png>)

#### **Raw**

The raw view is a large text area with the body of the response. It can show whether your response is minified.

![](<../../../assets/image (21).png>)

#### **Preview**

The preview view renders the response in an iframe sandbox. Some web frameworks return HTML errors by default, and Preview can be especially useful for debugging in these cases.

Due to iframe sandbox restrictions, JavaScript and images are disabled. For binary response types, you can select "Send and Download" to save the response locally. You can then preview it using the appropriate viewer. This gives you the flexibility to test audio files, PDF files, zip files, or any other file type that the API returns.

![](<../../../assets/image (10).png>)

#### **Visualize**

The Visualize view renders the data in the API response according to the visualization code added to the query tests.

### **Cookies**

You can check the cookies sent by the server in the **Cookies** tab. The cookie record includes its name, value, associated domain and path, and other information about the cookie.

### **Headers**

Headers are displayed as key-value pairs in the **Headers** tab. Hover over the information icon.

### **Network information**

Postman displays network information when your API returns a response. Hover over the network icon ![](<../../../../assets/image (37).png>), to get the local and remote IP address for the request you sent.

When you make an https request, the network icon includes a padlock. When you hover over the icon, the network information will show more information, including certificate verification details.

![](<../../../assets/image (71).png>)

### Response code

Postman displays the response code returned by the API. Hover over the response code to get a short description.

![](<../../../assets/image (7).png>)

### Response time

Postman automatically calculates the time in milliseconds it takes for a response to arrive from the server. This information can be useful for some preliminary performance testing. Hover over the response time for a graph showing how long each event in the process took.

![](<../../../assets/image (13).png>)

### Response size

Postman shows the approximate size of the response. Hover over the response size to get a breakdown by body and header sizes.

# Testing with Postman

In Postman, you can add tests to requests, collections, and folders within a collection. To add a test, open the request, collection, or folder and enter your code in the Scripts > Post-response tab. You can write your own JavaScript, or open the sidebar next to the code editor and select a snippet. Tests are run after the request is executed and a response is received from the API. The result appears in the Test results tab of the response.

Examples: 

```JavaScript
pm.test("Status test", function () {
    pm.response.to.have.status(200);
});
```

```JavaScript
pm.test("Отговорът трябва да съдържа поле 'success'", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.success).to.be.true;
});
```
![request-test-tab-v11 23](https://github.com/user-attachments/assets/85fdc3c7-d353-444f-a66f-d2c2dd85b080)

Post-response scripts can use dynamic variables with different scopes - global, environment, local, and collection variables.

### Creating variables: 

```JavaScript
pm.globals.set("baseUrl", "https://postman-echo.com");  //глобална променлива
pm.environment.set("token", "abc123");                  //променлива на средата
pm.collectionVariables.set("userId", "12345");          //променлива на колекция
pm.variables.set("variable_key", "variable_value");     //локална променлива
```

### Using variables in requests: 

https://postman-echo.com/get?user_id={{userId}}
{{baseUrl}}/get?user_id={{userId}}
{{token}} //в хедъри за аутентикация

### Automatically assign variables from response:

```JavaScript
var jsonData = pm.response.json();
pm.environment.set("userToken", jsonData.token);
```
Example: 

![image](https://github.com/user-attachments/assets/dab55f88-842b-4342-b6e7-9706ce2284b5)


![image](https://github.com/user-attachments/assets/211f711f-c9fd-4dc8-9a57-749ed3485337)

