---
description: >-
  Отделяне на абстракцията от нейната реализация, така че двете да могат да
  съществуват независимо
---

# Bridge

Когато имаме йерархии на интерфейсите и в двата интерфейса, както и в реализациите, тогава моделът на проектиране на моста се използва за отделяне на интерфейсите от имплементацията и за скриване на детайлите за изпълнение от клиентските програми. Изпълнението на модела на проектиране на моста следва идеята за предпочитане на [композицията](https://app.gitbook.com/o/c8e077E8abnSYoRWFCzu/s/-MUbVVR-jiMUx7iVRyw6/\~/changes/338/obektno-orientirano-programirane-2-chast/laboratorno-uprazhnenie-6/bridge/kompoziciya) пред наследяването.

Да разгледаме следния пример с интерфейсите Shape и Color, където двата интерфейса се имплементират от няколко класа.

<figure><img src="../../../assets/image (95).png" alt=""><figcaption></figcaption></figure>

Забележете, че класовете който имплементират интерфейса Color се инстанцират директно в  класовете Triangie, Pentagon.

Когато използваме модела мост този модел ще изглежда по следния начин

```java
public interface Color {
    public void applyColor();
}
```

```java
public abstract class Shape {
	//Composition - implementor
	protected Color color;
	
	//constructor with implementor as input argument
	public Shape(Color c){
		this.color=c;
	}
	
	abstract public void applyColor();
}
```

Забележете моста между абстракциите и използването на композиция при прилагането на  модела мост.

```java
public class Triangle extends Shape{

	public Triangle(Color c) {
		super(c);
	}

	@Override
	public void applyColor() {
		color.applyColor();
	} 
}
```

```java
public class Pentagon extends Shape{

	public Pentagon(Color c) {
		super(c);
	}

	@Override
	public void applyColor() {
		color.applyColor();
	} 
}
```
```java
public class RedColor implements Color{

	public void applyColor(){
		System.out.println("red.");
	}
}
```

```java
public class GreenColor implements Color{

	public void applyColor(){
		System.out.println("green.");
	}
}
```

```java
public class Application {

	public static void main(String[] args) {
		Shape tri = new Triangle(new RedColor());
		tri.applyColor();
		
		Shape pent = new Pentagon(new GreenColor());
		pent.applyColor();
	}

}
```

Моделът за проектиране на мостове може да се използва, когато абстракцията, така и имплементацията могат да имат различни йерархии независимо и искаме да скрием имплементацията от клиентското приложение.
