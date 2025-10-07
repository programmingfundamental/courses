---
layout: default
title: Visitor
parent: Лабораторно упражнение 12
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 1
---

# Visitor

Моделът на посетителя се използва, когато трябва да извършим операция на група от подобен вид обекти. С помощта на посетителския модел можем да преместим оперативната логика от обектите в друг клас. Например, помислете за количка за пазаруване, където можем да добавяме различни видове артикули (Елементи). Когато кликнем върху бутона за плащане, той изчислява общата сума, която трябва да бъде платена. Сега можем да имаме логиката на изчисление в класовете на елементите или можем да преместим тази логика в друг клас, използвайки посетителски модел. Нека приложим това в нашия пример за посетителски модел.

За да приложим посетителския модел, на първо място ще създадем различни видове артикули (елементи), които да се използват в количката за пазаруване.

```
public interface ItemElement {

	public int accept(ShoppingCartVisitor visitor);
}
```

Забележете, че методът за приемане приема аргумента на посетителя. Можем да имаме някои други методи, също специфични за елементите, но за простота не навлизам в толкова много подробности и се фокусирам само върху модела на посетителите. Нека да създадем някои конкретни класове за различни видове елементи.

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

Обърнете внимание на внедряването на метод accept() в конкретни класове, неговия извикващ метод visit() на Visitor и предаването му като аргумент. Имаме метод visit() за различен тип елементи в интерфейса на посетителя, който ще бъде имплементиран от конкретен клас посетител.

```
public interface ShoppingCartVisitor {

	int visit(Book book);
	int visit(Fruit fruit);
}
```

Сега ще внедрим интерфейс за посетители и всеки артикул ще има собствена логика за изчисляване на разходите.

```

public class ShoppingCartVisitorImpl implements ShoppingCartVisitor {

	@Override
	public int visit(Book book) {
		int cost=0;
		//apply 5$ discount if book price is greater than 50
		if(book.getPrice() > 50){
			cost = book.getPrice()-5;
		}else cost = book.getPrice();
		System.out.println("Book ISBN::"+book.getIsbnNumber() + " cost ="+cost);
		return cost;
	}

	@Override
	public int visit(Fruit fruit) {
		int cost = fruit.getPricePerKg()*fruit.getWeight();
		System.out.println(fruit.getName() + " cost = "+cost);
		return cost;
	}

}
```

```
public class ShoppingCartClient {

	public static void main(String[] args) {
		ItemElement[] items = new ItemElement[]{new Book(20, "1234"),new Book(100, "5678"),
				new Fruit(10, 2, "Banana"), new Fruit(5, 5, "Apple")};
		
		int total = calculatePrice(items);
		System.out.println("Total Cost = "+total);
	}

	private static int calculatePrice(ItemElement[] items) {
		ShoppingCartVisitor visitor = new ShoppingCartVisitorImpl();
		int sum=0;
		for(ItemElement item : items){
			sum = sum + item.accept(visitor);
		}
		return sum;
	}

}
```

Когато тичаме над клиентската програма на посетителския модел, получаваме следния изход.

```
Book ISBN::1234 cost =20
Book ISBN::5678 cost =95
Banana cost = 20
Apple cost = 25
Total Cost = 160
```

Забележете, че имплементацията, ако методът accept() във всички елементи е еднакъв, но може да бъде различен, например може да има логика да се провери дали елементът е свободен, след което изобщо не се обаждайте на метода visit().

#### Предимства на модела на посетителите

Ползата от този модел е, че ако логиката на операцията се промени, тогава трябва да направим промяна само в имплементацията на посетителя, вместо да го правим във всички класове елементи. Друго предимство е, че добавянето на нов елемент към системата е лесно, ще изисква промяна само в интерфейса и изпълнението на посетителите и съществуващите класове елементи няма да бъдат засегнати.

#### Ограничения на модела на посетителя

Недостатъкът на модела на посетителите е, че трябва да знаем типа на връщане на методите за посещение по време на проектирането, в противен случай ще трябва да променим интерфейса и всички негови реализации. Друг недостатък е, че ако има твърде много реализации на интерфейса на посетителите, това затруднява удължаването. Това е всичко за модела на дизайна на посетителите, уведомете ме, ако съм пропуснал нещо. Моля, споделете го и с другите, ако ви е харесало.
