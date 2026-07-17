---
layout: default
title: Обработка на изключения
parent: Лабораторно упражнение 7
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

## Checked и unchecked изключения

Checked изключенията се проверяват от компилатора. Ако метод може да предизвика checked изключение, то трябва да бъде обработено с `try-catch` или декларирано чрез `throws`.

Unchecked изключенията наследяват `RuntimeException`. Те не са задължителни за обработка от компилатора, но могат да бъдат обработени, когато това е необходимо.

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
try (Scanner scanner = new Scanner(Path.of("input.txt"))) {
    while (scanner.hasNextLine()) {
        System.out.println(scanner.nextLine());
    }
}
```

Тази конструкция намалява риска ресурсът да остане незатворен.
