---
layout: default
title: Laboratory lesson 12
parent: Object-oriented Programming - 2 part AEO
has_children: true
nav_order: 12
permalink: /docs/obektno-orientirano-programirane-2-chast-aeo/laboratorno-uprazhnenie-12
---

# Mediator

The Mediator design pattern is representative of behavioral patterns and - as its name suggests - aims to provide a means of communication between different objects. Using a "mediator" in the interaction reduces the degree of coupling between different objects by routing all communication through this mediator and making the objects independent of each other.

To implement the pattern, you need:
- an interface - a mediator;
- a specific implementation of the mediator, where the communication logic is;
- an interface or abstract class with a reference to the mediator (called a "colleague");
- specific implementations of the interface, which can communicate with each other only through the mediator.


Below is a sample implementation of the template, in which we have a cashier, customers, and payments are made.

Interfaces for mediator and colleague/customer:

```
public interface PaymentMediator {

    String pay(double amount, String sender, String receiver);
    void addClient(Client client);
}
```

```
public interface Client {

    String getName();

    String pay(double price, String clientName);
}
```

Concrete implementations of the two interfaces:

```
package mediator;

import java.util.ArrayList;
import java.util.List;

public class PaymentMediatorImpl implements PaymentMediator{

    private List<Client> clients = new ArrayList<>();

    @Override
    public String pay(double amount, String sender, String receiver) {
        for (Client c : clients) {
            if (sender.equalsIgnoreCase(c.getName())) {
                return sender + " is paying " + amount + " to " + receiver;
            }
        }
        return "No payments were made";
    }

    @Override
    public void addClient(Client client) {
        clients.add(client);
    }
}
```

```
package mediator;

public class Customer implements Client{

    private String fullName;
    private PaymentMediator paymentMediator;

    public Customer(String fullName, PaymentMediator paymentMediator) {
        this.fullName = fullName;
        this.paymentMediator = paymentMediator;
        paymentMediator.addClient(this);
    }

    @Override
    public String getName() {
        return fullName;
    }

    @Override
    public String pay(double price, String receiver) {
        return paymentMediator.pay(price, fullName, receiver);
    }
}
```

```
public class Cashier implements Client{

    private String name;
    private PaymentMediator paymentMediator;

    public Cashier(String name, PaymentMediator paymentMediator) {
        this.name = name;
        this.paymentMediator = paymentMediator;
        paymentMediator.addClient(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String pay(double price, String sender) {
        return paymentMediator.pay(price, sender, name);
    }
}
```

The client code looks like this:

```
package mediator;

public class Application {

    public static void main(String[] args) {
        PaymentMediator mediator = new PaymentMediatorImpl();

        Client cashier = new Cashier("Cashier 1", mediator);
        Client firstCustomer = new Customer("John Doe", mediator);
        Client secondCustomer = new Customer("Jane Doe", mediator);

        System.out.println(firstCustomer.pay(15.23, cashier.getName()));
        System.out.println(secondCustomer.pay(28.94, cashier.getName()));
    }
}
```
The result of the main function is:

```
John Doe is paying 15.23 to Cashier 1
Jane Doe is paying 28.94 to Cashier 1

```

Advantages of Mediator:
- reduction of connectivity between objects;
- possibility of reusing mediators in different contexts;
- communication between objects is centralized instead of being distributed in many different places.

Disadvantages of Mediator:
- although it reduces the coupling between objects, each object is in turn strongly coupled to a mediator; this could create performance problems in more complex systems;
- adding a mediator introduces an additional level of abstraction, which sometimes makes the code harder to understand;
- it is not suitable for use in simpler systems, where direct communication between objects is preferable.



# Visitor

Visitor model is used when we need to perform an operation on a group of similar type of objects. Using Visitor model we can move the operational logic from the objects to another class. For example, consider a shopping cart where we can add different types of items (Items). When we click on the checkout button, it calculates the total amount to be paid. Now we can have the calculation logic in the Item classes or we can move this logic to another class using Visitor model. Let’s implement this in our Visitor model example.

