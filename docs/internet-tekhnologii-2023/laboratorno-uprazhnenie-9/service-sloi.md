---
layout: default
title: Слой за бизнес логика
parent: Лабораторно упражнение 9
grand_parent: Интернет технологии
nav_order: 4
---


### @Service

Използва се анотиране за класове, които осигуряват реализацията на бизнес логика.

```
@Service
public class CourseServiceImpl implements CourseService {
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

