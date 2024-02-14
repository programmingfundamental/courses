---
layout: default
title: State
parent: Лабораторно упражнение 11
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 3
---

# State

Моделът на проектиране на състоянието се използва, когато обектът променя поведението си въз основа на вътрешното си състояние.

Ако трябва да променим поведението на даден обект въз основа на неговото състояние, можем да имаме променлива на състоянието в обекта. След това използвайте **if-else** condition block, за да извършите различни действия въз основа на състоянието. Моделът на състоянието се използва, за да осигури систематичен и свободно свързан начин за постигане на това чрез и реализации. **Контекст** на State модел е класът, който има State препратка към една от конкретните реализации на State. Контекстът препраща искането към State обект за обработка. Нека разберем това с един прост пример. Да предположим, че искаме да внедрим дистанционно за телевизор с прост бутон за извършване на действие. Ако State е включена, тя ще включи телевизора и ако State е изключена, ще изключи телевизора. Можем да го приложим, като използваме if-else условие като по-долу

```
public class TVRemoteBasic {

	private String state="";
	
	public void setState(String state){
		this.state=state;
	}
	
	public void doAction(){
		if(state.equalsIgnoreCase("ON")){
			System.out.println("TV is turned ON");
		}else if(state.equalsIgnoreCase("OFF")){
			System.out.println("TV is turned OFF");
		}
	}

	public static void main(String args[]){
		TVRemoteBasic remote = new TVRemoteBasic();
		
		remote.setState("ON");
		remote.doAction();
		
		remote.setState("OFF");
		remote.doAction();
	}

}
```

Забележете, че клиентският код трябва да знае конкретните стойности, които да използва за задаване на състоянието на дистанционното. Освен това, ако броят на State се увеличи, тогава тясното свързване между имплементацията и клиентския код ще бъде много трудно да се поддържа и разширява. Сега ще използваме модела на състоянието, за да приложим горния пример за дистанционно управление на телевизора.

На първо място ще създадем State интерфейс, който ще дефинира метода, който трябва да бъде реализиран от различни конкретни състояния и контекстен клас

```
public interface State {

	public void doAction();
}
```

В нашия пример можем да имаме две състояния - едно за включване на телевизора и друго, за да го изключим. Така че ще създадем две конкретни State реализации за тези поведения

```
public class TVStartState implements State {

	@Override
	public void doAction() {
		System.out.println("TV is turned ON");
	}

}
```

```
public class TVStopState implements State {

	@Override
	public void doAction() {
		System.out.println("TV is turned OFF");
	}

}
```

Сега сме готови да реализираме нашия Контекстен обект, който ще промени поведението си въз основа на вътрешното си състояние.

```
public class TVContext implements State {

	private State tvState;

	public void setState(State state) {
		this.tvState=state;
	}

	public State getState() {
		return this.tvState;
	}

	@Override
	public void doAction() {
		this.tvState.doAction();
	}

}
```

Забележете, че контекстът също така изпълнява State и поддържа справка за текущото му състояние и препраща искането до изпълнението на State.

Сега нека да напишем проста програма, за да тестваме нашия State модел на изпълнение на TV Remote.

```
public class TVRemote {

	public static void main(String[] args) {
		TVContext context = new TVContext();
		State tvStartState = new TVStartState();
		State tvStopState = new TVStopState();
		
		context.setState(tvStartState);
		context.doAction();
		
		
		context.setState(tvStopState);
		context.doAction();
		
	}

}
```

Изходът на горната програма е същият като основната реализация на TV Remote, без да се използва модел на състояние

Ползите от използването на State модел за прилагане на полиморфно поведение са ясно видими. Шансовете за грешка са по-малки и е много лесно да добавите повече състояния за допълнително поведение. По този начин правим нашия код по-здрав, лесно поддържан и гъвкав. Също така State модел помогна да се избегне условната логика if-else или switch-case в този сценарий.
