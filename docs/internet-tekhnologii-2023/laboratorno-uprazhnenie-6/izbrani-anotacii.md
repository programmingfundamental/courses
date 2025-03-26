---
layout: default
title: Реализация на HTTP комуникация
parent: Лабораторно упражнение 6
grand_parent: Интернет технологии
nav_order: 5
---

# Реализация на HTTP комуникация

В Spring инстанциите на класовете се създават и управляват от Spring контейнера, който използва т.нар. инверсия на контрола (IoC – Inversion of Control). Вместо ние сами да създаваме обекти чрез new, Spring го прави вместо нас и ни ги предоставя при нужда – това е известно като dependency injection (внедряване на зависимости). 

Създаването на инстанции в Spring става като се сканира за компоненти (класове), които са отбелязани със специални анотации. Създава се инстанции на класовете отбелязани с анотации (по подразбиране те са singleton). Инжектира ги в други класове, когато те ги заявят чрез анотации.

Компоненти са Java Bean класове, който е е обикновен Java обект (POJO – Plain Old Java Object), който отговаря на няколко специфични правила, за да може да се използва от други технологии като Java EE, JSP, JSF, Spring и др.

Изисквания за Java Bean:
- Празен (no-args) конструктор – нужен е за да може обектът да се създаде динамично.
- Приватни полета (fields) – достъпни чрез getter и setter методи.
- Сетъри и гетъри (accessor и mutator methods) – следват стандартен шаблон: getX(), setX(value).

Да е сериализуем (по желание) – понякога се изисква да имплементира Serializable, за да може да се записва в поток.



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

### @RestController

Класическите контролери в Spring се обозначават с анотацията @Controller. Това е специализация на класа @Component, която позволява автоматично да откриване на кляасове за внедряване чрез сканиране на classpath-a.

_@RestController_ е специализиран контролер, който включва освен _@Controller_ и анотацията _@ResponseBody_, която позволява автоматично сериализиране на върнатия обект в HttpResponse.

```
@RestController
public class ProductServiceController { 
}
```

### @RequestMapping

Анотацията @RequestMapping се използва за дефиниране на URI на заявката за достъп до крайните точки на REST. В него можем да дефинираме и метод на заявката. Методът за заявка по подразбиране е GET.

```
@RequestMapping(value = "/ex/foos", method = RequestMethod.GET)
public ResponseEntity<Object> getProducts() { }
```

### @GetMapping (@PostMapping, @PutMapping…)

Анотация за съотнасяне на HTTP GET заявки към специфични методи за обработка. По-конкретно, @GetMapping е съставна анотация, която действа като пряк път за @RequestMapping(method = RequestMethod.GET)

```
@GetMapping("/{id}")
public ResponseEntity<Object> getProducts() { }
```

### @RequestBody

Анотацията @RequestBody се използва за определяне на типа на съдържанието на тялото на заявката.

```
public ResponseEntity<Object> createProduct(@RequestBody Product product) {
}
```

### @PathVariable

Анотацията @PathVariable се използва за дефиниране на персонализиран или динамичен URI на заявката. Променливата id в URI на заявката се дефинира във фигурни скоби {}, както е показано по-долу.

```
@GetMapping("/{id}/view")
public ResponseEntity<Object> updateProduct(@PathVariable("id") String id) {
}
```

### @RequestParam

Анотацията @RequestParam се използва за четене на параметри от URL адреса на заявката. Можем да зададем стойност по подразбиране за параметрите на заявката, както е показано тук.

```
@GetMapping("/api/foos")
public ResponseEntity<Object> getProduct(
   @RequestParam(value = "name", required = false, defaultValue = "honey") String name) {
}
```

