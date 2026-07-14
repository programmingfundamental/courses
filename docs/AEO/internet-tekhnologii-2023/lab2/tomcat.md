---
layout: default
title: Tomcat
parent: Laboratory excercise 2
grand_parent: Internet Technologies
nav_order: 2
---

# Working with Tomcat

The Tomcat server is a free Web container that can run Web applications, including Java servlets and JSP pages. Tomcat is a Web application server written in Java, which, in response to a client HTTP request, can return not only static files, but also dynamic documents created as a result of the execution of a servlet or JSP.

### Installing Tomcat server

Tomcat is available for free download from:

​[https://tomcat.apache.org/](https://tomcat.apache.org/)

The latest version of the server can be downloaded as a .zip file, which we must unzip into a directory intended for network access. It is recommended that the directory name not contain spaces, because spaces are used as separators in Java and can cause problems.

Before using Tomcat Server, we need to install the JDK on our computer. We can download it from:

​[https://www.oracle.com/java/technologies/javase-downloads.html](https://www.oracle.com/java/technologies/javase-downloads.html)​

### Starting Tomcat

To start the Tomcat server, we first need to add the following variables to the Environment variables:

· JAVA\_HOME with the value of the directory where the JDK is installed.

· CATALINA\_HOME with the value of the directory where Tomcat is installed.

![](<../../../assets/image (9).png>)

This can be done from the operating system settings or from the console with the command

set JAVA\_HOME=C:\\........

To start the Tomcat server itself, we need to run the startup script, which is located in the bin subdirectory of the Tomcat server's root directory. For example, if we installed Tomcat in the directory F:\DevTools\apache-tomcat-10.0.16, our bin directory would be

F:\DevTools\apache-tomcat-10.0.16\bin

In this bin subdirectory there is a service management file service.bat with which we can start the server. Starting the server is done with the .bat file service.

### PowerShell commands to start Tomcat service under Windows.

./startup.bat <# starts Tomcat service as a program in Windows #>

./shutdown.bat <# stops the Tomcat service program #>
When started, the server listens on port 8080 for incoming HTTP requests, rather than the standard HTTP port 80. If there are no error messages, the server has started successfully.

To check if everything is working properly, we can launch our Web browser and enter the address

​[http://localhost:8080/](http://localhost:8080/)​

If everything is OK, the Tomcat homepage will appear.

### Structure on Tomcat

Some of the key directories of Tomcat:

* /bin – contains scripts for starting, shutting down the server, as well as for implementing other purposes (\*.sh for Unix systems and \*.bat for Windows systems).
* /conf – contains configuration files and their associated DTDs. The most important file in this directory is server.xml. It is the main configuration file of the container.
* /logs – log files are placed here by default contains
* /webapps – the directory where the WAR files of the applications are placed
