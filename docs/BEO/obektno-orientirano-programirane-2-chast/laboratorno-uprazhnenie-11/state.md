---
layout: default
title: State
parent: Лабораторно упражнение 11
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 3
---

# State

Моделът на проектиране на състоянието позволява промяна на поведението на обект при промяна на неговото вътрешно състояние. Обикновено различните състояния са обособени в отделни класове и поведението се делегира на текущото състояние.

State елиминира условната логика чрез полиморфизъм. Неговото използване спестява писането на множество условни конструкции, силно разклонена логика и нарушаването на OCP.

За реализация на шаблона State са необходими контекст (обект, чието поведение се променя), състояние (описвано чрез интерфейс, дефиниращ поведението на контекста) и конкретни състояния (представляващи реализации на дадено състояние).

Основни характеристики на State:

- поведението на обекта варира според състоянието

- преходите между различните състояния могат да бъдат контролирани или от самите състояния, или от контекста (съществуват различни имплементации)

- използва композиция и полиморфизъм

- намалява присъствието на условна логика.


Нека разгледаме пример за билет, който може да бъде резервиран, закупен и отказан. Реализацията по-долу е без използване на шаблона State.

```

public class Ticket {

    private String ticketState;
    private int ticketId;

    public Ticket(int ticketId) {
        this.ticketState = "available";
        this.ticketId = ticketId;
    }

    public String book() {
        if (ticketState.equalsIgnoreCase("available")) {
            ticketState = "booked";
            return "Ticket with id = " + this.ticketId + " has been booked";
        } else if (ticketState.equalsIgnoreCase("booked")) {
            return "Ticket with id = " + this.ticketId + " has been already booked";
        } else if (ticketState.equalsIgnoreCase("sold")) {
            return "Ticket with id = " + this.ticketId + " has been already sold";
        }
        return "Unrecognized state";
    }

    public String buy() {
        if (ticketState.equalsIgnoreCase("available") ||
				ticketState.equalsIgnoreCase("booked")) {
            ticketState = "sold";
            return "Ticket with id = " + this.ticketId + " has been sold";
        } else if (ticketState.equalsIgnoreCase("sold")) {
            return "Ticket with id = " + this.ticketId + " has been already sold";
        }
        return "Unrecognized state";
    }

    public String cancel() {
        if (ticketState.equalsIgnoreCase("available")) {
            return "Available ticket cannot be cancelled";
        } else if (ticketState.equalsIgnoreCase("booked")) {
            ticketState = "available";
            return "Ticket with id = " + this.ticketId + " has been cancelled";
        } else if (ticketState.equalsIgnoreCase("sold")) {
            return "Sold ticket cannot be cancelled";
        }
        return "Unrecognized state";
    }

}

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

Полученият резултат от изпълнението на горния пример е:

```
Ticket with id = 123 has been booked
Ticket with id = 123 has been cancelled
Ticket with id = 123 has been sold
Sold ticket cannot be cancelled

```


По-долу е показан същия пример, но реализиран с използване на шаблона State:


```
public interface TicketState {

    String bookTicket(Ticket ticket);

    String buyTicket(Ticket ticket);

    String cancelTicket(Ticket ticket);
}
```
Интерфейсът описва възможните състояния на контекста (Ticket).

Всяко състояние представлява отделен клас.

```
public class AvailableTicket implements TicketState {

    @Override
    public String bookTicket(Ticket ticket) {
        ticket.setTicketState(new BookedTicket());
        return "Ticket with id = " + ticket.getTicketId() + " has been booked";
    }

    @Override
    public String buyTicket(Ticket ticket) {
        ticket.setTicketState(new SoldTicket());
        return "Ticket with id = " + ticket.getTicketId() + " has been sold";
    }

    @Override
    public String cancelTicket(Ticket ticket) {
        return "No ticket to be cancelled";
    }
}
```

```
public class BookedTicket implements TicketState {
    @Override
    public String bookTicket(Ticket ticket) {
        return "Ticket with id = " + ticket.getTicketId() + " is already booked";
    }

    @Override
    public String buyTicket(Ticket ticket) {
        ticket.setTicketState(new SoldTicket());
        return "Ticket with id = " + ticket.getTicketId() + " has been sold";
    }

    @Override
    public String cancelTicket(Ticket ticket) {
        ticket.setTicketState(new AvailableTicket());
        return "Ticket with id = " + ticket.getTicketId() + " has been cancelled";
    }
}
```

```
public class SoldTicket implements TicketState {
    @Override
    public String bookTicket(Ticket ticket) {
        return "Sold ticket cannot be booked";
    }

    @Override
    public String buyTicket(Ticket ticket) {
        return "Sold ticket cannot be sold";
    }

    @Override
    public String cancelTicket(Ticket ticket) {
        return "Sold ticket cannot be cancelled";
    }
}
```

Контекстният обект ще изглежда така:

```
public class Ticket {

    private TicketState ticketState = new AvailableTicket();
    private int ticketId;

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
        return ticketState.bookTicket(this);
    }

    public String buy() {
        return ticketState.buyTicket(this);
    }

    public String cancel() {
        return ticketState.cancelTicket(this);
    }
}

```

Тестването и резултата от реализирания код изглеждат по напълно идентичен начин.

Реализираният със State пример представя ясно разделение на ролите и при него състоянието не пази вътрешни данни.

Важно е да се отбележи, че State не е универсално решение за премахване на всякаква условна логика.



В заключение, използването на шаблона State е подходящо при решаване на задачи, при които:

- има ясно разграничени състояния

- поведението на обекта се променя

- наблюдават се преходи между различни състояния

- не желаем условна логика в контекста.


