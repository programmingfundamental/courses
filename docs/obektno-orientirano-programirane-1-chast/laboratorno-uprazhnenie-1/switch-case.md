---
layout: default
title: Условен оператор Switch
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 7
---

# Условен оператор switch-case

Операторът switch-case се използва, когато имаме няколко опции (или възможности за избор) и може да се наложи да изпълним различна задача за всеки избор.

```
switch (variable or an integer expression)
{
     case constant:
     //Java code
     ;
     case constant:
     //Java code
     ;
     default:
     //Java code
     ;
}
```

Пример:

```
public class SwitchCaseExample1 {

public static void main(String[] args) {
     int num=2;
     switch(num+2)
     {
        case 1:
	  System.out.println("Case1: Value is: "+num);
	case 2:
	  System.out.println("Case2: Value is: "+num);
	case 3:
	  System.out.println("Case3: Value is: "+num);
        default:
	  System.out.println("Default: Value is: "+num);
      }
   }
}
```

Операторът break не е задължителен, прекратява изпълнението след избор.

### Пример без break:

```
public class SwitchCaseExample2 {

public static void main(String[] args){
      int i=2;
      switch(i)
      {
	 case 1:
	   System.out.println("Case1 ");
	 case 2:
	   System.out.println("Case2 ");
	 case 3:
	   System.out.println("Case3 ");
	 case 4:
           System.out.println("Case4 ");
	 default:
	   System.out.println("Default ");
      }
   }
}
```

**Изход:**

```
Case2 
Case3 
Case4 
Default 
```

### Пример с break

```
public class SwitchCaseExample2 {

public static void main(String[] args){
      int i=2;
      switch(i)
      {
	 case 1:
	   System.out.println("Case1 ");
	   break;
	 case 2:
	   System.out.println("Case2 ");
	   break;
	 case 3:
	   System.out.println("Case3 ");
	   break;
	 case 4:
           System.out.println("Case4 ");
           break;
	 default:
	   System.out.println("Default ");
      }
   }
}
```

**Output:**

```
Case2
```
