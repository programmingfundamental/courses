---
layout: default
title: Spring Boot
parent: Лабораторно упражнение 6
grand_parent: Интернет технологии
nav_order: 1
---
# Spring Boot

Spring Boot е фреймуърк, който помага на разработчиците да изграждат Spring-базирани приложения бързо и лесно. Той е основно разширение на Spring framework, като елиминира необходимостта от ръчно задаване на стериотипни конфигурации, изисквани за всяко Spring приложение.

Spring Boot дава началото си през април 2014 г., като неговото предназначение е намали част от тежестта на разработването на Java уеб приложения. Той позволява на разработчиците да се съсредоточат повече върху бизнес логиката, отколкото върху шаблонния технически код и свързаните с него конфигурации. С помощта на Spring Boot се създават Spring-базирани, готови за използване, самостоятелни приложения с необходимост от малки промени в конфигурацията от страна на разработчика. Той залага на opinionated view по отношение на Spring Framework, така че разработчиците да могат бързо да стартират приложенията с това, от което се нуждаят. Осигурява допълнителен слой между Spring Framework и потребителя, с което опростява определени аспекти на конфигурацията.

<figure><img src="../../../assets/image (18).png" alt=""><figcaption></figcaption></figure>

### Основни характеристики на Spring Boot

Spring Boot има няколко забележителни характеристики, които го отличават от останалите рамки:

* Бързо стартиране

Една от основните цели на Spring Boot е да осигури възможност за бързо стартиране на разработката на Spring приложения. Със Spring Boot можете да генерирате приложение, като посочите зависимостите, от които имате нужда във вашето приложение, а Spring Boot ще се погрижи за останалото.

* Автоматично конфигуриране

Spring Boot автоматично конфигурира минимума компоненти, необходими на Spring приложението. Той прави това на базата на наличните JAR файлове в classpath-а или на базата на свойства, зададени в property файлове. Например, ако Spring Boot открие наличието на JAR файл на драйвер на база данни (напр. in-memory БД H2) в classpath-а, той автоматично конфигурира съответния източник на данни за свързване към базата данни.

* Opinionated

Spring Boot е opinionated. Той автоматично конфигурира определен минимум от компоненти на  Spring приложението. Spring Boot се грижи за това с помощта на набор от начални зависимости. Т.нар. стартерни зависимости са насочени към конкретни области за развитие на приложението и предоставят свързани с тях зависимости. Например, ако разработвате уеб приложение, можете да конфигурирате spring-boot-starter-web зависимост, което гарантира, че всички свързани зависимости за разработване на уеб приложения, като spring-web и spring-webmvc, са налични в classpath-а на приложението.

* Standalone

Spring Boot приложенията вграждат в себе си уеб сървър, така че да могат да работят самостоятелно и не изискват  външен уеб сървър или сървър за приложения. Това позволява те да бъдат пакетирани като изпълним JAR файл и стартирани с командата java -jar. Това позволява Spring Boot приложенията лесно да бъдат контейнеризирани и развивани в облачното пространство.

* Production-ready

Spring Boot предоставя няколко полезни функции за наблюдение и управление на приложението, след като бъде пуснато в експлоатация, като health checks, thread dumps и други полезни показатели.
