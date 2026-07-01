---
layout: default
title: Decorator
parent: Лабораторно упражнение 7
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 3
---

# Decorator

### Проблем

В практиката често се налага към даден обект да бъдат добавяни нови функционалности, без да се променя неговият клас. Ако всяка възможна комбинация от функционалности се реализира чрез наследяване, броят на класовете може бързо да нарасне.

### Решение

Шаблонът **Decorator** позволява динамично добавяне на ново поведение към обект чрез обгръщането му в друг обект със същия интерфейс.

Всеки декоратор съдържа референция към обекта, който декорира, и може да добави ново поведение преди или след извикването на неговия метод.

### Дефиниция

Decorator е структурен шаблон за проектиране, който позволява към даден обект динамично да бъдат добавяни нови отговорности, без да се променя неговият клас.

### UML диаграма

<img width="1202" height="390" alt="Decorator" src="https://github.com/user-attachments/assets/0e8c867a-b0be-4059-96fc-11b04eb8a26b" />

### Примерна реализация

```java
public interface Message {

    String getContent();
}
```
```java
public class PlainMessage implements Message {

    private String content;

    public PlainMessage(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }
}
```
```java
public abstract class MessageDecorator implements Message {

    private Message message;

    public MessageDecorator(Message message) {
        this.message = message;
    }

    @Override
    public String getContent() {
        return message.getContent();
    }
}
```
```java
public class EncryptedMessageDecorator extends MessageDecorator {

    public EncryptedMessageDecorator(Message message) {
        super(message);
    }

    @Override
    public String getContent() {
        return encrypt(super.getContent());
    }

    private String encrypt(String content) {
        return "Encrypted[" + content + "]";
    }
}
```
```java
public class SignedMessageDecorator extends MessageDecorator {

    public SignedMessageDecorator(Message message) {
        super(message);
    }

    @Override
    public String getContent() {
        return super.getContent() + " | Signed";
    }
}
```
```java
public class CompressedMessageDecorator extends MessageDecorator {

    public CompressedMessageDecorator(Message message) {
        super(message);
    }

    @Override
    public String getContent() {
        return "Compressed[" + super.getContent() + "]";
    }
}
```
Използване
```java
public class Application {

    public static void main(String[] args) {

        Message message = new PlainMessage("System report");

        message = new SignedMessageDecorator(message);
        message = new EncryptedMessageDecorator(message);

        System.out.println(message.getContent());

        Message secondMessage = new CompressedMessageDecorator(
                new EncryptedMessageDecorator(
                        new PlainMessage("Backup data")
                )
        );

        System.out.println(secondMessage.getContent());
    }
}
```
Класът PlainMessage представя основния обект, към който могат да се добавят допълнителни функционалности.

Абстрактният клас MessageDecorator реализира същия интерфейс Message и съдържа референция към друг обект от същия тип. Това позволява декораторите да бъдат използвани навсякъде, където се очаква обект от тип Message.

Всеки конкретен декоратор добавя собствена обработка към резултата, върнат от декорирания обект. По този начин функционалностите могат да се комбинират динамично.

> [!IMPORTANT]
> Подобно на Adapter, Decorator използва делегиране чрез референция към друг обект. В
> литературата това често се нарича *object composition*, но не трябва да се бърка с
> композицията като UML отношение „част–цяло“.

### Предимства

* позволява динамично добавяне на функционалности към обекти;
* избягва създаването на голям брой наследници;
* спазва принципа Open/Closed;
* позволява комбиниране на различни функционалности;
* не променя оригиналния клас.

### Недостатъци

* увеличава броя на класовете;
* при много декоратори веригата от обекти може да стане трудна за проследяване;
* редът на прилагане на декораторите може да влияе върху крайния резултат;
* отстраняването на грешки може да бъде по-трудно при дълги вериги от декоратори.

### Приложение

Decorator се използва когато:
* е необходимо динамично добавяне на поведение към обект;
* наследяването би довело до твърде много класове;
* функционалностите трябва да могат да се комбинират свободно;
* не трябва да се променя съществуващият клас.
