# Запазена дума static

Запазена дума –static

Запазената дума static в Java се използва основно за управление на паметта. Static принадлежи повече на класа от колкото на инстанцията на класа. Можем да прилагаме „static“ на :

·         Променливи;

§  Статичните променливи се използват за общо свойство между всички обекти.\
Пример: Името на компания за хората работещи в нея.\
Статичната променлива се запазва само веднъж в паметта в клас областта по време на заделяне на памет за класа.

·         Методи;

§  Статичните методи не могат да използват не статични член-променливи или да извикват не статични методи.

·         Блокове;

§  Изпълнява се преди main метода, по време на зареждането на класовете.

·         Вложени класове.   &#x20;

```
class Student{ 
   static String college ="ITS"; // static variable
   int fn;
   String name; 
   public Student(int num, String n){ 
   fn = num; 
   name = n; 
   }
  static void changeCollegeName(String newName){  // static method
     college = newName; 
  }    
   void display (){
System.out.println(name + ": " + fn );
} 
} 
 
public class Main{ 
 public static void main(String args[]){ 
 Student s1 = new Student(123, "Plamen"); 
 Student s2 = new Student(124, "Gergana"); 
 s1.display(); 
 s2.display(); 
 System.out.println(Student.college);//invoke static variable
 Student.changeCollegeName("TY Varna"); //invoke static method
 System.out.println(Student.college);
 } 
} 
```