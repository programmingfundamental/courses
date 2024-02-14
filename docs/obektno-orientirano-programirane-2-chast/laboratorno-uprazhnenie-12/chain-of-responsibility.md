# Chain of Responsibility

Моделът на веригата на отговорност се използва за постигане на хлабаво свързване в софтуерния дизайн, където заявка от клиент се предава на верига от обекти, за да ги обработи. След това обектът във веригата сам ще реши кой ще обработва заявката и дали искането трябва да бъде изпратено до следващия обект във веригата или не.

Пример за модел на верига на отговорност в JDK може да видим в множество catch блокове в блоков код try-catch. Тук всеки catch блок е един вид процесор за обработка на конкретно изключение. Така че, когато възникне изключение в блока try, то се изпраща до първия блок catch за обработка. Ако catch блокът не може да го обработи, той препраща заявката към следващия обект във веригата, т.е. следващия catch блок. Ако дори последният catch блок не може да го обработи, изключението се хвърля извън веригата към извикващата програма.

Един примери за модел на веригата на отговорност е банкомат. Потребителят въвежда сумата, която трябва да бъде разпределена, а машината разпределя сумата по отношение на определени средства на стойности като 50 $, 20 $, 10 $ и т.н. Ако потребителят въведе сума, която не е кратна на 10, той хвърля грешка. Ще използваме модела на Веригата на отговорността, за да приложим решение.

Можем да се създаде клас Currency, който ще съхранява сумата за разпределяне и използвана от реализациите на веригата

```
public class Currency {

	private int amount;
	
	public Currency(int amt){
		this.amount=amt;
	}
	
	public int getAmount(){
		return this.amount;
	}
}
```

Базовият интерфейс трябва да има метод за определяне на следващия процесор във веригата и метода, който ще обработва заявката.

```
public interface DispenseChain {

	void setNextChain(DispenseChain nextChain);
	
	void dispense(Currency cur);
}
```

Трябва да се създадат различни процесорни класове, които ще имплементират интерфейса DispenseChain и ще осигурят внедряване на методи за дозиране. Тъй като системата трябва да работа с три вида банкноти - 50$, 20$ и 10$, трябва да се създадат три конкретни реализации.

```
public class Dollar50Dispenser implements DispenseChain {

	private DispenseChain chain;
	
	@Override
	public void setNextChain(DispenseChain nextChain) {
		this.chain=nextChain;
	}

	@Override
	public void dispense(Currency cur) {
		if(cur.getAmount() >= 50){
			int num = cur.getAmount()/50;
			int remainder = cur.getAmount() % 50;
			System.out.println("Dispensing "+num+" 50$ note");
			if(remainder !=0) this.chain.dispense(new Currency(remainder));
		}else{
			this.chain.dispense(cur);
		}
	}

}
```

```
public class Dollar20Dispenser implements DispenseChain{

	private DispenseChain chain;
	
	@Override
	public void setNextChain(DispenseChain nextChain) {
		this.chain=nextChain;
	}

	@Override
	public void dispense(Currency cur) {
		if(cur.getAmount() >= 20){
			int num = cur.getAmount()/20;
			int remainder = cur.getAmount() % 20;
			System.out.println("Dispensing "+num+" 20$ note");
			if(remainder !=0) this.chain.dispense(new Currency(remainder));
		}else{
			this.chain.dispense(cur);
		}
	}

}
```

```
public class Dollar10Dispenser implements DispenseChain {

	private DispenseChain chain;
	
	@Override
	public void setNextChain(DispenseChain nextChain) {
		this.chain=nextChain;
	}

	@Override
	public void dispense(Currency cur) {
		if(cur.getAmount() >= 10){
			int num = cur.getAmount()/10;
			int remainder = cur.getAmount() % 10;
			System.out.println("Dispensing "+num+" 10$ note");
			if(remainder !=0) this.chain.dispense(new Currency(remainder));
		}else{
			this.chain.dispense(cur);
		}
	}

}
```

Важното, е да се отбележи, че прилагането на метода на дозиране. Всяка реализация се опитва да обработи заявката и въз основа на сумата може да обработи част или цялата. Ако някой от веригата не може да я обработи напълно, той изпраща заявката до следващия процесор във веригата, за да обработи оставащата заявка. Ако процесорът не може да обработи нищо, той просто препраща същата заявка към следващата част от верига.

Веригата трябва да се създаде внимателно, в противен случай процесорът може да не получи никаква заявка. Например, в нашата реализация, ако запазим първата процесорна верига като и след това, тогава заявката никога няма да бъде препратена към втория процесор и веригата ще стане безполезна. Ето реализацията на банкомат за обработка на заявената от потребителя сума.

```
public class ATMDispenseChain {

	private DispenseChain c1;

	public ATMDispenseChain() {
		// initialize the chain
		this.c1 = new Dollar50Dispenser();
		DispenseChain c2 = new Dollar20Dispenser();
		DispenseChain c3 = new Dollar10Dispenser();

		// set the chain of responsibility
		c1.setNextChain(c2);
		c2.setNextChain(c3);
	}

	public static void main(String[] args) {
		ATMDispenseChain atmDispenser = new ATMDispenseChain();
		while (true) {
			int amount = 0;
			System.out.println("Enter amount to dispense");
			Scanner input = new Scanner(System.in);
			amount = input.nextInt();
			if (amount % 10 != 0) {
				System.out.println("Amount should be in multiple of 10s.");
				return;
			}
			// process the request
			atmDispenser.c1.dispense(new Currency(amount));
		}

	}

}
```

Заключение:

Клиентът не знае коя част от веригата ще обработва заявката и ще изпрати заявката до първия обект във веригата. Например, в програмата ATMDispenseChain не знае кой обработва заявката за отпускане на въведената сума.

Всеки обект във веригата ще има собствена реализация за обработка на заявката, пълна или частична, или за изпращането й до следващия обект във веригата.

Всеки обект във веригата трябва да има препратка към следващия обект във веригата, към който да препрати заявката, постигната чрез java композиция.

Внимателното създаване на веригата е много важно, в противен случай може да има случай, че заявката никога няма да бъде препратена към конкретен процесор или няма обекти във веригата, които да могат да обработват заявката.

Шаблонът за проектиране на веригата на отговорността е добър за решаване на проблема за загуба на свързване, но идва с компромиса от наличието на много класове за внедряване и проблеми с поддръжката, ако по-голямата част от кода е общ във всички реализации.
