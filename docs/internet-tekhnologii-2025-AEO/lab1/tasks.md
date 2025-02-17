---
layout: default
title: Tasks with Postman
parent: Laboratory excercise 1
grand_parent: Internet Technologies
nav_order: 4
---


# Tasks for working with Postman

1\.      Send a request to the following URL https://www1.tu-varna.bg/tu-varna/ using the GET method. View the response received:

a.      The response body in Pretty, Raw, and Preview views;

b.      The cookies and headers received;

c.      Network information, status code received, response time, response size.



2\.      Send a POST request to the following URL:\
[http://www2.tu-varna.bg/prep/index.php/stud/stgroup](http://www2.tu-varna.bg/prep/index.php/stud/stgroup)

In the request body, specify the following parameters:

| Key  | Value     |
| ---- | --------- |
| oks  | 2         |
| stat | действащи |



3\.      Send a POST request again to the following URL:\
[http://www2.tu-varna.bg/prep/index.php/stud/stgroup](http://www2.tu-varna.bg/prep/index.php/stud/stgroup)

In the request body, specify the following parameters:

| Key   | Value                            |
| ----- | -------------------------------- |
| oks   | 2                                |
| stat  | действащи                        |
| spec  | 162                              |
| fob   | 1                                |
| kurs  | 3                                |
| grupa | Your group number, e.g. 1        |



4\.     The following series of tasks aims to access an application that manages student data at TU-Varna. The stored data for each student includes their faculty number (fn), first name (firstName), and last name (lastName). 

The destination URL [http://195.216.228.13:8080/manage](http://195.216.228.13:8080/manage).

For each of the executed requests, test whether the status code received in response corresponds to expectations for success for the respective method.

a.      Send a GET request to display the data of a student with faculty number 116824. To do this, include the following query parameter:

| Key | Value  |
| --- | ------ |
| fn  | 116824 |

b.      Using a Post request, add a student with your data. Provide the following parameters carried by the request body:

| Key       | Value            |
| --------- | ---------------- |
| fn        | <Your FN>      |
| firstName | <Your name>     |
| lastName  | <Your surname> |



c.      Send a GET request to retrieve your data. To do this, provide your faculty number as a query parameter.

d.      Send a GET request, passing a non-existent faculty number as a query parameter.

e.      Send a GET request without passing the faculty number as a query parameter.

f.      Send a PUT request to update your data. Provide the following parameters in the request body:

| Key       | Value           |
| --------- | --------------- |
| fn        | <Prevoius FN> |
| firstName | <New name>      |
| lastName  | <New surname>  |

g.      Delete your data by sending a request using the DELETE method with the query parameter your faculty number.

h.     Send a GET request again to retrieve your data. To do this, provide your faculty number as a query parameter.

i.     Re-execute condition f.
