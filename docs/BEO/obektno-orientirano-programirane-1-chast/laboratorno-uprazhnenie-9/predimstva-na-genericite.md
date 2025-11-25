---
layout: default
title: Предимства на генериците
parent: Лабораторно упражнение 9
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 5
---
# Предимства на генериците

Програмите, които използват генерици, имат много предимства пред негенеричния код.

**1. Повторно използване на код:** Можем да напишем метод/клас/интерфейс веднъж и да го използваме за всеки тип, който пожелаем.

**2. Безопасност на типа:** генериците създават по-скоро грешки по време на компилиране, отколкото по време на изпълнение. Винаги е по-добре да знаете за проблемите във вашия код по време на компилиране, отколкото кода да се проваля по време на изпълнение. Да предположим, че искате да създадете ArrayList, който съхранява имената на учениците, и ако по погрешка програмистът добави целочислен обект вместо низ, компилаторът го позволява. Но когато извлечем тези данни от ArrayList, това създава проблеми по време на изпълнение.


Пример
```java
import java.util.*;
class Test
{
	public static void main(String[] args)
	{
		// Creatinga an ArrayList without any type specified
		ArrayList al = new ArrayList();

		al.add("Sachin");
		al.add("Rahul");
		al.add(10); // Compiler allows this

		String s1 = (String)al.get(0);
		String s2 = (String)al.get(1);

		// Causes Runtime Exception
		String s3 = (String)al.get(2);
	}
}

```


Изпълнение:
```
Exception in thread "main" java.lang.ClassCastException:

  java.lang.Integer cannot be cast to java.lang.String

   at Test.main(Test.java:19)
```


Когато дефинираме ArrayList, можем да посочим, че този списък може да приема само String обекти.


Пример:
```java
import java.util.*;
class Test
{
	public static void main(String[] args)
	{
		// Creating a an ArrayList with String specified
		ArrayList <String> al = new ArrayList<String> ();

		al.add("Sachin");
		al.add("Rahul");

		// Now Compiler doesn't allow this
		al.add(10);

		String s1 = (String)al.get(0);
		String s2 = (String)al.get(1);
		String s3 = (String)al.get(2);
	}
}

```


Изпълнение:
```
15: error: no suitable method found for add(int)

       al.add(10);
```


**3. Не е необходимо индивидуално преобразуване на типове:** Ако не използваме генерици, тогава в горния пример всеки път, когато извличаме данни от ArrayList, трябва да ги преобразуваме. Преобразуването на типа при всяка операция по извличане е голямо главоболие. Ако вече знаем, че нашият списък съдържа само низови данни, не е необходимо да ги преобразуваме всеки път.

```java

                       class Test {
                      
                       	public static void main(String[] args) {                   

                       		// Creating a an ArrayList with String specified
                      
                       		ArrayList<String> al = new ArrayList<String>();
                      
                       		al.add("Sachin");         

                       		al.add("Rahul");

                       		// Typecasting is not needed

                       		String s1 = al.get(0);
                      
                       		String s2 = al.get(1);

                       	}
}

```

**4. Generics насърчава повторното използване на кода:** С помощта на генерици в Java можем да напишем код, който ще работи с различни типове данни. Например,

```java
public <T> void genericsMethod (T данни) {...}
```

Тук създадохме генеричен метод. Същият този метод може да се използва за извършване на операции с целочислени данни, низови данни и т.н.

**5. Внедряване на генерични алгоритми:** Използвайки генерични алгоритми, можем да внедрим алгоритми, които работят върху различни типове обекти, като същевременно са и безопасни за типа.
