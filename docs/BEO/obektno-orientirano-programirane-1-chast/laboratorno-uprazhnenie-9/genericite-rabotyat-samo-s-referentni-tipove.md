---
layout: default
title: Генериците работят само с референтни типове
parent: Лабораторно упражнение 9
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 3
---

# Генериците работят само с референтни типове

Когато създаваме инстанция на генеричен тип, аргументът, предаден на параметъра на типа, трябва да бъде референтен тип. Не можем да използваме примитивни типове данни като **int** и **char.**

Пример:
```java
Test<int> obj = new Test<int>(20); 
```

Компилационна грешка:
```
Type argument cannot be of a primitive type
```

Горният ред ще доведе до грешка по време на компилиране, която може да бъде разрешена с помощта на wrappers за капсулиране на примитивен тип.

Но масивите от примитивни типове могат да бъдат предадени на параметъра тип, тъй като масивите са референтни типове.

```java
ArrayList<int[]> a = new ArrayList<>();
```


Пример:
```java
class Test<T> {
	// An object of type T is declared
	T obj;
	Test(T obj) { this.obj = obj; } // constructor
	public T getObject() { return this.obj; }
}

// Driver class to test above
class Main {
	public static void main(String[] args)
	{
		// instance of Integer type
		Test<Integer> iObj = new Test<Integer>(15);
		System.out.println(iObj.getObject());

		// instance of String type
		Test<String> sObj = new Test<String>("TU-Varna");
		System.out.println(sObj.getObject());
		iObj = sObj; // This results an error
	}
}

```


Компилационна грешка:
```
Required type: Test<Integer>
Provided: Test<String>
```


Въпреки че iObj и sObj са от тип Test, те са препратки към различни типове, тъй като техните параметри на типа се различават. По този начин генериците добавят безопасност на типа и предотвратяват грешки.
