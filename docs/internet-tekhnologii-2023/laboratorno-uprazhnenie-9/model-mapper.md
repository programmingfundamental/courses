---
layout: default
title: ModelMapper
parent: Лабораторно упражнение 9
grand_parent: Интернет технологии
nav_order: 5
---

<figure><img src="../../../assets/image (137).png" alt=""><figcaption></figcaption></figure>

ModelMapper е библиотека за автоматично преобразуване между обекти, често използвана за преобразуване между DTO (Data Transfer Object) и ентити класове. 

Стъпки за реализация:

1. Добавяме зависимостта за ModelMapper в pom.xml:

За да се използва ModelMapper, е необходимо да се добави следната зависимост в pom.xml:

```xml
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>3.2.2</version>
</dependency>
```
В Spring приложението, ModelMapper обикновено се дефинира като Spring bean. Това позволява той да бъде автоматично инжектиран чрез @Autowired. Конфигурацията може да се извърши чрез клас, анотиран с @Configuration

За автоматичното конфигуриране на bean можете да предприемете една от двете стъпки:

* в проекта добавете следния метод, анотиран с @Bean:

```java
@Bean
public ModelMapper modelMapper() {
	return new ModelMapper();
}
```

За да работи @Bean, методът трябва да бъде в клас, анотиран с @Configuration или друг Spring компонент (@Component, @Service, @RestController), за да може Spring да го разпознае и управлява като Bean.

* или създайте клас-наследник на ModelMapper, анотиран с @Component

```java
@Component
public class CustomMapper extends ModelMapper {
}
```

# След конфигурирането, ModelMapper може да бъде използван за преобразуване между обекти. Например:

2. Създаваме entity клас:
   
```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
 	private int id;
 	private String name;
    	private String email;
    	private String password;
}
 
```
3. 

**UserService.java**

```java
public interface UserService {

    public UserDto createUser(UserDto userDto);
    public UserDto getUser(int userId);
}
```

**UserServiceImpl.java**
```java
@Service
public class UserServiceImpl implements UserService {
  
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

	public UserServiceImpl(UserRepository userRepository, 
	                       ModelMapper modelMapper){
	this.userRepository = userRepository;
	this. modelMapper = modelMapper;
}

    @Override
    public UserDto createUser(UserDto userDto) {
	User user = modelMapper.map(userDto, User.class);
        User userSavedToDB = userRepository.save(user);
	UserDto userResponse = modelMapper.map(userSavedToDB, UserDto.class);
        return userResponse;
    }
  
    @Override
    public UserDto getUser(int userId) {
        User user = userRepository.findById(userId).get();
        UserDto userDto = modelMapper.map(user, UserDto.class);
        return userDto;
    }
}
```

**UserController.java**

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
      
    private final UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }
      
    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto){
        UserDto userCreated = this.userServiceImpl.createUser(userDto);
        return new ResponseEntity<>(userCreated, HttpStatus.CREATED);
    }
      
    @GetMapping("/get/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") int userId){
        UserDto userDto = this.userServiceImpl.getUser(userId);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
```

# ModelMapper позволява по-прецизна конфигурация при нужда, като например:

1. Пропускане на определени полета - ModelMapper по подразбиране мапва всички съвпадащи по име полета. В някои случаи може да е необходимо определени полета да не се мапват. Това се постига чрез игнориране на дадено свойство.

```java
modelMapper.typeMap(Source.class, Destination.class)
    .addMappings(mapper -> mapper.skip(Destination::setIgnoredField));
```

Когато се използва skip(), ModelMapper няма да зададе стойност за посоченото поле в целевия обект (Destination). Това е полезно например при чувствителни данни или изчисляеми стойности, които не трябва да се презаписват.

2. Кастъм правила за мапване - Когато изходното и целевото поле не съвпадат по име или се нуждаят от трансформация (например дата във формат String), може да се дефинира ръчно правило за мапване.

```java
modelMapper.typeMap(Source.class, Destination.class)
    .addMappings(mapper -> 
        mapper.map(src -> src.getFullName(), Destination::setName));
```

В този пример fullName от Source се мапва към name от Destination, въпреки че имената не съвпадат. Това позволява пълна гъвкавост при различни модели на данни.

3. Условия за мапване - Позволява се задаване на условие, което трябва да бъде изпълнено, за да се извърши мапването на дадено поле – например да не е null, да е по-дълго от определена дължина и др.

```java
Condition<String, String> notNullOrEmpty = ctx ->
    ctx.getSource() != null && !ctx.getSource().isEmpty();

modelMapper.getConfiguration().setPropertyCondition(notNullOrEmpty);
```

Така ModelMapper ще пропусне полетата, които са null или празни. Това е полезно при partial updates (частично обновяване), когато не трябва да се презаписват стойности с празни или null данни.

# Промяна на конфигурацията на ModelMapper

Метода getConfiguration() на класа ModelMapper връща текущата конфигурация на инстанцията. Това е начин да се достъпи, промени или разшири поведението на ModelMapper чрез настройките, които той поддържа.

getConfiguration() - Връща обект от тип org.modelmapper.config.Configuration; Служи за фина настройка на начина, по който ModelMapper извършва мапването между обекти; Позволява да се задават глобални правила, които важат за всички мапвания в дадената инстанция на ModelMapper.

Модифицирането на обекта се извършва със следните метод:

1. setSkipNullEnabled(true/false)
    - Ако е true, полетата със null стойност в source няма да се мапват към целта.
    - Пример: Полето email = null няма да изтрие стойността в целевия обект.

2. setFieldMatchingEnabled(true/false)
    - Ако е true, ModelMapper може да мапва по имена на полета, дори ако няма getter/setter.
    - По подразбиране ModelMapper работи само с getter/setter методи (JavaBeans стил).

3. setFieldAccessLevel(Configuration.AccessLevel) - Определя нивото на достъп до полетата:
    - PUBLIC, PROTECTED, PACKAGE_PRIVATE, PRIVATE
    - Например PRIVATE позволява достъп до частни полета директно.

4. setSourceNameTokenizer(...) и setDestinationNameTokenizer(...)
    - Позволяват персонализиране как се „разбиват“ имената на полета (напр. camelCase → отделни думи)
    - Използва се при по-сложни конвенции за именуване.

5. setAmbiguityIgnored(true/false)
    - Ако е true, няма да се хвърля изключение при нееднозначно мапване.
    - Пример: Когато две полета изглеждат подходящи, но ModelMapper не знае кое да избере.

6. setMatchingStrategy(...) - Определя стратегията за мапване:
    - STANDARD – по подразбиране
    - STRICT – по-строго съвпадение (само ако имената съвпадат точно)
    - LOOSE – по-свободно съвпадение, по-гъвкаво

```java
modelMapper.getConfiguration()
    .setMatchingStrategy(MatchingStrategies.STRICT);
```

## Цялостен пример

```java
mapper.getConfiguration()
      .setSkipNullEnabled(true)
      .setFieldMatchingEnabled(true)
      .setFieldAccessLevel(AccessLevel.PRIVATE)
      .setAmbiguityIgnored(true);
```