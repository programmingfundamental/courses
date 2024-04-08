---
layout: default
title: Laboratory lesson 6
parent: Object-oriented Programming - 2 part AEO
has_children: true
nav_order: 6
permalink: /docs/obektno-orientirano-programirane-2-chast-aeo/laboratorno-uprazhnenie-6
---

# Laboratory lesson 6


# Adapter

Adapter is member of structural design patterns and is used to make two non-related interfaces work together. The object which combines and modifies the behavior is called **adapter**.

Mobile device charger is a practical example of adapter. The battery needs 3 volts in order to be charged, but the ordinary contact supplies either 120V (USA) or 240V (Europe). In this way, the carger works as adapter between the contact and charging socket.


```
public class Volt {

	private int volts;
	
	public Volt(int v){
		this.volts=v;
	}

	public int getVolts() {
		return volts;
	}

	public void setVolts(int volts) {
		this.volts = volts;
	}
	
}
```

<pre><code><strong>public class Socket {
</strong>
	public Volt getVolt(){
		return new Volt(120);
	}
}
</code></pre>

Let's consider adapter for 3V, 12V and default 120V; an adapter interface is needed:

```
public interface SocketAdapter {
	public Volt get120Volt();
		
	public Volt get12Volt();
	
	public Volt get3Volt();
}
```

#### Adapter model

There are two adapter types - class adapter and object adapter, although both types give the same result.

1. **Class Adapter** – uses **inheritance** and extends the source interface, Socket class in out case;
2. **Обектен адаптер** - uses **composition** and contain the output object.

#### Solution with class adapter


```

public class SocketClassAdapterImpl extends Socket implements SocketAdapter{

	@Override
	public Volt get120Volt() {
		return getVolt();
	}

	@Override
	public Volt get12Volt() {
		Volt v= getVolt();
		return convertVolt(v,10);
	}

	@Override
	public Volt get3Volt() {
		Volt v= getVolt();
		return convertVolt(v,40);
	}
	
	private Volt convertVolt(Volt v, int i) {
		return new Volt(v.getVolts()/i);
	}

}
```

#### Solution with object adapter

```


public class SocketObjectAdapterImpl implements SocketAdapter{

	//Using Composition for adapter pattern
	private Socket sock = new Socket();
	
	@Override
	public Volt get120Volt() {
		return sock.getVolt();
	}

	@Override
	public Volt get12Volt() {
		Volt v= sock.getVolt();
		return convertVolt(v,10);
	}

	@Override
	public Volt get3Volt() {
		Volt v= sock.getVolt();
		return convertVolt(v,40);
	}
	
	private Volt convertVolt(Volt v, int i) {
		return new Volt(v.getVolts()/i);
	}
}
```

As it seen, both solutions are almost the same and implement SocketAdatper interface. The adapter interface could be also replaced by abstract class. Here is example of adapter usage:

```


public class Application {

	public static void main(String[] args) {
		
		testClassAdapter();
		testObjectAdapter();
	}

	private static void testObjectAdapter() {
		SocketAdapter sockAdapter = new SocketObjectAdapterImpl();
		Volt v3 = getVolt(sockAdapter,3);
		Volt v12 = getVolt(sockAdapter,12);
		Volt v120 = getVolt(sockAdapter,120);
		System.out.println("v3 volts using Object Adapter="+v3.getVolts());
		System.out.println("v12 volts using Object Adapter="+v12.getVolts());
		System.out.println("v120 volts using Object Adapter="+v120.getVolts());
	}

	private static void testClassAdapter() {
		SocketAdapter sockAdapter = new SocketClassAdapterImpl();
		Volt v3 = getVolt(sockAdapter,3);
		Volt v12 = getVolt(sockAdapter,12);
		Volt v120 = getVolt(sockAdapter,120);
		System.out.println("v3 volts using Class Adapter="+v3.getVolts());
		System.out.println("v12 volts using Class Adapter="+v12.getVolts());
		System.out.println("v120 volts using Class Adapter="+v120.getVolts());
	}
	
	private static Volt getVolt(SocketAdapter sockAdapter, int i) {
		switch (i){
		case 3: return sockAdapter.get3Volt();
		case 12: return sockAdapter.get12Volt();
		case 120: return sockAdapter.get120Volt();
		default: return sockAdapter.get120Volt();
		}
	}
}
```

The output of the code above is:

```
v3 volts using Class Adapter=3
v12 volts using Class Adapter=12
v120 volts using Class Adapter=120
v3 volts using Object Adapter=3
v12 volts using Object Adapter=12
v120 volts using Object Adapter=120
```

####



**PRACTICE**

**Task 1.** Create program for reading lines from a file in the following format:

first name; middle name; last name; course, group; faculty number; language; degree (bachelor or master)

Use Adapter in order to convert the lines to objects from class Student.


**Task 2.**
Use Adapter design pattern to create program for two types figures:

- Shape with behavior *draw()* - returns text with figure name, *resize(how much)*, *description()*
  
- Rectangle, Circle are the objects that implement Shape interface.
  
Create class which draws and resizes list of figures.

- GeometricShape with behavior *area()*, *perimetter()*, *drawShape()* - returns figure name;
  
- Triangle, Rhombus are the objects that implement GeometricShape interface.

Use class adapter in order to draw and resize the objects which implement GeometricShape.
