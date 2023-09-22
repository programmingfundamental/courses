---
layout: default
title: Условен оператор If
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 6
---

# Условен оператор If

### Твърдение If

Твърдението if се състои от условие, последвано от операции или набор от операции, както е показано по -долу:

```
if(condition){
  Statement(s);
}
```

Операторите се изпълняват само когато даденото условие е вярно. Ако условието е невярно, тогава операторите в тялото на израза се игнорират.\
![if statement flow diagram](https://beginnersbook.com/wp-content/uploads/2017/08/if\_statement\_flow\_diagram.jpg)

#### Пример

```
public class IfStatementExample {

   public static void main(String[] args){
      int num=70;
      if( num < 100 ){
	  /* This println statement will only execute,
	   * if the above condition is true
	   */
	  System.out.println("number is less than 100");
      }
   }
}
```

**Изход:**

```
number is less than 100
```

Вложен израз на if в Java&#x20;

Когато има оператор if в друг оператор if, той се нарича вложен оператор if. Структурата на вложения if, изглежда така:

```
if(condition_1) {
   Statement1(s);

   if(condition_2) {
      Statement2(s);
   }
}
```

#### Пример:

```
public class NestedIfExample {

   public static void main(String[] args){
        int num=70;
	if( num < 100 ){ 
           System.out.println("number is less than 100"); 
           if(num > 50){
	      System.out.println("number is greater than 50");
	   }
	}
   }
}
```

**Изход:**

```
number is less than 100
number is greater than 50
```

### If else в Java

```
if(condition) {
   Statement(s);
}
else {
   Statement(s);
}
```

Блока „if“ ще се изпълни, ако условието е вярно, и блока „else“ ще се изпълнят, ако условието е невярно.\
![If else flow diagram](https://beginnersbook.com/wp-content/uploads/2017/08/If\_else\_flow\_diagram.jpg)

#### Пример:

```
public class IfElseExample {

   public static void main(String[] args){
     int num=120;
     if( num < 50 ){
	System.out.println("num is less than 50");
     }
     else {
	System.out.println("num is greater than or equal 50");
     }
   }
}
```

**Изход:**

```
num is greater than or equal 50
```

### if-else-if в Java

if-else-if се използва, когато трябва да проверим множество условия.

```
if(condition_1) {
   /*if condition_1 is true execute this*/
   statement(s);
}
else if(condition_2) {
   /* execute this if condition_1 is not met and
    * condition_2 is met
    */
   statement(s);
}
else if(condition_3) {
   /* execute this if condition_1 & condition_2 are
    * not met and condition_3 is met
    */
   statement(s);
}
.
.
.
else {
   /* if none of the condition is true
    * then these statements gets executed
    */
   statement(s);
}
```

#### Пример:

```
public class IfElseIfExample {

   public static void main(String[] args){
	int num=1234;
	if(num <100 && num>=1) {
	  System.out.println("Its a two digit number");
	}
	else if(num <1000 && num>=100) {
	  System.out.println("Its a three digit number");
	}
	else if(num <10000 && num>=1000) {
	  System.out.println("Its a four digit number");
	}
	else if(num <100000 && num>=10000) {
	  System.out.println("Its a five digit number");			
	}
	else {
	  System.out.println("number is not between 1 & 99999");			
	}
   }
}
```

**Изход:**

```
Its a four digit number
```
