---
layout: default
title: HTTP communication
parent: Laboratory excercise 6
grand_parent: Internet Technologies
nav_order: 7
---


# HTTP communication implementation

In Spring, class instances are created and managed by the Spring container, which uses the so-called Inversion of Control (IoC). Instead of us creating objects ourselves using new, Spring does it for us and provides them to us when needed – this is known as dependency injection.

Creating instances in Spring is done by scanning for components (classes) that are marked with special annotations. It creates instances of the classes marked with annotations (by default they are singleton). It injects them into other classes when they declare them using annotations.

Components are Java Bean classes, which are a Plain Old Java Object (POJO) that meets several specific rules in order to be used by other technologies such as Java EE, JSP, JSF, Spring, etc.

Java Bean Requirements:
- Empty (no-args) constructor – required for the object to be created dynamically.
- Private fields – accessible via getter and setter methods.
- Setters and getters (accessor and mutator methods) – follow a standard pattern: getX(), setX(value).

Be serializable (optional) – sometimes required to implement Serializable in order to be able to write to a stream.

| Annotation | What it is for |
|-----|---------------------------------------------------|
| `@Component` | Marks a regular Spring bean. |
| `@Service` | Specialized `@Component` for business logic. |
| `@Repository` | Same as `@Component`, but for database access. |
| `@Controller` | For controllers in MVC applications. |
| `@RestController`| Combination of `@Controller` and `@ResponseBody`. |
| `@Autowired` | Injects a dependency (object). |
| `@Bean` | Creates a bean manually, usually in a `@Configuration` class. |
| `@Configuration` | A class containing bean definitions. |
| `@Qualifier` | Used when there is more than one possible dependency. |

### @RestController

Classic controllers in Spring are denoted by the @Controller annotation. This is a specialization of the @Component class, which allows automatic discovery of implementation classes by scanning the classpath.

_@RestController_ is a specialized controller that includes, in addition to _@Controller_, the _@ResponseBody_ annotation, which allows automatic serialization of the returned object into an HttpResponse.

```
@RestController
public class ProductServiceController {
}
```

### @RequestMapping

The @RequestMapping annotation is used to define the request URI for accessing REST endpoints. We can also define a request method in it. The default request method is GET.

```
@RequestMapping(value = "/ex/foos", method = RequestMethod.GET)
public ResponseEntity<Object> getProducts() { }
```

### @GetMapping (@PostMapping, @PutMapping…)

An annotation for mapping HTTP GET requests to specific processing methods. Specifically, @GetMapping is a composite annotation that acts as a shortcut for @RequestMapping(method = RequestMethod.GET)

```
@GetMapping("/{id}")
public ResponseEntity<Object> getProducts() { }
```

### @RequestBody

The @RequestBody annotation is used to specify the content type of the request body.

```
public ResponseEntity<Object> createProduct(@RequestBody Product product) {
}
```

### @PathVariable

The @PathVariable annotation is used to define a custom or dynamic request URI. The id variable in the request URI is defined in curly braces {} as shown below.

```
@GetMapping("/{id}/view")
public ResponseEntity<Object> updateProduct(@PathVariable("id") String id) {
}
```

### @RequestParam

The @RequestParam annotation is used to read parameters from the request URL. We can set a default value for the request parameters as shown here.

```
@GetMapping("/api/foos")
public ResponseEntity<Object> getProduct(
@RequestParam(value = "name", required = false, defaultValue = "honey") String name) {
}
```
