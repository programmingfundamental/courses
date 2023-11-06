---
layout: default
title: Задача
parent: Лабораторно упражнение 6
grand_parent: Програмиране за мобилни и Интернет устройства
nav_order: 3
---

# Задача

### Задача 1

Да се разработи мобилно приложение за създаване на картички за рожден ден. Да се предвиди:

BirthdayCardFragment, с помощта на който ще бъде изобразявана изготвената картичка;

Едно Activity, което да съдържа един FrameLayout и 2 бутона:

Button Add - извиква DialogFragment, посредством който се въвеждат данните, необходими за оформянето на картичката: име на рожденика, навършвана възраст, текст с пожелание и желан цвят на фона на картичката (тук може да се добави и изображение с Пикасо). При натискане на бутона за потвърждение да се добави нова инстанция на BirthdayCardFragment към frame, чрез която по подходящ начин се визуализират въведените данни.

Button Remove - да премахва последно създадената картичка.

### Задача 2 (Допълнителна)

#### Fragments. FragmentTransaction. FragmentManager.

Целта на настоящото упражнение е да се запознаем изначално с lifecycle на Fragment. Както и с функционалностите на FragmentTransaction и FragmentManager, позволяващи ни инстанцирането, премахването и др. операции свързани с фрагменти.

Да се създаде **ColorFragment**, който приема един параметър – Integer (argb) цвят и го изобразява по подходящ начин:

Activity, което съдържа 2 FrameLayouts и 3 бутона. Със следните функционалности, използвайки **FragmentManager** и **FragmentTransaction**:

* Button Add (add new ColorFragment Instance to frame 1) – добавя нова инстанция на фрагмента към frame 1, новата инстанция да се инстанцира със случаен цвят.
* Button Transfer (transfer Fragment Instance from frame 1 to frame 2) – да се премахне последната инстанция на фрагмент от frame1, като същата се добави към frame2. Да се проверява има ли изобщо добавени фрагменти във frame 1.
* Button Remove (remove Fragment Instance from frame 2) - премахва най-горната инстанция на фрагмент от frame2.

```
   // Generate random color
   Random rnd = new Random();
   Integer color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

   // set hex formated string from rgba Integer
   textView.setText( String.format("#%06X", (0xFFFFFF & color)) );
```
