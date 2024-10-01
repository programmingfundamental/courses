---
layout: default
title: Оператор Continue
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 11
---
# Оператор Continue

Прескача итерациите в изълнението на цикъл.

**Синтаксис:**

```
continue;
```

### Пример с for

```
public class ContinueExample {

public static void main(String[] args){
	for (int j=0; j<=6; j++)
	{
           if (j==4)
           {
	      continue;
	   }

           System.out.print(j+" ");
	}
   }
}
```

**Изход:**

```
0 1 2 3 5 6
```



![Continue Statement](https://beginnersbook.com/wp-content/uploads/2017/08/Continue\_Statement.jpg)

### Пример за While loop

```
public class ContinueExample2 {

public static void main(String[] args){
	int counter=10;
	while (counter >=0)
	{
           if (counter==7)
           {
	       counter--;
	       continue;
           }
           System.out.print(counter+" ");
           counter--;
	}
   }
}
```

**Изход:**

```
10 9 8 6 5 4 3 2 1 0
```

### Пример do-While loop

```
public class ContinueExample3 {

public static void main(String[] args){
	int j=0;
        do
	{
	   if (j==7)
	   {
		 j++;
		 continue;
	   }
	   System.out.print(j+ " ");
	   j++;
       }while(j<10);
		  
   }
}
```

**Изход:**

```
0 1 2 3 4 5 6 8 9 
```