To implement the visitor model, we will first create different types of items (elements) to be used in the shopping cart.

```
public interface ItemElement {

	int accept(ShoppingCartVisitor visitor);
}
```

Notice that the accept method takes the visitor argument. We could have some other methods that are also element-specific, but for simplicity I won't go into too much detail and will focus only on the visitor model. Let's create some specific classes for different types of elements.

```
public class Book implements ItemElement {

	private int price;
	private String isbnNumber;
	
	public Book(int cost, String isbn){
		this.price=cost;
		this.isbnNumber=isbn;
	}
	
	public int getPrice() {
		return price;
	}

	public String getIsbnNumber() {
		return isbnNumber;
	}

	@Override
	public int accept(ShoppingCartVisitor visitor) {
		return visitor.visit(this);
	}
}
```

```
public class Fruit implements ItemElement {
	
	private int pricePerKg;
	private int weight;
	private String name;
	
	public Fruit(int priceKg, int wt, String nm){
		this.pricePerKg=priceKg;
		this.weight=wt;
		this.name = nm;
	}
	
	public int getPricePerKg() {
		return pricePerKg;
	}

	public int getWeight() {
		return weight;
	}

	public String getName(){
		return this.name;
	}
	
	@Override
	public int accept(ShoppingCartVisitor visitor) {
		return visitor.visit(this);
	}
}
```

Notice the implementation of accept() method in specific classes, its calling method visit() on Visitor and passing it as an argument. We have a visit() method for different type of elements in the visitor interface which will be implemented by a specific visitor class.

```
public interface ShoppingCartVisitor {

	int visit(Book book);
	int visit(Fruit fruit);
}
```

Now we will implement a visitor interface and each item will have its own cost calculation logic.

```

public class ShoppingCartVisitorImpl implements ShoppingCartVisitor {

	@Override
	public int visit(Book book) {
		int cost=0;
		//apply 5$ discount if book price is greater than 50
		if (book.getPrice() > 50){
			cost = book.getPrice()-5;
		} else {
			cost = book.getPrice();
		}
		return cost;
	}

	@Override
	public int visit(Fruit fruit) {
		int cost = fruit.getPricePerKg() * fruit.getWeight();
		return (fruit.getName() + " cost = " + cost);
	}
}
```

```
public class ShoppingCartClient {

	public static void main(String[] args) {
		List<ItemElement> items = new ArrayList<>();
		items.add(new Book(20, "1234"));
		items.add(new Book(100, "5678"));
		items.add(new Fruit(10, 2, "Banana"));
		items.add(new Fruit(5, 5, "Apple"));
		
		int total = calculatePrice(items);
		System.out.println("Total Cost = " + total);
	}

	private static int calculatePrice(List<ItemElement> items) {
		ShoppingCartVisitor visitor = new ShoppingCartVisitorImpl();
		int sum = 0;
		for (ItemElement item : items) {
			sum = sum + item.accept(visitor);
		}
		return sum;
	}
}
```




#### Visitor advantages

The benefit of this design pattern is that if the logic of the operation changes, then we only need to make a change to the visitor implementation, instead of doing it to all the element classes. Another advantage is that adding new behavior to elements is easy and only involves a new concrete visitor implementation.

#### Visitor limitations

The disadvantage of the Visitor pattern is that we need to know the return type of the visit methods at design time, otherwise we will have to change the interface and all its implementations. Another disadvantage is the difficulty of adding new elements - if in the example above we add a new concrete implementation of ItemElement we will have to add a method to the interface, which will also lead to modification of the existing concrete visitors.





# Tasks

### Task 1

Write a program for a system to manage communication between aircraft at an airport.

The system should support different types of aircraft, such as passenger aircraft and cargo aircraft.

Aircraft should not communicate directly with each other. All requests and messages should go through a control tower that coordinates their actions.

Each aircraft should be able to: send a request to land; send a request to take off; receive messages from the control tower.

The control tower should: accept requests from aircraft; send responses and instructions; manage communication between all aircraft.

The implementation should allow for the easy addition of new aircraft types.

