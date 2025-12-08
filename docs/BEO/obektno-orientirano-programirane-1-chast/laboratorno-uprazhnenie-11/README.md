---
layout: default
title: Лабораторно упражнение 11
parent: Обектно-ориентирано програмиране - 1 част
has_children: true
nav_order: 11
permalink: /docs/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-11
---
# Лабораторно упражнение 11

# Колекции - 2 част

Map (карта) е структура от данни, която съхранява двойки ключ - стойност (key - value).

Основни характеристики на колекции от тип Map:

- съхранява данни като асоциации (двойки ключ-стойност)

- всеки ключ е уникален за дадената колекция

- стойностите от своя страна могат да се повтарят

- няма индекси

- предоставят бързо търсене по ключ.

Като пример за подобен тип структура могат да бъдат разгледани речник (дума - определение), телефонен указател (име - номер) и други подобни.

За разлика от List и Set, Map не наследява интерфейса Collection, което означава, че няма същите методи като тези тип колекции. Някои от основните методи за работа с карти са:

```
//добавяне на елемент
V put(K key, V value)
//достъп до елемент, връща null при липса на предавания като параметър ключ
V get(Object key)
//проверка дали колекцията съдържа посочения ключ
boolean containsKey(Object key)
//проверка дали колекцията съдържа посочената стойност
boolean containsValue(Object value)
//проверка за празна колекция
boolean isEmpty()
//връща броя двойки ключ-стойност
int size()
//връща колекция от уникални ключове
Set<K> keySet()
//връща колекция от всички стойности
Collection<V> values()
//връща сет с всяка двойка ключ-стойност 
Set<Map.Entry<K,V>> entrySet()
```

### Основни имплементации на Map

HashMap:

- най-често използваната имплементация

- високо бързодействие

- не гарантира подредба при добавянето на елементите

Подобна реализация е подходяща в случаи на чести операции put/get, при реализация на кеш и при работа с големи обеми данни.

LinkedHashMap:

- запазва реда на добавяне на елементи (по подобие на списъците)

- по-бавен в сравнение с HashMap

- подходящ за случаи, в които се налага обхождане/итериране в точно определен ред.

TreeMap:

- поддържа сортирани ключове - при добавяне двойката автоматично се добавя на място според реда на ключа

- не позволява наличие на нулеви ключове

- реализация на червено-черно дърво.


### Обхождане и сортиране на Map

Както е посочено по-горе, методът entrySet() връща множество двойки ключ-стойност. Следният пример показва различните начини за обхождане на карта:


```
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

Тъй като Map не е линейна структура, сортирането може да бъде направено или по ключ, или по стойност:

```
// за целта обекта, използван за ключ, трябва да имплементира Comparable
    public Map<String, Integer> sortByKey() {
        return new TreeMap<>(specialities);
    }

    public void sortByValue() {
        List<Map.Entry<String, Integer>> specialtyList = new ArrayList<>(specialities.entrySet());
        specialtyList.sort(Map.Entry.comparingByValue()); // при сложен обект, може да се дефинира Comparator
    }
```
