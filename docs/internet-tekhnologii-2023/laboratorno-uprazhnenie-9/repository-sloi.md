---
layout: default
title: Repository слой
parent: Лабораторно упражнение 9
grand_parent: Интернет технологии
nav_order: 3
---

# Repository слой

Обикновено се реализират приложения за управление на данни, в които е необходимо да се съхраняват, редактират и изтриват данни. За тях е необходимо да внедрите CRUD операции (Create, Read, Update, Delete) за отделните обекти. Вместо да разписвате едни и същи CRUD операции отново и отново, Spring Data предоставя различни абстракции като CrudRepository, PagingAndSortingRepository и JpaRepository. Те предоставят готова поддръжка за CRUD операции, страниране и сортиране.

Тези класове и интерфейси трябва да са дефинирани в анотация @Repository. @Repository се използва, за да индикира, че класът осигурява механизми за осъществяване на операции, свързани със съхранение, достъп, обновяване, изтриване и търсене на обекти.

Необходио е да създадем интерфейс, разширяващ JpaRepository:

```java
public interface CityRepository extends JpaRepository<City, Long> {
}
```

С това се постигат две цели:

·        Първо, чрез разширяване на JpaRepository, ние получаваме набор от общи CRUD методи за нашия тип, който позволява запазване на градове, изтриването им и т.н., без да се налага да разписваме тяхната имплементация

·        Второ, с помощта на инфраструктурата на хранилището на Spring Data JPA този интерфейс автоматично ще бъде сканиран.

Някои предоставяни общи методи:

```java
//Съхранява в БД подадения обект
<S extends T> S save(S entity);

//Връща обекта по подаден id
Optional<T> findById(ID id);

//Връща всички обекти от дадения тип
List<T> findAll();

//Изтрива зададения обект
void delete(T entity);
```

Посочените методи се достъпват директно в обслужващия слой:

```java
@Override
public City createCity(City city) {
 
    City newCity = cityRepository.save(city);
    return newCity;
}
```

## Query методи

Query методите са мощен инструмент за взаимодействие със записите от базата от данни, без да се налага да пишем SQL заявки. Зад кулисите, въз основа на метода на заявката, Spring Data JPA ще създаде SQL заявка и ще я изпълни вместо нас. Извиканата заявка се извлича от името на метода.

Пример:

```java
public interface UserRepository extends JpaRepository<User, Long> {

  List<User> findByEmailAddressAndLastname(String emailAddress, String lastname);

}
```

Spring Data JPA превежда името на метода в следната JPQL заявка:

**select u from User u where u.emailAddress = ?1 and u.lastname = ?2**

### Правила за създаване на Query методи

1. Името да метода трябва да започва с един от посочените префикси: find…By, read…By, query…By, count…By, get…By и др. Примери: findByName, readByName, queryByName, getByName
2. Ако искаме да ограничим броя на върнатите резултати, можем да добавим ключовите думи First или Top преди By. Примери: findFirstByName, readFirst2ByName, findTop10ByName
3. Ако искаме да изберем уникални резултати, добавяме ключова дума Distinct преди By. Примери: findDistinctByName или findNameDistinctBy
4. Комбинирайте изрази с AND или OR. Примери: findByNameOrDescription, findByNameAndDescription              

