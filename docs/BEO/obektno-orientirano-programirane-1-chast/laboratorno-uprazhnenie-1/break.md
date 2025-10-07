---
layout: default
title: Оператор Break
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 12
---
# Break

Прекратява оператора, който се изпълнява в момента:

### Пример за break в while-loop

```
public class BreakExample1 {
   public static void main(String[] args){
      int num =0;
      while(num<=100)
      {
          System.out.println("Value of variable is: "+num);
          if (num==2)
          {
             break;
          }
          num++;
      }
      System.out.println("Out of while-loop");
  }
}
```

**Изход:**

```
Value of variable is: 0
Value of variable is: 1
Value of variable is: 2
Out of while-loop
```

### Пример за break във for-loop

```
public class BreakExample2 {

public static void main(String[] args){
	int var;
	for (var =100; var>=10; var --)
	{
	    System.out.println("var: "+var);
	    if (var==99)
	    {
	         break;
	    }
	 }
	 System.out.println("Out of for-loop");
   }
}
```

**Output:**

```
var: 100
var: 99
Out of for-loop
```

### Пример за break в switch-case

```
public class BreakExample3 {

   public static void main(String[] args){
	int num=2;
	      
	switch (num)
	{
	    case 1:
	       System.out.println("Case 1 ");
	       break;
	    case 2:
	       System.out.println("Case 2 ");
	       break;
	    case 3:
	       System.out.println("Case 3 ");
	       break;
	    default:
	       System.out.println("Default ");
	}
   }
}
```

**Изход:**

```
Case 2
```
