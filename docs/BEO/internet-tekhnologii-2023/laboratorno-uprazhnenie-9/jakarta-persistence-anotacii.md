---
layout: default
title: Jakarta Persistence анотации
parent: Лабораторно упражнение 9
grand_parent: Интернет технологии
nav_order: 2
---

# ОСНОВНИ JPA АНОТАЦИИ (ENTITY МОДЕЛИРАНЕ)

Jakarta Persistence (JPA) е стандарт за обектно-релационно свързване (ORM) в Java, който позволява работа с релационни бази от данни чрез обекти. Вместо да пишем директно SQL заявки, JPA ни позволява да работим с Java обекти, които автоматично се преобразуват към записи в таблиците на базата.

JPA използва обикновени Java класове, наречени POJO (Plain Old Java Objects), за да представи данни. Тези класове съдържат само полета и методи за достъп (get/set) без да зависят от специфична инфраструктура или базови класове. Когато добавим JPA анотации към POJO клас, той се превръща в entity, който може да бъде запазен в базата. Един entity представлява таблица, a всеки ред в таблицата съответства на обект от този клас. 

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

Всеки JPA обект трябва да има първичен ключ, който го идентифицира уникално. Анотацията @Id дефинира първичния ключ. Можем да генерираме идентификаторите по различни начини, които са посочени от анотацията @GeneratedValue. Можем да избираме от четири стратегии за генериране на идентификатор с елемента strategy. Стойността може да бъде AUTO, TABLE, SEQUENCE, UUID  или IDENTITY.

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

## ВРЪЗКИ МЕЖДУ ТАБЛИЦИ

В релационните бази връзките се правят чрез foreign keys, докато в JPA те се представят чрез Java обекти.

Това означава, че вместо да се работи с ID стойности, се използват директно обекти.

### _@OneToMany_

Указва асоциация едно към много (1:M).

Анотацията @OneToMany може да се използва в рамките на клас за вграждане, съдържащ се в entity клас, за да се укаже връзка с колекция от обекти. Ако връзката е двупосочна, елементът mappedBy трябва да се използва за указване на полето на връзката или свойството на entity, който е собственик на връзката.

Пример:

Клас Customer:

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

### CascadeType

Определя как операциите върху entity се прехвърлят към свързаните entity-та.

```java
cascade = CascadeType.ALL
```

Видове:
- ALL → всички операции (save, delete, update)
- PERSIST → при запис
- MERGE → при update
- REMOVE → при delete

### @ManyToOne

Анотацията указва връзка много към един (M:1) между entity класове. Това означава, че много записи от една таблица могат да се свържат с един запис от друга таблица.

Обикновено се използва за foreign key връзки.

Пример: 

Клас Order:

```java
@Entity
@Table(name="orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	***

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="customer_id", nullable = false)
    private Customer customer;
}
```

### FetchType (LAZY / EAGER)

Определя кога се зареждат свързаните данни.

```java
@ManyToOne(fetch = FetchType.LAZY)
```

Видове:    
- LAZY 
    - данните се зареждат само когато са нужни   
    - по-ефективно за производителност
- EAGER 
    - данните се зареждат веднага
    - може да доведе до излишни заявки


### @JoinColumn

Анотацията указва колоната в таблицата, която служи като foreign key.

Тя се използва за да дефинира връзката между две таблици.

```java
@JoinColumn(name = "customer_id")
private Customer customer;
```

>
- name → името на колоната в таблицата
- свързва текущия entity с друг entity

**По подразбиране:**    
Ако НЕ се зададе name, се генерира автоматично:

```
<field_name>_<referenced_id>
```
>Където:    
`<field_name>` = името на полето в entity-то    
`<referenced_id>` = името на primary key колоната на свързаната таблица



### _@ManyToMany_

Указва асоциация много към много (M:M).

Това е най-сложният тип връзка в релационните бази данни, защото:
- един запис от първата таблица може да има много записи от втората
- и един запис от втората може да има много от първата

Затова НЕ може да се реализира директно с foreign key между двете таблици. Вместо това се използва междинна (join) таблица, която свързва двете entities чрез техните primary keys.

Пример 1

```java
@Entity
@Table(name="Customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
     ***
     
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="Cust_phones")
    private Set<PhoneNumber> phones;
}
```


- Customer има множество PhoneNumber обекти   
- @JoinTable указва, че ще бъде създадена междинна таблица с име Cust_phones   
- тъй като не са зададени колони, JPA ще ги генерира автоматично

```java
@Entity
@Table(name="phone")

public class PhoneNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToMany(mappedBy="phones", fetch = FetchType.EAGER)
    private Set<Customer> customers;
}
```
> 
- PhoneNumber също има множество Customer обекти    
- mappedBy="phones" показва, че тази страна НЕ управлява връзката     
- връзката се управлява от полето phones в класа Customer    
<br>


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
```
>
- name = "CUST_PHONES" → името на междинната таблица
- joinColumns → колоната, която сочи към текущия entity (Customer)
- inverseJoinColumns → колоната, която сочи към другия entity (PhoneNumber)

```java
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
>
- отново имаме обратната страна на връзката
- mappedBy="phones" показва, че връзката вече е дефинирана в Customer
- този клас само я използва, без да я управлява
