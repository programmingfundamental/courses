---
title: Android базирани технологии за мобилни устройства
sidebar:
  order: 14
---

Практически курс за магистри с **7 лабораторни упражнения по 135 минути**. В общия Kotlin проект **Mobile Context Monitor** ще управлявате state и lifecycle, ще съхранявате локални данни, ще използвате Android Services и WorkManager, ще работите със sensors, location и BLE и ще измервате ресурсите на приложението.

Всяко занятие включва мини експеримент, водена практическа задача, **самостоятелна задача в часа**, edge cases, тестване и анализ. Самостоятелната работа изисква собствено инженерно решение и е част от 135-те минути.

## Лабораторни упражнения

| № | Тема | Основен инженерен въпрос |
|---|---|---|
| 1 | [Android Lifecycle, State и Jetpack Compose](/courses/bg/android-bazirani-tekhnologii-za-mobilni-ustroistva/laboratorno-uprazhnenie-1/) | Кой притежава state и какво се възстановява след recreation? |
| 2 | [Architecture и Local Persistence](/courses/bg/android-bazirani-tekhnologii-za-mobilni-ustroistva/laboratorno-uprazhnenie-2/) | Как UI наблюдава една local source of truth? |
| 3 | [Android Service, Foreground Service и WorkManager](/courses/bg/android-bazirani-tekhnologii-za-mobilni-ustroistva/laboratorno-uprazhnenie-3/) | Кога са нужни Service, foreground notification и binding, а кога — persistent work? |
| 4 | [Sensors и обработка на физически данни](/courses/bg/android-bazirani-tekhnologii-za-mobilni-ustroistva/laboratorno-uprazhnenie-4/) | Как шумните measurements се превръщат в полезни събития? |
| 5 | [Location и Context-Aware Applications](/courses/bg/android-bazirani-tekhnologii-za-mobilni-ustroistva/laboratorno-uprazhnenie-5/) | Как точност, permissions и battery ограничават проследяването? |
| 6 | [Bluetooth Low Energy](/courses/bg/android-bazirani-tekhnologii-za-mobilni-ustroistva/laboratorno-uprazhnenie-6/) | Как управляваме asynchronous GATT workflow и загубена връзка? |
| 7 | [Performance, Energy и Robustness](/courses/bg/android-bazirani-tekhnologii-za-mobilni-ustroistva/laboratorno-uprazhnenie-7/) | Кои оптимизации намаляват разходите при запазено качество на данните? |

## Подготовка

Необходими са Kotlin, ООП, coroutines и начален опит с Android. Използват се Android Studio, Jetpack Compose, ViewModel, Flow/StateFlow, Room, DataStore, Android Service/Foreground Service и WorkManager. Упражнение 3 включва Service lifecycle, local Binder, notification със Stop, cancellation и сравнение с persistent scheduled work. UI служи за управление и наблюдение на системата.

Първите три упражнения могат да се проведат изцяло на emulator. За sensors, location и profiling физическо устройство е силно препоръчително. Реалният BLE вариант изисква Android API 37 central и съвместим peripheral; при липса на хардуер или по-стар device се използва fake transport със същите failure сценарии. Hardware упражненията включват replay/mock вариант и отбелязват ограниченията на симулираните измервания.
