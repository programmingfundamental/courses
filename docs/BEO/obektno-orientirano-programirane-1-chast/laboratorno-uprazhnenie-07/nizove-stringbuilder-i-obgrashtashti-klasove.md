---
layout: default
title: Символни низове, StringBuilder и обгръщащи класове
parent: Лабораторно упражнение 7
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 1
---

# Символни низове, StringBuilder и обгръщащи класове


Символен низ в Java представлява последователност от символи. Най-често се използва класът `String`. За изменяемо изграждане на текст се използва `StringBuilder`. Обгръщащите класове представят примитивни стойности като обекти.

## Клас `String`

`String` е клас за работа с текст. Обектите от тип `String` са неизменими. След създаване на низ неговото съдържание не се променя. Операции като конкатенация, замяна или преобразуване връщат нов низ.

```java
String text = "Java";
String result = text + " programming";

System.out.println(text);
System.out.println(result);
```

Променливата `text` продължава да сочи към `"Java"`. Променливата `result` сочи към нов низ.

## Създаване на низ

Низ може да се създаде чрез литерал.

```java
String name = "Ivan";
```

Може да се създаде и чрез конструктор, но това не е необходимо в обичайния случай.

```java
String name = new String("Ivan");
```

Литералната форма е по-кратка и се използва за стандартно създаване на текстови стойности.

Низ може да се създаде и чрез друга променлива от тип `String`.

```java
String first = "Java";
String second = first;
```

В този пример `second` получава същата текстова стойност като `first`.

Низ може да бъде резултат и от израз.

```java
String name = "Ivan";
String message = "Hello, " + name + "!";
```

Изразът `"Hello, " + name + "!"` създава нов низ, който се записва в променливата `message`.

## Основни операции със `String`

```java
String text = "Java Programming";

int length = text.length();
boolean containsJava = text.contains("Java");
String upper = text.toUpperCase();
String part = text.substring(0, 4);
```

Методът `length()` връща броя символи. Методът `contains(String value)` проверява дали даден текст се съдържа в низа. Методът `toUpperCase()` връща нов низ с главни букви. Методът `substring(int start, int end)` извлича част от низа.

## Търсене в `String`

Методите за търсене проверяват дали даден символ или низ се съдържа в друг низ.

```java
String text = "Java Programming";

boolean containsJava = text.contains("Java");
int firstIndex = text.indexOf("a");
int lastIndex = text.lastIndexOf("a");
boolean startsWithJava = text.startsWith("Java");
boolean endsWithIng = text.endsWith("ing");
```

`contains()` връща булева стойност. `indexOf()` връща индекса на първото срещане или `-1`, ако търсената стойност не е
намерена.

## Извличане от `String`

Извличането създава нов низ от част от съществуващ низ.

```java
String text = "Java Programming";

String firstWord = text.substring(0, 4);
char firstLetter = text.charAt(0);
```

Методът `substring(int start, int end)` използва начален индекс включително и краен индекс изключително. Методът
`charAt(int index)` връща символа на посочения индекс.

## Замяна и промяна на `String`

Тъй като `String` е immutable, методите за промяна връщат нов низ.

```java
String text = "Java Programming";

String replaced = text.replace("Java", "Kotlin");
String lower = text.toLowerCase();
String trimmed = " Java ".trim();
```

Оригиналният низ не се променя. Резултатът трябва да бъде записан в променлива, ако трябва да се използва по-късно.

## Разделяне и конкатенация

Методът `split()` разделя низ на масив от низове.

```java
String names = "Ivan,Petar,Maria";
String[] parts = names.split(",");
```

Конкатенацията обединява текстови стойности.

```java
String firstName = "Ivan";
String lastName = "Petrov";
String fullName = firstName + " " + lastName;
```

При много последователни промени на текст е по-подходящо да се използва `StringBuilder`.

## Сравнение на низове

Низове се сравняват по съдържание чрез `equals()`.

```java
String first = "Java";
String second = "Java";

System.out.println(first.equals(second));
```

Операторът `==` сравнява референции, а не съдържание на обекти. Затова за проверка на текстово съдържание се използва `equals()`.

## `==` и `equals()` при обекти

Операторът `==` има различно значение при примитивни и референтни типове.

При примитивни типове `==` сравнява стойностите.

```java
int first = 10;
int second = 10;

System.out.println(first == second);
```

При референтни типове `==` сравнява дали двете променливи сочат към един и същ обект.

```java
String first = new String("Java");
String second = new String("Java");

System.out.println(first == second);
System.out.println(first.equals(second));
```

Първото сравнение връща `false`, защото променливите сочат към два различни обекта. Второто сравнение връща `true`,
защото съдържанието на двата низа е еднакво.

## Escape последователности

Escape последователностите позволяват запис на специални символи в низ.

```java
String line = "First line\nSecond line";
String quoted = "He said: \"Java\"";
```

Последователността `\n` означава нов ред. Последователността `\"` позволява кавичка вътре в текст.

