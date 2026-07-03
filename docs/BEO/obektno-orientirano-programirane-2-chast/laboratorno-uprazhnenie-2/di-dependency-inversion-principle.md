---
layout: default
title: DI - Dependency Inversion Principle
parent: Лабораторно упражнение 2
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 5
---

# DI - Dependency Inversion Principle

## Принцип за обръщане на зависимостите

Принципът за обръщане на зависимостите гласи, че класовете от високо ниво не трябва да зависят от конкретни класове от ниско ниво. И двата вида класове трябва да зависят от абстракции.

**Абстракциите не трябва да зависят от детайлите. Детайлите трябва да зависят от абстракциите.**

На практика абстракциите и конкретните реализации често се разполагат в различни пакети или модули. По този начин класовете от високо ниво зависят единствено от интерфейсите, а конкретните реализации могат лесно да бъдат заменяни.

### Проблем

Следният пример показва клас NotificationService, който изпраща съобщения чрез конкретна реализация.

```java
public class EmailSender {

    public void send(String message) {
        System.out.println("Email: " + message);
    }
}
```
```java
public class NotificationService {

    private EmailSender emailSender;

    public NotificationService() {
        this.emailSender = new EmailSender();
    }

    public void notifyUser(String message) {
        emailSender.send(message);
    }
}
```
Класът NotificationService зависи директно от конкретния клас EmailSender. Ако по-късно трябва да се използва SMS, push notification или друг начин за изпращане на съобщения, класът NotificationService трябва да бъде променен.

### Решение

Вместо зависимост към конкретен клас се въвежда абстракция.

```java
public interface MessageSender {

    void send(String message);
}
```
```java
public class EmailSender implements MessageSender {

    @Override
    public void send(String message) {
        System.out.println("Email: " + message);
    }
}
```
```java
public class SmsSender implements MessageSender {

    @Override
    public void send(String message) {
        System.out.println("SMS: " + message);
    }
}
```
```java
public class NotificationService {

    private MessageSender messageSender;

    public NotificationService(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void notifyUser(String message) {
        messageSender.send(message);
    }
}
```
Използване:
```java
public class Application {

    public static void main(String[] args) {
        MessageSender sender = new EmailSender();
        NotificationService service = new NotificationService(sender);

        service.notifyUser("Your order has been shipped.");
    }
}
```

При тази реализация NotificationService не зависи от конкретна реализация. Той работи с интерфейса MessageSender, а конкретният начин за изпращане се подава отвън.

### Предимства

Прилагането на DIP води до:
* намаляване на зависимостите между класовете;
* по-лесна подмяна на реализации;
* по-лесно тестване;
* по-добра гъвкавост;
* по-добра поддръжка на кода.

Именно този принцип стои в основата на повечето шаблони за проектиране (например Strategy, Factory Method, Abstract Factory, Adapter и други), както и на механизмите за Dependency Injection, използвани във фреймуърци като Spring.

### Недостатъци / трудности

DIP изисква въвеждане на абстракции, което може да направи малките приложения излишно сложни. Принципът трябва да се прилага там, където съществува реална нужда от подмяна, разширяване или тестване на зависимостите.

### Приложение

DIP се използва при:
* dependency injection;
* service/repository архитектура;
* Spring приложения;
* работа с външни услуги;
* тестване чрез mock обекти.


# Обобщение

| Принцип   | Основна идея                                                                                       |
|-----------|----------------------------------------------------------------------------------------------------|
| SRP       | Един клас трябва да има една ясно определена отговорност.                                          |
| OCP       | Кодът трябва да може да се разширява без промяна на вече съществуващата логика.                    |
| LSP       | Наследниците трябва да могат да заменят родителските си класове без нарушаване на поведението.     |
| ISP       | По-добре няколко малки интерфейса, отколкото един голям общ интерфейс.                             |
| DIP       | Класовете трябва да зависят от абстракции, а не от конкретни реализации.                           |

SOLID принципите подпомагат създаването на по-гъвкав, разбираем и поддържан код. Те не трябва да се прилагат механично, а според конкретния проблем, размера на проекта и очакваните промени.
