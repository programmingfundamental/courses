---
layout: default
title: Generics в Java
parent: Лабораторно упражнение 9
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 1
---

# Generics в Java


Generics позволяват класове, интерфейси и методи да работят с тип, който се задава при използване. Така един и същ код може да бъде използван с различни типове, без да се губи типова безопасност.

```java
class Box<T> {

    private T value;

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
```

`T` е параметър на типа. Той се заменя с конкретен тип при създаване на обект.

```java
Box<String> textBox = new Box<>();
textBox.setValue("Java");

String value = textBox.getValue();
```

В този пример `T` се заменя със `String`.

## Необходимост от generics

Без generics стойностите често трябва да се съхраняват като `Object`. Това позволява запис на всякакъв тип и измества грешките към времето на изпълнение.

```java
class Box {

    private Object value;

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
```

При такъв клас е необходимо явно преобразуване.

```java
Box box = new Box();
box.setValue("Java");

String value = (String) box.getValue();
```

Generics премахват нуждата от такова преобразуване и позволяват компилаторът да проверява типовете.

## Генеричен клас с повече от един параметър

```java
class Pair<K, V> {

    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
```

Класът `Pair<K, V>` използва два параметъра на типа. `K` може да представлява тип на ключ, а `V` тип на стойност.

```java
Pair<String, Integer> grade = new Pair<>("Ivan", 6);
```

## Генеричен метод

Генеричен метод декларира собствен параметър на типа преди типа на връщаната стойност.

```java
class Printer {

    public static <T> void print(T value) {
        System.out.println(value);
    }
}
```

Методът може да бъде извикан с различни типове.

```java
Printer.print("Java");
Printer.print(100);
Printer.print(12.5);
```

## Генеричен интерфейс

Интерфейс също може да има параметър на типа.

```java
interface Repository<T> {

    void save(T item);

    T findById(int id);
}
```

Клас, който имплементира интерфейса, задава конкретен тип или остава генеричен.

```java
class StudentRepository implements Repository<Student> {

    @Override
    public void save(Student item) {

    }

    @Override
    public Student findById(int id) {
        return null;
    }
}
```

## Generics и референтни типове

Generics работят с референтни типове. Не може да се използва примитивен тип като `int`, `double` или `boolean`.

```java
// Box<int> box = new Box<>(); // не се компилира
Box<Integer> box = new Box<>();
```

За примитивни стойности се използват обгръщащи класове като `Integer`, `Double` и `Boolean`.

## Raw типове

Raw тип се получава, когато generic клас се използва без параметър на типа.

```java
Box box = new Box();
```

Този подход премахва част от проверките на компилатора и не трябва да се използва в нов код. Правилната форма е:

```java
Box<String> box = new Box<>();
```

## Предимства

Generics осигуряват типова безопасност, намаляват нуждата от явно преобразуване и позволяват повторно използване на класове, интерфейси и методи с различни типове.
