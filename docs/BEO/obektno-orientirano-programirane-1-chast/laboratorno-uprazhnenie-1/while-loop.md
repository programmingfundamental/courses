---
layout: default
title: Оператор за цикъл while
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 9
---
# While loop

## Цикъл с пред условие:

Синтаксис:

```
while(condition)
{
   statement(s);
}
```

![while loop java](https://beginnersbook.com/wp-content/uploads/2015/03/while\_loop\_java.jpg)

### Пример:

```
class WhileLoopExample {
    public static void main(String[] args){
         int i=10;
         while(i>1){
              System.out.println(i);
              i--;
         }
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

### Пример

```
class WhileLoopExample2 {
    public static void main(String[] args){
         int i=10;
         while(i>1)
         {
             System.out.println(i);
              i++;
         }
    }
}
```

Безкраен цикъл:

```
while (true){
    statement(s);
}
```

### Пример с масив

```
class WhileLoopExample3 {
    public static void main(String[] args){
         int arr[]={2,11,45,9};
         //i starts with 0 as array index starts with 0 too
         int i=0;
         while(i<4){
              System.out.println(arr[i]);
              i++;
         }
    }
}
```

**Изход:**

```
2
11
45
9
```
