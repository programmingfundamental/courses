---
layout: default
title: Генерични класове
parent: Лабораторно упражнение 9
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 1
---

# Генеричен клас

Генеричният клас е клас, който декларира един или повече параметри на типа.

```java
public class Storage<T> {

    private T value;

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
```

В примера T е параметър на типа. При създаване на обект от класа се задава конкретният тип, с който ще работи обектът.

```java
Storage<String> textStorage = new Storage<>();
textStorage.setValue("Java");

String text = textStorage.getValue();
```

```java
Storage<Integer> numberStorage = new Storage<>();
numberStorage.setValue(100);

Integer number = numberStorage.getValue();
```

В първия случай Storage работи със String, а във втория – с Integer.

### Генеричен клас с повече от един параметър

Генеричните класове могат да декларират повече от един параметър на типа.

```java
public class Pair<K, V> {

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

Използване:

```java
Pair<String, Integer> student = new Pair<>("Ivan", 12345);

System.out.println(student.getKey());
System.out.println(student.getValue());
```

В примера K представя типа на ключа, а V – типа на стойността.
