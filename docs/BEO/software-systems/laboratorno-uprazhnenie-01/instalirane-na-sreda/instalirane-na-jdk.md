---
layout: default
title: Инсталиране на JDK
parent: Инсталиране на среда
#grand_parent: Програмни системи
nav_order: 1
#permalink: /docs/BEO/software-systems/laboratorno-uprazhnenie-1/ide/instalirane-na-sreda/instalirane-na-jdk
---

# Инсталиране на JDK

Преди да започнете да пишете програми на Java, е нужно да инсталирате JDK. Може да го изтеглите от:

[https://www.oracle.com/java/technologies/downloads/](https://www.oracle.com/java/technologies/downloads/)

Стартирате инсталатора и следвате стъпките, препоръчително е да направите инсталацията в директориите по подразбиране. След като приключите с инсталацията е необходимo да настроите променливите на средата.

Променливи на средата могат да бъдат зададени конзолно или като се следва следния път в графичния интерфейс на Windows:

Моят компютър /десен бутон/ -> Свойства -> Допълнителни системни настройки -> Променливи на средата

Или в windows search търсите: Edit the system environment variables     

<img width="346" height="372" alt="Screenshot 2026-01-22 140654" src="https://github.com/user-attachments/assets/6a9b1ecf-7ea8-497b-a0a5-2747515dcb5e" /><br>     

      
Натиска се върху Environment variables ->     
 - Променливата Path се редактира и към нея се добавя пътят до папка bin в инсталационната папка на JDK.
 - Добавя се нова системна променлива JAVA\_HOME, указваща пътя до инсталационната папка на JDK

<img width="746" height="217" alt="Screenshot 2026-01-22 141833" src="https://github.com/user-attachments/assets/d5b11211-c9f1-4dfc-8b4f-1f059405d9a5" /><br>


Пътят до съответните папки може лесно да бъде копиран в File Explorer (десен бутон върху пътя на съответната папка).   
За проверка дали Java е инсталирана успешно, рестартирайте и стартирайте Command prompt и впишете в командния ред командата java -version.

