---
layout: default
title: Генерични класове
parent: Лабораторно упражнение 9
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 2
---
# Генерични класове

Генеричният клас се създава по същия начин като обикновен клас. Единствената разлика е, че съдържа секция с параметър за типа. Като параметър може да се зададе повече от един тип, като се разделят със запетая. Класовете, които приемат един или повече параметри, са известни като параметризирани класове или параметризирани типове.

Генеричният клас се дефинира със следния формат:

```java
class name<T1, T2, ..., Tn> { /* ... */ }
```

Името на класа е последвано от секция с параметър за типа, ограничена с ъглови скоби ( <> ). Тя определя _параметрите на типа_ (наричани още _променливи на типа_ ) T1 , T2 , ... и Tn .

За да превърнете класа Box в генерик,  използвайте _generic type декларация като смените кода_ "public class Box" на "public class Box\<T>". Това въвежда типовата променлива T, която може да бъде използвана навсякъде в класа.

След тази промяна класът Box има следния вид:

```java
public class Box<T> {
    // T stands for "Type"
    private T t;

    public void set(T t) { this.t = t; }
    public T get() { return t; }
}

```

Както можете да видите, всички срещания на Object се заменят с T . Като аргумент може да се подаде всеки **непримитивен** тип, който посочите: всеки тип клас, всеки тип интерфейс, всеки масив или дори друга променлива за тип. Същата тази техника може да се приложи и за създаване на генерици интерфейси.

За да създадем обекти от генеричен клас, използваме следния синтаксис.

```java
BaseType <Type> obj = new BaseType <Type>();
```

Пример за генеричния клас Box:

```java
Box<Integer> integerBox = new Box<Integer>();
```

Можете да асоциирате извикването на генеричен тип c извикване на метод, но вместо да подадете аргумент към метод, вие предавате _аргумент от тип_ - в този случай Integer - към самия клас Box.

**Забележка:** В качеството на тип не можем да използваме примитиви като int, char или double.

Цялостен пример може да бъде разгледан и с помощта на класа Test, разписан по-долу.


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
		Test<String> sObj= new Test<String>("TU-Varna");
		System.out.println(sObj.getObject());
	}
}

```


Изпълнение:
```java
15
TU-Varna
```



В генеричните класове можем да предадем повече от един параметър.


Пример
```java
 class Test<T, U>
{
	T obj1; // An object of type T
	U obj2; // An object of type U

	// constructor
	Test(T obj1, U obj2)
	{
		this.obj1 = obj1;
		this.obj2 = obj2;
	}

	// To print objects of T and U
	public void print()
	{
		System.out.println(obj1);
		System.out.println(obj2);
	}
}

// Driver class to test above
class Main
{
	public static void main (String[] args)
	{
		Test <String, Integer> obj = new Test<String, Integer>("TU-Varna", 15);
		obj.print();
	}
}
```


Изпълнение:
```
TU-Varna
15
````



В Java SE 7 и по-нови можете да замените аргументите на типа, необходими за извикване на конструктора на общ клас с празен набор от аргументи на тип (<>), стига компилаторът да може да определи или изведе аргументите на типа от контекста . Тази двойка ъглови скоби, <>, неофициално се нарича _diamond_. Например, можете да създадете екземпляр на Box\<Integer> със следния оператор:

```
Box<Integer> integerBox = new Box<>();
```
