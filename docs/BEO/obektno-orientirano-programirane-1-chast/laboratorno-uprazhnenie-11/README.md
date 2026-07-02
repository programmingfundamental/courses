---
layout: default
title: Лабораторно упражнение 11
parent: Обектно-ориентирано програмиране - 1 част
has_children: true
nav_order: 11
permalink: /docs/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-11
---

# Лабораторно упражнение 11

# Интерфейс Map и асоциативни колекции

**Map** е интерфейс от Java Collections Framework, предназначен за съхраняване на асоциативни двойки **ключ–стойност (key–value)**. Всеки ключ в картата е уникален и служи за достъп до съответната стойност.

За разлика от интерфейсите **List**, **Set** и **Queue**, които съхраняват отделни елементи, **Map** работи с двойки ключ–стойност.

### Основни характеристики на колекции от тип Map:

- съхранява данни като асоциации (двойки ключ-стойност)

- всеки ключ е уникален за дадената колекция

- стойностите от своя страна могат да се повтарят

- няма индекси

- предоставят бързо търсене по ключ

- един ключ може да бъде свързан само с една стойност (при повторно добавяне старата стойност се заменя).

Като пример за подобен тип структура могат да бъдат разгледани речник (дума - определение), телефонен указател (име - номер) и други подобни.

### Map не наследява Collection

Причината е, че Collection работи с единични елементи, докато Map работи с двойки ключ–стойност и затова образува отделна йерархия.

### Методи за работа с Map

**Основни операции**

```java
put()
get()
remove()
containsKey()
containsValue()
```

**Операции, носещи информация за колекцията**

```java
size()
isEmpty()
```

**Операции, използвани при обхождане**

```java
keySet()
values()
entrySet()
```

### Основни имплементации на Map

| Имплементация | Подредба          | Бързодействие  | Особености                   |
|---------------|-------------------|----------------|------------------------------|
| HashMap       | няма              | много бърза    | най-често използвана         |
| LinkedHashMap | ред на добавяне   | малко по-бавна | запазва реда                 |
| TreeMap       | сортирани ключове | по-бавна       | използва червено-черно дърво |

### Обхождане на Map

Най-често картите се обхождат чрез метода entrySet(), тъй като той предоставя едновременно достъп както до ключа, така и до стойността.

```java
import java.util.HashMap;
import java.util.Map;

public class Faculty {

    // pair specialty - number of students
    private Map<String, Integer> specialities = new HashMap<>();

    public void displayPairs() {
        for (Map.Entry<String, Integer> pair : specialities.entrySet()) {
            System.out.println(pair.getKey() + " " + pair.getValue());
        }
    }

    public void displayKeys() {
        for (String key : specialities.keySet()) {
            System.out.println(key);
        }
    }

    public void displayValues() {
        for (Integer value : specialities.values()) {
            System.out.println(value);
        }
    }
}
```

### Сортиране на Map

Тъй като Map не е линейна структура, сортирането може да бъде направено или по ключ, или по стойност:

**Подреждане по стойност**

```java
// за целта обекта, използван за ключ, трябва да имплементира Comparable
    public Map<String, Integer> sortByKey() {
        return new TreeMap<>(specialities);
    }

    public void sortByValue() {
        List<Map.Entry<String, Integer>> specialtyList = new ArrayList<>(specialities.entrySet());
        specialtyList.sort(Map.Entry.comparingByValue()); // при сложен обект, може да се дефинира Comparator
    }
```

**Подреждане по ключ**

Ключовете могат да бъдат автоматично поддържани в сортиран ред чрез използване на TreeMap.

```java
Map<String,Integer> sorted =  new TreeMap<>(specialities);
```
