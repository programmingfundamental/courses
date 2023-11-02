---
layout: default
title: Лабораторно упражнение 9
parent: Обектно-ориентирано програмиране - 1 част
has_children: true
nav_order: 9
permalink: /docs/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-9
---
# Лабораторно упражнение 9

## Колекции

### Структори от данни

* Хранилища за данни
* Основни операции
  * добавяне
  * триене
  * търсене
  * обхождане

### Масиви - предимства

| предимства                                          | недостатъци                                                                                  |
| --------------------------------------------------- | -------------------------------------------------------------------------------------------- |
| пределно прост "интерфейс"                          | добавянето или изтриването на елемент (с изключение на последна позиция) е скъпа операция    |
| най-ефективни от гледна точка на използвана памет\* | размерът им е фиксиран при инициализацията                                                   |
| бърз достъп на елемент по индекс: O(1)              | търсенето на елемент по стойност е бавно: О(N) за произволен масив O(logN) за сортиран масив |

### Класа Arrays

Arrays дефинира методи за работа с масиви

Arrays.fill - метод за запълване на масиви е една стойност Arrays.sort - метод за сортиране на масиви Arrays.toString - метод за текстово представяне на масиви

### Колекции

* Java предоставя т.нар. collections framework, съдържащ интерфейси, имплементации и алгоритми върху най-използваните структури от данни
* За разлика от масивите,
  * размерът им е динамичен
  * съдържат само референтни типове
* Почти всички интерфейси и класове се намират в пакета java.util

### Итератори

* Итераторите предоставят унифициран начин за обхождане на елементите на дадена колекция.
* Колекциите (както и масивите) могат да се обхождат с foreach loop
* В java са дефинирани интерфейсите поведението на който се имплементира от всички колекции.

```
//Интерфейси Iterable
public interface Iterator<E> {
    boolean hasNext();
    E next();
    void remove();
}
//Интерфейси Iterable
public interface Iterable<T> {
    Iterator<T> iterator();
}
```

* Метода next() връща следващия елемент в колекцията
* Методът remove() премахва от колекцията елемента, последно върнат от next()
* Ако колекцията бъде модифицирана докато бъде итерирана, по какъвто и да е начин, различен от извикване на remove() на итератора, поведението на итератора е недефинирано води до грешка (ConcurrentModificationException)

### Основни структури от данни

* Масиви
* Свързани списъци
* Хеш таблици
* Дървета

### Предефинирани методи за работа с java.util.Collection

Collection е интерфейс, за всички колекции който го имплементират могат да се използват методите:

**Пример**

```
//дефиниране на колекция от интерфейса Collection
Collection<Integer> integers; //колекция от тип int
Collection<Book> books; //колекция от тип книги
```

```
//броя на елементите в колекцията
int size()
//Проверка дали е празна молекцията
boolean isEmpty()
//Проверка дали колекцията съдържа елемента
boolean contains(Object element)
//Добавяне на елемент в колекция
boolean add(E element)
//Премахване на елемент от колекция
boolean remove(Object element)
//Извличане на итератора на колекцията
Iterator<E> iterator()
//Проверка дали всички елементи се съдържт в колекцията
boolean containsAll(Collection<?> c)
//Докабяне на списък от обекти
boolean addAll(Collection<? extends E> c)
//Премахване на сисък от елементи
boolean removeAll(Collection<?> c)
//Премахване на всички които не присъстват в сисъка от елементи
boolean retainAll(Collection<?> c)
//Почистване на паметта към която сочи колекцията
void clear()
//Преобразуване на колекцията в масив от класа Ojbect
Object[] toArray() 
//Преoбразуване на колекцията в масив от програмно дефиниран клас
<T> T[] toArray(T[] a) 
```

### Обхождане на колекции

```
//създаване на списък с дробни числа
List<Float> nums = Arrays.asList(4.999f, 0.271f, 7.1f, -1f);

//Обхождане чрез enhanced for-loop:
for (float current : nums) {
	System.out.printf("%.2f%n", current);
}

//Обхождане чрез итератор
Iterator<Float> iterator = nums.iterator();
while (iterator.hasNext()) {
	System.out.printf("%.2f%n", iterator.next());
}
```

### Предефинирани методи за работа с List

