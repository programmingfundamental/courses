---
layout: default
title: Singleton
parent: Лабораторно упражнение 3
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# Singleton

### Проблем

Да се разработи клас Logger, който записва съобщенията, генерирани от различните модули на приложение.

### Решение без използване на шаблона 

Най-естественото решение е използването на обикновен клас:

```java
import java.util.ArrayList;
import java.util.List;

public class Logger {

    private List<String> messages = new ArrayList<>();

    public void log(String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }
}
```

Клиентски код:

```java
public class Application {

    public static void main(String[] args) {

        Logger authenticationLogger = Logger.getInstance();
        Logger paymentLogger = Logger.getInstance();

        authenticationLogger.log("User login");
        paymentLogger.log("Payment completed");

        for (String message : authenticationLogger.getMessages()) {
            System.out.println(message);
        }
    }
}
```
Резултат:
```java
User login
```

### Недостатъци на решението

В представения пример променливите *authenticationLogger* и *paymentLogger* изглеждат като различни логъри, но всъщност е желателно всички съобщения да се записват на едно място. Ако всеки модул създава собствен обект Logger, управлението на записите се затруднява и се губи централизацията на логването.

Следователно е необходимо решение, което да гарантира използването на един и същ обект от всички части на приложението.

### Шаблонът като решение

Шаблонът **Singleton** гарантира, че от даден клас може да съществува само една инстанция и предоставя глобална точка за достъп до нея.

Това се реализира чрез:
* частен конструктор, който не позволява създаване на обекти извън класа;
* статично поле, което съхранява единствената инстанция;
* публичен статичен метод, който връща тази инстанция.

### Дефиниция

Singleton е шаблон за проектиране от групата на **шаблоните за създаване (Creational Design Patterns)**. Неговата цел е да гарантира наличието на единствен обект от даден клас и да осигури контролиран глобален достъп до него.

Всички реализации на шаблона следват две основни изисквания:
* конструкторът е деклариран като private;
* единствената инстанция се достъпва чрез публичен статичен метод.

### UML диаграма

<img width="267" height="282" alt="Singleton" src="https://github.com/user-attachments/assets/d080398b-160d-436a-98b4-85125b483e65" />


### Примерна реализация (Lazy Initialization)

```java
import java.util.ArrayList;
import java.util.List;

public class Logger {

    private static Logger instance;

    private List<String> messages = new ArrayList<>();

    private Logger() {
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return messages;
    }
}
```
Използване:

```java
public class Application {

    public static void main(String[] args) {

        Logger authenticationLogger = Logger.getInstance();
        Logger paymentLogger = Logger.getInstance();

        authenticationLogger.log("User login");
        paymentLogger.log("Payment completed");

        for (String message : authenticationLogger.getMessages()) {
            System.out.println(message);
        }
    }
}
```
Конструкторът на класа е деклариран като private, поради което създаването на обекти чрез оператора new извън класа е невъзможно.

Единствената инстанция се съхранява в статичното поле instance. При първото извикване на метода getInstance() обектът се създава и се записва в това поле. При всички следващи извиквания се връща вече съществуващият обект.

По този начин всички части на приложението използват един и същ обект Logger, което позволява централизирано управление на процеса на записване.


**Реализации на Singleton**

| Реализация             | Характеристика                                                                                     |
|------------------------|----------------------------------------------------------------------------------------------------|
| Eager Initialization   | Инстанцията се създава при зареждане на класа. Подходящо, когато обектът винаги ще бъде използван. |
| Lazy Initialization    | Инстанцията се създава при първото извикване на getInstance().                                     |
| Thread-safe Singleton  | Използва синхронизация, за да бъде безопасен при работа с множество нишки.                         |
| Enum Singleton         | Реализира се чрез enum; прост и надежден вариант, препоръчван в съвременна Java.                   |

Разглежданата в настоящото упражнение реализация е най-подходяща за илюстриране на основната идея на шаблона.

> [!IMPORTANT]
> Singleton гарантира една инстанция на клас и глобална точка за достъп до нея. Това обаче не означава, че шаблонът
> трябва да се използва винаги, когато е необходим „общ“ обект. Прекомерното му използване може да доведе до скрити
> зависимости и затруднено тестване.

### Предимства

* гарантира съществуването на единствена инстанция;
* предоставя глобална точка за достъп;
* централизира управлението на споделени ресурси;
* предотвратява случайното създаване на множество обекти.

### Недостатъци на шаблона

* нарушава принципа *Single Responsibility Principle (SRP)*, тъй като класът едновременно управлява собствената си логика и жизнения си цикъл;
* създава глобално състояние в приложението;
* затруднява модулното тестване;
* при неправилна реализация може да създаде проблеми в многонишкова среда.

### Приложение

Singleton е подходящ при разработване на:
* конфигурационни мениджъри;
* логващи системи;
* кеширащи компоненти;
* управление на връзки към база данни;
* управление на споделени системни ресурси.
