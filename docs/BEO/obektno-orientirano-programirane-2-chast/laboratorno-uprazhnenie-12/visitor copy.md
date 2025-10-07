---
layout: default
title: Mediator
parent: Лабораторно упражнение 12
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# Mediator

Шаблонът за проектиране Mediator е представител на поведенческите шаблони и - както подсказва името му - има за цел да осигури средство за комуникация между различни обекти. Използването на "посредник" при взаимодействието намалява степента на свързаност между различните обекти чрез насочване на цялата комуникация през този посредник и прави обектите независими един от друг.

За имплементиране на шаблона са необходими:
- интерфейс - медиатор;
- конкретна имплементация на медиатора, където е логиката за комуникация;
- интерфейс или абстрактен клас с референция към медиатора (нарича се "колега");
- конкретни имплементации на интерфейса, които могат да комуникират помежду си само през медиатора.


По-долу е показана примерна имплементация на шаблона, в която имаме касиер, клиенти и се осъществяват плащания.

Интерфейси за медиатор и за колега/клиент:

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

Конкретни имплементации на двата интерфейса:

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

Клиентският код има следния вид:

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
Резултатът от главната функция е:

```
John Doe is paying 15.23 to Cashier 1
Jane Doe is paying 28.94 to Cashier 1

```

Предимства на Mediator:
- намаляване свързаността между обектите;
- възможност за преизползване на медиаторите в различни контексти;
- комуникацията между обектите е централизирана вместо да е разписана на много различни места.

Недостатъци на Mediator:
- въпреки че намалява свързаността между обектите, всеки обект от своя страна е силно свързан с даден медиатор; това би могло да създаде проблеми при системи с производителността при по-сложни системи;
- добавянето на медиатор внася допълнително ниво на абстракция, което понякога прави кода по-труден за разбиране;
- не е подходящ за използване при по-прости системи, когато директната комуникация между обектите е за предпочитане.
