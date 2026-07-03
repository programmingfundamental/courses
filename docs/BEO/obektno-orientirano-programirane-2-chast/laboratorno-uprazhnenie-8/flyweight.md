---
layout: default
title: Flyweight
parent: Лабораторно упражнение 8
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# Flyweight

### Проблем

В практиката често се налага създаването на голям брой обекти, които съдържат частично еднаква информация. Ако повтарящата се информация се съхранява във всеки обект поотделно, това води до излишно използване на памет.

В банкова система например могат да съществуват хиляди банкови карти от един и същ тип — Visa Debit, Mastercard Credit и т.н. Всяка карта има индивидуални данни като номер, собственик и срок на валидност, но част от информацията е обща за всички карти от даден тип: доставчик, вид карта, валута, такса и дизайн.

### Решение

Шаблонът Flyweight отделя споделеното състояние на обектите в самостоятелен обект, който може да бъде преизползван многократно.

Индивидуалното състояние остава извън flyweight обекта и се съхранява в контекста, който го използва.

### Дефиниция

Flyweight е структурен шаблон за проектиране, който намалява броя на създаваните обекти чрез споделяне на общо, неизменяемо състояние между множество контексти.

Основни понятия:
* Flyweight - Споделен обект, който съдържа общото състояние.
* Intrinsic state - Вътрешно състояние, което се споделя и не зависи от конкретния контекст.
* Extrinsic state - Външно състояние, което е различно за всеки контекст и се подава или съхранява отделно.
* Flyweight Factory - Клас, който създава, кешира и преизползва flyweight обекти.

В примера шаблонът на картата (CardTemplate) е flyweight обект, а конкретната банкова карта (BankCard) е контекстът, който го използва.

### UML диаграма

<img width="1563" height="381" alt="Flyweight" src="https://github.com/user-attachments/assets/3a7b7ec7-f1d5-4797-88fa-0ad9cdfc3c70" />


### Примерна реализация

Flyweight
```java
public class CardTemplate {

    private final String provider;
    private final String cardType;
    private final String currency;
    private final double fee;
    private final String design;

    public CardTemplate(String provider, String cardType, String currency,
                        double fee, String design) {
        this.provider = provider;
        this.cardType = cardType;
        this.currency = currency;
        this.fee = fee;
        this.design = design;
    }

    public String getTemplateInfo() {
        return provider + " " + cardType + ", "
                + currency + ", fee: " + fee
                + ", design: " + design;
    }
}
```

Flyweight Factory
```java
import java.util.HashMap;
import java.util.Map;

public class CardTemplateFactory {

    private static final Map<String, CardTemplate> templates = new HashMap<>();

    public static CardTemplate getTemplate(String provider, String cardType,
                                           String currency, double fee,
                                           String design) {

        String key = provider + "-" + cardType + "-" + currency + "-" + design;

        CardTemplate template = templates.get(key);

        if (template == null) {
            template = new CardTemplate(provider, cardType, currency, fee, design);
            templates.put(key, template);
        }

        return template;
    }

    public static int getTemplatesCount() {
        return templates.size();
    }
}
```

Context
```java
public class BankCard {

    private final String cardNumber;
    private final String owner;
    private final String expirationDate;
    private final CardTemplate template;

    public BankCard(String cardNumber, String owner,
                    String expirationDate, CardTemplate template) {
        this.cardNumber = cardNumber;
        this.owner = owner;
        this.expirationDate = expirationDate;
        this.template = template;
    }

    public String getCardInfo() {
        return cardNumber + " - " + owner + " - "
                + expirationDate + " - "
                + template.getTemplateInfo();
    }
}
```

Използване
```java
public class Application {

    public static void main(String[] args) {

        CardTemplate visaDebit =
                CardTemplateFactory.getTemplate(
                        "Visa", "Debit", "EUR", 2.50, "Blue");

        CardTemplate anotherVisaDebit =
                CardTemplateFactory.getTemplate(
                        "Visa", "Debit", "EUR", 2.50, "Blue");

        CardTemplate mastercardCredit =
                CardTemplateFactory.getTemplate(
                        "Mastercard", "Credit", "EUR", 4.00, "Silver");

        BankCard firstCard = new BankCard(
                "1111-2222-3333-4444",
                "John Smith",
                "12/28",
                visaDebit);

        BankCard secondCard = new BankCard(
                "5555-6666-7777-8888",
                "Anna Brown",
                "09/27",
                anotherVisaDebit);

        BankCard thirdCard = new BankCard(
                "9999-0000-1111-2222",
                "Peter Johnson",
                "03/29",
                mastercardCredit);

        System.out.println(firstCard.getCardInfo());
        System.out.println(secondCard.getCardInfo());
        System.out.println(thirdCard.getCardInfo());

        System.out.println(CardTemplateFactory.getTemplatesCount());
    }
}
```

Класът CardTemplate съдържа общото състояние на картите: доставчик, тип карта, валута, такса и дизайн. Това състояние е еднакво за много карти и не зависи от конкретен клиент.

Класът BankCard съдържа индивидуалното състояние: номер на карта, собственик и срок на валидност. Освен това той съдържа референция към обект от тип CardTemplate.

Класът CardTemplateFactory отговаря за създаването и кеширането на шаблоните. Ако вече съществува шаблон със същите характеристики, фабриката връща съществуващия обект, вместо да създава нов.

В примера първата и втората карта използват един и същ шаблон Visa Debit, затова във фабриката се съхраняват само два шаблона, въпреки че са създадени три банкови карти.

> [!IMPORTANT]
> Flyweight не споделя целите обекти, а само тяхното общо и неизменяемо състояние.
> Индивидуалните данни остават извън flyweight обекта и се съхраняват в контекста.

### Предимства

* намалява броя на създаваните обекти;
* намалява използваната памет при голям брой сходни обекти;
* отделя общото от индивидуалното състояние;
* позволява преизползване на вече създадени обекти.

### Недостатъци

* усложнява структурата на приложението;
* изисква ясно разграничаване между вътрешно и външно състояние;
* фабриката трябва да управлява кеша от споделени обекти;
* не е подходящ, ако обектите не споделят значителна част от състоянието си.

### Приложение

Flyweight е подходящ когато:
* се създава голям брой сходни обекти;
* голяма част от състоянието на тези обекти се повтаря;
* споделеното състояние може да бъде отделено и направено неизменяемо;
* оптимизацията на паметта е важна за приложението.

