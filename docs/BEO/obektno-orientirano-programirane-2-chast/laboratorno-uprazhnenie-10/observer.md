---
layout: default
title: Observer
parent: Лабораторно упражнение 10
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 1
---

# Observer

### Проблем

В практиката често се налага няколко обекта да реагират при промяна в състоянието на друг обект. Ако наблюдаваният обект познава директно всички конкретни класове, които трябва да бъдат уведомени, системата става силно свързана и трудна за разширяване.

### Решение

Observer позволява един обект да поддържа списък от зависими обекти и автоматично да ги уведомява при настъпване на промяна.

Наблюдаваният обект работи с общ интерфейс на наблюдателите, без да зависи от конкретните им класове.

### Дефиниция

Observer е поведенчески шаблон за проектиране, който дефинира зависимост „един към много“ между обекти, така че при промяна в състоянието на един обект всички негови наблюдатели да бъдат уведомени автоматично.

Основни участници:
* Subject - Обектът, който се наблюдава и уведомява регистрираните наблюдатели.
* Observer - Интерфейсът, чрез който наблюдателите получават известие.
* Concrete Subject - Конкретна реализация на наблюдавания обект.
* Concrete Observer - Конкретен наблюдател, който реагира на промяната.

### UML диаграма

<img width="332" height="579" alt="Observer" src="https://github.com/user-attachments/assets/fbb4b3c6-5fce-4706-9b1b-7ca0ab4670c5" />


### Примерна реализация

```java
public interface Observer {

    String update(String message);
}
```
```java
public interface Subject {

    void register(Observer observer);

    void unregister(Observer observer);

    String notifyObservers(String message);
}
```
```java
import java.util.HashSet;
import java.util.Set;

public class Topic implements Subject {

    private Set<Observer> observers = new HashSet<>();

    @Override
    public void register(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void unregister(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public String notifyObservers(String message) {
        StringBuilder result = new StringBuilder();

        for (Observer observer : observers) {
            result.append(observer.update(message)).append("\n");
        }

        return result.toString();
    }

    public String postMessage(String message) {
        return notifyObservers(message);
    }
}
```
```java
public class TopicSubscriber implements Observer {

    private String name;

    public TopicSubscriber(String name) {
        this.name = name;
    }

    @Override
    public String update(String message) {
        return name + " received message: " + message;
    }
}
```
Използване
```java
public class Application {

    public static void main(String[] args) {

        Topic topic = new Topic();

        Observer firstSubscriber = new TopicSubscriber("First subscriber");

        Observer secondSubscriber = new TopicSubscriber("Second subscriber");

        topic.register(firstSubscriber);
        topic.register(secondSubscriber);

        System.out.println(topic.postMessage("New message"));
    }
}
```
Класът Topic е наблюдаваният обект. Той поддържа колекция от регистрирани наблюдатели и ги уведомява при публикуване на ново съобщение.

Интерфейсът Observer дефинира метода update(), чрез който всеки наблюдател получава информация за настъпилата промяна.

Класът TopicSubscriber е конкретен наблюдател. При получаване на съобщение той връща текст, който показва, че е бил уведомен.

Клиентският код регистрира наблюдателите към обекта Topic. След това при извикване на postMessage() всички регистрирани наблюдатели получават съобщението автоматично.

> [!IMPORTANT]
> Observer намалява зависимостта между наблюдавания обект и конкретните наблюдатели. Topic не знае какви точно
> класове са регистрирани към него — той работи само с интерфейса Observer.

### Предимства

* намалява зависимостта между обектите;
* позволява динамично добавяне и премахване на наблюдатели;
* един обект може да уведомява много други обекти;
* улеснява разширяването на системата с нови реакции при промяна.

### Недостатъци

* при голям брой наблюдатели уведомяването може да стане по-бавно;
* редът на уведомяване не винаги е гарантиран;
* може да бъде по-трудно да се проследи кой обект реагира на дадена промяна;
* при неправилно отписване на наблюдатели могат да се задържат ненужни референции.

### Приложение

Observer е подходящ когато:
* промяна в един обект трябва да води до реакция в други обекти;
* броят на заинтересованите обекти може да се променя динамично;
* наблюдаваният обект не трябва да зависи от конкретните наблюдатели;
* се реализират събития, известия, абонаменти или listener механизми.



