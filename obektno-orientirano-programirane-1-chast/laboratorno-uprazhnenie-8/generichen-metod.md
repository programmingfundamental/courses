# Генеричен метод

генеричният метод в Java приема параметър и връща някаква стойност след изпълнение на задача. Той е обикновен метод, но предвидените за неговите нужди типове са параметризирани. Това позволява генеричният метод да се използва по по-общ начин. Компилаторът се грижи за безопасното използване на типовете, което позволява на програмистите да пишат своя код лесно, тъй като не им се налага да извършват множество индивидуални кастинги на типове.

Можем също да напишем генерични методи, които да бъдат извиквани с различни типове аргументи въз основа на типа аргументи, предадени на генеричния метод.

{% tabs %}
{% tab title="First Tab" %}
```
class Test {
	// A Generic method example
	static <T> void genericDisplay(T element)
	{
		System.out.println(element.getClass().getName() + " = " + element);
	}
}

	// Driver method
	public static void main(String[] args)
	{
		// Calling generic method with Integer argument
		Test.genericDisplay(11);

		// Calling generic method with String argument
		Test.genericDisplay("TU-Varna");

		// Calling generic method with double argument
		Test.genericDisplay(1.0);
	}
}

```
{% endtab %}

{% tab title="Second Tab" %}
java.lang.Integer = 11

java.lang.String = TU-Varna

java.lang.Double = 1.0
{% endtab %}
{% endtabs %}

