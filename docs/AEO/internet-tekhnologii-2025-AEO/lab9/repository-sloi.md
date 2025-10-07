---
layout: default
title: Repository layer
parent: Laboratory excercise 9
grand_parent: Internet Technologies
nav_order: 3
---


# Repository layer

Typically, data management applications are implemented where data needs to be stored, edited, and deleted. For these, you need to implement CRUD operations (Create, Read, Update, Delete) for individual objects. Instead of writing the same CRUD operations over and over again, Spring Data provides various abstractions such as CrudRepository, PagingAndSortingRepository, and JpaRepository. They provide out-of-the-box support for CRUD operations, paging, and sorting.

These classes and interfaces must be defined in the @Repository annotation. @Repository is used to indicate that the class provides mechanisms for performing operations related to storing, accessing, updating, deleting, and searching objects.

We need to create an interface extending JpaRepository:

```java
public interface CityRepository extends JpaRepository<City, Long> {
}
```

This achieves two goals:

· First, by extending JpaRepository, we get a set of generic CRUD methods for our type, which allows saving cities, deleting them, etc., without having to write their implementation

· Second, using the Spring Data JPA repository infrastructure, this interface will be automatically scanned.

Some common methods provided:

```java
//Stores the submitted object in the database
<S extends T> S save(S entity);

//Returns the object by the given id
Optional<T> findById(ID id);

//Returns all objects of the given type
List<T> findAll();

//Deletes the specified object
void delete(T entity);
```

The specified methods are accessed directly in the service layer:

```java
@Override
public City createCity(City city) {
 
    City newCity = cityRepository.save(city);
    return newCity;
}
```

## Query methods

Query methods are a powerful tool for interacting with database records without having to write SQL queries. Behind the scenes, based on the query method, Spring Data JPA will create an SQL query and execute it for us. The invoked query is retrieved from the method name.

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

By using Query methods for more complex queries, we can reach a situation where our method looks like this: findAllByPostAndStatusAndReviewLikeAndVotesGreaterThanEqualOrderByCreatedOn

Besides the confusing appearance, a query submitted this way will be slower to execute. To solve this problem, we can use the @Query annotation.

Examples:

```java
@Query("SELECT t FROM Tutorial t")
List<Tutorial> findAll();

@Query("SELECT u FROM User u WHERE u.emailAddress = ?1")
User findByEmailAddress(String emailAddress);

@Query("SELECT t FROM tutorials t WHERE t.title LIKE %?1%")
List<Tutorial> findByTitleAndSort(String title, Sort sort);

@Query("SELECT MAX(eventId) AS eventId FROM Event")
Long lastProcessedEvent();

@Query("SELECT t FROM Tutorial t 
WHERE t.published=:isPublished AND t.level BETWEEN :start AND :end")
List<Tutorial> findByLevelBetween(
		@Param("start") int start, 
<strong>                @Param("end") int end, 
</strong>                @Param("isPublished") boolean isPublished
                );
 
```

Let's look at an example where we create a simple application with a list of training courses. Let's create a POJO class Course that describes the courses with fields like id, name, category, rating, and description.

```java
public class Course {
    
    private Long id;
    private String name;
    private String category;
    private int rating;
    private String description;
    private CourseLevel level;
    
    //Constructor, getters & setters...
}
```

Let's define a CourseRepository interface, which we will use to manage the courses in the database. The interface extends JpaRepository and defines a user method findAllByCategory(), which returns all courses in a given category.

```java
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

<strong>    //The usual methods for implementing CRUD operations are inherited from the JpaRepository interface
</strong>
    //user method
    List<Course> findAllByCategory(String category);
}
```

Let's extend the CourseRepository interface by adding custom methods to retrieve courses by difficulty level (CourseLevel), as well as a combined query by category and level.

```java
 @Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
	...
     List<Course> findAllByLevel(CourseLevel level);
     List<Course> findAllByCategoryAndLevel(String category, CourseLevel level);
}
```


Let's now create the service layer of the application. We define it with an interface that contains the operations supported in the application.

```java
public interface CourseService {

    Course createCourse(Course course);

    // + all other methods related to CRUD operations

    List<Course> getCoursesByCategory(String category);
}
```

Compose a concrete CourseServiceImpl class that performs these operations.

```java
// Annotated with @Service to indicate that it is a class
// that implements business logic
@Service
public class CourseServiceImpl implements CourseService {

    private CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
        }
    
    @Override
    public Course createCourse(Course course) {
    return courseRepository.save(course);
    }

<strong>    //+ all other methods related to CRUD operations
</strong>
<strong>    @Override
</strong>    public List<Course> getCoursesByCategory(String category) {
    return courseRepository.findAllByCategory(category);
    }
}
```

The CourseServiceImpl class is annotated with the @Service annotation to indicate that it is a service class and contains business logic. It uses the CourseRepository to perform the necessary database operations.

Now we need to define a CourseController, which defines the REST endpoints. The Spring controller contains one or more endpoints and accepts requests from the client. It then uses the services offered by the service layer and generates a response. The RestController binds the result to the response body and shares it with the requester of the endpoint.

```java
@RestController
@RequestMapping("/courses/")
public class CourseController {

    private CourseService courseService;

    public CourseController (CourseService courseService) {
        this. courseService = courseService;
        }

    @PostMapping
    public ResponseEntity&#x3C;Course> createCourse(@RequestBody Course course) {
        Course course = courseService.createCourse(course);
        return new ResponseEntity<>(course, HttpStatus.CREATED); 
    }

    //  + Точки на достъп за останалите операции
        
    @GetMapping("category/{name}")
    public ResponseEntity<List<Course>> getCourseByCategory(
    @PathVariable("name") String category) {
        List<Course> courseList = courseService.getCoursesByCategory(category);
        return new ResponseEntity<>(courseList, HttpStatus.OK); 
    }
}
```

# Class Optional\<T>

A container object that may or may not contain a non-null value.

Introduced in Java 8, it is used to represent a value that may or may not be present. In other words, an Optional object can either contain a non-zero value (in which case it is considered present), or it can contain no value at all (in which case it is considered empty).

Given the above, an Optional object can have one of the following possible states:

* Present: the value is present in the Optional object and can be accessed by calling the get() method.
* Empty: the Optional object contains no value; you cannot access its contents with get().

Optional is typically used as a return type for methods that may not always have a result to return. For example, a method that looks up a user by ID may not find a match, in which case it will return an empty Optional.

Optional can help reduce the number of null pointer exceptions in your code. It is not intended as a replacement for existing reference types, such as String or List, but rather as an addition to the Java type system.

### Optional\<T> and throwing exceptions

#### orElseThrow() method

Методът _orElseThrow()_ връща съдържащата се стойност, ако съществува, или хвърля `NoSuchElementException` или предвидено за целта изключение. 

Example:

```java
public class CourseServiceImpl implements CourseService {

    private CourseRepository courseRepository;
    private ModelMapper mapper;

    public CourseServiceImpl(CourseRepository courseRepository, ModelMapper mapper) {
        this.courseRepository = courseRepository;
        this.mapper = mapper;
    }

@Override
public CourseDto getCourseById(long id) {

    Course course = courseRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

    return mapToDto(course);
}
```
