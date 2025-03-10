---
layout: default
title: Задачи за работа с Postman
parent: Лабораторно упражнение 1
grand_parent: Интернет технологии
nav_order: 3
---

# Задачи за работа с Postman 

1\.      Изпратете заявка към следния URL https://www1.tu-varna.bg/tu-varna/, използвайки метода GET. Разгледайте полученият отговор:

a.      Тялото на отговора в изгледи Pretty, Raw и Preview;

b.      Получените бисквитки и хедъри;

c.      Мрежова информация, полученият статус код, време за отговор, размер на отговора.



2\.      Изпратете POST заявка към следния URL:\
[http://www2.tu-varna.bg/prep/index.php/stud/stgroup](http://www2.tu-varna.bg/prep/index.php/stud/stgroup)

В тялото на заявката задайте следните параметри:

| Key  | Value     |
| ---- | --------- |
| oks  | 2         |
| stat | действащи |



3\.      Изпратете POST заявка отново към следния URL:\
[http://www2.tu-varna.bg/prep/index.php/stud/stgroup](http://www2.tu-varna.bg/prep/index.php/stud/stgroup)

В тялото на заявката задайте следните параметри:

| Key   | Value                            |
| ----- | -------------------------------- |
| oks   | 2                                |
| stat  | действащи                        |
| spec  | 162                              |
| fob   | 1                                |
| kurs  | 3                                |
| grupa | Номерът на Вашата група, напр. 1 |



4\.      Последващата поредица от задачи има за цел да достъпи приложение, което управлява данните на студенти от ТУ-Варна. Съхраняваните данни за всеки студент включват неговият факултетен номер (fn), първо име (firstName) и фамилия (lastName). 

Целевият URL e [http://195.216.228.13:8080/manage](http://195.216.228.13:8080/manage).

При всяка от изпълнените заявки тествайте дали получения при оттговора статус код съотвества на очаквания за успех при съответния метод. 

a.      Изпратете GET заявка, за да изведете данните на студент с факултетен номер 116824. За целта включете следният query параметър:

| Key | Value  |
| --- | ------ |
| fn  | 116824 |

b.      С помощта на Post заявка добавете студент с Вашите данни. Предвидете следните параметри, пренасяни от тялото на заявката:

| Key       | Value            |
| --------- | ---------------- |
| fn        | <Вашият ФН>      |
| firstName | <Вашето име>     |
| lastName  | <Вашата фамилия> |



c.      Изпратете GET заявка, с която да изведете Вашите данни. За целта подайте факултетният си номер като query параметър.

d.      Изпратете GET заявка, като подадете несъществуващ факултетен номер като query параметър.

e.      Изпратете GET заявка, без да подавате факултетен номер като query параметър.

f.       Изпратете PUT заявка, с която да актуализирате своите данни. Предвидете следните параметри в тялото на заявката:

| Key       | Value           |
| --------- | --------------- |
| fn        | <Въведеният ФН> |
| firstName | <Ново име>      |
| lastName  | <Нова фамилия>  |

g.      Изтрийте Вашите данни, като изпратите заявка с метод DELETE с query параметър Вашият факултетен номер.

h.      Отново изпратете GET заявка, с която да изведете Вашите данни. За целта подайте факултетният си номер като query параметър.

i.       Изпълнете отново условие f.
