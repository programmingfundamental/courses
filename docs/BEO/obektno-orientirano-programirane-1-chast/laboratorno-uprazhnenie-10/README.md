---
layout: default
title: Лабораторно упражнение 10
parent: Обектно-ориентирано програмиране - 1 част
has_children: true
nav_order: 11
permalink: /docs/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-10
---

# Лабораторно упражнение 10

## Колекции и интерфейс Collection

## Структури от данни

Структурата от данни представлява начин за организиране и съхраняване на информацията, така че достъпът до нея и обработката ѝ да бъдат ефективни.

Най-често използваните структури от данни са:

  - Масиви (Arrays)
  - Свързани списъци (Linked Lists)
  - Хеш таблици (Hash Tables)
  - Дървета (Trees)

Независимо от конкретната структура, най-често върху нея се извършват следните операции:

  - добавяне
  - триене
  - търсене
  - обхождане

В Java структурите от данни се реализират чрез масиви и чрез Java Collections Framework (JCF). В настоящото упражнение ще бъдат разгледани интерфейсите, наследяващи Collection, а интерфейсът Map ще бъде представен отделно.

## Java Collections Framework

Java Collections Framework (JCF) представлява набор от интерфейси и класове, предназначени за работа с групи от обекти.

Framework-ът предоставя готови реализации на различни структури от данни и унифициран набор от операции за работа с тях: добавяне; премахване; търсене; обхождане; сортиране.

Колекциите са:

-	динамични
-	гъвкави
-	предоставят богата функционалност;
-	работят само с обекти (референтни типове)

## Масиви и колекции

| Характеристика | Масив                   | Колекция                    |
| -------------- | ----------------------- | --------------------------- |
| Размер         | Фиксиран                | Динамичен                   |
| Типове данни   | Примитивни и референтни | Само референтни             |
| Дублиране      | Позволява               | Зависи от типа              |
| Достъп         | По индекс               | Според конкретната колекция |
| Функционалност | Ограничена              | Богата                      |
| Гъвкавост      | Ниска                   | Висока                      |

Масивите са подходящи, когато размерът на данните е известен предварително и е необходим бърз достъп по индекс.

Колекциите са предпочитан избор, когато броят на елементите се променя динамично или се налага често добавяне, премахване, търсене и сортиране.

## Интерфейс Collection

