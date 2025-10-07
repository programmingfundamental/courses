---
layout: default
title: Java Servlets
parent: Laboratory excercise 2
grand_parent: Internet Technologies
nav_order: 6
---


# Java Servlets
Java Servlets are programs that run on a web server and act as a middle layer between requests from a web browser or other HTTP client and databases or applications located on or behind the server. Using Servlets provides various capabilities such as collecting information from users through web forms on pages, presenting records from a database or other source, and dynamically creating web pages.

Servlets provide a component-based, platform-independent method for building web-based applications. Servlets have access to the entire family of Java APIs, including the JDBC API for accessing databases.

### Servlets perform the following basic tasks:

* read data sent by clients (browsers). This includes HTML forms on a web page, an applet or some specific HTTP client, cookies, media types, compression schemes, and so on;
* process the data and generate results. This process may require communicating with a database, executing an RMI or CORBA call, using a web service, or directly computing a response.
* send data to clients (browsers). The data can be in a variety of formats, including text (HTML or XML), binary (GIF images), Excel, and so on.
* send additional meta information to clients (browsers). This includes rendering the type of document to be returned (e.g. HTML), creating cookies, caching parameters, and so on.
