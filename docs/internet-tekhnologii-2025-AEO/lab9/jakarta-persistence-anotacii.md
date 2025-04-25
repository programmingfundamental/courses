---
layout: default
title: Jakarta Persistence annotations
parent: Laboratory excercise 9
grand_parent: Internet Technologies
nav_order: 2
---


# Jakarta Persistence annotations

Jakarta Persistence (JPA) is a Java object-relational mapping (ORM) standard that allows us to work with relational databases through objects. Instead of writing SQL queries directly, JPA allows us to work with Java objects that are automatically mapped to database table records.

JPA uses plain Java classes called POJOs (Plain Old Java Objects) to represent data. These classes contain only fields and accessor methods (get/set) without depending on any specific infrastructure or base classes. When we add JPA annotations to a POJO class, it becomes an entity that can be stored in the database. An entity represents a table, and each row in the table corresponds to an object of that class.

### _@Entity_

The annotation is added at the class level, setting the latter as entity. The Entity class must have a default constructor and a field set as the primary key.

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

Sets the primary table of the annotated entity.

In most cases, we don't want the name of the database table to match the name of the entity class. In such a situation, we can explicitly specify it using the @Table annotation.

```java
@Entity
@Table(name="Students")
public class Student {
    
    // fields, getters and setters
}
```

The optional annotation elements indexes, uniqueConstraints, schema, and catalog can be used to specify the indexes, unique constraints, schema, and catalog of the table.

### _@Id_

Every JPA entity must have a primary key that uniquely identifies it. The @Id annotation defines the primary key. We can generate the identifiers in different ways, which are specified by the @GeneratedValue annotation. We can choose from four strategies for generating an identifier with the strategy element. The value can be AUTO, TABLE, SEQUENCE, UUID, or IDENTITY.

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

Specifies the details of a column in the table. Elements such as _name, length, nullable, and unique can be specified._

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

Sometimes we may want to make a field transient. We can achieve this with the @Transient annotation. It indicates that the field will not be persisted. For example, we can calculate the age of a student from the date of birth. So let's annotate the age field with the @Transient annotation:

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

Hibernate annotation. Marks the field as a timestamp for the object's creation.

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

Hibernate annotation. Marks the field as a timestamp for the object's update.

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

Specifies a one-to-many (1:M) association.

The @OneToMany annotation can be used within an embedding class contained within an entity class to indicate a relationship to a collection of entities. If the relationship is bidirectional, the mappedBy element must be used to specify the relationship field or property of the entity that owns the relationship.

Example:

Class Customer

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

Class Order

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

Specifies a many-to-many association (M:M).

Example 1

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

Example 2

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
