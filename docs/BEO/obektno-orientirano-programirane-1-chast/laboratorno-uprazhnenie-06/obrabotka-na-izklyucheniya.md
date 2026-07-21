---
layout: default
title: Обработка на изключения
parent: Лабораторно упражнение 6
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 1
---

# Обработка на изключения


Изключение е обект, който описва проблем, възникнал по време на изпълнение на програма. Когато възникне изключение и то не бъде обработено, нормалното изпълнение на програмата се прекъсва.

```java
int result = 10 / 0;
```

В този пример възниква `ArithmeticException`, защото деление на нула не може да бъде извършено за цели числа.

## `try` и `catch`

Блокът `try` съдържа код, при който може да възникне изключение. Блокът `catch` съдържа код за обработка на конкретен тип изключение.

```java
try {
    int result = 10 / 0;
    System.out.println(result);
} catch (ArithmeticException exception) {
    System.out.println("Division by zero is not allowed.");
}
```

Ако в `try` възникне `ArithmeticException`, изпълнението преминава към `catch`. Програмата не прекъсва аварийно, а изпълнява предвидената обработка.

## Йерархия на изключенията

Всички изключения и грешки в Java наследяват класа `Throwable`. Двата основни наследника са `Error` и `Exception`.

`Error` описва сериозни проблеми на средата за изпълнение. Такива проблеми обикновено не се обработват в приложния код.

`Exception` описва ситуации, които могат да бъдат предвидени и обработени от програмата.

## Клас `Throwable`

`Throwable` е базовият клас за всички обекти, които могат да бъдат хвърляни и обработвани като проблеми по време на изпълнение. От него наследяват както `Exception`, така и `Error`.

Обект от тип изключение съдържа информация за възникналия проблем. Част от тази информация може да се достъпи чрез наследени методи.

| Метод | Предназначение |
| ----- | -------------- |
| `getMessage()` | Връща текстовото описание на изключението |
| `printStackTrace()` | Отпечатва информация за изключението и стека на извикванията |
| `getStackTrace()` | Връща стека на извикванията като масив от `StackTraceElement` |

```java
try {
    int number = Integer.parseInt("abc");
} catch (NumberFormatException exception) {
    System.out.println(exception.getMessage());
}
```

Методът `getMessage()` връща съобщението, свързано с конкретното изключение. Стекът на извикванията показва през кои методи е преминало изпълнението преди възникване на проблема.

## Проверявани (Checked) и непроверявани (unchecked) изключения

Проверяваните изключения (Checked) се проверяват от компилатора. Ако метод може да предизвика проверявано изключение,
компилаторът изисква това изключение да бъде обработено с `try-catch` или да бъде декларирано чрез `throws` в
сигнатурата на метода.

Непроверяваните изключения (unchecked) наследяват `RuntimeException`. Компилаторът не изисква задължителна обработка или деклариране на тези изключения, но те могат да бъдат обработени с `try-catch`, когато това е необходимо.

## `NullPointerException`

`NullPointerException` е unchecked изключение. То възниква, когато чрез референция със стойност `null` се направи опит
за достъп до поле или метод.

```java
String text = null;
System.out.println(text.length());
```

Променливата `text` не сочи към реален обект. Затова извикването на `length()` води до `NullPointerException`.

Това изключение показва, че преди използване на референцията трябва да бъде гарантирано, че тя сочи към обект.

```java
if (text != null) {
    System.out.println(text.length());
}
```

## `NumberFormatException`

`NumberFormatException` е unchecked изключение. То възниква, когато текст не може да бъде преобразуван до число.

```java
String value = "abc";
int number = Integer.parseInt(value);
```

Методът `Integer.parseInt(value)` очаква текст, който съдържа валидно цяло число. Текстът `"abc"` не може да бъде
преобразуван до `int`, затова възниква `NumberFormatException`.

```java
try {
    int number = Integer.parseInt(value);
    System.out.println(number);
} catch (NumberFormatException exception) {
    System.out.println("Invalid number.");
}
```

Този тип изключение се среща често при работа с входни данни, защото въведената стойност първоначално е текст.

## Няколко `catch` блока

Един `try` блок може да бъде последван от няколко `catch` блока. Всеки `catch` блок обработва различен тип изключение.

```java
try {
    int[] numbers = {1, 2, 3};
    int index = Integer.parseInt("5");
    System.out.println(numbers[index]);
} catch (NumberFormatException exception) {
    System.out.println("Invalid number.");
} catch (ArrayIndexOutOfBoundsException exception) {
    System.out.println("Invalid index.");
}
```

Ако текстът не може да бъде преобразуван до число, се изпълнява първият `catch` блок. Ако индексът е извън границите на масива, се изпълнява вторият `catch` блок.

