---
layout: default
title: Шаблонни класове и методи (Generics)
parent: Лабораторно упражнение 8
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 1
---

# Шаблонни класове и методи (Generics)


В тази тема „шаблонни“ означава **параметризирани с типове** (generic), а не шаблон за създаване на проект в IntelliJ IDEA. Java generics не са механизмът `template` от C++; обичайно се реализират чрез изтриване на типовите параметри (type erasure), без отделна версия на класа за всеки типов аргумент.

Generics позволяват класове, интерфейси и методи да работят с тип, който се задава при използване. Така един и същ код може да бъде използван с различни типове, без да се губи типова безопасност.

```java
class Box<T> {

    private T value;

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
```

`T` е параметър на типа. Той се заменя с конкретен тип при създаване на обект.

```java
Box<String> textBox = new Box<>();
textBox.setValue("Java");

String value = textBox.getValue();
```

В този пример `T` се заменя със `String`.

## Параметри на типа

Параметърът на типа е име, което временно представлява конкретен тип. Той се записва в ъглови скоби след името на
класа, интерфейса или метода.

```java
class Box<T> {

    private T value;
}
```

В примера `T` не е конкретен клас. Той е параметър, който ще бъде заменен при използване на `Box`.

```java
Box<String> textBox = new Box<>();
Box<Integer> numberBox = new Box<>();
```

При `Box<String>` параметърът `T` се разглежда като `String`. При `Box<Integer>` параметърът `T` се разглежда като
`Integer`.

В Java съществуват утвърдени конвенции за именуване на параметрите на типа.

| Име | Значение | Къде се използва |
| --- | -------- | ---------------- |
| `T` | Type | общ тип |
| `E` | Element | елементи в колекции като `List` и `Set` |
| `K` | Key | ключове в `Map` |
| `V` | Value | стойности в `Map` |
| `R` | Result | резултат от операция или функция |
| `U` | Втори свободен тип | когато `T` вече се използва |

Тези имена са конвенция. Те не променят поведението на програмата, но правят generic декларациите по-разпознаваеми.

## Необходимост от generics

Без generics стойностите често трябва да се съхраняват като `Object`. Това позволява запис на всякакъв тип и измества грешките към времето на изпълнение.

```java
class Box {

    private Object value;

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
```

При такъв клас е необходимо явно преобразуване.

```java
Box box = new Box();
box.setValue("Java");

String value = (String) box.getValue();
```

Generics премахват нуждата от такова преобразуване и позволяват компилаторът да проверява типовете.

## Типова безопасност

Типова безопасност означава, че компилаторът проверява дали се използват стойности от правилния тип. При generics
грешка от несъвместим тип се открива при компилация, а не чак при изпълнение.

```java
Box<String> box = new Box<>();

box.setValue("Java");
// box.setValue(10); // не се компилира
```

В примера `box` е деклариран като `Box<String>`. Затова в него може да бъде записан `String`, но не и `int` или
`Integer`.

Типовата безопасност намалява нуждата от явно преобразуване и намалява риска от грешки по време на изпълнение.

## Шаблонен клас с повече от един параметър

```java
final class Pair<K, V> {

    private final K key;
    private final V value;

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

Класът `Pair<K, V>` използва два параметъра на типа. `K` може да представлява тип на ключ, а `V` тип на стойност.

```java
Pair<String, Integer> grade = new Pair<>("Ivan", 6);
```

Типовете на ключа и стойността са независими. Например `Pair<Integer, String>` може да описва факултетен номер и име, а `Pair<String, Double>` — код на продукт и цена. Чрез `grade.getKey()` получаваме `String`, а чрез `grade.getValue()` — `Integer`, без явно преобразуване.

## Видове двойки ключ–стойност

| Представяне | Пример | Подходящо използване |
| --- | --- | --- |
| Шаблонен клас | `Pair<K, V>` | Собствено поведение и контрол върху промяната |
| Шаблонен запис | `KeyValue<K, V>` | Компактно представяне с финални компоненти и генерирано сравнение |
| Именуван тип за конкретната задача | `StudentGrade` | Когато имената на данните носят повече смисъл от `key` и `value` |
| Елемент на речник | `Map.Entry<K, V>` | При обхождане на `Map`; разглежда се в упражнение 10 |

```java
record KeyValue<K, V>(K key, V value) {
}

record StudentGrade(String studentName, int grade) {
}
```

```java
KeyValue<String, Integer> result = new KeyValue<>("Ivan", 6);
String name = result.key();
int gradeValue = result.value();
System.out.println(result.equals(new KeyValue<>("Ivan", 6))); // true

StudentGrade studentGrade = new StudentGrade("Ivan", 6);
System.out.println(studentGrade.studentName());
```

`Pair` и `KeyValue` са типове, дефинирани в тези примери, а не общ стандартен клас `Pair` от `java.util`. Една двойка съхранява две стойности; тя сама по себе си не осигурява уникалност на ключове или търсене по ключ като `Map`.

## Добри практики при шаблонни класове

- Посочвайте типовите аргументи: `Pair<String, Integer>`, а не raw `Pair`.
- Използвайте `K` и `V` за ключ и стойност, `T` за общ тип и `E` за елемент. Когато ролите са конкретни, предпочитайте именуван тип като `StudentGrade`.
- Използвайте `<>` при конструктора, когато компилаторът може да изведе типовете.
- Използвайте обгръщащи типове: `Integer`, а не `int`, като типов аргумент.
- Не заобикаляйте проверките чрез `Object` и ненужни преобразувания. Неподходящ тип трябва да бъде отхвърлен при компилация.
- За двойка без промяна използвайте финални полета или `record`. Ако компонент е изменяем обект, преценете нуждата от защитно копиране.
- Определете допуска ли се `null`. В примерите с `gradeValue` се приема ненулев `Integer`, защото разопаковането на `null` не е валидно.

```java
Pair<String, Integer> count = new Pair<>("books", 3);
// Pair<String, Integer> wrong = new Pair<>("books", "three"); // не се компилира
```

Основите са описани в [Generic Types — Java Tutorials](https://docs.oracle.com/javase/tutorial/java/generics/types.html).

## Шаблонен метод

Генеричен метод декларира собствен параметър на типа преди типа на връщаната стойност.

```java
class Printer {

    public static <T> void print(T value) {
        System.out.println(value);
    }
}
```

Методът може да бъде извикан с различни типове.

```java
Printer.print("Java");
Printer.print(100);
Printer.print(12.5);
```

## Шаблонен интерфейс

Интерфейс също може да има параметър на типа.

```java
interface Repository<T> {

    void save(T item);

    T findById(int id);
}
```

Клас, който имплементира интерфейса, задава конкретен тип или остава генеричен.

```java
class StudentRepository implements Repository<Student> {

    @Override
    public void save(Student item) {

    }

    @Override
    public Student findById(int id) {
        return null;
    }
}
```

## Generics и референтни типове

Generics работят с референтни типове. Не може да се използва примитивен тип като `int`, `double` или `boolean`.

```java
// Box<int> box = new Box<>(); // не се компилира
Box<Integer> box = new Box<>();
```

За примитивни стойности се използват обгръщащи класове като `Integer`, `Double` и `Boolean`.

## Raw типове

Raw тип се получава, когато generic клас се използва без параметър на типа.

```java
Box box = new Box();
```

Този подход премахва част от проверките на компилатора и не трябва да се използва в нов код. Правилната форма е:

```java
Box<String> box = new Box<>();
```

## Предимства

Generics осигуряват типова безопасност, намаляват нуждата от явно преобразуване и позволяват повторно използване на класове, интерфейси и методи с различни типове.
