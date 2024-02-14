---
layout: default
title: Конфигуриране на PostgreSQL DB
parent: Лабораторно упражнение 10
grand_parent: Интернет технологии
nav_order: 1
---

# Конфигуриране на PostgreSQL DB

1. В pgAdmin създайте потребител и база от данни

<figure><img src="../../../assets/image (120).png" alt=""><figcaption></figcaption></figure>

<figure><img src="../../../assets/image (116).png" alt=""><figcaption></figcaption></figure>

<figure><img src="../../../assets/image (142).png" alt=""><figcaption></figcaption></figure>

<figure><img src="../../../assets/image (100).png" alt=""><figcaption></figcaption></figure>

<figure><img src="../../../assets/image (113).png" alt=""><figcaption></figcaption></figure>

За да активирате JPA в Spring Boot приложението, се нуждаем от зависимостта _spring-boot-starter-data-jpa_. Необходимо е да се добави и зависимост към JDBC драйвер, специфичен за базата данни, в нашия случай драйвера на PostgreSQL. Spring boot конфигурира Hibernate като JPA провайдер по подрабиране.

```xml
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
      <groupId>org.postgresql</groupId>
      <artifactId>postgresql</artifactId>
      <scope>runtime</scope>
    </dependency>
```

2. В application.properties добавете следните конфигурации:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/tasks-db
spring.datasource.username=myadmin
spring.datasource.password=mypassword
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto=update
```

Със свойството spring.jpa.hibernate.ddl-auto можете да зададете как Hibernate да реализира автоматичното генериране на схема. Възможни опции:

·       validate – извършва се проверка дали вече съществуващата схема съответства на предвидените entity.  Това е полезна опция, ако базата данни се управлява външно, но все пак искате да сте сигурни, че схемата отговаря на вашите очаквания.

·       create – създава схeма, като ако са налице предходно съществуващи данни, те се унищожават.

·       create-drop – изтрива схемата в края на сесията. Подходящо за провеждане на тестове.

·       update - Hibernate автоматично генерира схемата според предоставените Java entity. Работи на принципа на добавяне на нови изменения. Не се препоръчва да се използва при вече работещи приложения.

3\.      Добавете Data Source в Database (за IntelliJ IDEA Ultimate)

Data Source e местоположението на данните на вашето приложение. 

<figure><img src="../../../assets/image (125).png" alt=""><figcaption></figcaption></figure>

<figure><img src="../../../assets/image (124).png" alt=""><figcaption></figcaption></figure>

Тестваме връзката:

<figure><img src="../../../assets/image (90).png" alt=""><figcaption></figcaption></figure>

В прозореца Persistence:

<figure><img src="../../../assets/image (160).png" alt=""><figcaption></figcaption></figure>

<figure><img src="../../../assets/image (165).png" alt=""><figcaption></figcaption></figure>
