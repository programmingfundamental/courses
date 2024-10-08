---
layout: default
title: Инсталиране на JDK
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 1 част
has_children: true
nav_order: 1
---

# Инсталиране на JDK

Преди да започнете да пишете програми на Java, е нужно да инсталирате JDK. Може да го изтеглите от:

https://tuvarnabg-my.sharepoint.com/:u:/g/personal/vkolesnichenko_tu-varna_bg/ET92nCILMK9MpO_krBzE8RkBYg90A97t1NTBC-aor3a26A?e=e6PcXa

или [https://www.oracle.com/java/technologies/downloads/](https://www.oracle.com/java/technologies/downloads/)

Стартирате инсталатора и следвате стъпките, препоръчително е да направите инсталацията в директориите по подразбиране.

След като приключите с инсталацията е необходими да, настройте променливите на средата

Променливи на средата могат да бъдат зададени конзолно или като се следва следния път в графичния интерфейс на Windows:

Този компютър /десен бутон/ -> Свойства -> Допълнителни системни настройки -> Променливи на средата

Или в windows search търсите: Edit the system environment variables

![](<../../../../assets/2 (1).png>) ![](../../../../assets/3.png)

Променливата Path се редактира и към нея се добавя пътят до папка bin в инсталационната папка на JDK

![](<../../../../assets/4 (1).png>)

Добавя се нова системна променлива JAVA\_HOME, указваща пътя до инсталационната папка на JDK

![](<../../../../assets/5 (1).png>)

Пътят до съответните папки може лесно да бъде копиран в Windows Explorer (десен бутон върху пътя на съответната папка)

![](../../../../assets/6.png)

За проверка дали Java е инсталирана успешно, рестартирайте и стартирайте Command prompt и впишете в командния ред командата java -version.
