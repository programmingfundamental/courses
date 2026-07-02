---
layout: default
title: State
parent: Лабораторно упражнение 11
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 3
---

# State

### Проблем

При някои обекти поведението зависи от тяхното текущо състояние. Често това води до използването на множество условни конструкции (if, switch), които постепенно усложняват логиката на програмата.

Например билет може да бъде свободен, резервиран или продаден. В зависимост от текущото му състояние едни операции са позволени, а други – не. При добавяне на нови състояния или промяна в бизнес логиката условните конструкции стават все по-сложни и трудни за поддръжка.

### Решение

Шаблонът State разделя различните състояния в самостоятелни класове. Вместо контекстният обект да съдържа условна логика, той делегира изпълнението на текущото състояние.

При промяна на състоянието поведението на обекта автоматично се променя чрез използването на друга реализация на интерфейса State.

### Дефиниция

State е поведенчески шаблон за проектиране, който позволява на даден обект да променя поведението си при промяна на вътрешното си състояние.

Основните участници в шаблона са:
* Context - Обектът, чието поведение зависи от текущото състояние.
* State - Интерфейсът, който дефинира поведението за различните състояния.
* Concrete State - Конкретна реализация на дадено състояние.

### UML диаграма

<img width="453" height="456" alt="State" src="https://github.com/user-attachments/assets/7824061e-55aa-44ac-ab38-0fa238cbbc70" />


### Примерна реализация

Интерфейс State

```java
public interface TicketState {

    String book(Ticket ticket);

    String buy(Ticket ticket);

    String cancel(Ticket ticket);

}
```
Concrete State
```java
public class AvailableTicket implements TicketState {

    @Override
    public String book(Ticket ticket) {
        ticket.setTicketState(new BookedTicket());
        return "Ticket " + ticket.getTicketId() + " has been booked.";
    }

    @Override
    public String buy(Ticket ticket) {
        ticket.setTicketState(new SoldTicket());
        return "Ticket " + ticket.getTicketId() + " has been sold.";
    }

    @Override
    public String cancel(Ticket ticket) {
        return "Available ticket cannot be cancelled.";
    }
}
```
```java
public class BookedTicket implements TicketState {

    @Override
    public String book(Ticket ticket) {
        return "Ticket is already booked.";
    }

    @Override
    public String buy(Ticket ticket) {
        ticket.setTicketState(new SoldTicket());
        return "Ticket " + ticket.getTicketId() + " has been sold.";
    }

    @Override
    public String cancel(Ticket ticket) {
        ticket.setTicketState(new AvailableTicket());
        return "Reservation cancelled.";
    }
}
```
```java
public class SoldTicket implements TicketState {

    @Override
    public String book(Ticket ticket) {
        return "Sold ticket cannot be booked.";
    }

    @Override
    public String buy(Ticket ticket) {
        return "Ticket is already sold.";
    }

    @Override
    public String cancel(Ticket ticket) {
        return "Sold ticket cannot be cancelled.";
    }
}
```
Context
```java
public class Ticket {

    private final int ticketId;

    private TicketState ticketState = new AvailableTicket();

    public Ticket(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketState(TicketState ticketState) {
        this.ticketState = ticketState;
    }

    public String book() {
        return ticketState.book(this);
    }

    public String buy() {
        return ticketState.buy(this);
    }

    public String cancel() {
        return ticketState.cancel(this);
    }

}
```
Използване
```java
public class Application {

    public static void main(String[] args) {

        Ticket ticket = new Ticket(123);

        System.out.println(ticket.book());
        System.out.println(ticket.cancel());
        System.out.println(ticket.buy());
        System.out.println(ticket.cancel());

    }

}
```
Резултатът е:
```java
Ticket 123 has been booked.
Reservation cancelled.
Ticket 123 has been sold.
Sold ticket cannot be cancelled.
```

Класът Ticket представлява контекстния обект (Context). Вместо да съдържа условна логика за всички възможни състояния, той пази референция към текущото състояние чрез поле от тип TicketState.

Интерфейсът TicketState дефинира общото поведение на всички състояния. Всяка конкретна реализация (AvailableTicket, BookedTicket и SoldTicket) определя как трябва да се изпълняват операциите book(), buy() и cancel().

При необходимост от промяна на състоянието конкретният клас извиква метода setTicketState() на контекста и задава ново състояние. След тази промяна всички следващи извиквания автоматично използват поведението на новото състояние.

> [!IMPORTANT]
> Шаблонът State не премахва логиката за преход между състоянията. Вместо това я разпределя между отделните класове,
> описващи конкретните състояния. Така контекстният обект остава прост и не съдържа множество условни конструкции.

### Предимства

* премахва голяма част от условната логика;
* разделя поведението на отделни класове;
* улеснява добавянето на нови състояния;
* подпомага спазването на принципа Open/Closed;
* подобрява четимостта и поддържаемостта на кода.

### Недостатъци

* увеличава броя на класовете;
* при голям брой състояния структурата може да стане по-сложна;
* преходите между състоянията трябва да бъдат проектирани внимателно.

### Приложение

Шаблонът State е подходящ когато:
* поведението на обекта зависи от неговото текущо състояние;
* има ясно разграничени състояния и преходи между тях;
* условната логика започва да се разраства;
* се цели отделяне на логиката за всяко състояние в самостоятелен клас.
