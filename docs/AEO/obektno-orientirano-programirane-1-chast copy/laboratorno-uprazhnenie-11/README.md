---
layout: default
title: Лабораторно упражнение 11
parent: Обектно-ориентирано програмиране - 1 част
has_children: true
nav_order: 11
permalink: /docs/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-11
---
# Лабораторно упражнение 11

Колекции

### Структори от данни

* Хранилища за данни
* Основни операции
  * добавяне
  * триене
  * търсене
  * обхождане

### Масиви - предимства

<table><thead><tr><th width="427">предимства</th><th>недостатъци</th></tr></thead><tbody><tr><td>пределно прост "интерфейс"</td><td>добавянето или изтриването на елемент (с изключение на последна позиция) е скъпа операция</td></tr><tr><td>най-ефективни от гледна точка на използвана памет*</td><td>размерът им е фиксиран при инициализацията</td></tr><tr><td>бърз достъп на елемент по индекс: O(1)</td><td>търсенето на елемент по стойност е бавно: О(N) за произволен масив O(logN) за сортиран масив</td></tr></tbody></table>

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

### Предефинирани методи за работа с Map

Map е интерфейс, за всички колекции който го имплементират могат да се използват методите:

* Колекция съпоставяща обект ключ към обект стойност.
* Не може да съдържа дублирани ключове.
  * При опит за добавяне на съществуващ ключ се променя стойноста

```
    V put(K key, V value)
    V get(Object key)
    V remove(Object key)
    boolean containsKey(Object key)
    int size()
    boolean isEmpty()
    Set<K> keySet()
    Collection<V> values()
```

### Обхождане на колекции Map

```
    // множеството от ключовете
    Set<Integer> keys = map.keySet();
    // колекция от стойностите
    Collection<String> values = map.values();
    // Set<Entry<Integer, String>> s = map.entrySet();
```

### Имплементации на Map

* HashMap - хеш таблица
* LinkedHashMap - хеш таблица + свързан списък
* EnumMap - битов масив
* TreeMap - червено-черно дърво

### Колекции с наредба vs Колекции без наредба

* TreeMap/TreeSet - червено-черни дървета. Запазват естествена наредба. Елементите трябва да имплементират интерфейса Comparable (или да се подава имплементация на Comparator). Логаритмична сложност за повечето операции
* HashMap/HashSet - хеш таблици. Нямат естествена наредба. Елементите трябва да имплементират методите hashCode() и equals(). Константна сложност за повечето операции

[![image](https://camo.githubusercontent.com/31968f0af76ac27e981721dfb0ad56ad28d0c1b71c61276913d25b54904c9ea6/68747470733a2f2f67697470697463682e636f6d2f70697463686d652f63646e2f6769746875622f666d692f6a6176612d636f757273652f6d61737465722f35373335363831394446363344434242443632373134323244454235303933364532394631324631413930454241323842434538434138343844413342413737434336353642353345313039363636383032384636394135464541383941323637343830303334393632373330423937463332383638313239364533333632333539413139443230383030443839363743344237333738464435313436353732443938373331414134393334323935302f696d616765732f30352e302e312d646174612d737472756374757265732d636f6d706c65786974792e706e67)](https://camo.githubusercontent.com/31968f0af76ac27e981721dfb0ad56ad28d0c1b71c61276913d25b54904c9ea6/68747470733a2f2f67697470697463682e636f6d2f70697463686d652f63646e2f6769746875622f666d692f6a6176612d636f757273652f6d61737465722f35373335363831394446363344434242443632373134323244454235303933364532394631324631413930454241323842434538434138343844413342413737434336353642353345313039363636383032384636394135464541383941323637343830303334393632373330423937463332383638313239364533333632333539413139443230383030443839363743344237333738464435313436353732443938373331414134393334323935302f696d616765732f30352e302e312d646174612d737472756374757265732d636f6d706c65786974792e706e67)

## Generics

```
List list = new LinkedList();
list.add(new Integer(1)); 
Integer i = list.iterator().next(); //неможе да се опредили типа на обекта върнат от метода next()
```

* трябва експлицитно да кастваме, което е досадно

```
List list = new LinkedList();
list.add(new Integer(1)); 
Integer i = (Integer) list.iterator().next(); //кастване към класа Integer
```

* Опасн е защото има вероятност да сгрешим в предположението си за типа и cast-ът да "гръмне" с ClassCastException по време на изпълнение
* Много по-удобно
* програмистът може да изрази намерението си да ползва определен тип и компилаторът може да гарантира коректността на програмата за този тип

### Пример

`List<E>` - // Чете се "списък от E"

**Не-generic кутия**

```
public class Box {
private Object value;
 
    public Object getValue() {
        return value;
    }
 
    public void setValue(Object value) {
        this.value = value;
    }
}
```

**Generic кутия**

```
public class Box<Т> {
    private Т value;
 
    public Т getValue() {
        return value;
    }
 
    public void setValue(Т value) {
        this.value = value;
    }
}
```

### Създаване на инстанции

```
// пълен сиснтаксис
Box<Integer> integerBox = new Box<Integer>();
// кратък синтаксис
Box<Integer> integerBox = new Box<>();
```

Конвенция за именуване на параметрите за тип:

* E - Element
* T - Type
* K - Key
* V - Value
* N - Number
* S, U, V etc. - 2-ри, 3-ти, 4-ти тип

### Generic методи

* Могат да ползват типовите параметри на класа/метода
* Могат да добавят нови параметри за тип, недекларирани от класа
* Новите параметри за тип са видими единствено за метода, който ги декларира
* Generic методите могат да са статични, нестатични или конструктори

### Пример

```
public class Pair<K, V> {
    private K key;
    private V value;
 
    // Generic конструктор
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    // Generic методи
    public K getKey() { return key; }
    public void setKey(K key) { this.key = key; }
    public V getValue() { return value; }
    public void setValue(V value) { this.value = value; }
}
```

```
//клас с общо предназначение
public class Util {
 
    // Generic статичен метод
    public static <K, V> boolean areEqual(Pair<K, V> p1, Pair<K, V> p2) {
        return p1.getKey().equals(p2.getKey()) &&
                   p1.getValue().equals(p2.getValue());
    }
 
}
```

```
//Generic методи - извикване
Pair<Integer, String> p1 = new Pair<>(1, "apple");
Pair<Integer, String> p2 = new Pair<>(2, "pear");
 
// ълен синтаксис
boolean areEqual = Util.<Integer, String>areEqual(p1, p2);
 
// кратък синтаксис
areEqual = Util.areEqual(p1, p2);
```

### Generic типове и наследяване

* Integer is-a Object
* Integer is-a Number
* Double is-a Number
* Box is-not-а Box (Техният общ родител е Object)

### Ограничени (bounded/restricted) Generic типове

Може да се специфицира, че generic тип е съвместим само с даден тип или негови наследници/имплементации (upper bound).

```
public <T extends Number> List<T> fromArrayToList(T[] a) {
    // [...]
}
```

```
// Ако типовете са повече от един, те се разделят с &, като в този случай
// най-много един може да бъде клас (останалите трябва да са интерфейси)
// и ако има клас, той трябва да стои първи в списъка.
// Обърнете внимание, че въпреки че Comparable е интерфейс, а не клас,
// ключовата дума е пак extends.
public <T extends Number & Comparable> List<T> anotherMethod(T[] a) {
    // [...]
}
```

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
