---
layout: default
title: Jakarta Persistence анотации
parent: Лабораторно упражнение 10
grand_parent: Интернет технологии
nav_order: 2
---

# Jakarta Persistence анотации

Entity клас в JPA е POJO, представляващ данни, които могат да бъдат запазени в базата данни. Един entity представлява таблица, съхранявана в база данни. Всеки ред в таблицата съответства на обект от този клас. 

### _@Entity_

Анотацията се добавя на ниво клас, като задава последният като entity.  Entity класът трябва да разполага с дефолтен конструктор и поле, зададено като първичен ключ.  

```java
@Entity
public class Student {
    @Id
    private Long id;
    private String name;
    // getters and setters
}
```

### _@Table_

Задава първичната таблица на анотирания entity.

В повечето случаи не желаем името на таблицата в базата от данни да съвпада с името на entity класа. При подобна ситуация, можем изрично да го посочим посредством анотацията @Table.

```java
@Entity
@Table(name="Students")
public class Student {
    
    // fields, getters and setters
}
```

С помощта на опционалните елементи на анотацията indexes, uniqueConstraints, schema и catalog могат да се посочат индексите, ограниченията за уникалност, схемата и каталог на таблицата.

### _@Id_

Всеки JPA обект трябва да има първичен ключ, който го идентифицира уникално. Анотацията @Id дефинира първичния ключ. Можем да генерираме идентификаторите по различни начини, които са посочени от анотацията @GeneratedValue. Можем да избираме от четири стратегии за генериране на идентификатор с елемента strategy. Стойността може да бъде AUTO, TABLE, SEQUENCE или IDENTITY.

```java
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // getters and setters
}
```

### _@Column_

Уточнява детайлите на колона в таблицата. Може да бъдат зададени елементи като _name, length, nullable и unique._

```java
@Entity
@Table(name="Students")
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="student_name", length=50, nullable=false, unique=false)
    private String name;
    
    // other fields, getters and setters
}
```

### _@Transient_

Понякога може да искаме да направим дадено поле преходно. Можем да постигнем това с анотацията @Transient. Тя указва, че полето няма да бъде запазено.  Например можем да изчислим възрастта на ученик от датата на раждане. Така че нека да коментираме възрастта на полето с анотацията @Transient:

```java
@Entity
@Table(name="Students")
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Column(name="student_name", length=50, nullable=false)
    private String name;
    
    @Transient
    private Integer age;
    
    // other fields, getters and setters
}
```

### _@CreationTimestamp_

Hibernate анотация. Маркира полето като печат за времето на създаване на обекта.

```java
@Entity
public class Book {
    //other fields

    @CreationTimestamp
    private Instant createdOn;

    // standard setters and getters
}
```

### _@UpdateTimestamp_

Hibernate анотация. Маркира полето като печат за времето на актуализиране на обекта.

```java
@Entity
public class Book {
    //other fields

    @UpdateTimestamp
    private Instant lastUpdatedOn;

    // standard setters and getters
}
```

### _@OneToMany_

Указва асоциация едно към много (1:M).

Анотацията @OneToMany може да се използва в рамките на клас за вграждане, съдържащ се в entity клас, за да се укаже връзка с колекция от обекти. Ако връзката е двупосочна, елементът mappedBy трябва да се използва за указване на полето на връзката или свойството на entity, който е собственик на връзката.

Пример:

Клас Customer

```java
@Entity
@Table(name="customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    ****

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, 
    orphanRemoval = true)
    private Set<Order> orders = new HashSet<>();
}
```

Клас Order

```java
@Entity
@Table(name="orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	***

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="task_id", nullable = false)
    private Customer customer;
}
```

### _@ManyToMany_

Указва асоциация много към много (M:M).

Пример 1

```java
@Entity
@Table(name="Customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
     ***
     
    @ManyToMany
    @JoinTable(name="Cust_phones")
    private Set<PhoneNumber> phones;
}


@Entity
@Table(name="phone")

public class PhoneNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToMany(mappedBy="phones")
    private Set<Customer> customers;
}
```

Пример 2

```java
@Entity
@Table(name="customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    ***
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "CUST_PHONES",
        joinColumns = @JoinColumn(name = "customer_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "phone_id", referencedColumnName = "id"))
    private Set<PhoneNumber> phones;
}

@Entity
@Table(name="phone_numbers")
public class PhoneNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    ****

    @ManyToMany(mappedBy="phones")
    private Set<Customer> customers; 
}
```