List е интерфейс, за всички колекции който го имплементират могат да се използват методите:

```
boolean add(E e)
boolean contains(Object o)
E get(int index)
int indexOf(Object o)
boolean remove(Object o)
E remove(int index)
int size()
boolean isEmpty()
Object[] toArray()
List<E> subList(int fromIndex, int toIndex)
```

### Имплементации на List

* ArrayList - resize-ващ се (динамичен) масив
* LinkedList - двойно свързан списък
* Vector - resize-ващ се (динамичен) масив. Synchronized. Legacy
* Stack - наследява Vector. Legacy -> замества се от Dequeue

## Предефинирани методи за работа с Queue

Queue е интерфейс, за всички колекции който го имплементират могат да се използват методите:

```
// Добавя елемент в края на опашката
boolean offer(E e)

// Извлича първия елемент от опашката
E peek()

// Извлича и премахва първия елемент от опашката
// Връща null ако опашката е празна
E poll()

// Премахва и извлича първия елемент от опашката
// Ако опашката е празха хвърля NoSuchElementException
E remove()
```

### Имплементации на Queue

* PriorityQueue - heap (пирамида)
* LinkedList
* ArrayDeque - resize-ващ се (динамичен) масив

### Предефинирани методи за работа с Set

Set е интерфейс, за всички колекции който го имплементират могат да се използват методите:

```
boolean add(E e)
boolean contains(Object o)
boolean remove(Object o)
int size()
boolean isEmpty()
Object[] toArray()
```

### Имплементации на Set

* TreeSet - TreeMap. Червено-черно дърво
  *   конструктори

      ```
      TreeSet(); // natural ordering
      TreeSet(Collection<? extends E> c);
      TreeSet(Comparator<? super E> comparator);
      TreeSet(SortedSet<E> s);
      ```
* HashSet - хеш таблица
  *   конструктори

      ```
      HashSet(); // default initial capacity (16) and load factor (0.75).
      HashSet(Collection<? extends E> c);
      HashSet(int initialCapacity);
      HashSet(int initialCapacity, float loadFactor);
      ```
* LinkedHashSet - хеш таблица + свързан списък
* EnumSet - битов масив

### Операции над множества със Set

```
    Set<String> one = new HashSet<>();
    one.add("foo");
    one.add("bar");
    Set<String> two = new HashSet<>();
    two.add("foo");
    two.add("baba");
    
    Set<String> union = new HashSet<>(one);
    union.addAll(two); // union, съюз, обединение = [baba, bar, foo]
    
    Set<String> intersection = new HashSet<>(one);
    intersection.retainAll(two); // intersection, пресичане, обща част = [foo]
    
    Set<String> difference = new HashSet<>(one);
    difference.removeAll(two); // difference, разлика, различна част  = [bar]
```

### Колекции с наредба vs Колекции без наредба

* TreeMap/TreeSet - червено-черни дървета. Запазват естествена наредба. Елементите трябва да имплементират интерфейса Comparable (или да се подава имплементация на Comparator). Логаритмична сложност за повечето операции
* HashMap/HashSet - хеш таблици. Нямат естествена наредба. Елементите трябва да имплементират методите hashCode() и equals(). Константна сложност за повечето операци

### Generic и колекции

* Всички ипове колекции имат Generic еквивалент което позволява използването на предефинираните алгоритми върху колекции с програмно дефинирани обекти.
* За да могат да бъдат използвани е необходимо дефиниране на:
  * интерфейса Comparable
  * метода hashCode()
  * метода equals()

### Сортиране на колекции

Класа Collections съържа функции за сортиране на колекции.

Функцията Collections.sort приема като входен параметър функцията която трябва да се сортира.

Критерия по който ще се сортира колекцията се подава като втори параметър посредством обект от клас имплементиращ интерфейса Comparator.

**Пример**

```
public class SortById implements Comparator<EMailImpl> {
	@Override
	public int compare(EMailImpl o1, EMailImpl o2) {
		return o1.compareToId(o2.getUsername());
	}
}
```

Метода compare служи за сравнение на два обекта от колекцията. Той връща като резултат

* \-1 ако левия обект е по малък
* 0 ако двата обекта са равни
* 1 ако деснич обект е по малък
