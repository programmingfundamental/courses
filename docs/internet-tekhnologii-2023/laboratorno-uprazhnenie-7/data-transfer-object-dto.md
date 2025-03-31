---
layout: default
title: Data transfer object (DTO)
parent: Лабораторно упражнение 7
grand_parent: Интернет технологии
nav_order: 6
---

# Data transfer object (DTO)

Data Transfer Object Design Pattern е често използван шаблон за проектиране. Основно се използва за предаване на данни с множество атрибути в една заявка от клиент към сървър, за да се избегнат множество повиквания към отдалечения сървър.

Предимство на използването на DTO в RESTful API, написани на Java (и със Spring Boot), е че те могат да помогнат да се скрият имплементационните детайли на обектите (JPA entities). Разкриването на тези обекти чрез крайни точки може да се превърне в проблем със сигурността, ако не боравим внимателно с това какви свойства могат да бъдат променяни и чрез какви операции.

Създаваме DTO клас:
   
```java
@Setter
@Getter
@NoArgsConstructor
public class UserDto {
	private int id;
	private String name;   	
	private String email;
}
```
