---
title: Map and Associative Collections
sidebar:
  order: 1
---

# Map and Associative Collections

## The `Map` interface and associative collections

`Map` is an interface in the Java Collections Framework for storing key–value associations. Each key in a map is unique and is used to access its associated value. Unlike `List`, `Set`, and `Queue`, which store individual elements, `Map` stores key–value pairs.

## Main characteristics of `Map` collections

- Data is stored as key–value associations.
- Each key is unique within a map.
- Values may be repeated.
- Maps do not have indexes.
- Lookup by key is generally efficient.
- A key can be associated with only one value; adding the same key again replaces its previous value.

A dictionary (word–definition) or phone book (name–number) are examples:

```java
Map<String, String> phoneBook = new HashMap<>();

phoneBook.put("Ivan", "0888123456");
phoneBook.put("Maria", "0888987654");
phoneBook.put("Georgi", "0888456789");

System.out.println(phoneBook);
```

Each key is a person's name and each value is a phone number. A possible output is:

```text
{Ivan=0888123456, Georgi=0888456789, Maria=0888987654}
```

(The iteration order of a `HashMap` is not guaranteed.)

## `Map` does not extend `Collection`

`Collection` works with individual elements, while `Map` works with key–value pairs. Therefore, `Map` belongs to a separate interface hierarchy.

## Methods for working with `Map`

### Basic operations

Common basic operations are `put()`, `get()`, `remove()`, `containsKey()`, and `containsValue()`.

```java
Map<String, Integer> grades = new HashMap<>();

grades.put("Ivan", 6);
grades.put("Maria", 5);
System.out.println(grades.get("Ivan"));
System.out.println(grades.containsKey("Maria"));
grades.remove("Maria");
System.out.println(grades);
```

`put()` adds a key–value association, `get()` retrieves a value by key, `containsKey()` checks whether a key exists, and `remove()` removes an association.

```java
Map<String, Integer> grades = new HashMap<>();
grades.put("Ivan", 5);
grades.put("Ivan", 6);
```

Because keys are unique, the second `put()` replaces the value of the existing key; it does not add another entry.

### Operations that describe the collection

`size()` returns the number of mappings. `isEmpty()` checks whether there are any mappings.

### Operations used for traversal

`keySet()` returns the keys, `values()` the values, and `entrySet()` the key–value entries.

## Main `Map` implementations

| Implementation | Ordering | Expected `get`/`put` complexity | Characteristics |
| --- | --- | --- | --- |
| `HashMap` | No guaranteed order | Average O(1) | Uses hashing |
| `LinkedHashMap` | Insertion order | Average O(1) | Preserves iteration order |
| `TreeMap` | Sorted keys | O(log n) | Uses a balanced tree |

## What is `Map.Entry`?

`Map.Entry<K, V>` is a nested interface in `Map` representing one key–value pair. A `Map<K, V>` stores many such mappings. Its `entrySet()` method returns a `Set<Map.Entry<K, V>>`, a view of the map's entries.

```java
import java.util.HashMap;
import java.util.Map;

class EntryExample {
    public static void main(String[] args) {
        Map<String, Integer> stock = new HashMap<>();
        stock.put("pens", 3);
        stock.put("books", 5);

        for (Map.Entry<String, Integer> entry : stock.entrySet()) {
            String product = entry.getKey();
            int quantity = entry.getValue();
            entry.setValue(quantity + 1);
            System.out.println(product + ": " + entry.getValue());
        }
        System.out.println(stock.get("pens")); // 4
    }
}
```

`getKey()` reads the key and `getValue()` reads the value. For entries obtained from a `HashMap`'s `entrySet()`, `setValue(...)` also changes the corresponding value in the map. There is no `setKey()`; changing a key requires removing and adding a mapping. Support for `setValue` depends on the implementation.

An independent immutable pair can be created with:

```java
Map.Entry<String, Integer> fixed = Map.entry("pens", 3);
System.out.println(fixed.getKey()); // pens
// fixed.setValue(4); // unsupported
```

`Map.entry(...)` does not add the pair to a map and does not allow `null` keys or values. `AbstractMap.SimpleEntry<K, V>` is available for an independent pair with a mutable value.

When both key and value are needed, traverse `entrySet()`. Do not add or remove elements through the map itself during that traversal; use an iterator to remove. Do not keep a view entry for later use after changing the map; make a separate copy if needed. See the [Map.Entry documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Map.Entry.html).

## Traversing a `Map`

`entrySet()` returns a set of key–value pairs and is suitable when both are needed. `keySet()` returns only keys; a value can then be retrieved separately with `get(key)`.

```java
import java.util.HashMap;
import java.util.Map;

public class Faculty {
    // specialty - number of students
    private Map<String, Integer> specialties = new HashMap<>();

    public String pairsToString() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> pair : specialties.entrySet()) {
            result.append(pair.getKey())
                  .append(" ")
                  .append(pair.getValue())
                  .append("\n");
        }
        return result.toString();
    }

    public String keysToString() {
        StringBuilder result = new StringBuilder();
        for (String key : specialties.keySet()) {
            result.append(key).append("\n");
        }
        return result.toString();
    }

    public String valuesToString() {
        StringBuilder result = new StringBuilder();
        for (Integer value : specialties.values()) {
            result.append(value).append("\n");
        }
        return result.toString();
    }
}
```

## Sorting a `Map`

Since `Map` is not a linear structure, sorting is performed either by key or by value.

### Ordering by key

The key type must implement `Comparable` for this example:

```java
public Map<String, Integer> sortByKey() {
    return new TreeMap<>(specialties);
}
```

### Ordering by value

Create and sort a list of entries. For more complex values, define a `Comparator`.

```java
public void sortByValue() {
    List<Map.Entry<String, Integer>> specialtyList =
        new ArrayList<>(specialties.entrySet());
    specialtyList.sort(Map.Entry.comparingByValue());
}
```
