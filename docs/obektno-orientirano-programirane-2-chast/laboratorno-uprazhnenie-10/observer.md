---
layout: default
title: Observer
parent: Лабораторно упражнение 10
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 1
---

# Observer

Шаблонът за проектиране на наблюдател е полезен, когато се интересувате от състоянието на даден обект и искате да получавате известия, когато има някаква промяна. В модела на наблюдателя Обектът, който наблюдава състоянието на друг Обект, се нарича наблюдател, а Обектът, който се наблюдава, се нарича субект.

Като пример за Java програма която използва модела на наблюдател, ще реализираме тема и наблюдателите могат да се регистрират в тази тема. Всеки път, когато в темата бъде публикувано ново съобщение, всички наблюдатели ще бъдат уведомени и те могат да  използват съобщението. Основният интерфейс ще е Subject, който дефинира договорните методи, които трябва да бъдат приложени от всеки конкретен субект.

```
public interface Subject {

	public void register(Observer obj);
	public void unregister(Observer obj);

	public void notifyObservers();

	public Object getUpdate(Observer obj);	
}
```

След това ще създадем договор за наблюдателя, ще има метод за прикачване на субекта към наблюдателя и друг метод, който да се използва от субекта за уведомяване за всяка промяна.

<pre><code>public interface Observer {
<strong>	public void update();
</strong>
	public void setSubject(Subject sub);
}
</code></pre>

Сега договорът е готов, продължава се с конкретна имплементация.

```
public class Topic implements Subject {

	private List<Observer> observers;
	private String message;
	private boolean changed;
	
	public Topic(){
		this.observers=new ArrayList<>();
	}
	@Override
	public void register(Observer obj) {
		if(obj == null) throw new NullPointerException("Null Observer");
		
		if(!observers.contains(obj)) observers.add(obj);
	}

	@Override
	public void unregister(Observer obj) {
		observers.remove(obj);
	}

	@Override
	public void notifyObservers() {
		List<Observer> observersLocal = null;
			if (!changed)
				return;
			observersLocal = new ArrayList<>(this.observers);
			this.changed=false;
		for (Observer obj : observersLocal) {
			obj.update();
		}

	}

	@Override
	public Object getUpdate(Observer obj) {
		return this.message;
	}
	
	//method to post message to the topic
	public void postMessage(String msg){
		System.out.println("Message Posted to Topic:"+msg);
		this.message=msg;
		this.changed=true;
		notifyObservers();
	}

}
```

Реализацията на метода за регистриране и дерегистриране на наблюдател е много проста, допълнителният метод е postMessage(), който ще се използва от клиентското приложение за публикуване на String съобщение в темата. Забележете булевата променлива за проследяване на промяната в състоянието на темата и използвана при уведомяване на наблюдатели. Тази променлива е необходима, така че ако няма актуализация и някой извика метод notifyObservers(), той не изпраща фалшиви известия до наблюдателите. Забележете също използването на синхронизация в метода notifyObservers(), за да сте сигурни, че известието се изпраща само до наблюдателите, регистрирани преди съобщението да бъде публикувано в темата. Ето изпълнението на Наблюдатели, които ще наблюдават темата.

```
public class TopicSubscriber implements Observer {
	
	private String name;
	private Subject topic;
	
	public TopicSubscriber(String nm){
		this.name=nm;
	}
	@Override
	public void update() {
		String msg = (String) topic.getUpdate(this);
		if(msg == null){
			System.out.println(name+":: No new message");
		}else
		System.out.println(name+":: Consuming message::"+msg);
	}

	@Override
	public void setSubject(Subject sub) {
		this.topic=sub;
	}

}
```

Обърнете внимание на внедряването на метода update(), където той извиква метода Subject getUpdate(), за да накара съобщението да се консумира. Можехме да избегнем това извикване, като подадохме съобщение като аргумент на метода update(). Ето една проста тестова програма за използване на внедряването на нашата тема.

```
public class Application{

	public static void main(String[] args) {
		//create subject
		Topic topic = new MyTopic();
		
		//create observers
		Observer obj1 = new TopicSubscriber("Obj1");
		Observer obj2 = new TopicSubscriber("Obj2");
		Observer obj3 = new TopicSubscriber("Obj3");
		
		//register observers to the subject
		topic.register(obj1);
		topic.register(obj2);
		topic.register(obj3);
		
		//attach observer to subject
		obj1.setSubject(topic);
		obj2.setSubject(topic);
		obj3.setSubject(topic);
		
		//check if any update is available
		obj1.update();
		
		//now send message to subject
		topic.postMessage("New Message");
	}

}
```
