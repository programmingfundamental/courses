---
title: Създаване на Maven проект в Eclipse IDE
sidebar:
  order: 5
draft: true
---

# Създаване на Maven проект в Eclipse IDE

1. Създаване на нов проект

File > New > Maven Project

За нуждите на проекта използваме папката по подразбиране на избраното от нас работно пространство.

![](/courses/assets/0.png)

При създаване на проектите Maven използва archetypes. Archetype е шаблон, опростен прототип на проекта, който желаем да създадем. Тъй като в случая изграждаме уеб приложение, избираме архетипа maven-archetype-webapp.

![](</courses/assets/1%20(1).png>)

При следващата стъпка задаваме:

* Идентификатор на групата, от която е част проекта (groupId). GroupId е един от ключовите идентификатори на проекта и обикновено се основава като пълното квалифицирано име на домейн на организация. Например (bg.tu.varna.sit)
* Идентификатор на проекта/артефакт (artifactId) - уникалното базово име на основния артефакт, генериран от този проект. Основният артефакт за проект обикновено е JAR файл. Останалите артефакти в проекта използват името на основния артефакт, за да образуват своите.

![](/courses/assets/2.png)

2\. Ако в проекта по подразбиране е предвидена Java, различна от версия 8, са необходими допълнителни настройки

Десен бутон върху създадения проект > Properties

![](</courses/assets/3%20(1).png>)

В Java Build Path задаваме пътя до инсталирания JDK 8, следвайки инструкциите по-долу.

![](/courses/assets/4.png)

![](/courses/assets/5.png)

![](</courses/assets/6%20(1).png>)

![](</courses/assets/7%20(1).png>)

![](</courses/assets/8%20(1).png>)

![](/courses/assets/9.png)

Редактираме версията на Java в Java Compiler и Project Facets.

![](/courses/assets/10.png)

![](/courses/assets/11.png)

Отваряме pom.xml и нанасяме последни редакции в тагове \<maven.compiler.source> и \<maven.compiler.target>.

![](/courses/assets/12.png)

3\. В pom.xml добавяме dependency към jakarta.servlet-api

[https://mvnrepository.com/artifact/jakarta.servlet/jakarta.servlet-api/5.0.0](https://mvnrepository.com/artifact/jakarta.servlet/jakarta.servlet-api/5.0.0)

```xml
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>5.0.0</version>
    <scope>provided</scope>
</dependency>
```

![](/courses/assets/pom.jpg)

Десен бутон върху pom.xml > Run As > Maven Build. Задаваме Goals “clean install”.

![](/courses/assets/14.png)

Ако след преминаване на стъпките по-горе все още се извежда съобщение за грешка: десен бутон върху проекта > Maven > Update project (или клавишна комбинация Alt + F5)

4\. Настройване на сървъра

![](/courses/assets/15.png)

![](/courses/assets/tomcat1.jpg)

![](/courses/assets/tomcat2.jpg)

![](/courses/assets/18.png)

5\. Стартиране на проекта

Десен бутон на проекта > Run As > Run on Server

![](/courses/assets/19.png)

![](/courses/assets/20.png)
