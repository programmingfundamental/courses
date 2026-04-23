---
layout: default
title: Bridge
parent: Лабораторно упражнение 7
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

---
description: >-
  Отделяне на абстракцията от нейната реализация, така че двете да могат да
  съществуват независимо
---

# Bridge

Bridge е структурен шаблон за проектиране, който разделя абстракция от конкретна имплементация като по този начин прави възможно тяхното независимо развитие. С други думи, използването на този шаблон създава две различни йерархии, свързани чрез композиция - осигурявайки по този начин гъвкавост при необходимост от разширение. 
Изпълнението на модела на проектиране на моста следва идеята за предпочитане на [композицията](https://app.gitbook.com/o/c8e077E8abnSYoRWFCzu/s/-MUbVVR-jiMUx7iVRyw6/\~/changes/338/obektno-orientirano-programirane-2-chast/laboratorno-uprazhnenie-6/bridge/kompoziciya) пред наследяването.

Шаблонът е демонстриран чрез пример с интерфейси Shape и Color, където двата интерфейса се имплементират от няколко класа.

<figure><img src="../../../../assets/image (95).png" alt=""><figcaption></figcaption></figure>

Важно е да се отбележи, че класовете, имплементиращи интерфейса Color, се инстанцират директно в  класовете Triangie, Pentagon.

При използване на Bridge модела ще изглежда по следния начин:

```java
public interface Color {
    public String applyColor();
}
```

```java
public abstract class Shape {
	
	private Color color;
	
	//constructor with implementor as input argument
	public Shape(Color c){
		this.color=c;
	}
	
	public abstract String applyColor();
}
```

Мостът между абстракциите и използването на композиция при прилагането е важен момент от реализацията на шаблона Bridge.

```java
public class Triangle extends Shape{

	public Triangle(Color c) {
		super(c);
	}

	@Override
	public String applyColor() {
		return color.applyColor();
	} 
}
```

```java
public class Pentagon extends Shape{

	public Pentagon(Color c) {
		super(c);
	}

	@Override
	public String applyColor() {
		return color.applyColor();
	} 
}
```
```java
public class RedColor implements Color{

	public String applyColor(){
		return "red";
	}
}
```

```java
public class GreenColor implements Color{

	public String applyColor(){
		return "green";
	}
}
```

```java
public class Application {

	public static void main(String[] args) {
		Shape tri = new Triangle(new RedColor());
		System.out.println(tri.applyColor());
		
		Shape pent = new Pentagon(new GreenColor());
		System.out.println(pent.applyColor());
	}

}
```

Горепосоченият пример илюстрира твърдението, че Bridge може да се използва когато както абстракцията, така и имплементацията могат да имат различни йерархии независимо и е необходимо скриването на имплементацията от клиентското приложение.
