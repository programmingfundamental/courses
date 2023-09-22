---
layout: default
title: Разлика 6
parent: Лабораторно упражнение 6
grand_parent: Обектно-ориентирано програмиране - 1 част
has_children: true
nav_order: 6
---
# Разлика 6

Абстрактният клас може да има static, final или static final променливи с всеки спецификатор за достъп

```
abstract class Example1{
   private int numOne=10;
   protected final int numTwo=20;
   public static final int numThree=500;
   public void display1(){
      System.out.println("Num1="+numOne);
   }
}
class Example2 extends Example1{
   public void display2(){
      System.out.println("Num2="+numTwo);
      System.out.println("Num2="+numThree);
   }
}
class Demo{
   public static void main(String args[]){
      Example2 obj=new Example2(); 
      obj.display1();
      obj.display2();
   }
}
```

Интерфейсът може да има само public static final (константна) променлива

```
interface Example1{
   int numOne=10;
}
class Example2 implements Example1{
   public void display1(){
      System.out.println("Num1="+numOne);
   }
}
class Demo{
   public static void main(String args[]){
      Example2 obj=new Example2();
      obj.display1();
   }
}
```
