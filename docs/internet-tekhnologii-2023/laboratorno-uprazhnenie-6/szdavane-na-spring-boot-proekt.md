# Създаване на Spring boot проект

### Използване на Spring Initializr

Има много начини за създаване на Spring Boot приложение. Най-лесният начин е с помощта на Spring Initializr на https://start.spring.io/, онлайн генератор на приложения за Spring Boot.

<figure><img src="../../assets/image (50).png" alt=""><figcaption></figcaption></figure>

1\.    Изберете Maven Project и версия на Spring Boot

2\.     Въведете подробностите за проекта Maven, както следва:

a.     Група: bg.tu-varna.sit

b.    Артефакт: tasks-manager

c.     Име на пакета: bg.tu\_varna.sit.tasks\_manager

d.    Пакет: JAR

e.    Версия на Java: 17

f.      Език: Java

3\.    Щракнете върху бутона Добавяне на зависимости. Можете да потърсите стартери, ако вече сте запознати с имената им. Ще видите много стартови модули, организирани в различни категории, като Core, Web и Data. Изберете Spring Web.

4\.    Щракнете върху бутона Генериране на проект. Разархивирайте изтегления ZIP файл и отворете проекта в предпочитаното от Вас IDE.

### Използване на Spring Tool Suite

Spring Tool Suite (STS: https://spring.io/tools) е разширение за широко използвани IDE като Eclipse и Visual Studio Code и съдържа множество свързани със Spring framework добавки. Можете лесно да създадете Spring Boot приложение с Eclipse варианта на STS като изберете File ➤ New ➤ Other ➤ Spring Boot ➤ Spring Starter Project ➤ Next. Ще достъпите съветника, който изглежда подобно на Spring Initializr.
