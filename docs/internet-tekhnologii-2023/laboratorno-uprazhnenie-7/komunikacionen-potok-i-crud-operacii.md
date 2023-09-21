# Комуникационен поток и CRUD операции

Следната диаграма демонстрира потока, свързан с обработката на заявка и отговор в REST API в Spring Boot приложение.

<figure><img src="../../.gitbook/assets/image (97).png" alt=""><figcaption></figcaption></figure>

1\.      Потребителят извиква REST точка на достъп, която се управлява от REST контролера.

2\.      След това контролерът използва сервизния слой, за да обработи заявката.

3\.      Сервизният слой разчита на хранилището за комуникация с базата данни.

4\.      След като има отговор от хранилището, той се обработва от сервизния слой и се препраща към контролера.

5\.      Контролерът може да извърши допълнителна обработка и крайният отговор се предоставя на API клиента.

Да разгледаме пример, при който се създава семпло приложение със списък на курсове за обучение. Нека да създадем POJO клас Course, който описва курсовете с полета като id, name, category, rating и description.

```java
public class Course {
    
    private Long id;
    private String name;
    private String category;
    private int rating;
    private String description;
    
    //Конструктор, getters & setters...
}
```

Да дефинираме интерфейс CourseRepository, с помощта на който ще управляваме курсовете в базата от данни. Интерфейсът разширява JpaRepository и дефинира потребителски метод findAllByCategory(), който извежда всички курсове от дадена категория.

<pre class="language-java"><code class="lang-java">@Repository
public interface CourseRepository extends JpaRepository&#x3C;Course, Long> {

<strong>    //Обичайните методи за осъществяване на CRUD операциите се наследяват от интерфейса JpaRepository
</strong>
    //потребителски метод
    List&#x3C;Course> findAllByCategory(String category);
}
</code></pre>

Нека сега да създадем сервизния слой на приложението. Дефинираме го с интерфейс, който съдържа операциите, поддържани в приложението.

```java
public interface CourseService {

    Course createCourse(Course course);

    // + всички останали методи, касаещи CRUD операциите

    List<Course> getCoursesByCategory(String category);
}
```

&#x20;Съставете конкретен клас CourseServiceImpl, който изпълнява тези операции.

<pre class="language-java"><code class="lang-java">// Анотиран с @Service, за да индикира, че е клас, 
// който реализира бизнес логика 
@Service
<strong>public class CourseServiceImpl implements CourseService {
</strong>
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
</strong>    public List&#x3C;Course> getCoursesByCategory(String category) {
    return courseRepository.findAllByCategory(category);
    }
}

</code></pre>

Класът CourseServiceImpl е анотиран с анотация @Service, за да покаже, че е сервизен клас и съдържа бизнес логика. Той използва CourseRepository за извършване на необходимите операции с базата от данни.

Сега ни остава да дефинираме CourseController, който дефинира крайните точки на REST. Spring контролерът съдържа една или повече крайни точки и приема заявки от клиента. След това използва услугите, предлагани от сервизния слой, и генерира отговор. RestContoller-ът свързва резултата с тялото на отговора и го споделя със заявителя на крайната точка

<pre class="language-java"><code class="lang-java">@RestController
@RequestMapping("/courses/")
public class CourseController {

    private CourseService courseService;

    public CourseController (CourseService courseService) {
        this. courseService = courseService;
        }

<strong>    @PostMapping
</strong>    public ResponseEntity&#x3C;Course> createCourse(@RequestBody Course course) {
        Course course = courseService.createCourse(course);
        return new ResponseEntity&#x3C;>(course, HttpStatus.CREATED); 
    }

    //  + Точки на достъп за останалите операции
        
    @GetMapping("category/{name}")
    public ResponseEntity&#x3C;List&#x3C;Course>> getCourseByCategory(
    @PathVariable("name") String category) {
        List&#x3C;Course> courseList = courseService.getCoursesByCategory(category);
        return new ResponseEntity&#x3C;>(courseList, HttpStatus.OK); 
    }
}

</code></pre>