## `StringBuilder`

`StringBuilder` е изменяем клас за изграждане на текст. Той е подходящ, когато текстът се променя многократно.

```java
StringBuilder builder = new StringBuilder();

builder.append("Java");
builder.append(" ");
builder.append("Programming");

String result = builder.toString();
```

Методът `append()` добавя текст към текущото съдържание. Методът `toString()` връща готовия резултат като `String`.

## Mutable и immutable обекти

Обект е immutable, когато състоянието му не може да бъде променено след създаване. `String` е immutable тип. Операции
като конкатенация не променят съществуващия низ, а създават нов низ.

```java
String text = "Java";
text = text + " language";
```

След втория ред променливата `text` сочи към нов обект.

Обект е mutable, когато състоянието му може да бъде променяно след създаване. `StringBuilder` е mutable тип.

```java
StringBuilder builder = new StringBuilder("Java");
builder.append(" language");
```

Методът `append()` променя съществуващия обект от тип `StringBuilder`.

## Основни методи на `StringBuilder`

```java
StringBuilder builder = new StringBuilder("Java");

builder.append(" Language");
builder.insert(0, "The ");
builder.replace(0, 3, "A");
builder.delete(0, 2);
builder.reverse();
```

`StringBuilder` променя собственото си съдържание. Това го прави по-подходящ за цикли и натрупване на текст.

| Метод | Предназначение |
| ----- | -------------- |
| `append()` | добавя стойност в края |
| `insert()` | вмъква стойност на зададена позиция |
| `delete()` | изтрива част от съдържанието |
| `replace()` | заменя част от съдържанието |
| `charAt()` | връща символ по индекс |
| `length()` | връща текущата дължина |
| `capacity()` | връща размера на вътрешния буфер |
| `toString()` | връща резултата като `String` |

## `StringBuffer`

`StringBuffer` е подобен на `StringBuilder`, но е синхронизиран. Това означава, че е предназначен за ситуации, в които няколко нишки могат да работят с един и същ обект. При стандартни еднонишкови програми се използва `StringBuilder`.

## Сравнение между `String`, `StringBuilder` и `StringBuffer`

| Характеристика | `String` | `StringBuilder` | `StringBuffer` |
| -------------- | -------- | --------------- | -------------- |
| Изменяемост | immutable | mutable | mutable |
| Поведение при промяна | създава се нов обект | променя текущия буфер | променя текущия буфер |
| Основна употреба | текст, който не се променя често | чести промени в еднонишков код | чести промени при многонишкова среда |
| Thread-safe поведение | да, поради неизменимостта | не | да, чрез синхронизация |
| Производителност при много промени | по-ниска | най-висока | по-ниска от `StringBuilder` |

`String` е подходящ за готови текстови стойности. `StringBuilder` е подходящ при многократно добавяне, изтриване или
замяна на текст. `StringBuffer` има сходно поведение със `StringBuilder`, но добавя синхронизация.

## ASCII и Unicode

Символите в Java се представят чрез Unicode. Типът `char` съхранява един Unicode символ.

ASCII е по-стар стандарт, който съдържа ограничен набор от символи, основно латински букви, цифри и управляващи
символи. Unicode поддържа много по-голям набор от символи от различни езици.

```java
char latinLetter = 'A';
char cyrillicLetter = 'Я';
```

И двата символа могат да бъдат съхранени в променлива от тип `char`, защото Java използва Unicode.

## Обгръщащи класове

Обгръщащите класове представят примитивните типове като обекти.

| Примитивен тип | Обгръщащ клас |
| -------------- | ------------- |
| `boolean` | `Boolean` |
| `char` | `Character` |
| `byte` | `Byte` |
| `short` | `Short` |
| `int` | `Integer` |
| `long` | `Long` |
| `float` | `Float` |
| `double` | `Double` |

Колекциите и generic типовете в Java работят с обекти, затова за примитивни стойности се използват обгръщащи класове.

```java
Integer number = 10;
Double price = 15.50;
Boolean active = true;
```

Обгръщащите класове са необходими, когато примитивна стойност трябва да се използва като обект. Това се среща при:

- съхраняване на стойности в колекции;
- използване на generic типове;
- извикване на методи за преобразуване, сравнение или проверка;
- представяне на липсваща стойност чрез `null`, когато това е допустимо за конкретната логика.

## Boxing и unboxing

Boxing е преобразуване от примитивен тип към обгръщащ клас. Unboxing е преобразуване от обгръщащ клас към примитивен тип.

```java
Integer number = 10;
int value = number;
```

В първия ред стойността `10` се обгръща в `Integer`. Във втория ред стойността се извлича обратно като `int`.

## Полезни методи

```java
int number = Integer.parseInt("123");
double price = Double.parseDouble("12.50");
boolean digit = Character.isDigit('5');
boolean letter = Character.isLetter('A');
```

Методите `parseInt()` и `parseDouble()` преобразуват текст към числова стойност. Методите на `Character` позволяват проверка на символи.

