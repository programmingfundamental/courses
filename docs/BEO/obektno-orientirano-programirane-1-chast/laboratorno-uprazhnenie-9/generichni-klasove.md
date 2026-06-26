---
layout: default
title: Генерични класове
parent: Лабораторно упражнение 9
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 2
---
# Генерични класове

Генеричният клас е клас, който декларира един или повече параметри на типа (type parameters). Това позволява един и същ клас да работи с различни типове данни, без да се налага създаването на отделни реализации за всеки тип.

Генеричният клас се дефинира със следния формат:

```java
class name<T1, T2, ..., Tn> { /* ... */ }
```

След името на класа в ъглови скоби ( <> ) се декларират един или повече параметри на типа. Традиционно те се означават с главни букви, но имената могат да бъдат произволни. 

Пример: клас Box има следния вид

```java
public class Box<T> {
    // T stands for "Type"
    private T t;

    public void set(T t) { this.t = t; }
    public T get() { return t; }
}

```

В примера Т представлява параметър на типа. Вместо него при създаване на обект може да бъде използван всеки **референтен тип** (клас, интерфейс, масив или друг генеричен тип)

ОБект от генеричен клас се създава чрез извикване на конкретен тип:

```java
Box<Integer> numbers = new Box<>();
Box<String> names = new Box<>();
```

При създаването на обекта компилаторът замества параметъра Т със съответния тип. От версия 7 на езика е възможно използването на diamond оператора (<>), при който компилаторът автоматично извежда типа от контекста, както е показано в горния пример.

**Ограничения**: Като аргументи на типа могат да бъдат използвани само **референтни типове**. Примитивните типове не могат да бъдат използвани директно; вместо тях се използват съответните обгръщащи класове.

**Предимства**: 
- повторно използване на кода;
- проверка на типовете по време на компилация (type safety);
- намаляване на необходимостта от изрично преобразуване на типовете;
- по-лесно откриване на грешки още при компилация.

Цялостен пример може да бъде разгледан и с помощта на класа Test, разписан по-долу:

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
