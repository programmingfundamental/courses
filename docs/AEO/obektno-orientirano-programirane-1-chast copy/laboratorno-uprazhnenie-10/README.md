---
layout: default
title: Laboratory exercise 7
parent: Object-oriented Programming - 1 part
has_children: true
nav_order: 7
permalink: /docs/object-oriented-programming-1-part/laboratory-exercise-7
---

# Laboratory exercise 7

## Collections - part 1

### Data structures

* Main data structures:
  * arrays
  * linked lists
  * hash tables
  * trees

* Basic operations for working with structures:
  * addition
  * removal/erasure
  * search
  * iteration/crawling

Data structures are implemented in Java as Arrays, List, Set and Map.

### Collections

Collections in Java are part of the Java Collections Framework (JCF), which is a set of interfaces and classes that make it easier to work with groups of objects.

The framework provides data structures and common operations on them such as adding, removing, searching, traversing, and sorting.

Collections are:
- dynamic
- flexible
- feature-rich
- object-oriented (only work with objects)



### Arrays vs collections

| feature              		  | arrays                                    | collections                    |
| --------------------------- | ----------------------------------------- | ------------------------------ |
| size                        | fixed								      | dynamic					       |
| data type                   | primitives and objects                    | objects                        |
| duplication    	          | allows                                    | depends on collection type     |
| operations                  | limited set, access by index              | vast set                       |
| structure                   | one-dimensional/multidimensional          | List, Set, Map                 |
| flexibility                 | low                                       | high                           |


Arrays are suitable for use in situations where the size is known in advance or fast access by index is required.


Collections, on the other hand, are suitable for use when the size is not known in advance, multiple additions or removals of elements are required, and complex data structures are being worked with.


### List and Set

