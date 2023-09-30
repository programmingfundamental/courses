---
layout: default
title: Оператор за цикъл For
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 7
---
# Оператор за цикъл For loop

Операторите за цикли се използват когато се налага многократно изпълнение на набор от изрази, докато не бъде изпълнено определено условие. В Java имаме три типа основни цикли: for, while и do-while.

Синтаксис

```
for(initialization; condition ; increment/decrement)
{
   statement(s);
}
```

### Пример:

```
class ForLoopExample {
    public static void main(String[] args){
         for(int i=10; i>1; i--){
              System.out.println("The value of i is: "+i);
         }
    }
}
```

Изход:

```
The value of i is: 10
The value of i is: 9
The value of i is: 8
The value of i is: 7
The value of i is: 6
The value of i is: 5
The value of i is: 4
The value of i is: 3
The value of i is: 2
```

### Безкраен циъл:

```
for ( ; ; ) {
    // statement(s)
}
```

### Пример с array:

```
class ForLoopExample3 {
    public static void main(String[] args){
         int[] arr = {2,11,45,9};
         //i starts with 0 as array index starts with 0 too
         for(int i=0; i<arr.length; i++){
              System.out.println(arr[i]);
         }
    }
}
```

Изход:

```
2
11
45
9
```
