---
layout: default
title: Laboratory lesson 4
parent: Object-oriented Programming - 2 part AEO
has_children: true
nav_order: 4
permalink: /docs/obektno-orientirano-programirane-2-chast-aeo/laboratorno-uprazhnenie-4
---

# Factory

**Factory** is a design pattern that present interface for creation of a base object which allowas subclasses to modify its type.


Factory assumes that you replace direct calls to construct objects (using the operator) with calls to a special _factory_ method. Objects are still created via a constructor, but it is called inside the factory. Items returned to the factory are often labeled as _products._

There is a limitation: subclasses could return different products only if these products has common parent or interface. The factory method should have returning type just like the returning type of the interface method.

### Usage

**1. Factory is used when exact types and dependencies are unknown in advance**

**2. Factory is used when the users should use some kind of framework for the internal components usage**


### Implementation

1. All products must implement one and the same interface, which has meaningful methods in each product's context.
2. Factory creates objects and the returning type must be according to the interface method.
3. In the constructor code, find all references to product constructors and replace them with factory calls while extracting the code to create the product in the factory. At this point, the factory code can look pretty ugly. It can have a large operator that selects which product class to instantiate.
4. Then create subclases for each type of product in the factory and modify creational methods inside.
5. If there are too many product types, the parameter in the creational method could be re-used.
6. If after all extractions the method of base factory is empty, it could be replaced by abstract one.

| Pros                                                                                                                                 | Cons                                                                                                                                                                                        |
| -------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Avoids tight coupling                                                                                | Possible increased code complexity. |
| Easier code maintenance. |                                                                                                                                                                                                           |
| OCP is followed.             |                                                                                                                                                                                                           |

### Example

Create apllication for notifications - SMS, Email and Push.

The notifications will be sent by method notifyUser.

```
public interface Notification {
    void notifyUser();
}
```

The interface must be implemented by the different classes

```
public class SMSNotification implements Notification {
 
    @Override
    public void notifyUser()
    {
        System.out.println("Sending an SMS notification");
    }
}
```

```
public class EmailNotification implements Notification {
 
    @Override
    public void notifyUser()
    {
        System.out.println("Sending an e-mail notification");
    }
}
```

```
public class PushNotification implements Notification {
 
    @Override
    public void notifyUser()
    {
        System.out.println("Sending a push notification");
    }
}
```

### Factory

Object creation will be through factory classes.

```
public class SMSNotificationFactory {
    public SMSNotification create() {
        return new SMSNotification();
    }
}
```

```
public class EmailNotificationFactory {
    public EmailNotification create() {
        return new EmailNotification();
    }
}
```

```
public class PushNotificationFactory {
    public PushNotification create() {
         return new PushNotification();
    }
}
```



# Abstract Factory

Abstract Factory does not use a conditional block to create the objects, instead it uses a factory class for each subclass. Abstract Factory class, returns the subclass based on the input factory class.

Let's see the application of this model in the creation of computer configurations.

#### Abstract class

```java
public abstract class Computer {
     
    public abstract String getRAM();
    public abstract String getHDD();
    public abstract String getCPU();
     
    @Override
    public String toString(){
        return "RAM= "+this.getRAM()+", HDD="+this.getHDD()+", CPU="+this.getCPU();
    }
}
```

It will have abstract methods for each component

#### Concrete classes

```java
public class DvaesktopComputer extends Computer {
 
    private String ram;
    private String hdd;
    private String cpu;
     
    public PC(String ram, String hdd, String cpu){
        this.ram=ram;
        this.hdd=hdd;
        this.cpu=cpu;
    }
    @Override
    public String getRAM() {
        return this.ram;
    }
 
    @Override
    public String getHDD() {
        return this.hdd;
    }
 
    @Override
    public String getCPU() {
        return this.cpu;
    }
 
}
```

```java
public class ServerComputer extends Computer {
 
    private String ram;
    private String hdd;
    private String cpu;
     
    public Server(String ram, String hdd, String cpu){
        this.ram=ram;
        this.hdd=hdd;
        this.cpu=cpu;
    }
    @Override
    public String getRAM() {
        return this.ram;
    }
 
    @Override
    public String getHDD() {
        return this.hdd;
    }
 
    @Override
    public String getCPU() {
        return this.cpu;
    }
 
}
```

In these child classes there are only abstract methods implemented.

#### Abstract Factory class

Abstract Factory coudl be interface or abstract class

```java
public interface ComputerAbstractFactory {
	public Computer createComputer();
}
```

Method createComputer creates instance of the parent class; factory classes will implement this interface and will return corresponding subclass.

```java
public class DvaesktopComputerFactory implements ComputerAbstractFactory {

	private String ram;
	private String hdd;
	private String cpu;
	
	public PCFactory(String ram, String hdd, String cpu){
		this.ram=ram;
		this.hdd=hdd;
		this.cpu=cpu;
	}
	@Override
	public Computer createComputer() {
		return new PC(ram,hdd,cpu);
	}

}
```

```java
public class ServerComputerFactory implements ComputerAbstractFactory {

	private String ram;
	private String hdd;
	private String cpu;
	
	public ServerFactory(String ram, String hdd, String cpu){
		this.ram=ram;
		this.hdd=hdd;
		this.cpu=cpu;
	}
	
	@Override
	public Computer createComputer() {
		return new Server(ram,hdd,cpu);
	}

}
```

#### Factory class as entering point.

```java
public class ComputerFactory {

	public static Computer getComputer(ComputerAbstractFactory factory){
		return factory.createComputer();
	}
}
```


```java
public class Application {

	public static void main(String[] args) {
		testAbstractFactory();
	}

	private static void testAbstractFactory() {
		Computer pc = ComputerFactory.getComputer(new DvaesktopComputerFactory("2 GB","500 GB","2.4 GHz"));
		Computer server = ComputerFactory.getComputer(new ServerComputerFactory("16 GB","1 TB","2.9 GHz"));
		System.out.println("AbstractFactory PC Config::"+pc);
		System.out.println("AbstractFactory Server Config::"+server);
	}
}
```





# Practice

### Task 1

Modify the notification example above with a way to send notifications, stored in provider object. Replace the interface with abstract class.

* Abstract class has abstract method notifyUser and methods for adding and removing message
* The message should be passed as parameter
* Use enumeration as parameter in createNotification

### Task 2

Apply the Factory pattern to build a program for sketching buildings.

Sketching represents information about the type of building (house, block), the name of the architect, the area and the unfolded area of the building.
 
The house is determined by number of floors, price, area, number of bedrooms, number of bathrooms.

The block is determined by number of floors, price, area, number of apartments.
