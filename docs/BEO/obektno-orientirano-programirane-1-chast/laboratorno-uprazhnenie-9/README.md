---
layout: default
title: Лабораторно упражнение 9
parent: Обектно-ориентирано програмиране - 1 част
has_children: true
nav_order: 9
permalink: /docs/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-9
---
# Лабораторно упражнение 9

### Generics в Java

Генериците позволяват класове, интерфейси и методи да работят с различни типове данни, без да се губи типовата безопасност. Чрез тях типът на данните се подава като параметър.

```java
List<String> names = new ArrayList<>();
List<Integer> numbers = new ArrayList<>();
```
В първия случай списъкът може да съдържа само обекти от тип String, а във втория – само обекти от тип Integer.

### Необходимост от генерици

Преди въвеждането на генериците често се е използвал типът Object, тъй като той е родителски клас на всички класове в Java.

```java
public class Storage {

    private Object value;

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
```

Този подход позволява съхраняване на стойности от различни типове, но води до два основни проблема:
* липса на проверка на типа по време на компилация;
* необходимост от явно преобразуване на типа при извличане на стойността.

```java
Storage storage = new Storage();

storage.setValue("Java");

String text = (String) storage.getValue();
```
Ако в обекта бъде записана стойност от друг тип, грешката ще се появи едва по време на изпълнение.

Генериците решават този проблем, като позволяват типът да бъде зададен предварително.
