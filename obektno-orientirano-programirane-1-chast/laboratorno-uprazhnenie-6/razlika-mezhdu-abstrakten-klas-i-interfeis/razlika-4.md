# Разлика 4

В абстрактния клас ключовата дума „абстрактно“ е задължителна за деклариране на метод като абстрактен

```
abstract class Example1{
   public abstract void display1();
}

class Example2 extends Example1{
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

В интерфейсите ключовата дума „абстрактно“ не е задължителна за деклариране на метод като абстрактен, тъй като всички методи са абстрактни по подразбиране

```
interface Example1{
    public void display1();
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

