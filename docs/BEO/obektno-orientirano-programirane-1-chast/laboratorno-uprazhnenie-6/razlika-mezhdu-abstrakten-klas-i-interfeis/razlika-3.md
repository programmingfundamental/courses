---
layout: default
title: Разлика 3
parent: Разлика между Абстрактен клас и Интерфейс
grand_parent: Лабораторно упражнение 6
has_children: true
nav_order: 3
---
# Разлика 3

Абстрактният клас може да има както абстрактни, така и конкретни методи

```
abstract class Example1 {
   abstract void display1();
   public void display2(){
     System.out.println("display2 method");
   }
}
class Example2 extends Example1{
   public void display1(){
      System.out.println("display1 method");
   }
}
class Demo{
   public static void main(String args[]){
     Example2 obj=new Example2();
     obj.display1();
   }
}
```

Интерфейсът в повечето случаи може да има само абстрактни методи, не може да има конкретни методи (изключват се дефолтните реализирани методи)

```
interface Example1{
   void display1();
}
class Example2 implements Example1{
   public void display1(){
      System.out.println("display1 method");
   }
}
class Demo{
   public static void main(String args[]){
      Example2 obj=new Example2();
      obj.display1();
   }
}
```
