# Полиморфизъм



**Полиморфизъм**&#x20;

Думата **полиморфизъм** идва от гръцка дума и означава, че има много форми. Полиморфизмът е обектно-ориентирана концепция за програмиране, която се отнася до способността на променлива, функция или обект да приеме множество форми. Чрез наследяване обектите могат да презаписват (override) поведение, наследено от родителския клас, със специфично за наследника такова. Полиморфизма позволява един и същи метод да изпълнява различно поведение по два начина:&#x20;

1\.    Пренатоварване (overloading)

Полиморфизъм по време на компилиране използва пренатоварване (overloading) на метода. Методите в един клас  могат да има едно и също име , но трябва да приемат различен брой параметри. Различни имплементации могат да съществуват в зависимост от броя на подаваните параметри и типа на параметрите.

Изисквания:

·        Да има същото име;

·        Да има различен брой параметри или да има различен тип параметри;

Не може да имаме повече от един параметър с еднакъв брой и тип параметри.

Не може да имаме два метода, които имат еднакъв брой и тип параметри, но връщат различен тип, защото компилатора няма да знае кой от двата метода да извика.

```
class Shapes {
  public void area() {
    System.out.println("Find area ");
  }
public void area(int r) {
    System.out.println("Circle area = "+3.14*r*r);
  }
 
public void area(double b, double h) {
    System.out.println("Triangle area="+0.5*b*h);
  }
public void area(int l, int b) {
    System.out.println("Rectangle area="+l*b);
  }
 
 
}
 
class Main {
  public static void main(String[] args) {
    Shapes myShape = new Shapes();  // Create a Shapes object
     
    myShape.area();
    myShape.area(5);
    myShape.area(6.0,1.2);
    myShape.area(6,2);
     
  }
}

```

2\.    Презаписване (overriding)

Полиморфизъм по време на изпълнение (runtime) използва пренаписване на метода (overriding). Клас наследник може да предостави различна имплементация от имплементацията на класа родител. Инструкцията към компилатора която използваме е @Override – показва на компилатора, че този метод от родителския клас има нова имплементация. Тази инструкция (анотация) се използва по време на компилирането, за да се провери дали метода е презаписан правилно.

Изисквания за презаписване на метод:

·        Да носи същото име като метода в родителския клас;

·        Да приема същия брой параметри;

·        Да приема параметри от същия тип ;

·        Да връща същия тип.

Статични методи не могат да бъдат презаписвани, защото принадлежат на класа, докато инстанциите на методите (нестатичните) принадлежат на обекта.

```
class Shapes {
  public void area() {
    System.out.println("The formula for area of ");
  }
}
class Triangle extends Shapes {
  public void area() {
    System.out.println("Triangle is ½ * base * height ");
  }
}
class Circle extends Shapes {
  public void area() {
    System.out.println("Circle is 3.14 * radius * radius ");
  }
}
class Main {
  public static void main(String[] args) {
    Shapes myShape = new Shapes();  // Create a Shapes object
    Shapes myTriangle = new Triangle();  // Create a Triangle object
    Shapes myCircle = new Circle();  // Create a Circle object
    myShape.area();
    myTriangle.area();
    myShape.area();
    myCircle.area();
  }
}
```
