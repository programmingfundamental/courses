---
description: >-
  Когато създаваме инстанция на генеричен тип, аргументът, предаден на
  параметъра на типа, трябва да бъде референтен тип. Не можем да използваме
  примитивни типове данни като int и char. Test<int> obj =
---

# Генериците работят само с референтни типове

Когато създаваме инстанция на генеричен тип, аргументът, предаден на параметъра на типа, трябва да бъде референтен тип. Не можем да използваме примитивни типове данни като **int** и **char.**

```
Test<int> obj = new Test<int>(20); 
```

Горният ред ще доведе до грешка по време на компилиране, която може да бъде разрешена с помощта на wrappers за капсулиране на примитивен тип.&#x20;

Но масивите от примитивни типове могат да бъдат предадени на параметъра тип, тъй като масивите са референтни типове.

```
ArrayList<int[]> a = нов ArrayList<>();
```

{% tabs %}
{% tab title="Пример" %}
```
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
{% endtab %}

{% tab title="Резултат" %}
error:

&#x20;incompatible types:

&#x20;Test cannot be converted to Test
{% endtab %}
{% endtabs %}

Въпреки че iObj и sObj са от тип Test, те са препратки към различни типове, тъй като техните параметри на типа се различават. По този начин генериците добавят безопасност на типа и предотвратяват грешки.
