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

Примерно преобразуването между: LocalDate ↔ String

JAXB често не работи директно с LocalDate, затова го адаптираме до String.

### 1. Адаптер

```java
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public LocalDate unmarshal(String v) throws Exception {
        return v == null ? null : LocalDate.parse(v);
    }

    @Override
    public String marshal(LocalDate v) throws Exception {
        return v == null ? null : v.toString();
    }
}
```

Идеята е:
- при marshal: LocalDate -> String
- при unmarshal: String -> LocalDate

### Клас, който ще се сериализира

```java
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlJavaTypeAdapter;
import java.time.LocalDate;

@XmlRootElement
public class Task {

    private int id;
    private String title;
    private String description;

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate deadline;

    public Task() {}

    public Task(int id, String title, String description, LocalDate deadline) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
    }

    // getters и setters
}
```

Без адаптер JAXB не винаги знае как да преобразува съставни типове.

С адаптер се казва:

- в Java искам полето да е LocalDate
- в XML искам то да се пази като String

Това е основната логика на XmlAdapter<ValueType, BoundType>