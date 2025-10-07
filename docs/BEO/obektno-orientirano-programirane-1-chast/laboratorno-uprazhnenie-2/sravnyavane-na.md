---
layout: default
title: Сравняване
parent: Лабораторно упражнение 2
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 7
---
# Сравняване

Когато присвояваме целочислена стойност на целочислен обект, стойността автоматично се преобразува в целочислен обект. Например твърдението "Integer x = 10" създава обект "x" със стойност 10.

Но какъв ще е изхода от следния пример?


Пример
```
public class Application {
    public static void main(String args[]) {
         Integer x = 400, y = 400;
         if (x == y)
            System.out.println("Едни и същи");
         else
            System.out.println("Не са едни и същи");
    }
}
```


Second Tab
```
Не са едни и същи
```
info
Тъй като x и y се отнасят до различни обекти, получаваме изхода като "Не са едни и същи"



Но как може да се обясни изхода от следния пример:


Пример
```
public class Application {
    public static void main(String args[]) {
         Integer x = 40, y = 40;
         if (x == y)
            System.out.println("Едни и същи");
         else
            System.out.println("Не са едни и същи");
    }
}
```


Резултат
```
Едни и същи
```

info
В Java стойностите от -128 до 127 се кешират, така че същите обекти се връщат. Използването на valueOf() използва кеширани обекти, ако стойността е между -128 до 127.



Ако изрично създадем нови обекти с помощта на оператор new, получаваме изхода като "Не са едни и същи". Вижте следната Java програма, където не се използва valueOf().


Пример
```
public class Application {
    public static void main (String args[]) {
          Integer x = new Integer(40), y = new Integer(40);
         if (x == y)
            System.out.println("Едни и същи");
         else
            System.out.println("Не са едни и същи");
    }
}
```


Резултат
```
Не са едни и същи
```



С получената информация до тук, какъв ще е изхода от следния пример и защо?


Пример
```
public class Application {
    public static void main(String[] args) {
          Integer X = new Integer(10);
          Integer Y = 10;
          
          System.out.println(X == Y);
    }
}
```


Second Tab
```
false
```

info
Тук ще бъдат създадени два обекта. Първият обект, който е насочен от X поради използването оператор new и втори обект, ще бъде създаден заради autoboxing.




\
