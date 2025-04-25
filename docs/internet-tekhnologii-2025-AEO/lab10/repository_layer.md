---
layout: default
title: Repository layer
parent: Laboratory excercise 10
grand_parent: Internet Technologies
nav_order: 1
---

# Repository layer

Typically, data management applications are implemented where data needs to be stored, edited, and deleted. For these, you need to implement CRUD operations (Create, Read, Update, Delete) for individual objects. Instead of writing the same CRUD operations over and over again, Spring Data provides various abstractions such as CrudRepository, PagingAndSortingRepository, and JpaRepository. They provide out-of-the-box support for CRUD operations, paging, and sorting.

We need to create an interface extending JpaRepository:

```java
public interface CityRepository extends JpaRepository<City, Long> {
}
```

This achieves two goals:

· First, by extending JpaRepository we get a set of common CRUD methods for our type, which allows saving cities, deleting them, etc., without having to write their implementation

· Second, using the Spring Data JPA repository infrastructure, this interface will be automatically scanned.

Some common methods provided:

```java
//Stores the given object in the DB
<S extends T> S save(S entity);

//Returns the object by the given id
Optional<T> findById(ID id);

//Returns all objects of the given type
List<T> findAll();

//Deletes the given object
void delete(T entity);
```

The following methods are accessed directly in the service layer:

```java
@Override
public City createCity(City city) {

City newCity = cityRepository.save(city);
return newCity;
}
```

## Query methods

Query methods are a powerful tool for interacting with database records without having to write SQL queries. Behind the scenes, based on the query method, Spring Data JPA will create a SQL query and execute it for us. The invoked query is retrieved from the method name.

Example:

```java
public interface UserRepository extends JpaRepository<User, Long> {

List<User> findByEmailAddressAndLastname(String emailAddress, String lastname);

}
```

Spring Data JPA translates the method name into the following JPQL query:

**select u from User u where u.emailAddress = ?1 and u.lastname = ?2**

### Rules for creating Query methods

1. The method name must start with one of the following prefixes: find…By, read…By, query…By, count…By, get…By, etc. Examples: findByName, readByName, queryByName, getByName
2. If we want to limit the number of results returned, we can add the keywords First or Top before By. Examples: findFirstByName, readFirst2ByName, findTop10ByName
3. If we want to select unique results, we add the keyword Distinct before By. Examples: findDistinctByName or findNameDistinctBy
4. Combine expressions with AND or OR. Examples: findByNameOrDescription, findByNameAndDescription

[List of supported keywords](https://docs.spring.io/spring-data/jpa/docs/3.0.x/reference/html/#repository-query-keywords).

More sample methods:

```java

Optional<Dog> findById(Long id);

List<Dog> findByAgeAndHeight(Integer age, Double height);

Integer countByName(String name);

Dog findFirstByName(String name);

List<Dog> findTop10ByColor(String color);

Dog findTopByOrderByBirthDateDesc();

List<Dog> findByAgeLessThanOrHeightGreaterThan(Integer age, double height);

```

### @Query annotation

Using the Query methods for more complex queries, we can reach a situation where our method looks like this: findAllByPostAndStatusAndReviewLikeAndVotesGreaterThanEqualOrderByCreatedOn

In addition to the confusing appearance, the query submitted in this way will be slower to execute. To solve this problem, we can use the @Query annotation.

Examples:

<pre class="language-java"><code class="lang-java">@Query("SELECT t FROM Tutorial t")
List&#x3C;Tutorial> findAll();

@Query("SELECT u FROM User u WHERE u.emailAddress = ?1")
User findByEmailAddress(String emailAddress);

@Query("SELECT * FROM tutorials t WHERE t.title LIKE %?1%")
List&#x3C;Tutorial> findByTitleAndSort(String title, Sort sort);

@Query("SELECT MAX(eventId) AS eventId FROM Event")
Long lastProcessedEvent();

@Query("SELECT t FROM Tutorial t
WHERE t.published=:isPublished AND t.level BETWEEN :start AND :end")
List&#x3C;Tutorial> findByLevelBetween( 
@Param("start") int start,
<strong> @Param("end") int end,
</strong> @Param("isPublished") boolean isPublished 
);
</code></pre>
