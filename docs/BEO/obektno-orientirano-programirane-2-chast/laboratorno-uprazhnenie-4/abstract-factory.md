---
layout: default
title: Abstract Factory
parent: Лабораторно упражнение 4
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# Abstract Factory

### Проблем

Да се разработи част от банкова система за издаване на банкови карти.

Системата трябва да поддържа различни картови организации, например Visa и Mastercard. За всяка организация трябва да могат да бъдат създавани дебитна и кредитна карта.

### Решение без използване на шаблона

Един възможен подход е клиентският код директно да създава необходимите обекти:

```java
public class Application {

    public static void main(String[] args) {

        DebitCard debitCard = new VisaDebitCard();
        CreditCard creditCard = new VisaCreditCard();

        System.out.println(debitCard.getCardType());
        System.out.println(creditCard.getCardType());
    }
}
```
При необходимост от използване на карти на друга картова организация клиентският код трябва да бъде променен:

```java
DebitCard debitCard = new MastercardDebitCard();
CreditCard creditCard = new MastercardCreditCard();
```

### Недостатъци на решението

При този подход клиентският код зависи от конкретните класове, които създава. При промяна на използваната картова организация е необходимо всички места, в които се създават конкретни обекти, да бъдат модифицирани.

Освен това липсва централизирано място, което да гарантира, че създадените обекти принадлежат към една и съща картова организация. Например по невнимание може да бъде създадена дебитна карта Visa заедно с кредитна карта Mastercard.

Следователно е необходимо решение, което централизира създаването на свързаните обекти и гарантира, че те принадлежат към една и съща картова организация.


### Решение

Шаблонът **Abstract Factory** предоставя общ интерфейс за създаване на семейства от взаимосвързани обекти.

Вместо клиентът директно да създава конкретните обекти, той използва фабрика. В зависимост от избраната фабрика се създава цялото семейство продукти.

Например:
* VisaCardFactory създава Visa Debit Card и Visa Credit Card;
* MastercardCardFactory създава Mastercard Debit Card и Mastercard Credit Card.

По този начин смяната на цялото семейство продукти се свежда единствено до смяна на използваната фабрика.

### Дефиниция

Abstract Factory е шаблон за проектиране от групата на създаващите (Creational) шаблони, който предоставя интерфейс за създаване на семейства от взаимосвързани или зависими обекти, без да се посочват техните конкретни класове.

### UML диаграма

<img width="755" height="310" alt="AbstractFactory" src="https://github.com/user-attachments/assets/67b82c3d-2ceb-4082-addf-50377f0af977" />

### Примерна реализация

Интерфейси на продуктите:
```java
public interface DebitCard {

    String getCardType();

}
```
```java
public interface CreditCard {

    String getCardType();

}
```
Конкретни продукти:
```java
public class VisaDebitCard implements DebitCard {

    @Override
    public String getCardType() {
        return "Visa Debit";
    }

}
```
```java
public class VisaCreditCard implements CreditCard {

    @Override
    public String getCardType() {
        return "Visa Credit";
    }

}
```
```java
public class MastercardDebitCard implements DebitCard {

    @Override
    public String getCardType() {
        return "Mastercard Debit";
    }

}
```
```java
public class MastercardCreditCard implements CreditCard {

    @Override
    public String getCardType() {
        return "Mastercard Credit";
    }

}
```
Абстрактна фабрика:
```java
public interface CardFactory {

    DebitCard createDebitCard();

    CreditCard createCreditCard();

}
```
Конкретни фабрики:
```java
public class VisaCardFactory implements CardFactory {

    @Override
    public DebitCard createDebitCard() {
        return new VisaDebitCard();
    }

    @Override
    public CreditCard createCreditCard() {
        return new VisaCreditCard();
    }

}
```
```java
public class MastercardCardFactory implements CardFactory {

    @Override
    public DebitCard createDebitCard() {
        return new MastercardDebitCard();
    }

    @Override
    public CreditCard createCreditCard() {
        return new MastercardCreditCard();
    }

}
```
Клиентски код:
```java
public class Application {

    public static void main(String[] args) {

        CardFactory factory = new VisaCardFactory();

        DebitCard debitCard = factory.createDebitCard();
        CreditCard creditCard = factory.createCreditCard();

        System.out.println(debitCard.getCardType());
        System.out.println(creditCard.getCardType());

    }

}
```
При необходимост от използване на друго семейство продукти е достатъчно да бъде сменена използваната фабрика:
```java
CardFactory factory = new MastercardCardFactory();
```
Без никакви промени в останалата част на приложението.

Клиентският код работи единствено с интерфейса CardFactory и интерфейсите на продуктите (DebitCard и CreditCard).

След като бъде избрана конкретната фабрика, тя създава всички необходими обекти от съответното семейство. Клиентът не създава директно конкретните продукти и не зависи от техните реализации.

В реални приложения изборът на конкретната фабрика често се извършва чрез конфигурационен файл, dependency injection, потребителски избор или друг механизъм. След като фабриката бъде избрана, останалата част на приложението работи единствено чрез общите интерфейси.

> [!IMPORTANT]
> Abstract Factory се използва за създаване на семейства от взаимосвързани обекти. Неговата
> основна цел не е просто да капсулира създаването на обекти, а да гарантира, че използваните
>  продукти принадлежат към едно и също семейство и могат да работят съвместно.


### Предимства

* капсулира създаването на обекти;
* позволява лесна смяна на цяло семейство продукти;
* клиентският код работи само с интерфейси;
* намалява зависимостта от конкретни класове;
* улеснява разширяването на приложението с нови семейства продукти.

### Недостатъци

* увеличава броя на класовете;
* при добавяне на нов вид продукт трябва да бъдат променени всички фабрики;
* структурата е по-сложна в сравнение с Factory Method.

### Приложение

Шаблонът Abstract Factory е подходящ когато:
* се работи със семейства от взаимосвързани обекти;
* е необходимо лесно превключване между различни реализации;
* клиентският код не трябва да зависи от конкретните класове;
* трябва да се гарантира, че използваните обекти принадлежат към едно и също семейство.

