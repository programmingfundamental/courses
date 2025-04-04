---
layout: default
title: Упражнения за извънаудиторна заетост
parent: Лабораторно упражнение 6
grand_parent: Обектно-ориентирано програмиране - 1 част
has_children: true
nav_order: 2
---
# Упражнения за извънаудиторна заетост

### Задача 1

Дефинирайте клас Shape с абстрактен метод calculateSurface(), getName() и toString(), който извежда името и площта на фигурата, полета width и height. Дефинирайте два нови класа за триъгълник (Triangle) и правоъгълник (Rectangle), които наследяват класа Shape. Методът calculateSurface трябва да връща площта на правоъгълника (height\*_width) и триъгълника (height\*_width/2). Дефинирайте клас за кръг с подхо­дящ конструктор, при когото при инициализация и двете полета (height и width) са с еднаква стойност (радиуса) и имплементирайте метода за изчисляване площта. Направете масив от различни фигури и изведете текстова информация за тях.

### Задача 2

Дефинирайте интерфейс движение (Movement) с метод move - връща като резултат типа движение като текст.

Дефинирайте класове Dog, Bird и Fish. Имплементирайте методите от интерфейса.

Създайте масив с кучета, птици и риби и изведете начина, по който се движат.

### Задача 3

Дефинирайте интерфейс движение (Movement) с метод move - връща като резултат типа на движението като текст.

Дефинирайте интерфейси Домашно животно (Pet) и Диво животно (Wild).

Дефинирайте абстрактен клас Animal наследяващ интерфейса Movement, който има основни характеристики за животно (напр. Име, възраст ..). Дефинирайте абстрактен метод sound(). Създайте метод за текстово представяне на обекта.

Дефинирайте класове наследници на Animal – Dog, Bird и Fish. Имплементирайте методите от интерфейса и абстрактния клас.

Създайте масив с кучета, птици и риби и изведете начина, по който се движат и звука, който издават. Според интерфейса, който имплементират, изведете дали са диви или питомни.

### Задача 4

Дефинирайте интерфейс за превозно средство (Vehicle), който има 3 метода:

* changeGear, който приема параметър за брой предавки;
* speedUp - с колко се ускорява;
* applyBrakes - с колко се намалява.

Дефинирайте два класа, който имплементират интерфейса за превозно средство: Колело и Кола.

* За класа Колело при изчисляване на ускорението да се вземе предвид теглото на водача.
* При изчисление на ускорението на колата да се вземе предвид нейната мощност.

В main метода дефинирайте две променливи Vehicle, като им присвоите различни обекти (Колело и Кола). Използвайте всички методи в класовете и изведете резултатите.

### Бонус

Дадена банка предлага различни типове сметки за нейните клиенти: депозитни сметки, сметки за кредит и ипотечни сметки. Клиентите могат да бъдат физически лица или фирми. Всички сметки имат клиент, баланс и месечен лихвен процент. Депозитните сметки дават възможност да се внасят и теглят пари. Сметките за кредит и ипотечните сметки позволяват само да се внасят пари. Всички сметки могат да изчисляват стойността на лихвата си за даден период (в месеци). В общия случай това става като се умножи броят\_на\_месеците \* месечния\_лихвен\_процент. Кредитните сметки нямат лихва за първите три месеца, ако са на физически лица. Ако са на фирми – нямат лихва за първите два месеца. Депозитните сметки нямат лихва, ако техният баланс е положителен и по-малък от 1000. Ипотечните сметки имат ½ лихва за първите 12 месеца за фирми и нямат лихва за първите 6 месеца за физически лица. Вашата задача е да напишете обектно-ориентиран модел на банковата система чрез класове и интерфейси. Трябва да моделирате класовете, интерфейсите, базовите класове и абстрактните операции и да имплементирате съответните изчисления за лихвите.