[Списък на поддържаните ключови думи](https://docs.spring.io/spring-data/jpa/docs/3.0.x/reference/html/#repository-query-keywords).

Още примерни методи:

```java

Optional<Dog> findById(Long id);

List<Dog> findByAgeAndHeight(Integer age, Double height);

Integer countByName(String name);

Dog findFirstByName(String name);

List<Dog> findTop10ByColor(String color);

Dog findTopByOrderByBirthDateDesc();

List<Dog> findByAgeLessThanOrHeightGreaterThan(Integer age, double height);

```

### @Query анотация

Използвайки Query методите при по-сложни заявки, можем да достигнем ситуация, при която методът ни да има следния вид: findAllByPostAndStatusAndReviewLikeAndVotesGreaterThanEqualOrderByCreatedOn

Освен объркващия външен вид, подадена по този начин заявката ще бъде по-бавна за изпълнение. За разрешаване на този проблем можем да използваме анотацията @Query.

Примери:
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

Да разгледаме пример, при който се създава семпло приложение със списък на курсове за обучение. Нека да създадем POJO клас Course, който описва курсовете с полета като id, name, category, rating и description.

```java
public class Course {
    
    private Long id;
    private String name;
    private String category;
    private int rating;
    private String description;
    private CourseLevel level;
    
    //Конструктор, getters & setters...
}
```

Да дефинираме интерфейс CourseRepository, с помощта на който ще управляваме курсовете в базата от данни. Интерфейсът разширява JpaRepository и дефинира потребителски метод findAllByCategory(), който извежда всички курсове от дадена категория.
```java
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

<strong>    //Обичайните методи за осъществяване на CRUD операциите се наследяват от интерфейса JpaRepository
</strong>
    //потребителски метод
    List<Course> findAllByCategory(String category);
}
```

Да разширим интерфейса CourseRepository, като добавим потребителски методи за извличане на курсове според нивото на трудност (CourseLevel), както и комбинирана заявка по категория и ниво.

```java
 @Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
	...
     List<Course> findAllByLevel(CourseLevel level);
     List<Course> findAllByCategoryAndLevel(String category, CourseLevel level);
}
```


Нека сега да създадем сервизния слой на приложението. Дефинираме го с интерфейс, който съдържа операциите, поддържани в приложението.

```java
public interface CourseService {

    Course createCourse(Course course);

    // + всички останали методи, касаещи CRUD операциите

    List<Course> getCoursesByCategory(String category);
}
```

Съставете конкретен клас CourseServiceImpl, който изпълнява тези операции.

```java
// Анотиран с @Service, за да индикира, че е клас, 
// който реализира бизнес логика 
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

<strong>    //+ всички останали методи, касаещи CRUD операциите
</strong>
<strong>    @Override
</strong>    public List<Course> getCoursesByCategory(String category) {
    return courseRepository.findAllByCategory(category);
    }
}
```

Класът CourseServiceImpl е анотиран с анотация @Service, за да покаже, че е сервизен клас и съдържа бизнес логика. Той използва CourseRepository за извършване на необходимите операции с базата от данни.

Сега ни остава да дефинираме CourseController, който дефинира крайните точки на REST. Spring контролерът съдържа една или повече крайни точки и приема заявки от клиента. След това използва услугите, предлагани от сервизния слой, и генерира отговор. RestContoller-ът свързва резултата с тялото на отговора и го споделя със заявителя на крайната точка

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

# Клас Optional\<T>

Обект-контейнер, който може да съдържа или да не съдържа стойност, различна от null.

Въведен в Java 8, той се използва за представяне на стойност, която може или не може да бъде налична. С други думи, обектът Optional може или да съдържа ненулева стойност (в този случай се счита за present), или може да не съдържа никаква стойност (в този случай се счита за empty).

Предвид изясненото дотук, обектът Optional може да има едно от следните възможни състояния:

* Present: стойността е налична в обекта Optional и може да бъде достъпна чрез извикване на метода get().
* Empty: обектът Optional не съдържа стойност; не можете да получите достъп до съдържанието му с get().

Optional обикновено се използва като тип на връщане за методи, при които не винаги може да имат резултат за връщане. Например, метод, който търси потребител по ID, може да не намери съвпадение, и в този случай ще върне празен Optional.

Optional може да помогне за намаляване на броя на null pointer exceptions във вашия код. Той не е предназначен като заместител на съществуващи референтни типове, като String или List, а по-скоро като допълнение към системата от типове Java.

### Optional\<T> и хвърляне на изключения

#### Методът orElseThrow() Method

Методът _orElseThrow()_ връща съдържащата се стойност, ако съществува, или хвърля `NoSuchElementException` или предвидено за целта изключение. 

Пример:

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
