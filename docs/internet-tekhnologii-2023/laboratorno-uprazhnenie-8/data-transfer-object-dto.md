---
layout: default
title: Data transfer object (DTO)
parent: Лабораторно упражнение 8
grand_parent: Интернет технологии
nav_order: 2
---

# Data transfer object (DTO)

Data Transfer Object Design Pattern е често използван шаблон за проектиране. Основно се използва за предаване на данни с множество атрибути в една заявка от клиент към сървър, за да се избегнат множество повиквания към отдалечения сървър.

Предимство на използването на DTO в RESTful API, написани на Java (и със Spring Boot), е че те могат да помогнат да се скрият имплементационните детайли на обектите (JPA entities). Разкриването на тези обекти чрез крайни точки може да се превърне в проблем със сигурността, ако не боравим внимателно с това какви свойства могат да бъдат променяни и чрез какви операции.

<figure><img src="../../../assets/image (137).png" alt=""><figcaption></figcaption></figure>

ModelMapper e библиотека, която се използва за преобразуване на entity обект в DTO и обратно.

Стъпки за реализация:

1. Добавяме библиотеката ModelMapper

```xml
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>3.1.1</version>
</dependency>
```

За автоматичното конфигуриране на bean можете да предприемете една от двете стъпки:

* в Main класа добавете следния метод, анотиран с @Bean:

```java
@Bean
public ModelMapper modelMapper() {
	return new ModelMapper();
}
```

* или създайте клас-наследник на ModelMapper, анотиран с @Component

```java
@Component
public class CustomMapper extends ModelMapper {
}
```

2. Създаваме entity клас:

<pre class="language-java"><code class="lang-java">@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

<strong>        private int id;
</strong>   	private String name;
    	private String email;
    	private String password;
}
</code></pre>

3. Създаваме DTO клас:

<pre class="language-java"><code class="lang-java">@Setter
@Getter
@NoArgsConstructor
public class UserDto {
    private int id;
    private String name;   	
<strong>    private String email;
</strong>}

</code></pre>

**UserService.java**

```java
public interface UserService {  
 
    public User createUser(UserDto userDto);
    public UserDto getUser(int userId);
}
```

**UserServiceImpl.java**

<pre class="language-java"><code class="lang-java">@Service
public class UserServiceImpl implements UserService {
  
    private UserRepository userRepository;
    private ModelMapper modelMapper;

	public UserServiceImpl(UserRepository userRepository, 
	                       ModelMapper modelMapper){
	this.userRepository = userRepository;
	this. modelMapper = modelMapper;
<strong>    }
</strong>      
    @Override
    public User createUser(UserDto userDto) {
	Use user = modelMapper.map(userDto, User.class);
        User userSavedToDB = userRepository.save(user);
	UserDto userResponse = modelMapper.map(userSavedToDB, UserDto.class);
        return userResponse;
    }
  
    // update it with UserDto
    @Override
    public UserDto getUser(int userId) {
        User user = userRepository.findById(userId).get();
        UserDto userDto = modelMapper.map(user, UserDto.class);
        return userDto;
    }
}
</code></pre>

**UserController.java**

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
      
    private UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }
      
    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto){
        UserDto userCreated = this.userServiceImpl.createUser(userDto);
        return new ResponseEntity<UserDto>(userCreated, HttpStatus.CREATED);
    }
      
    @GetMapping("/get/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") int userId){
        UserDto userDto = this.userServiceImpl.getUser(userId);
        return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
    }
}
```
