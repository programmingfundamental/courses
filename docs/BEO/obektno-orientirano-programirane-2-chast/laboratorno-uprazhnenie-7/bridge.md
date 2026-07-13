---
layout: default
title: Bridge
parent: Лабораторно упражнение 7
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# Bridge

### Проблем

Да се разработи част от система за банкови карти.

Системата трябва да поддържа различни типове карти — дебитни и кредитни. Картите могат да бъдат издавани от различни доставчици на картови услуги — Visa, Mastercard и Borica.

### Решение без използване на шаблона

Един възможен подход е всяка комбинация между тип карта и доставчик да бъде реализирана чрез отделен клас.

```java
public abstract class Card {

    public abstract String getCardInformation();
}
```
```java
public class VisaDebitCard extends Card {

    @Override
    public String getCardInformation() {
        return "Debit card - Visa";
    }
}
```
```java
public class MastercardDebitCard extends Card {

    @Override
    public String getCardInformation() {
        return "Debit card - Mastercard";
    }
}
```
```java
public class VisaCreditCard extends Card {

    @Override
    public String getCardInformation() {
        return "Credit card - Visa";
    }
}
```
```java
public class MastercardCreditCard extends Card {

    @Override
    public String getCardInformation() {
        return "Credit card - Mastercard";
    }
}
```
Ако се добави нов доставчик, например Borica, трябва да бъдат създадени нови класове:
```java
public class BoricaDebitCard extends Card {

    @Override
    public String getCardInformation() {
        return "Debit card - Borica";
    }
}
```
```java
public class BoricaCreditCard extends Card {

    @Override
    public String getCardInformation() {
        return "Credit card - Borica";
    }
}
```

### Недостатъци на решението

При този подход броят на класовете нараства с всяка нова комбинация между тип карта и доставчик. Ако се добави нов тип карта, трябва да се създадат класове за всички съществуващи доставчици. Ако се добави нов доставчик, трябва да се създадат класове за всички съществуващи типове карти.

Така две независими характеристики — типът на картата и доставчикът — се комбинират чрез наследяване, което води до бързо нарастване на броя класове.

Следователно е необходимо решение, което позволява типовете карти и доставчиците да се развиват независимо и да се комбинират свободно.


### Шаблонът като решение

Bridge разделя абстракцията от нейната имплементация, като изгражда две независими йерархии, свързани чрез референция между обектите.

По този начин всяка от двете йерархии може да бъде разширявана независимо, без това да води до промени в другата.

### Дефиниция

Bridge е структурен шаблон за проектиране, който разделя абстракцията (Abstraction) от нейната имплементация (Implementor), така че двете да могат да се развиват независимо една от друга.

В шаблона Bridge основните участници са:
* Абстракция (Abstraction) – описва логиката от по-високо ниво и съдържа референция към обект, реализиращ имплементацията.
* Имплементация (Implementor) – интерфейс, който дефинира операциите, реализирани от конкретните имплементации.

### UML диаграма

<img width="1030" height="375" alt="Bridge" src="https://github.com/user-attachments/assets/514cff54-518c-4e66-9632-270d0ad39645" />





| Термин 					   | В примера 										  |
|------------------------------|--------------------------------------------------|
| Абстракция (Abstraction)     | Card 											  |
| Имплементация (Implementor)  | PaymentProvider 							      |
| Конкретна абстракция         | DebitCard, CreditCard 						      |
| Конкретна имплементация      | VisaProvider, MastercardProvider, BoricaProvider |

### Примерна реализация

Имплементатор
```java
public interface PaymentProvider {

    String getProviderName();

}
```
```java
public class VisaProvider implements PaymentProvider {

    @Override
    public String getProviderName() {
        return "Visa";
    }

}
```
```java
public class MastercardProvider implements PaymentProvider {

    @Override
    public String getProviderName() {
        return "Mastercard";
    }

}
```
```java
public class BoricaProvider implements PaymentProvider {

    @Override
    public String getProviderName() {
        return "Borica";
    }

}
```

Абстракция
```java
public abstract class Card {

    private final PaymentProvider provider;

    protected Card(PaymentProvider provider) {
        this.provider = provider;
    }

    protected PaymentProvider getProvider() {
        return provider;
    }

    public abstract String getCardInformation();

}
```
```java
public class CreditCard extends Card {

    public CreditCard(PaymentProvider provider) {
        super(provider);
    }

    @Override
    public String getCardInformation() {
        return "Credit card - " + getProvider().getProviderName();
    }
}
```
```java
public class DebitCard extends Card {

    public DebitCard(PaymentProvider provider) {
        super(provider);
    }

    @Override
    public String getCardInformation() {
        return "Debit card - " + getProvider().getProviderName();
    }
}
```
Използване
```java
public class Application {

    public static void main(String[] args) {

        Card debitVisa =
                new DebitCard(new VisaProvider());

        Card creditMastercard =
                new CreditCard(new MastercardProvider());

        Card debitBorica =
                new DebitCard(new BoricaProvider());

        System.out.println(debitVisa.getCardInformation());
        System.out.println(creditMastercard.getCardInformation());
        System.out.println(debitBorica.getCardInformation());

    }

}
```

В примера съществуват две независими йерархии.

Първата описва различните видове банкови карти (`DebitCard`, `CreditCard`), а втората – различните доставчици на картови услуги (`VisaProvider`, `MastercardProvider`, `BoricaProvider`).

Абстрактният клас `Card` съдържа частно поле от тип `PaymentProvider`. То е декларирано като `final`, тъй като доставчикът се задава при създаване на картата и не се променя след това.

Достъпът до доставчика от класовете наследници се осъществява чрез защитения метод `getProvider()`. По този начин полето остава капсулирано, а наследниците могат да използват необходимата информация, без да работят директно с вътрешното състояние на родителския клас.

Класовете `DebitCard` и `CreditCard` използват доставчика чрез метода `getProvider()` и добавят информацията за него към описанието на съответната карта.

По този начин може свободно да се комбинира всеки тип карта с всеки доставчик, без да се създават отделни класове за всяка комбинация.

>[!IMPORTANT]
> Подобно на Adapter и Decorator, Bridge използва делегиране чрез референция към друг обект
> (object composition). За разлика от тях целта на Bridge не е адаптиране на интерфейси или
> добавяне на нови отговорности, а разделяне на две независими йерархии, които могат да се
> разширяват самостоятелно.

### Предимства

* позволява независимо разширяване на абстракцията и имплементацията;
* избягва създаването на голям брой класове при множество комбинации;
* намалява зависимостите между двете йерархии;
* улеснява поддръжката и разширяването на системата;
* подпомага спазването на принципа Open/Closed.

### Недостатъци

* увеличава броя на класовете;
* първоначалната архитектура е по-сложна;
* използването му не е оправдано при малки и прости приложения.

### Приложение

Bridge е подходящ когато:
* абстракцията и имплементацията трябва да могат да се развиват независимо;
* съществуват две независими характеристики, които могат да се комбинират по различни начини;
* наследяването би довело до голям брой класове;
* се цели намаляване на зависимостите между различните части на системата.
