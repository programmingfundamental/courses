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

Интерфейсът може да има само абстрактни методи, не може да има конкретни методи

```
interface Example1{
   public abstract void display1();
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
