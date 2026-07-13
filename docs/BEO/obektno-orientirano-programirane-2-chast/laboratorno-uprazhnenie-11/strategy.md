---
layout: default
title: Strategy
parent: Лабораторно упражнение 11
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 1
---

# Strategy

### Проблем

Да се разработи клас Order, който изчислява крайната цена на поръчка.

Поръчката има начална цена, като в различни ситуации може да се приложи различен вид отстъпка: без отстъпка, студентска отстъпка или VIP отстъпка.


### Решение без използване на шаблона

```java
public class Order {

    private double initialPrice;
    private String discountType;

    public Order(double initialPrice, String discountType) {
        this.initialPrice = initialPrice;
        this.discountType = discountType;
    }

    public double getFinalPrice() {
        if (discountType.equalsIgnoreCase("none")) {
            return initialPrice;
        }

        if (discountType.equalsIgnoreCase("student")) {
            return initialPrice * 0.90;
        }

        if (discountType.equalsIgnoreCase("vip")) {
            return initialPrice * 0.80;
        }

        return initialPrice;
    }
}
```

### Недостатъци на решението

При този подход класът Order съдържа логиката за всички видове отстъпки. При добавяне на нова отстъпка трябва да се променя методът getFinalPrice().

Освен това изборът на алгоритъм е реализиран чрез условни конструкции, което прави кода по-труден за поддръжка при увеличаване на броя варианти.

Следователно е необходимо решение, при което различните начини за изчисляване на крайната цена да бъдат отделени в самостоятелни класове и да могат да се заменят без промяна в класа Order.


### Шаблонът като решение

Strategy отделя различните алгоритми в самостоятелни класове и позволява те да бъдат взаимозаменяеми.

Контекстният обект използва избрана стратегия, без да познава детайлите на нейното изпълнение.

### Дефиниция

Strategy е поведенчески шаблон за проектиране, който дефинира семейство от алгоритми, капсулира всеки от тях в отделен клас и позволява тяхната взаимна замяна по време на изпълнение.

Основни термини:
* Context - Обектът, който използва избраната стратегия.
* Strategy - Интерфейсът, който дефинира общото поведение на алгоритмите.
* Concrete Strategy - Конкретна реализация на даден алгоритъм.

### UML диаграма

<img width="640" height="358" alt="Strategy" src="https://github.com/user-attachments/assets/1c65702e-4345-4dc7-823f-8e3257756781" />


### Примерна реализация

```java
public interface DiscountStrategy {

    double applyDiscount(double price);
}
```
```java
public class NoDiscountStrategy implements DiscountStrategy {

    @Override
    public double applyDiscount(double price) {
        return price;
    }
}
```
```java
public class StudentDiscountStrategy implements DiscountStrategy {

    @Override
    public double applyDiscount(double price) {
        return price * 0.90;
    }
}
```
```java
public class VipDiscountStrategy implements DiscountStrategy {

    @Override
    public double applyDiscount(double price) {
        return price * 0.80;
    }
}
```
Контекст
```java
public class Order {

    private final double initialPrice;
    private final DiscountStrategy discountStrategy;

    public Order(double initialPrice, DiscountStrategy discountStrategy) {
        this.initialPrice = initialPrice;
        this.discountStrategy = discountStrategy;
    }

    public double getFinalPrice() {
        return discountStrategy.applyDiscount(initialPrice);
    }
}
```
Използване
```java
public class Application {

    public static void main(String[] args) {

        Order firstOrder = new Order(100.00, new NoDiscountStrategy());

        Order secondOrder = new Order(100.00, new StudentDiscountStrategy());

        Order thirdOrder = new Order(100.00, new VipDiscountStrategy());

        System.out.println(firstOrder.getFinalPrice());
        System.out.println(secondOrder.getFinalPrice());
        System.out.println(thirdOrder.getFinalPrice());
    }
}
```

Класът Order е контекстът. Той съдържа началната цена и референция към обект от тип DiscountStrategy.

Интерфейсът DiscountStrategy дефинира общ метод за изчисляване на цена след прилагане на отстъпка.

Всеки конкретен клас реализира различен алгоритъм за изчисление. Класът Order не знае как точно се пресмята отстъпката, а само извиква метода applyDiscount() на избраната стратегия.

> [!IMPORTANT]
> При Strategy се променя алгоритъмът, а не състоянието на обекта. Това е основната разлика
> между Strategy и State.

### Предимства

* премахва част от условната логика;
* позволява лесно добавяне на нови алгоритми;
* стратегиите са взаимозаменяеми;
* контекстният обект не зависи от конкретните реализации;
* подпомага спазването на принципа Open/Closed.

### Недостатъци

* увеличава броя на класовете;
* клиентът трябва да избере подходящата стратегия;
* не е оправдан при много малко и рядко променящи се варианти.

### Приложение

Strategy е подходящ когато:
* съществуват няколко варианта на един и същ алгоритъм;
* изборът на алгоритъм трябва да може да се променя;
* не е желателно използването на множество условни конструкции;
* различните алгоритми трябва да бъдат отделени в самостоятелни класове.

