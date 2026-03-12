---
layout: default
title: Лабораторно упражнение 4
parent: Интернет технологии
has_children: true
nav_order: 4
permalink: /docs/internet-tehnologii-2023/laboratorno-uprazhnenie-4
---

# Лабораторно упражнение 4

## XML Адаптери

JAXB/ Jakarta XML Binding служи за преобразуване между Java обекти и XML. Когато Java обект не се сериализира по подразбиране, се използва XmlAdapter, който преобразува между:

- Bound type – реалният Java тип в твоя клас
- Value type – типът, който JAXB записва/чете в XML

За да кажеш на JAXB да ползва адаптера, се използва анотацията @XmlJavaTypeAdapter. Тя може да се сложи върху поле, метод, клас или дори на package ниво. Ако е на поле, важи само за него; ако е на клас, важи за всички препратки към този клас.

Примерно преобразуването между: LocalDateTime ↔ String

JAXB често не работи директно с LocalDateTime, затова го адаптираме до String.

### 1. Адаптер

```java
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    @Override
    public LocalDateTime unmarshal(String value) {
        return value == null ? null : LocalDateTime.parse(value);
    }

    @Override
    public String marshal(LocalDateTime value) {
        return value == null ? null : value.toString();
    }
}
```

Идеята е:
- при marshal: LocalDateTime -> String
- при unmarshal: String -> LocalDateTime

### Клас, който ще се сериализира

```java
@XmlRootElement(name = "task")
@XmlAccessorType(XmlAccessType.FIELD)
public class Task {

    private int id;
    private String title;
    private String description;

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime deadline;

    public Task() {}

    public Task(int id, String title, String description, LocalDateTime deadline) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
    }
}
```

Без адаптер JAXB не винаги знае как да преобразува съставни типове.

С адаптер се казва:

- в Java искам полето да е LocalDateTime
- в XML искам то да се пази като String

Това е основната логика на XmlAdapter<ValueType, BoundType>