---
layout: default
title: Задачи
parent: Лабораторно упражнение 9
grand_parent: Интернет технологии
nav_order: 5
---

# Задачи

1. В разработваното до момента приложение заменете съхранението в колекция с PostgreSQL база данни. Да се съхранява информация за датата и часа на създаване на съответната задача (dateCreated) и датата и часа на нейната актуализация (dateUpdated).  
2. Добавете възможност за създаване на отчети за изпълнение на задача.

По отношение на една задача могат да бъдат изпратени много отчети за изпълнение.

Да се съхранява следната информация: пореден номер на отчета (id), текстово описание на отчета (content), отработено време в часове (hoursWorked),  дата и час на създаване (dateCreated), дата и час на актуализиране (dateUpdated).

Предвидете функционалности за:

·     добавяне на отчет към дадена задача;

·     преглед на всички отчети към дадена задача;

·     преглед на отчет към дадена задача;

·     актуализация на отчет към дадена задача;

·     изтриване на отчет към дадена задача.

Опишете предвидените точки за достъп в таблицата по-долу.

<figure><img src="../../../assets/image (166).png" alt=""><figcaption></figcaption></figure>

3. Създайте методи за:

* извеждане на отчети по дадена задача с брой отработени часове в даден интервал;
* извеждане на отчета по дадена задача с най-голям брой отработени часове;
* изчисляване на отработените часове за изпълнение на задача по подаден номер.

