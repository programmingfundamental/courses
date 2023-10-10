---
layout: default
title: Wrapper Class
parent: Лабораторно упражнение 2
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 6
---
# Wrapper Class

Wrapper Class е клас, чийто обект обвива или съдържа примитивни типове данни. Когато създаваме обект към Wrapper Class, той съдържа поле и в това поле можем да съхраняваме примитивни типове данни. С други думи, можем да обвием примитивна стойност в обект от Wrapper Class.

Wrapper Class в java осигурява механизма за конвертиране на примитивен тип данни в обект. Този процес се нарича **boxing**, а преобразуването на обект в примитивен тип данни се нарича **unboxing**.\


От J2SE 5.0, функциите за **boxing** и **unboxing** преобразувания се изпълняват автоматично. Автоматичното преобразуване на примитивен тип данни в обект е известно като автоматичен **boxing** и обратно, като автоматичен **unboxing**.

\
Пакетът java.lang съдържа обгръщащите кмласове в java. Списъкът на Wrapper Class е даден по-долу:

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

### Autoboxing и Unboxing

Автоматичното преобразуване на примитивни типове в обекта на съответните им класове е известно като autoboxing.

```
import java.lang.Integer;
 
public class Application {                                                                                                     
    public static void main(String args[]) { 
        //Конвертиране на int към Integer 
        int a = 20; 
        Integer i = Integer.valueOf(a);//Конвертиране на int към Integer 
        Integer j = a;//autoboxing, новите компилатори ще използват Integer.valueOf(a) вътрешно
        System.out.println(a+" "+i+" "+j);
    }
}
```

Автоматичното преобразуване на обект от клас в съответния примитивен тип е известно като разопаковане (auto unboxing)

```
import java.lang.Integer;
 
public class Application {   
    public static void main(String args[]) {   
        //Конвертиране на Integer в int   
        Integer a=new Integer(3);   
        int i=a.intValue();//unboxing или конвентиране на Integer в int 
        int j=a;//auto unboxing, новите компилатори ще използват a.intValue() вътрешно   
     
        System.out.println(a+" "+i+" "+j);   
    }
}
```

### Пример:

```
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
