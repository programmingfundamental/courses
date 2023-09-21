# Adapter

Шаблонът за проектиране на адаптер е един от **шаблоните за структурен дизайн** и се използва, така че два несвързани интерфейса да могат да работят заедно. Обектът, който се присъединява към тези несвързани интерфейси, се нарича **адаптер** .

Реалн пример за дизайн на адаптера е зарядното устройство за мобилни устройства. Мобилната батерия се нуждае от 3 волта за зареждане, но нормалният контакт произвежда или 120 V (САЩ), или 240 V (Европа). Така мобилното зарядно устройство работи като адаптер между гнездото за мобилно зареждане и стенния контакт. Ще се опитаме да внедрим мулти-адаптер, като използваме шаблон за проектиране на адаптер. Така че първо ще имаме два класа - `Volt`(за измерване на волта) и `Socket`(произвеждане на постоянни волта от 120V).

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

Сега искаме да изградим адаптер, който може да произвежда 3 волта, 12 волта и 120 волта по подразбиране. Така че първо ще създадем интерфейс на адаптер с тези методи.

```
public interface SocketAdapter {
	public Volt get120Volt();
		
	public Volt get12Volt();
	
	public Volt get3Volt();
}
```

#### Модел на двупосочен адаптер

При прилагането на модела на адаптер има два подхода - адаптер на клас и адаптер на обект - но и двата подхода дават един и същ резултат.

1. **Class Adapter** – Тази форма използва **наследяване на java** и разширява интерфейса на източника, в нашия случай Socket клас.
2. **Обектен адаптер** - Този формуляр използва **Java Composition** и адаптерът съдържа изходния обект.

#### Шаблон за проектиране на адаптер - адаптер за клас

Ето реализацията на подхода **за клас адаптер** на нашия адаптер.

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

#### Шаблон за проектиране на адаптер - Реализация на адаптер на обект

Ето реализацията на **Object adapter** на нашия адаптер.

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

Забележете, че и двете реализации на адаптера са почти еднакви и имплементират `SocketAdapter`интерфейса. Интерфейсът на адаптера може също да бъде **абстрактен клас** . Ето тестова програма за използване на нашата реализация на модел на дизайн на адаптер.

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

Когато стартираме горната тестова програма, получаваме следния изход.

```
v3 volts using Class Adapter=3
v12 volts using Class Adapter=12
v120 volts using Class Adapter=120
v3 volts using Object Adapter=3
v12 volts using Object Adapter=12
v120 volts using Object Adapter=120
```

####
