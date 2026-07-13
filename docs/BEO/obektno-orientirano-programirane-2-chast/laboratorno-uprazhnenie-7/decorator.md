---
layout: default
title: Decorator
parent: Лабораторно упражнение 7
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 3
---

# Decorator

### Проблем

Да се разработи система за обработка на съобщения.

В зависимост от конкретната ситуация към дадено съобщение може да бъде добавен времеви маркер, съобщението може да бъде криптирано или компресирано. Възможно е към едно и също съобщение да бъдат приложени няколко обработки едновременно.

### Решение без използване на шаблона

Един възможен подход е за всяка различна обработка да бъде създаден отделен клас.

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
public class TimestampMessage implements Message {

    private Message message;

    public TimestampMessage(Message message) {
        this.message = message;
    }

    @Override
    public String getContent() {
        return "[Timestamp] " + message.getContent();
    }
}
```
```java
public class EncryptedMessage implements Message {

    private Message message;

    public EncryptedMessage(Message message) {
        this.message = message;
    }

    @Override
    public String getContent() {
        return "Encrypted[" + message.getContent() + "]";
    }
}
```
На пръв поглед това решение работи, но при комбиниране на няколко обработки започва да се появява проблем. Ако не се използва общ механизъм за обгръщане, може да се стигне до създаване на класове за различни комбинации:
```java
public class TimestampEncryptedMessage implements Message {

    private String content;

    public TimestampEncryptedMessage(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return "Encrypted[[Timestamp] " + content + "]";
    }
}
```
При добавяне на нови обработки ще се появяват все повече комбинации:
```java
TimestampMessage
EncryptedMessage
CompressedMessage
TimestampEncryptedMessage
TimestampCompressedMessage
EncryptedCompressedMessage
TimestampEncryptedCompressedMessage
...
```

### Недостатъци на решението

При този подход броят на класовете нараства бързо, защото всяка нова обработка може да се комбинира с вече съществуващите. Това усложнява структурата на приложението и затруднява поддръжката.

Освен това част от логиката започва да се дублира в класовете, които представят различни комбинации от обработки. При промяна в начина на криптиране, компресиране или добавяне на времеви маркер може да се наложи промяна на повече от един клас.

Следователно е необходимо решение, което позволява обработките да се добавят динамично и да се комбинират свободно, без създаване на отделен клас за всяка възможна комбинация.


### Шаблонът като решение

Шаблонът **Decorator** позволява динамично добавяне на ново поведение към обект чрез обгръщането му в друг обект със същия интерфейс.

Всеки декоратор съдържа референция към обекта, който декорира, и може да добави ново поведение преди или след извикването на неговия метод.

### Дефиниция

Decorator е структурен шаблон за проектиране, който позволява към даден обект динамично да бъдат добавяни нови отговорности, без да се променя неговият клас.

### UML диаграма

<img width="1232" height="390" alt="Decorator" src="https://github.com/user-attachments/assets/2176bc90-364e-4ccc-a978-1286968f6a68" />


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
public class TimestampMessageDecorator extends MessageDecorator {

    public TimestampMessageDecorator(Message message) {
        super(message);
    }

    @Override
    public String getContent() {
        return "[" + LocalDateTime.now() + "] " + super.getContent();
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