Методите `parseInt()` и `parseDouble()` връщат примитивни стойности.

```java
int number = Integer.parseInt("123");
double price = Double.parseDouble("12.50");
```

Методът `valueOf()` връща обект от съответния обгръщащ клас.

```java
Integer number = Integer.valueOf("123");
Double price = Double.valueOf("12.50");
```

Ако текстът не съдържа валидна числова стойност, възниква `NumberFormatException`.

```java
int number = Integer.parseInt("abc");
```

Изключението `NumberFormatException` се разглежда при обработката на изключения.

| Метод | Предназначение |
| ----- | -------------- |
| `valueOf()` | създава обект от примитивна стойност или низ |
| `parseInt()` | преобразува символен низ в `int` |
| `parseDouble()` | преобразува символен низ в `double` |
| `intValue()` | връща стойността като `int` |
| `doubleValue()` | връща стойността като `double` |
| `toString()` | връща текстово представяне на стойността |

## Методи за проверка в обгръщащи класове

Класът `Character` съдържа методи за проверка на символи.

```java
boolean digit = Character.isDigit('5');
boolean letter = Character.isLetter('A');
boolean whitespace = Character.isWhitespace(' ');
```

Тези методи връщат `true` или `false` според вида на символа.

| Метод | Предназначение |
| ----- | -------------- |
| `Character.isDigit()` | проверява дали символът е цифра |
| `Character.isLetter()` | проверява дали символът е буква |
| `Character.isLetterOrDigit()` | проверява дали символът е буква или цифра |
| `Character.isWhitespace()` | проверява дали символът е интервален символ |
| `Character.isUpperCase()` | проверява дали символът е главна буква |
| `Character.isLowerCase()` | проверява дали символът е малка буква |

## Методи за сравнение в обгръщащи класове

Обгръщащите класове съдържат методи за сравнение на стойности.

```java
int result = Integer.compare(10, 20);
int max = Integer.max(10, 20);
int min = Integer.min(10, 20);
```

`Integer.compare(first, second)` връща отрицателна стойност, нула или положителна стойност според това дали първата
стойност е по-малка, равна или по-голяма от втората.

| Метод | Предназначение |
| ----- | -------------- |
| `equals()` | проверява две стойности за равенство |
| `compare()` | сравнява две стойности |
| `compareTo()` | сравнява текущия обект с друг обект от същия тип |

## Клас `Math`

Класът `Math` съдържа статични методи за често използвани математически операции. Не се създава обект от `Math`.
Методите се извикват чрез името на класа.

```java
int absolute = Math.abs(-10);
double power = Math.pow(2, 3);
double root = Math.sqrt(25);
```

`Math.abs(-10)` връща абсолютната стойност. `Math.pow(2, 3)` повдига 2 на трета степен. `Math.sqrt(25)` връща квадратен
корен.

## Основни методи на `Math`

| Метод | Предназначение |
| ----- | -------------- |
| `Math.abs(value)` | връща абсолютна стойност |
| `Math.max(first, second)` | връща по-голямата стойност |
| `Math.min(first, second)` | връща по-малката стойност |
| `Math.pow(base, exponent)` | степенуване |
| `Math.sqrt(value)` | квадратен корен |
| `Math.round(value)` | закръгляне до най-близко цяло число |
| `Math.ceil(value)` | закръгляне нагоре |
| `Math.floor(value)` | закръгляне надолу |

## Константи в `Math`

Класът `Math` съдържа и готови математически константи.

```java
double circleArea = Math.PI * radius * radius;
double e = Math.E;
```

`Math.PI` представя числото π. `Math.E` представя основата на естествения логаритъм.

## Клас `Random`

Класът `Random` се използва за генериране на псевдослучайни стойности. За разлика от `Math`, при `Random` се създава
обект.

```java
import java.util.Random;

Random random = new Random();

int number = random.nextInt();
boolean flag = random.nextBoolean();
double value = random.nextDouble();
```

Стойностите са псевдослучайни, защото се генерират от алгоритъм. При всяко извикване се получава следваща стойност от
поредицата.

## Генериране на число в диапазон

Методът `nextInt(int bound)` връща цяло число от 0 включително до зададената граница изключително.

```java
Random random = new Random();

int number = random.nextInt(10);
```

В примера възможните стойности са от 0 до 9.

За диапазон от 1 до 10 се добавя 1 към резултата.

```java
int number = random.nextInt(10) + 1;
```

За диапазон между `min` и `max` включително може да се използва следната формула:

```java
int number = random.nextInt(max - min + 1) + min;
```

## Основни методи на `Random`

| Метод | Предназначение |
| ----- | -------------- |
| `nextInt()` | връща произволна стойност от тип `int` |
| `nextInt(bound)` | връща `int` в диапазона от 0 до `bound - 1` |
| `nextDouble()` | връща `double` в диапазона от 0.0 до 1.0 |
| `nextBoolean()` | връща `true` или `false` |