Редът на `catch` блоковете има значение. По-специфичните типове трябва да бъдат поставени преди по-общите типове.

## Multi-catch

Когато няколко типа изключения трябва да бъдат обработени по един и същ начин, може да се използва `multi-catch`. Типовете се разделят със символа `|`.

```java
try {
    int[] numbers = {1, 2, 3};
    int index = Integer.parseInt("5");
    System.out.println(numbers[index]);
} catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
    System.out.println("Invalid input.");
}
```

В този пример двата типа изключения водят до една и съща обработка. Променливата `exception` съдържа конкретния обект на възникналото изключение.

## `finally`

Блокът `finally` се изпълнява след `try` и `catch`, независимо дали е възникнало изключение.

```java
try {
    System.out.println("Open resource");
} catch (RuntimeException exception) {
    System.out.println("Handle error");
} finally {
    System.out.println("Close resource");
}
```

`finally` се използва за освобождаване на ресурси, когато това не се управлява автоматично.

## `throw`

Ключовата дума `throw` се използва за ръчно хвърляне на конкретно изключение. След `throw` се посочва обект от тип
изключение. Най-често този обект се създава чрез `new`.

```java
public void setAge(int age) {
    if (age < 0) {
        throw new IllegalArgumentException("Age cannot be negative.");
    }
}
```

Методът не допуска невалидно състояние. При отрицателна стойност се хвърля `IllegalArgumentException`.

`throw` прекъсва нормалното изпълнение на текущия блок. След хвърлянето на изключението изпълнението се прехвърля към
подходящ `catch` блок. Ако такъв блок не съществува, изключението се предава към извикващия код.

## `throw` в конструктор

Конструкторът може да проверява дали подадените стойности са валидни. Ако стойностите не позволяват създаване на
коректен обект, може да се хвърли изключение.

```java
class Product {

    String name;
    double price;

    Product(String name, double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }

        this.name = name;
        this.price = price;
    }
}
```

В примера не се допуска създаване на продукт с отрицателна цена. Обектът се създава само ако началното му състояние е
коректно.

## `throw` в `record`

В `record` може да се дефинира компактен конструктор. Той се използва, когато подадените стойности трябва да бъдат
проверени преди създаване на обекта.

```java
record Product(String name, double price) {

    Product {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
    }
}
```

В компактния конструктор не се присвояват ръчно стойности към полетата. След изпълнение на проверките компилаторът
автоматично записва параметрите в съответните компоненти. Ако бъде хвърлено изключение, обект от този `record` не се
създава.

## `throws`

Ключовата дума `throws` се използва в декларация на метод и показва, че методът може да предаде изключение към извикващия код.

```java
public static String readFirstLine(String path) throws IOException {
    return Files.readAllLines(Path.of(path)).get(0);
}
```

Кодът, който извиква този метод, трябва да обработи или също да декларира `IOException`.

## Разлика между `throw` и `throws`

`throw` и `throws` имат различно предназначение, въпреки че и двете ключови думи са свързани с изключения.

| Ключова дума | Място на използване | Предназначение |
| ------------ | ------------------- | -------------- |
| `throw` | в тяло на метод, конструктор или блок | Хвърля конкретен обект от тип изключение |
| `throws` | в декларация на метод или конструктор | Обявява, че изключение може да бъде предадено към извикващия код |

```java
public static void validateAge(int age) {
    if (age < 0) {
        throw new IllegalArgumentException("Age cannot be negative.");
    }
}
```

В примера `throw` създава и хвърля конкретно изключение.

```java
public static String readText(String path) throws IOException {
    return Files.readString(Path.of(path));
}
```

В този пример `throws IOException` не хвърля изключение само по себе си. То показва, че методът може да предаде `IOException` към кода, който го извиква.

## Собствено изключение

Собствено изключение се дефинира чрез клас, който наследява `Exception` или `RuntimeException`.

```java
class InvalidGradeException extends RuntimeException {

    public InvalidGradeException(String message) {
        super(message);
    }
}
```

Такъв клас позволява грешките в конкретна предметна област да бъдат описани с по-точен тип.

## `try-with-resources`

`try-with-resources` се използва за ресурси, които трябва да бъдат затворени. Ресурсът се затваря автоматично след края на блока.

```java
class SimpleResource implements AutoCloseable {

    public void use() {
        System.out.println("Resource is used");
    }

    @Override
    public void close() {
        System.out.println("Resource is closed");
    }
}

try (SimpleResource resource = new SimpleResource()) {
    resource.use();
}
```

Ресурсът трябва да реализира `AutoCloseable`. След приключване на `try` блока методът `close()` се извиква автоматично.
Тази конструкция намалява риска ресурсът да остане незатворен.
