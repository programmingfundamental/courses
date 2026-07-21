---
layout: default
title: Map и асоциативни колекции
parent: Лабораторно упражнение 10
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 1
---

# Map и асоциативни колекции

## Интерфейс Map и асоциативни колекции

**Map** е интерфейс от Java Collections Framework, предназначен за съхраняване на асоциативни двойки **ключ–стойност (key–value)**. Всеки ключ в картата е уникален и служи за достъп до съответната стойност.

За разлика от интерфейсите **List**, **Set** и **Queue**, които съхраняват отделни елементи, **Map** работи с двойки ключ–стойност.

## Основни характеристики на колекции от тип Map:

- съхранява данни като асоциации (двойки ключ-стойност)
- всеки ключ е уникален за дадената колекция
- стойностите от своя страна могат да се повтарят
- няма индекси
- предоставят бързо търсене по ключ
- един ключ може да бъде свързан само с една стойност (при повторно добавяне старата стойност се заменя).

Като пример за подобен тип структура могат да бъдат разгледани речник (дума - определение), телефонен указател (име - номер) и други подобни:

```java
Map<String, String> phoneBook = new HashMap<>();

phoneBook.put("Ivan", "0888123456");
phoneBook.put("Maria", "0888987654");
phoneBook.put("Georgi", "0888456789");

System.out.println(phoneBook);
```
В примера всеки ключ представлява име на човек, а съответната стойност – негов телефонен номер:

```java
{Ivan=0888123456, Georgi=0888456789, Maria=0888987654}
```

## Map не наследява Collection

Причината е, че Collection работи с единични елементи, докато Map работи с двойки ключ–стойност и затова образува отделна йерархия.

## Методи за работа с Map

## Основни операции

```java
put()
get()
remove()
containsKey()
containsValue()
```

```java
Map<String, Integer> grades = new HashMap<>();

grades.put("Ivan", 6);
grades.put("Maria", 5);

System.out.println(grades.get("Ivan"));

System.out.println(grades.containsKey("Maria"));

grades.remove("Maria");

System.out.println(grades);
```
Методът put() добавя нова двойка ключ–стойност, get() извлича стойността по ключ, containsKey() проверява дали съществува даден ключ, а remove() премахва съответната двойка.


```java
Map<String, Integer> grades = new HashMap<>();

grades.put("Ivan", 5);
grades.put("Ivan", 6);

```
Тъй като ключовете трябва да бъдат уникални, второто извикване на put() не добавя нов елемент, а заменя стойността на съществуващия ключ.


## Операции, носещи информация за колекцията

```java
size()
isEmpty()
```

## Операции, използвани при обхождане

```java
keySet()
values()
entrySet()
```

## Основни имплементации на Map

| Имплементация  | Подредба          | Очаквана сложност за `get`/`put` | Особености                 |
| -------------- | ----------------- | -------------------------------- | -------------------------- |
| `HashMap`      | не гарантира ред  | O(1) средно                      | използва хеширане          |
| `LinkedHashMap` | ред на добавяне   | O(1) средно                      | запазва реда на обхождане  |
| `TreeMap`      | сортирани ключове | O(log n)                         | използва балансирано дърво |

## Обхождане на Map

Методът `entrySet()` връща множество от двойки ключ-стойност. Той е подходящ при обхождане, когато са необходими и ключът, и стойността. Методът `keySet()` връща само ключовете, а стойността може да бъде получена допълнително чрез `get(key)`.

```java
import java.util.HashMap;
import java.util.Map;

public class Faculty {

    // pair specialty - number of students
    private Map<String, Integer> specialities = new HashMap<>();

    public String pairsToString() {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, Integer> pair : specialities.entrySet()) {
            result.append(pair.getKey())
                  .append(" ")
                  .append(pair.getValue())
                  .append("\n");
        }
        return result.toString();
    }

    public String keysToString() {
        StringBuilder result = new StringBuilder();

        for (String key : specialities.keySet()) {
            result.append(key).append("\n");
        }
        return result.toString();
    }

    public String valuesToString() {
        StringBuilder result = new StringBuilder();

        for (Integer value : specialities.values()) {
            result.append(value).append("\n");
        }
        return result.toString();
    }
}
```

## Сортиране на Map

Тъй като Map не е линейна структура, сортирането може да бъде направено или по ключ, или по стойност:

## Подреждане по ключ

```java
// за целта обекта, използван за ключ, трябва да имплементира Comparable
    public Map<String, Integer> sortByKey() {
        return new TreeMap<>(specialities);
    }
```

## Подреждане по стойност
```java
    public void sortByValue() {
        List<Map.Entry<String, Integer>> specialtyList = new ArrayList<>(specialities.entrySet());
        specialtyList.sort(Map.Entry.comparingByValue()); // при сложен обект, може да се дефинира Comparator
    }
```
