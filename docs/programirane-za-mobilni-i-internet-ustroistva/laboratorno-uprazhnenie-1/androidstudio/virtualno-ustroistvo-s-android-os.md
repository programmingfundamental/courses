---
layout: default
title: Среда за разработване
parent: Лабораторно упражнение 1
grand_parent: Програмиране за мобилни и Интернет устройства
has_children: true
nav_order: 2
permalink: /docs/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-1/osnovi-naprogramiraneto-s-java-za-nachinaeshi
---

# Виртуално устройство с Android OS

Емулатора на Android устройства позволява да се тестват и дебъгват приложения преди качването им на реални устройства.

Създаването на Android устройства става с Android Virtual Device (AVD) Manager. По подразбиране има инсталирано едно устройство с последната версия на Android.

Създаването на виртуални устройства става с помощта на помощник за инсталация където се избират размера на дисплея, оперативната и физическата памет на устройството допълнителни сензори от които се нуждаем за тестване на приложението.

## Проблеми при стартирането на емулатора

За ефективна работа с емулатора и Android Studio е необходима минимум 8GB оперативна памет на компютъра за разработка.

За да работи емулатора:

* Трябва да се активира технологията Intel® Virtualization Technology от BOIS или съответния еквивалент AMD Virtualization за AMD процесорите
* Инсталиране на Intel x86 Emulator Accelerator от Android SDK Manager ![Android\_Studio\_NewProject\_Screen\_1.PNG](https://github.com/theVelislavKolesnichenko/AndroidBasics/raw/master/Wiki/Images/Android\_SDK\_Manager\_1.PNG)
* От AMD Manager отворете виртуалното устройство в режим за редактиране където изключете Use Host GPU и включете Snapshot, по този начин устройството ще се стартира от изображение вместо да се изпълнява всеки път при стартиране.
