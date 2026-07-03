---
layout: default
title: Mediator
parent: Лабораторно упражнение 12
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# Mediator

### Проблем

В практиката често има група обекти, които трябва да комуникират помежду си. Ако всеки обект пази референции към всички останали, системата става силно свързана и трудна за поддръжка.

Например в чат приложение всеки потребител може да изпраща съобщения до останалите. Ако всеки потребител трябва да познава всички други потребители, добавянето, премахването или промяната на участници ще усложни кода.

### Решение

Шаблонът Mediator въвежда посредник, през който минава комуникацията между обектите.

Вместо обектите да комуникират директно помежду си, те изпращат съобщения към медиатора, а той решава как да ги разпространи.

### Дефиниция

Mediator е поведенчески шаблон за проектиране, който централизира комуникацията между група обекти чрез посредник и намалява директните зависимости между тях.

Основни участници:
* Mediator - Интерфейсът, който дефинира начина за комуникация между обектите.
* Concrete Mediator - Конкретна реализация на посредника, която координира взаимодействието.
* Colleague - Обект, който комуникира с други обекти чрез медиатора.
* Concrete Colleague - Конкретна реализация на участник в комуникацията.

### UML диаграма

<img width="546" height="581" alt="Mediator" src="https://github.com/user-attachments/assets/26ab159d-e303-4d40-b896-4ffbf153065d" />


### Примерна реализация

Mediator
```java
public interface ChatMediator {

    void addUser(User user);

    String sendMessage(String message, User sender);
}
```
Concrete Mediator
```java
import java.util.ArrayList;
import java.util.List;

public class ChatRoom implements ChatMediator {

    private List<User> users = new ArrayList<>();

    @Override
    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public String sendMessage(String message, User sender) {
        StringBuilder result = new StringBuilder();
        for (User user : users) {
            if (user != sender) {
                result.append(user.receive(message, sender.getName()))
                        .append("\n");
            }
        }
        return result.toString();
    }
}
```
Colleague
```java
public abstract class User {

    private String name;
    private ChatMediator mediator;

    public User(String name, ChatMediator mediator) {
        this.name = name;
        this.mediator = mediator;
        mediator.addUser(this);
    }

    public String getName() {
        return name;
    }

    public String send(String message) {
        return mediator.sendMessage(message, this);
    }

    public String receive(String message, String sender) {
        return name + " received message from "
                + sender + ": " + message;
    }
}
```
Concrete Colleague
```java
public class ChatUser extends User {

    public ChatUser(String name, ChatMediator mediator) {
        super(name, mediator);
    }
}
```
Използване
```java
public class Application {

    public static void main(String[] args) {

        ChatMediator chatRoom = new ChatRoom();

        User firstUser = new ChatUser("John", chatRoom);

        User secondUser = new ChatUser("Anna", chatRoom);

        User thirdUser = new ChatUser("Peter", chatRoom);

        System.out.println(firstUser.send("Hello!"));
        System.out.println(secondUser.send("Hi, John!"));
    }
}
```

Интерфейсът ChatMediator дефинира действията, чрез които потребителите могат да бъдат регистрирани и да изпращат съобщения.

Класът ChatRoom е конкретният медиатор. Той пази списък от потребители и координира изпращането на съобщения между тях.

Класът User представлява участник в комуникацията. Той не познава останалите потребители и не изпраща съобщения директно към тях. Вместо това използва медиатора чрез метода send().

Класът ChatUser е конкретен потребител. Всички потребители комуникират чрез един и същ медиатор.

> [!IMPORTANT]
> Mediator не премахва комуникацията между обектите, а я централизира. Вместо всеки обект да
> познава всички останали, всички участници познават само медиатора.

### Предимства

* намалява директните зависимости между обектите;
* централизира логиката за комуникация;
* улеснява добавянето и премахването на участници;
* прави отделните обекти по-независими един от друг.

### Недостатъци

* медиаторът може да стане твърде сложен, ако поеме прекалено много логика;
* добавя допълнително ниво на абстракция;
* всички участници зависят от медиатора;
* не е оправдан при малък брой обекти с проста комуникация.

### Приложение

Mediator е подходящ когато:
* много обекти трябва да комуникират помежду си;
* директните зависимости между тях стават твърде много;
* логиката за комуникация трябва да бъде централизирана;
* се реализират чат системи, GUI компоненти, контролери, диалогови прозорци или координация между модули.