![](<https://miro.medium.com/v2/resize:fit:2000/format:webp/1*3ETouv3NvjVqSGIOk0HEBw.png>)

Both interfaces inherit from the Collection interface, which provides the following common methods:
```
//returns the number of elements in the collection
int size()
//checks and returns whether the collection is empty
boolean isEmpty()
//checks and returns whether the collection contains broadcasts as a parameter element
boolean contains(Object element)
//adds an item to a collection (returns true on successful addition)
boolean add(E element)
//removes an item from a collection (returns true on successful deletion)
boolean remove(Object element)
//retrieves and returns the collection iterator
Iterator<E> iterator()
//checks and returns whether all elements are contained in the collection
boolean containsAll(Collection<?> c)
//adds all objects from the list to the collection
boolean addAll(Collection<? extends E> c)
//removes all objects from the list to the collection
boolean removeAll(Collection<?> c)
//removes all items from the collection that are not present in the supplied list of items
boolean retainAll(Collection<?> c)
//cleans up the memory pointed to by the collection
void clear()
//converts the collection to an array of the Object class
Object[] toArray() 
//converts the collection to an array of a programmatically defined class T
<T> T[] toArray(T[] a) 
```


**List**

A List type collection:
- allows duplication of elements;
- elements are ordered by the order of addition (the add method always adds items as an element parameter at the end of the list)
- supports indexes and, accordingly, an element can be accessed by index as in arrays
- suitable for use in solving problems requiring lists, sequences or work queues.

Some of the typical implementations of the List interface are:
- ArrayList – provides fast access, slow operations in the middle
- LinkedList – fast addition and/or removal of elements, slower access to them
- Vector – obsolete implementation, synchronized list

  
**Set**

A Set type collection:
- does not allow duplicate elements
- the ordering of the elements is not guaranteed, as it depends on the specific implementation
- does not support indexes
- suitable when working with unique elements, filtering or searching is needed.

Commonly used implementations of Set are:
- HashSet – fastest, no ordering of the elements
- LinkedHashSet – preserves the order of addition
- TreeSet – supports automatically sorted order.


### Iterators

* Iterators provide unified way to iterate over the elements of a collection.
* Collections (as well as arrays) can be iterated over with a foreach loop.
* Java defines interfaces whose behavior is implemented by all collections.

```
//Interfaces Iterable
public interface Iterator<E> {
    boolean hasNext();
    E next();
    void remove();
}
//Interfaces Iterable
public interface Iterable<T> {
    Iterator<T> iterator();
}
```

* The next() method returns the next element in the collection;
* The remove() method removes from the collection the element last returned by next();
* If the collection is modified while it is being iterated, in any way other than by calling remove() on the iterator, the iterator's behavior is undefined and results in an error (ConcurrentModificationException).

### Comparable and Comparator

These are two interfaces that define the order of elements in a collection and are used for sorting.

Using the Comparable interface make possible definition of natural order in the context of a specific object, i.e. by what criterion the elements will be sorted when sorting a collection containing such objects.

If there is a need to use TreeSet collections or Collections.sort(), the base class for the specific collection must implement the Comparable interface. The interface method returns a positive value, a negative value, or zero, which ensures sorting in a natural order (ascending).

```
public class Book implements Comparable<Object> {
    private String title;
	private String author;
    private int publishingYear;
	private double price;

    // constructor, getters, setters, equals, toString

    @Override
    public int compareTo(Object o) {
        Book book = (Book) o;
        if (this.publishingYear < book.publishingYear) return -1;
        if (this.publishingYear > book.publishingYear) return 1;
        return 0;
    }
}
```


The Comparator interface is used to implement external comparison rules.
Unlike Comparable, the interface method compare(T o1, T o2) takes two objects of the same type and compares them according to the specified criterion.
Using Comparator provides the ability to sort by different criteria without having to change the base class for the collection.
The example below shows different ways to sort a collection of books.


```
import java.util.Comparator;

public class AuthorComparator implements Comparator<Object> {

    @Override
    public int compare(Object o1, Object o2) {
        Book book1 = (Book) o1;
        Book book2 = (Book) o2;
        return book1.getAuthor().compareTo(book2.getAuthor());
    }
}
```
```
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Library {

    private List<Book> books = new ArrayList<>();

    public void sortByPublishingYear() {
        Collections.sort(books); // sorting by criteria defined in method compareTo
    }

    public void sortByAuthor() {
        Collections.sort(books, new AuthorComparator());
    }

    public void sortByTitle() {
        books.sort(new Comparator<Book>() {
            @Override
            public int compare(Book o1, Book o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
    }

    public void sortByPrice() {
        books.sort(Comparator.comparingDouble(Book::getPrice));
    }
}
```

*Important*: Only List type collections can be sorted.


### Queue

A Queue is a data structure in the Java Collections Framework that follows the FIFO (First In, First Out) principle.

Elements are added at the end and removed from the beginning – just like in a queue of people.

A Queue is used when:

- tasks need to be processed in the order in which they arrived

- a buffer implementation is required

- an implementation of algorithms such as BFS (Breadth First Search)

- a system with a queue is being modeled.

The Queue interface inherits the Collection interface, but adds specialized methods: methods that throw exceptions on error and methods that return a special value (false, null).


- Add

-- add(e) — throws an exception if the space is full

-- offer(e) — returns false if the element cannot be added to the queue



- Remove

-- remove() — throws an exception if the queue is empty

-- poll() — returns null if the queue is empty



- View the first element (without removing)

-- element() — throws an exception if there is none

-- peek() — returns null if the queue is empty



The most commonly used implementations of the Queue interface are:

- LinkedList — the most commonly used implementation of Queue; provides fast addition/removal from both ends; can act as a Queue, Stack, or Deque

- PriorityQueue — does not guarantee FIFO; sorts elements according to their natural order or by Comparator; used for algorithms like Dijkstra

- ArrayDeque — very fast implementation of Queue and Deque; better than Stack and LinkedList in most cases; no capacity limitations.

Using the Queue interface is appropriate in the following cases:
- for tasks that are waiting to be processed (producer-consumer)
- for event processing
- при навигация на графи (BFS)
- при управление на принтери, процеси, заявки, мрежови пакети -->
