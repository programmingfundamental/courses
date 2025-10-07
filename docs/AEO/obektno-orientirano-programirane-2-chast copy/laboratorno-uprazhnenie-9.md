---
layout: default
title: Laboratory lesson 9
parent: Object-oriented Programming - 2 part AEO
has_children: true
nav_order: 9
permalink: /docs/obektno-orientirano-programirane-2-chast-aeo/laboratorno-uprazhnenie-9
---

# Observer

Observer is member of behavioural design patterns and is useful in situations when we need to monitor state of an object and want to receive notifications when there is some change. When such pattern is implemented, there is two type of objects - observers (monitoring objects) and subject (the monitored object).

Consider following example:

```
public interface Subject {

	void register(Observer obj);
	void unregister(Observer obj);

	void notifyObservers();

	Object getUpdate(Observer obj);	
}
```


<pre><code>public interface Observer {
<strong>	public void update();
</strong>
	void setSubject(Subject sub);
}
</code></pre>

After contracts are declared, there has to be some concrete implementations:

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

The implementation of the observer register and unregister method is very simple, the additional method is postMessage() which will be used by the client application to post a String message to the topic. Note the boolean variable to track the change in topic state and used when notifying observers. This variable is needed so that if there is no update and someone calls the notifyObservers() method, it doesn't send false notifications to the observers. Also notice the use of synchronization in the notifyObservers() method to ensure that the notification is only sent to observers registered before the message is posted to the topic. Here is the implementation of Observers who will monitor the topic.


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


# Practice

### Task 1

Create a program to register news and readers. Each news has topics, readers register for the topics, and when news is added to a topic for which a reader is registered, he is informed and marks the news as read by him. If the news content is updated, the reader is also notified and reads the new content.