![](<https://miro.medium.com/v2/resize:fit:2000/format:webp/1*3ETouv3NvjVqSGIOk0HEBw.png>)

Интерфейсът Collection е базовият интерфейс в Java Collections Framework. Той дефинира общите операции, които се поддържат от повечето колекции. Най-често използваните методи са:

```java
//връща броя на елементите в колекцията
int size()
//проверява и връща дали е празна колекцията
boolean isEmpty()
//проверява и връща дали колекцията съдържа предавания като параметър елемент
boolean contains(Object element)
//добавя елемент в колекция (връща истина при успешно добавяне)
boolean add(E element)
//премахва елемент от колекция (връща истина при успешно изтриване)
boolean remove(Object element)
//извлича и връща итератора на колекцията
Iterator<E> iterator()
//проверява и връща дали всички елементи се съдържат в колекцията
boolean containsAll(Collection<?> c)
//добавя всички обекти от списъка към колекцията
boolean addAll(Collection<? extends E> c)
//премахва всички обекти от списъка към колекцията
boolean removeAll(Collection<?> c)
//премахва всички елементи от колекцията, които не присъстват в подавания списък от елементи
boolean retainAll(Collection<?> c)
//премахва всички елементи от колекцията
void clear()
//преобразува колекцията в масив от класа Ojbect
Object[] toArray()
//преобразува колекцията в масив от програмно дефиниран клас
<T> T[] toArray(T[] a)
```

Пример:

```java
public class Application {
    public static void main(String[] args) {
        Collection<String> fruits = new ArrayList<>();

        fruits.add("Apple");
        fruits.add("Banana");
        fruits.add("Orange");

        System.out.println(fruits.contains("Banana"));
        System.out.println(fruits.size());

        fruits.remove("Banana");

        System.out.println(fruits);
    }
}
```
В примера към колекцията се добавят три елемента. След това се проверява дали тя съдържа определена стойност, премахва се един елемент и се извежда крайното съдържание:

```java
true
3
[Apple, Orange]
```


## Интерфейс List

List представлява подредена колекция, която:

-	позволява дублиране на елементи;
-	запазва реда на добавяне;
-	поддържа достъп по индекс;
-	позволява произволен достъп до елементите.

Подходящ е за:

- списъци;
- последователности;
- работни опашки;
- колекции, в които редът има значение.

Най-често използвани реализации::

-	**ArrayList** – бърз достъп по индекс, по-бавно вмъкване и изтриване в средата;
-	**LinkedList** – по-бавно търсене, но бързо добавяне и премахване;
-	**Vector** – синхронизирана, по-стара реализация; в съвременните приложения се използва сравнително рядко.

Пример:

```java
List<String> names = new ArrayList<>();

names.add("Ivan");
names.add("Maria");
names.add("Georgi");

System.out.println(names.get(1));

names.remove(0);

System.out.println(names);
```
Методът get() извлича елемент по индекс, а remove() премахва елемент от списъка:

```java
Maria
[Maria, Georgi]
```

## Интерфейс Set

Set представлява колекция, която:

-	не позволява дублиране на елементи;
-	не поддържа индекси;
-	съхранява само уникални елементи.

Подходящ е при:

- филтриране на дублирани стойности;
- работа с множества;
- бързо търсене на уникални елементи.

Най-често използвани реализации:

- HashSet – най-бърза реализация, без гарантиран ред;
- LinkedHashSet – запазва реда на добавяне;
- TreeSet – поддържа автоматично сортирани елементи.

Пример:

```java
Set<String> cities = new HashSet<>();

cities.add("Varna");
cities.add("Sofia");
cities.add("Varna");

System.out.println(cities);
```
Резултат:
```java
[Varna, Sofia]
```

## Интерфейс Queue

**Queue** (опашка) реализира принципа **FIFO (First In – First Out)**.

Новите елементи се добавят в края на опашката, а обработката започва от първия добавен елемент.

Използва се при:

- обработка на заявки;
- буфери;
- producer-consumer задачи;
- алгоритми като BFS.

Основните методи са:

| Добавяне | Премахване | Преглед   |
| -------- | ---------- | --------- |
| add()    | remove()   | element() |
| offer()  | poll()     | peek()    |

Използването на add(), remove() и element() е подходящо, когато липсата на възможност за изпълнение се счита за програмна грешка и трябва незабавно да бъде сигнализирана чрез изключение. Ако подобна ситуация се очаква и трябва да бъде обработена по нормален начин, по-подходящи са offer(), poll() и peek().

Най-често използвани реализации:

- LinkedList
- PriorityQueue
- ArrayDeque

```java
Queue<String> queue = new LinkedList<>();

queue.offer("Task 1");
queue.offer("Task 2");
queue.offer("Task 3");

System.out.println(queue.peek());

System.out.println(queue.poll());

System.out.println(queue.peek());
```
Методът peek() връща първия елемент, без да го премахва, а poll() го извлича и премахва от опашката:
```java
Task 1
Task 1
Task 2
```

## Итератори

Всички колекции, наследяващи Collection, имплементират интерфейса **Iterable**, което позволява използването на **foreach**. За по-гъвкаво обхождане се използва интерфейсът **Iterator**.

Итераторът е интерфейс, който предоставя стандартен механизъм за последователно обхождане на елементите в дадена колекция, без да е необходимо да се познава вътрешната й реализация. Концептуално той може да се разглежда като курсор, който последователно преминава през елементите на колекцията.

```java
public interface Iterable<T> {
    Iterator<T> iterator();
}
```

```java
public interface Iterator<E> {

    boolean hasNext();

    E next();

    void remove();
}
```

Основните методи са:

- hasNext() – проверява дали има следващ елемент;
- next() – връща следващия елемент;
- remove() – премахва последния върнат елемент.

Пример:

```java
Iterator<String> iterator = names.iterator();

while (iterator.hasNext()) {
    System.out.println(iterator.next());
}
```

## Сортиране на колекции

Сортирането на елементите в колекции от тип List може да се реализира чрез интерфейсите **Comparable** и **Comparator**.

## Comparable

Интерфейсът Comparable определя естествения ред на обектите.

Пример:

```java
public class Book implements Comparable<Book> {

    private int publishingYear;

    @Override
    public int compareTo(Book other) {
        return Integer.compare(publishingYear, other.publishingYear);
    }
}
```

Използване:

```java
Collections.sort(books);
```

## Comparator

Интерфейсът Comparator позволява дефиниране на външни критерии за сортиране.

```java
public class AuthorComparator implements Comparator<Book> {

    @Override
    public int compare(Book b1, Book b2) {
        return b1.getAuthor().compareTo(b2.getAuthor());
    }
}
```

Използване:

```java
Collections.sort(books, new AuthorComparator());
```

## Анонимен Comparator

```java
books.sort(new Comparator<Book>() {
    @Override
    public int compare(Book b1, Book b2) {
        return b1.getTitle().compareTo(b2.getTitle());
    }
});
```

## Lambda израз

```java
books.sort((b1, b2) -> Double.compare(b1.getPrice(), b2.getPrice()));
```

## Method Reference

```java
books.sort(Comparator.comparingInt(Book::getPublishingYear));
```

*Забележка: предложения на IntelliJ IDEA*

Съвременните среди за разработка често предлагат автоматично опростяване на кода. Например при използване на анонимен клас за Comparator, IntelliJ IDEA може последователно да предложи преобразуване към по-кратък синтаксис, при който се преминава през lambda израз и се стига до използване на method reference, което е причината те да са посочени по-горе като валидни начини за сортиране.

Тези записи са функционално еквивалентни.

## Comparable vs Comparator

| Comparable               | Comparator                                  |
| ------------------------ | ------------------------------------------- |
| Определя естествения ред | Определя външен критерий                    |
| Реализира се в класа     | Реализира се в отделен клас или чрез lambda |
| Един критерий            | Възможни са множество критерии              |
| Метод compareTo()        | Метод compare()                             |
