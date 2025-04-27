---
layout: default
title: Model Mapper
parent: Laboratory excercise 9
grand_parent: Internet Technologies
nav_order: 5
---


<figure><img src="../../../assets/image (137).png" alt=""><figcaption></figcaption></figure>

ModelMapper is an automatic object mapping library, often used to map between DTO (Data Transfer Object) and entity classes.

Implementation Steps:

1. Add the dependency for ModelMapper in pom.xml:

To use ModelMapper, the following dependency needs to be added to pom.xml:

```xml
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>3.2.2</version>
</dependency>
```
In a Spring application, ModelMapper is usually defined as a Spring bean. This allows it to be automatically injected via @Autowired. Configuration can be done via a class annotated with @Configuration

To automatically configure a bean, you can take one of two steps:

* add the following method annotated with @Bean to your project:
  
```java
@Bean
public ModelMapper modelMapper() {
	return new ModelMapper();
}
```

For @Bean to work, the method must be in a class annotated with @Configuration or another Spring component (@Component, @Service, @RestController) so that Spring can recognize and manage it as a Bean.

* or create a ModelMapper descendant class annotated with @Component

```java
@Component
public class CustomMapper extends ModelMapper {
}
```

# Once configured, ModelMapper can be used to convert between entities. For example:

2. Create an entity class:
   
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
	private Role role;
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

# Working with Enum types in ModelMapper

ModelMapper automatically recognizes and maps enum values ​​when the names match between the source and target objects, but if the fields do not match (e.g. String ↔ Enum), a manual transformation can be performed:

```java
modelMapper.typeMap(UserDto.class, User.class).addMappings(mapper ->
    mapper.map(src -> Role.valueOf(src.getRole()), User::setRole)
);
```

# ModelMapper allows for more fine-grained configuration when needed, such as:

1. Omitting certain fields - ModelMapper maps all matching fields by name by default. In some cases, it may be necessary to not map certain fields. This is achieved by ignoring a given property.
   
```java
modelMapper.typeMap(Source.class, Destination.class)
    .addMappings(mapper -> mapper.skip(Destination::setIgnoredField));
```

When skip() is used, ModelMapper will not set a value for the specified field in the Destination object. This is useful for example for sensitive data or calculated values ​​that should not be overwritten.

2. Custom mapping rules - When the source and destination fields do not match in name or need transformation (e.g. date to String format), a manual mapping rule can be defined.
   
```java
modelMapper.typeMap(Source.class, Destination.class)
    .addMappings(mapper -> 
        mapper.map(src -> src.getFullName(), Destination::setName));
```

In this example, fullName from Source is mapped to name from Destination, even though the names do not match. This allows for full flexibility across different data models.

3. Mapping Conditions - Allows you to specify a condition that must be met for a given field to be mapped – for example, not null, longer than a certain length, etc.

```java
Condition<String, String> notNullOrEmpty = ctx ->
    ctx.getSource() != null && !ctx.getSource().isEmpty();

modelMapper.getConfiguration().setPropertyCondition(notNullOrEmpty);
```

This will cause ModelMapper to skip fields that are null or empty. This is useful for partial updates when you don't want to overwrite values ​​​​with empty or null data.

# Modifying the ModelMapper configuration

The getConfiguration() method of the ModelMapper class returns the current configuration of the instance. This is a way to access, change, or extend the behavior of ModelMapper through the settings it supports.

getConfiguration() - Returns an object of type org.modelmapper.config.Configuration; Used to fine-tune the way ModelMapper maps between objects; Allows you to set global rules that apply to all mappings in a given ModelMapper instance.

Modifying an object is done with the following methods:

1. setSkipNullEnabled(true/false)
- If true, fields with null values ​​in the source will not be mapped to the target.
- Example: The field email = null will not delete the value in the target object.

2. setFieldMatchingEnabled(true/false)
- If true, ModelMapper can map by field names even if there is no getter/setter.
- By default, ModelMapper only works with getter/setter methods (JavaBeans style).

3. setFieldAccessLevel(Configuration.AccessLevel) - Определя нивото на достъп до полетата:
- PUBLIC, PROTECTED, PACKAGE_PRIVATE, PRIVATE
- For example, PRIVATE allows access to private fields directly.

4. setSourceNameTokenizer(...) и setDestinationNameTokenizer(...)
- Allows customization of how field names are "broken" (e.g. camelCase → separate words)
- Used for more complex naming conventions.

5. setAmbiguityIgnored(true/false)
- If true, no exception will be thrown on ambiguous mapping.
- Example: When two fields seem to match, but ModelMapper doesn't know which one to choose.

6. setMatchingStrategy(...) - Определя стратегията за мапване:
- STANDARD – default
- STRICT – stricter match (only if names match exactly)
- LOOSE – looser match, more flexible

```java
modelMapper.getConfiguration()
    .setMatchingStrategy(MatchingStrategies.STRICT);
```

## Complete example

```java
mapper.getConfiguration()
      .setSkipNullEnabled(true)
      .setFieldMatchingEnabled(true)
      .setFieldAccessLevel(AccessLevel.PRIVATE)
      .setAmbiguityIgnored(true);
```

