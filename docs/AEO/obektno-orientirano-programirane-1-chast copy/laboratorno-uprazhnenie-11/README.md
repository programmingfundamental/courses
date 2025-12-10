---
layout: default
title: Laboratory exercise 8
parent: Object-oriented Programming - 1 part
has_children: true
nav_order: 8
permalink: /docs/object-oriented-programming-1-part/laboratory-exercise-8
---

# Laboratory exercise 8

## Collections - part 2


A map is a data structure that stores key - value pairs.


Main characteristics of Map collections:

- stores data as associations (key-value pairs)

- each key is unique for the given collection

- values ​​can be repeated

- no indexes

- provide fast search by key.
  

As an example of a similar type of structure, a dictionary (word - definition), a telephone directory (name - number) and the like can be considered.


Unlike List and Set, Map does not inherit from the Collection interface, which means it does not have the same methods as those types of collections. Some of the main methods for working with Maps are:

```
//element addition
V put(K key, V value)
//accesses an element, returns null if no key is passed as a parameter
V get(Object key)
//checking if the collection contains the specified key
boolean containsKey(Object key)
//checking if the collection contains the specified value
boolean containsValue(Object value)
//checks for empty collection
boolean isEmpty()
//returns number of pairs
int size()
//returns collection of unique keys
Set<K> keySet()
//returns collection of all values
Collection<V> values()
//returns collection with all pairs
Set<Map.Entry<K,V>> entrySet()
```


### Basic implementations of Map


HashMap:

- the most commonly used implementation

- high performance

- does not guarantee ordering when adding elements

- suitable for frequent put/get operations, when implementing a cache and when working with large volumes of data.


LinkedHashMap:

- preserves the order of addition of elements (similar to lists)

- slower than HashMap

- suitable for cases where traversal/iteration in a specific order is required.


TreeMap:

- supports sorted keys - when adding, the pair is automatically added in place according to the order of the key

- does not allow null keys

- implementation of a red-black tree.



### Map navigation and sorting


As mentioned above, the entrySet() method returns multiple key-value pairs. The following example shows the different ways to traverse a map:


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


Since Map is not a linear structure, sorting can be done either by key or by value:


```
// for this purpose, the object used as a key must implement Comparable
    public Map<String, Integer> sortByKey() {
        return new TreeMap<>(specialities);
    }

    public void sortByValue() {
        List<Map.Entry<String, Integer>> specialtyList = new ArrayList<>(specialities.entrySet());
        specialtyList.sort(Map.Entry.comparingByValue()); // for a complex object, a Comparator can be defined
    }
```


### Task


I. Create a Book Editor interface with methods
* void generateBook(String title, int numberOfPages ): creates a book with title and numberOfPages empty pages
  
* void swapPages(int firstPageNumber, int secondPageNumber): swaps pages with given as parameters page numbers; throws an exception if it cannot be executed (InvalidPageException)

II. Create a Page class implementing Comparable

Private fields:

* pageNumber

* content

II.1 Default and parameterized constructors

Methods:

II. 2 Accessors and modifiers for all attributes

II.3 Comparator interface method implementation (optional)

II.4. Method for equality (optional)

II.5 For exchanging page content, the reference to Page passed as a parameter

II.6 For tectual description

III. Class Book implementing interface BookEditor, which stores sorted by page number books. 

Class has private attributes for: bookTitle, collection with pages


III.1 Constructor by number of pages and title. Creates a book with a title and blank pages - the specified number

III.2 Constructor by title - creates a book with only 1 page

Methods: 

III.3. addPage - page is passed as parameter

III.4. changePage - page is passed as parameter

III.5. removePage - page number is passed as parameter

III.6. swapPages - pages are passed as parameters, produces an exception if it cannot be executed

III.7 For textual description

III.8 Interface methods implementation

IV. Main function

IV.1 Creates an object - III. Initialize it with a constructor.

IV.2 Print the book- IV.1 to the console output

IV.3. Add Page to - IV.1, print

IV.4 Remove Page from - IV.1

IV.5 Swap two of the pages, print

IV.6 Handle exceptions
