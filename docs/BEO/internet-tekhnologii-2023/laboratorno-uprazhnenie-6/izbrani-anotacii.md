---
layout: default
title: Реализация на HTTP комуникация
parent: Лабораторно упражнение 6
grand_parent: Интернет технологии
nav_order: 3
---

# Реализация на HTTP комуникация

В Spring инстанциите на класовете се създават и управляват от Spring контейнера, който използва т.нар. инверсия на контрола (IoC – Inversion of Control). Вместо ние сами да създаваме обекти чрез new, Spring го прави вместо нас и ни ги предоставя при нужда – това е известно като dependency injection (внедряване на зависимости). 

Създаването на инстанции в Spring става като се сканира за компоненти (класове), които са отбелязани със специални анотации. Създава се инстанции на класовете отбелязани с анотации (по подразбиране те са singleton). Инжектира ги в други класове, когато те ги заявят чрез анотации.

Компонентите в Spring представляват Spring Beans – обекти, които се създават, управляват и предоставят от Spring контейнера. Spring Bean е обикновен Java клас (POJO – Plain Old Java Object), който е регистриран в контейнера и участва в жизнения цикъл на приложението.

Spring Bean може да бъде създаден чрез анотации като:
- @Component
- @Service
- @Repository
- @Controller
- @RestController
- или чрез @Bean метод в клас, маркиран с @Configuration.

За да бъде един клас използван като Spring Bean, е необходимо:
- да бъде достъпен за Spring component scanning или изрично регистриран;
- да може да бъде създаден от Spring чрез конструктор;
- да участва в dependency injection механизма;
- да няма ограничения, които пречат на Spring proxy механизми при необходимост.

За разлика от класическия Java Bean, Spring Bean не изисква задължително празен (no-args) конструктор, нито задължително наличие на setter методи. В съвременния Spring се препоръчва използване на constructor injection, при което зависимостите се подават чрез конструктор.

Spring Bean-овете най-често се дефинират като singleton, което означава, че Spring създава една обща инстанция за цялото приложение, освен ако не е указан друг scope.



| Анотация         | За какво служи                                  |
|------------------|--------------------------------------------------|
| `@Component`     | Отбелязва обикновен Spring bean.                |
| `@Service`       | Специализиран `@Component` за бизнес логика.    |
| `@Repository`    | Също `@Component`, но за достъп до база данни.  |
| `@Controller`    | За контролери в MVC приложения.                 |
| `@RestController`| Комбинация от `@Controller` и `@ResponseBody`.  |
| `@Autowired`     | Инжектира зависимост (обект).                   |
| `@Bean`          | Създава ръчно bean, обикновено в `@Configuration` клас. |
| `@Configuration` | Клас, съдържащ дефиниции на bean-ове.           |
| `@Qualifier`     | Използва се, когато има повече от една възможна зависимост. |

### @RequestMapping

Анотацията @RequestMapping се използва за дефиниране на URI на заявката за достъп до крайните точки на REST. В него можем да дефинираме и метод на заявката. Методът за заявка по подразбиране е GET.

```java
@RequestMapping(value = "/ex/foos", method = RequestMethod.GET)
public ResponseEntity<Object> getProducts() { }
```

### @GetMapping (@PostMapping, @PutMapping…)

Анотация за съотнасяне на HTTP GET заявки към специфични методи за обработка. По-конкретно, @GetMapping е съставна анотация, която действа като пряк път за @RequestMapping(method = RequestMethod.GET)

```java
@GetMapping("/{id}")
public ResponseEntity<Object> getProducts() { }
```

### @PathVariable

Анотацията @PathVariable се използва за дефиниране на персонализиран или динамичен URI на заявката. Променливата id в URI на заявката се дефинира във фигурни скоби {}, както е показано по-долу.

```java
@GetMapping("/{id}/view")
public ResponseEntity<Object> updateProduct(@PathVariable("id") String id) {
}
```

### @RequestParam

Анотацията @RequestParam се използва за четене на параметри от URL адреса на заявката. Можем да зададем стойност по подразбиране за параметрите на заявката, както е показано тук.

```java
@GetMapping("/api/foos")
public ResponseEntity<Object> getProduct(
   @RequestParam(value = "name", required = false, defaultValue = "honey") String name) {
}
```

### @ModelAttribute

Анотацията @ModelAttribute се използва за свързване на параметри от HTTP заявка към Java обект. Тя автоматично попълва полетата на обекта със стойности от query parameters, form data или path параметри, когато имената съвпадат.
Най-често се използва при GET заявки за филтриране или подаване на множество параметри към DTO обект.

```java
@GetMapping("/reports")
public ResponseEntity<List<Object>> getReports(@ModelAttribute FilterReportDto filter) {
}
```

### @RequestBody

Анотацията @RequestBody се използва за определяне на типа на съдържанието на тялото на заявката.

```java
public ResponseEntity<Object> createProduct(@RequestBody Product product) {
}
```
