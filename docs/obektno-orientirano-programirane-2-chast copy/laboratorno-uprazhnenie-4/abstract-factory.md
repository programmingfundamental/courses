# Abstract Factory

Abstract Factory не използва условен блок, за да създаде обектите, вместо това използва factory клас за всеки подклас. Abstract Factory клас, връща подкласа въз основа на factory клас на входа.

Нека да видим прилагането на този модел при създаването на компютърни конфигурациии.

#### Първо ще е необходим абстрактен клас

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

Класа ще съдържа абстрактни методи за компонентите на всеки компонент

#### Следващата стъпка е да създадем фактическия клас наследник на абстрактния

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

В тези класове само имплементираме абстрактните методи от родителския клас

#### Следващата стъпка е да се създадат Abstract Factory класа

Abstract Factory може да бъде интерфейс или абстрактен клас

```java
public interface ComputerAbstractFactory {
	public Computer createComputer();
}
```

Методът createComputer връща екземпляр на супер класа Computer. Сега фабричните класове ще реализират този интерфейс и ще върнат съответния подклас.

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

#### Сега следва да се  създаде Factory клас, който ще осигури входната точка за създаване на подкласове.

```java
public class ComputerFactory {

	public static Computer getComputer(ComputerAbstractFactory factory){
		return factory.createComputer();
	}
}
```

Tова е клас с метод който приема аргумент и връща  обект. На този етап изпълнението трябва да стане ясно. Нека да напишем прост метод за тестване и да видим как да използваме абстрактната фабрика, за да получим инстанцията на подкласовете.

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

#### Предимства <a href="#abstract-factory-design-pattern-benefits" id="abstract-factory-design-pattern-benefits"></a>

* Моделът на дизайна на Abstract Factory осигурява подход към кода за интерфейс, а не към изпълнението.
* Моделът Abstract Factory е "фабрика на фабрики" и може лесно да бъде разширен, за да побере повече продукти, например можем да добавим още един подклас лаптоп и фабрика LaptopFactory.
* Моделът на Abstract Factory е здрав и избягва условната логика на фабричния модел.
