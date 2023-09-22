---
layout: default
title: Разлика 5
parent: Лабораторно упражнение 6
grand_parent: Обектно-ориентирано програмиране - 1 част
has_children: true
nav_order: 5
---
# Разлика 5

Абстрактният клас може да има protected и public абстрактни методи

```
abstract class Example1{
   protected abstract void display1();
   public abstract void display2();
   public abstract void display3();
}
class Example2 extends Example1{
   public void display1(){
       System.out.println("display1 method");
   }
   public void display2(){
      System.out.println("display2 method");
   }
   public void display3(){
      System.out.println("display3 method");
   }
}
class Demo{
   public static void main(String args[]){
      Example2 obj=new Example2();
      obj.display1();
   }
}
```

Интерфейсът може да има само публични абстрактни методи

```
interface Example1{
   void display1();
}
class Example2 implements Example1{
   public void display1(){
      System.out.println("display1 method");
   }
   public void display2(){ 
      System.out.println("display2 method");
   }
}
class Demo{
   public static void main(String args[]){
       Example2 obj=new Example2();
       obj.display1();
   }
}
```

