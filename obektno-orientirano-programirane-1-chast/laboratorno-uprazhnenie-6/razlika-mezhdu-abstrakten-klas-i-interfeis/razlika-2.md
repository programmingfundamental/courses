# Разлика 2

Абстрактният клас може да бъде разширен (наследен) от клас или абстрактен клас

```
class Example1{
   public void display1(){
      System.out.println("display1 method");
   }
}
abstract class Example2{
   public void display2(){
       System.out.println("display2 method");
   }
}
abstract class Example3 extends Example2{
   abstract void display3();
}
class Example4 extends Example3{
   public void display2(){
       System.out.println("Example4-display2 method");
   }
   public void display3(){
       System.out.println("display3 method");
   }
}
class Demo{
   public static void main(String args[]){
       Example4 obj=new Example4();
       obj.display2();
   }
}
```

Интерфейсите могат да бъдат наследен само от интерфейси. Класовете трябва да ги имплементират, вместо да ги наследяват

```
interface Example1{
    public void display1();
}
interface Example2 extends Example1{
}
class Example3 implements Example2{
   public void display1(){
      System.out.println("display1 method");
   }
}
class Demo{
   public static void main(String args[]){
      Example3 obj=new Example3();
      obj.display1();
   }
}
```
