---
layout: default
title: Wrapper Class
parent: Лабораторно упражнение 2
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 6
---
# Wrapper Class

**Wrapper Class** е клас, чийто обект обвива или съдържа **примитивни типове данни**.  
С други думи, Wrapper Class позволява на **примитивните типове** да се третират като **обекти**.

Когато създаваме обект от Wrapper Class, той съдържа поле, в което можем да съхраняваме **примитивна стойност**. Това позволява:

* Използване на **примитивни типове** в структури от данни, които приемат само обекти (напр. `ArrayList`, `HashMap`).  
* Извикване на методи върху стойности, които иначе са **примитивни типове**.  
* Удобно преобразуване между **примитивни типове** и обекти чрез **boxing** и **unboxing**.


## Преобразуване на примитивни типове в Wrapper Class

Java предлага механизъм за **опаковане на примитивни типове в обекти**, наречен **boxing**, и обратно – **unboxing**.

- **Boxing:** конвертира **примитивен тип** в обект на Wrapper Class.  
- **Unboxing:** конвертира обект на Wrapper Class обратно в **примитивен тип**.  

От Java 5 нататък тези операции се изпълняват **автоматично**:

- **Autoboxing:** автоматично опаковане на **примитивен тип** в обект.  
- **Auto unboxing:** автоматично разопаковане на обект обратно в **примитивен тип**.


## Wrapper класове в Java

Пакетът `java.lang` съдържа всички Wrapper класове за **примитивните типове**:

| Primitive Type | Wrapper class |
| -------------- | ------------- |
| boolean        | Boolean       |
| char           | Character     |
| byte           | Byte          |
| short          | Short         |
| int            | Integer       |
| long           | Long          |
| float          | Float         |
| double         | Double        |

## Пример за Boxing и Autoboxing

Boxing е процесът, при който **примитивен тип** се преобразува в обект, за да може да се използва в обектно-ориентирани структури. Autoboxing е автоматичният вариант на този процес.

```java
import java.lang.Integer;
 
public class Application {                                                                                                     
    public static void main(String args[]) { 
        //Конвертиране на int към Integer 
        int a = 20; 
        Integer i = Integer.valueOf(a);
        Integer j = a;
        System.out.println(a+" "+i+" "+j);
    }
}
```

- Integer i = Integer.valueOf(a); – ръчно създаване на обект от Wrapper Class.
- Integer j = a; – автоматично преобразуване на примитивен тип int в Integer (autoboxing).

## Пример за Unboxing и Auto Unboxing

Unboxing е процесът, при който обект на Wrapper Class се преобразува обратно в примитивен тип. Auto unboxing е автоматичният вариант, който компилаторът извършва сам.

```java
import java.lang.Integer;
 
public class Application {   
    public static void main(String args[]) {   
        //Конвертиране на Integer в int   
        Integer a=new Integer(3);   
        int i=a.intValue(); //unboxing 
        int j=a; //auto unboxing   
     
        System.out.println(a+" "+i+" "+j);   
    }
}
```

-  i = a.intValue(); – извлича примитивната стойност ръчно.
- int j = a; – компилаторът автоматично извиква a.intValue() (auto unboxing).


## Създаване на Wrapper обекти и връщане обратно към примитивен тип

```java
class Application {
    public static void main(String args[]) {
    
        byte a = 1;
        Byte byteobj = new Byte(a);
 
        int b = 10;
        Integer intobj = new Integer(b);
 
        float c = 18.6f;
        Float floatobj = new Float(c);
 
        double d = 250.5;
        Double doubleobj = new Double(d);

        char e='a';
        Character charobj=e;
 
        System.out.println("Boxing");
        System.out.println("Byte обекта byteobj:  " + byteobj);
        System.out.println("Integer обекта intobj:  " + intobj);
        System.out.println("Float обекта floatobj:  " + floatobj);
        System.out.println("Double обекта doubleobj:  " + doubleobj);
        System.out.println("Character обекта charobj:  " + charobj);
 
        byte bv = byteobj;
        int iv = intobj;
        float fv = floatobj;
        double dv = doubleobj;
        char cv = charobj;
 
        System.out.println("Unboxing");
        System.out.println("byte стойността на bv: " + bv);
        System.out.println("int стойността на iv: " + iv);
        System.out.println("float стойността на fv: " + fv);
        System.out.println("double стойността на dv: " + dv);
        System.out.println("char стойността на cv: " + cv);
    }
}

```
