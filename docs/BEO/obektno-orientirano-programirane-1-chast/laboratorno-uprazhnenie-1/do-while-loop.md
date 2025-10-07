---
layout: default
title: Оператор за цикъл Do-while
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 10
---

# Do-while loop

Цикъл със след условие

**Синтаксис:**

```
do
{
   statement(s);
} while(condition);
```

![do while loop java](https://beginnersbook.com/wp-content/uploads/2015/03/do-while\_java.jpg)

### Пример:

```
class DoWhileLoopExample {
    public static void main(String[] args){
         int i=10;
         do{
              System.out.println(i);
              i--;
         }while(i>1);
    }
}
```

**Изход:**

```
10
9
8
7
6
5
4
3
2
```

### Пример с масив:

```
class DoWhileLoopExample2 {
    public static void main(String[] args){
         int arr[]={2,11,45,9};
         //i starts with 0 as array index starts with 0
         int i=0;
         do{
              System.out.println(arr[i]);
              i++;
         }while(i<4);
    }
}
```

Изход:

2\
11\
45\
9
