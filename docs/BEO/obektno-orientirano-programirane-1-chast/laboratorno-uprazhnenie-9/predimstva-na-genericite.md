---
layout: default
title: Предимства на генериците
parent: Лабораторно упражнение 9
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 5
---
# Предимства на генериците

Програмите, които използват генерици, имат много предимства пред негенеричния код.

**1. Повторно използване на код:** Възможно е създаването на метод/клас/интерфейс веднъж и неговото последващо използване за всеки тип, който е необходим.

**2. Безопасност на типа:** генериците създават по-скоро грешки по време на компилиране, отколкото по време на изпълнение. Нека е необходимо създаването на списък, който съхранява имената на ученици. Ако по погрешка програмистът добави целочислен обект вместо низ, компилаторът го позволява - но при извличането на тези данни от списъка това създава проблеми по време на изпълнение.


Пример
```java
import java.util.*;
class Test
{
	public static void main(String[] args)
	{
		// Creatinga an ArrayList without any type specified
		List al = new ArrayList();

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


При дефиниране на списък е необходимо посочването на типа обекти, които могат да бъдат съхранявани в него:


Пример:
```java
import java.util.*;
class Test
{
	public static void main(String[] args)
	{
		// Creating a an ArrayList with String specified
		List<String> al = new ArrayList<>();

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


**3. Не е необходимо индивидуално преобразуване на типове:** Ако не се използват генерици, тогава в горния пример всеки път при извличане на данни от списъка ще е необходимо тяхното преобразуване.

```java

                       class Test {
                      
                       	public static void main(String[] args) {                   

                       		// Creating a an ArrayList with String specified
                      
                       		List<String> al = new ArrayList<>();
                      
                       		al.add("Sachin");         

                       		al.add("Rahul");

                       		// Typecasting is not needed

                       		String s1 = al.get(0);
                      
                       		String s2 = al.get(1);

                       	}
}

```

**4. Generics насърчава повторното използване на кода:** С помощта на генерици в Java е възможно създаването на код, който ще работи с различни типове данни. Например:

```java
public <T> void genericsMethod (T данни) {...}
```

Тук се създава генеричен метод. Същият този метод може да бъде използван за извършване на операции с целочислени данни, низови данни и т.н.

**5. Внедряване на генерични алгоритми:** Използвайки генерични алгоритми е възможно внедряването на алгоритми, които работят върху различни типове обекти, като същевременно са и безопасни за типа.
