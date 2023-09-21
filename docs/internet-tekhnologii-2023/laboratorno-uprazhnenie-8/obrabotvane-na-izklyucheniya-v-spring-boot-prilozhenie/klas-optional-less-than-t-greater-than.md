# Клас Optional\<T>

Обект-контейнер, който може да съдържа или да не съдържа стойност, различна от null.

&#x20;Въведен в Java 8, той се използва за представяне на стойност, която може или не може да бъде налична. С други думи, обектът Optional може или да съдържа ненулева стойност (в този случай се счита за present), или може да не съдържа никаква стойност (в този случай се счита за empty).

&#x20;Предвид изясненото дотук, обектът Optional може да има едно от следните възможни състояния:

* Present: стойността е налична в обекта Optional и може да бъде достъпна чрез извикване на метода get().
* Empty: обектът Optional не съдържа стойност; не можете да получите достъп до съдържанието му с get().

Optional обикновено се използва като тип на връщане за методи, при които не винаги може да имат резултат за връщане. Например, метод, който търси потребител по ID, може да не намери съвпадение, и в този случай ще върне празен Optional.

Optional може да помогне за намаляване на броя на null pointer exceptions във вашия код. Той не е предназначен като заместител на съществуващи референтни типове, като String или List, а по-скоро като допълнение към системата от типове Java.

### Optional\<T> и хвърляне на изключения

#### Методът orElseThrow() Method

Методът _orElseThrow()_ връща съдържащата се стойност, ако съществува, или хвърля `NoSuchElementException` или предвидено за целта изключение. &#x20;

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
